package com.example.aikeyboard

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.util.*
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

class VoiceToTextActivity : AppCompatActivity() {
    private lateinit var clearButton: Button
    private lateinit var copyButton: Button
    private lateinit var clearAllButton: Button

    private lateinit var recognizedTextTextView: EditText
    private lateinit var selectAudioFileButton: Button
    private lateinit var convertedListRecyclerView: RecyclerView
    private lateinit var progressSection: LinearLayout
    private lateinit var statusTextView: TextView
    private lateinit var progressBar: ProgressBar
    
    private var recognizedText = ""
    private lateinit var voiceToTextManager: VoiceToTextManager
    private lateinit var convertedTextAdapter: ConvertedTextAdapter
    private val convertedTexts = mutableListOf<String>()
    
    companion object {
        private const val PICK_AUDIO_FILE_REQUEST = 400
        private const val STORAGE_PERMISSION_REQUEST = 500
        private const val TAG = "VoiceToTextActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_to_text)
        
        initializeViews()
        setupButtons()
        setupRecyclerView()
        loadSettings()
        
        // Khởi tạo VoiceToTextManager
        voiceToTextManager = VoiceToTextManager(this)
    }
    
    private fun initializeViews() {
        clearButton = findViewById(R.id.clearButton)
        copyButton = findViewById(R.id.copyButton)
        clearAllButton = findViewById(R.id.clearAllButton)

        recognizedTextTextView = findViewById(R.id.recognizedTextTextView)
        selectAudioFileButton = findViewById(R.id.selectAudioFileButton)
        convertedListRecyclerView = findViewById(R.id.convertedListRecyclerView)
        progressSection = findViewById(R.id.progressSection)
        statusTextView = findViewById(R.id.statusTextView)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun setupButtons() {
        clearButton.setOnClickListener {
            clearText()
        }
        
        copyButton.setOnClickListener {
            copyText()
        }
        
        clearAllButton.setOnClickListener {
            clearAllConvertedTexts()
        }
        
        selectAudioFileButton.setOnClickListener {
            if (checkStoragePermission()) {
                selectAudioFile()
            } else {
                requestStoragePermission()
            }
        }
    }
    
    private fun setupRecyclerView() {
        convertedTextAdapter = ConvertedTextAdapter(this, convertedTexts)
        convertedListRecyclerView.layoutManager = LinearLayoutManager(this)
        convertedListRecyclerView.adapter = convertedTextAdapter
        
        // Load dữ liệu đã lưu
        convertedTextAdapter.loadConvertedTexts()
    }
    

    
    private fun clearText() {
        recognizedTextTextView.text.clear()
        saveRecognizedText("")
        Toast.makeText(this, "Text cleared", Toast.LENGTH_SHORT).show()
    }
    
    private fun copyText() {
        val text = recognizedTextTextView.text.toString()
        if (text.isNotEmpty()) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Recognized Text", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Text copied", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun clearAllConvertedTexts() {
        AlertDialog.Builder(this)
            .setTitle("Clear All Texts")
            .setMessage("Are you sure you want to delete all converted texts?")
            .setPositiveButton("Clear All") { _, _ ->
                convertedTextAdapter.clearAll()
                Toast.makeText(this, "All texts cleared", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun loadSettings() {
        // Load saved text
        recognizedText = getSharedPreferences("VoiceToTextSettings", MODE_PRIVATE)
            .getString("saved_text", "") ?: ""
        recognizedTextTextView.setText(recognizedText)
    }
    

    
    private fun saveRecognizedText(text: String) {
        recognizedText = text
        val prefs = getSharedPreferences("VoiceToTextSettings", MODE_PRIVATE)
        prefs.edit().putString("saved_text", recognizedText).apply()
    }
    
    override fun onPause() {
        super.onPause()
        saveRecognizedText(recognizedTextTextView.text.toString())
    }

    private fun selectAudioFile() {
        if (!checkStoragePermission()) {
            // Hiển thị dialog giải thích và xin quyền
            AlertDialog.Builder(this)
                .setTitle("File Access Permission Required")
                .setMessage("The app needs file access permission to select audio files. Please grant permission to continue.")
                .setPositiveButton("Grant Permission") { _, _ ->
                    requestStoragePermission()
                }
                .setNegativeButton("Cancel", null)
                .show()
            return
        }
        
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, PICK_AUDIO_FILE_REQUEST)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == PICK_AUDIO_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                processAudioFile(uri)
            }
        }
    }
    
    private fun processAudioFile(uri: Uri) {
        try {
            val fileName = getFileName(uri)
            
            // Show progress
            showProgress("Preparing audio file...")
            
            // Copy file to temp location for processing
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("audio_", ".m4a", cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            
            // Process with VoiceToTextManager
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    runOnUiThread {
                        updateProgress("Sending to Whisper API...")
                    }
                    
                    val transcription = voiceToTextManager.sendAudioToWhisperAPI(tempFile)
                    
                    runOnUiThread {
                        hideProgress()
                        recognizedTextTextView.setText(transcription)
                        saveRecognizedText(transcription)
                        
                        // Thêm vào danh sách đã chuyển đổi
                        convertedTextAdapter.addText(transcription)
                        
                        Toast.makeText(this@VoiceToTextActivity, "Audio file processed: $fileName", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        hideProgress()
                        Log.e(TAG, "Error processing audio file", e)
                        Toast.makeText(this@VoiceToTextActivity, "Error processing audio file: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    // Clean up temp file
                    if (tempFile.exists()) {
                        tempFile.delete()
                    }
                }
            }
            
        } catch (e: Exception) {
            hideProgress()
            Log.e(TAG, "Error processing audio file", e)
            Toast.makeText(this, "Error processing audio file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun getFileName(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameIndex) ?: "Unknown"
        } ?: "Unknown"
    }
    
    private fun checkStoragePermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    private fun requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                STORAGE_PERMISSION_REQUEST
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST
            )
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            STORAGE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectAudioFile()
                } else {
                    Toast.makeText(this, "File access permission needed to select audio files", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showProgress(message: String) {
        runOnUiThread {
            statusTextView.text = message
            progressSection.visibility = View.VISIBLE
            selectAudioFileButton.isEnabled = false
        }
    }
    
    private fun updateProgress(message: String) {
        runOnUiThread {
            statusTextView.text = message
        }
    }
    
    private fun hideProgress() {
        runOnUiThread {
            progressSection.visibility = View.GONE
            selectAudioFileButton.isEnabled = true
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        voiceToTextManager.release()
    }
} 