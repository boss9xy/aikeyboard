package com.example.aikeyboard

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException


class VoiceToTextManager(
    private val context: Context
) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var isRecording = false
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    companion object {
        private const val TAG = "VoiceToTextManager"
        private const val MAX_FILE_SIZE = 25 * 1024 * 1024 // 25MB
    }
    
    interface VoiceToTextCallback {
        fun onRecordingStarted()
        fun onRecordingStopped()
        fun onTranscriptionStarted()
        fun onTranscriptionCompleted(text: String)
        fun onError(message: String)
    }
    
    private var callback: VoiceToTextCallback? = null
    
    fun setCallback(callback: VoiceToTextCallback) {
        this.callback = callback
    }
    
    fun isRecording(): Boolean = isRecording
    
    fun startRecording(): Boolean {
        if (isRecording) {
            Log.w(TAG, "Already recording")
            return false
        }
        
        return try {
            setupMediaRecorder()
            mediaRecorder?.start()
            isRecording = true
            callback?.onRecordingStarted()
    
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            cleanupOnError()
            callback?.onError("Failed to start recording: ${e.message}")
            false
        }
    }
    
    fun stopRecording() {
        if (!isRecording) {
            Log.w(TAG, "Not recording")
            callback?.onError("Not recording")
            return
        }
        
        try {
            releaseRecorder()
            callback?.onRecordingStopped()
            
            outputFile?.let { file ->
                if (file.exists() && file.length() > 0) {
                    callback?.onTranscriptionStarted()
                    transcribeAudioFile(file)
                } else {
                    Log.w(TAG, "No audio data recorded")
                    callback?.onError("No audio data recorded")
                    cleanup()
                }
            } ?: run {
                Log.w(TAG, "No output file found")
                callback?.onError("No output file found")
                cleanup()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording", e)
            cleanupOnError()
            callback?.onError("Failed to stop recording: ${e.message}")
        }
    }
    
    fun cancelRecording() {
        if (!isRecording) {
            Log.w(TAG, "Not recording, nothing to cancel")
            return
        }
        
        try {
            cleanup()
            callback?.onRecordingStopped()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel recording", e)
            cleanupOnError()
        }
    }
    
    private fun setupMediaRecorder() {
        val file = createTempFile()
        outputFile = file
        
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(16000)
            setAudioEncodingBitRate(128000)
            setOutputFile(file.absolutePath)
            prepare()
        }
    }
    
    private fun createTempFile(): File {
        val tempDir = context.cacheDir
        return File.createTempFile("voice_recording_", ".m4a", tempDir)
    }
    
    private fun releaseRecorder() {
        try {
            mediaRecorder?.apply {
                if (isRecording) {
                    stop()
                }
                release()
            }
        } finally {
            isRecording = false
            mediaRecorder = null
        }
    }
    
    private fun cleanupOnError() {
        outputFile?.let { file ->
            if (file.exists()) {
                file.delete()
            }
        }
        cleanup()
    }
    
    private fun cleanup() {
        outputFile = null
        releaseRecorder()
    }
    
    private fun transcribeAudioFile(audioFile: File) {
        recordingJob?.cancel()
        recordingJob = scope.launch {
            try {
                val transcription = sendAudioToWhisperAPI(audioFile)
                callback?.onTranscriptionCompleted(transcription)
                
                // Clean up the audio file
                if (audioFile.exists()) {
                    audioFile.delete()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Transcription failed", e)
                callback?.onError("Transcription failed: ${e.message}")
                
                // Clean up the audio file on error
                if (audioFile.exists()) {
                    audioFile.delete()
                }
            }
        }
    }
    
    /**
     * Gửi file âm thanh đến Whisper API để chuyển đổi thành văn bản
     * Public method để có thể gọi từ VoiceToTextActivity
     */
    suspend fun sendAudioToWhisperAPI(audioFile: File): String {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient.Builder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(300, java.util.concurrent.TimeUnit.SECONDS) // 5 phút cho upload
                .readTimeout(300, java.util.concurrent.TimeUnit.SECONDS)   // 5 phút cho download
                .build()
            
            // Use auto language detection (empty string = auto)
            val language = ""
            
            // Create multipart request
            val requestBodyBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    audioFile.name,
                    audioFile.asRequestBody("audio/m4a".toMediaType())
                )
                .addFormDataPart("model", "whisper-1")
                .addFormDataPart("response_format", "text")
            
            // Add language parameter if specified (auto-detect if empty)
            if (language.isNotEmpty()) {
                requestBodyBuilder.addFormDataPart("language", language)
            }
            
            val requestBody = requestBodyBuilder.build()
            
            val request = Request.Builder()
                .url("https://api.openai.com/v1/audio/transcriptions")
                .addHeader("Authorization", "Bearer ${getOpenAIKey()}")
                .post(requestBody)
                .build()
            
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected response: ${response.code}")
                }
                
                val responseBody = response.body?.string()
                    ?: throw IOException("Empty response body")
                
        
                responseBody
            }
        }
    }
    
    private fun getOpenAIKey(): String {
        // Lấy API key từ SettingsActivity hoặc SharedPreferences
        val sharedPrefs = context.getSharedPreferences("AIKeyboardPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("gpt_api_key", "") ?: ""
    }
    
    fun release() {
        recordingJob?.cancel()
        cleanup()
        scope.cancel()
    }
} 