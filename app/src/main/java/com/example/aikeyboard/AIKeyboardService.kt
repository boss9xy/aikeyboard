package com.example.aikeyboard

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import com.example.aikeyboard.SmartKeyboardView
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.example.aikeyboard.text.TelexComposer
import com.example.aikeyboard.text.PinyinComposer
import com.example.aikeyboard.text.JapaneseComposer
import com.example.aikeyboard.text.KoreanComposer
import com.example.aikeyboard.text.FrenchComposer
import com.example.aikeyboard.text.GermanComposer
import com.example.aikeyboard.text.SpanishComposer
import com.example.aikeyboard.text.ItalianComposer
import com.example.aikeyboard.text.RussianComposer
import com.example.aikeyboard.text.ArabicComposer
import com.example.aikeyboard.text.ThaiComposer
import com.example.aikeyboard.text.HindiComposer
import com.example.aikeyboard.text.SmartVietnameseProcessor
import com.example.aikeyboard.text.TextProcessor
import com.example.aikeyboard.models.Language
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import java.util.*
import java.util.Locale
import android.content.res.Configuration
import kotlinx.coroutines.flow.collect
import android.media.SoundPool
import android.media.AudioAttributes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aikeyboard.models.Suggestion


class AIKeyboardService : InputMethodService(), TextToSpeech.OnInitListener,
    ClipboardManager.OnPrimaryClipChangedListener, KeyboardView.OnKeyboardActionListener {

    private var keyboard: View? = null
    private var btnPasteAndRead: Button? = null
    private var btnStopTts: Button? = null
    private var btnMic: ImageButton? = null
    private var smartbarTopScrollView: HorizontalScrollView? = null
    private var smartbarBottomScrollView: HorizontalScrollView? = null
    private var smartbarContainer: LinearLayout? = null
    private var btnToggleSmartbar: Button? = null
    private var isSmartbarExpanded = false
    private var translateButton: Button? = null
    private var askButton: Button? = null
    private var gptTranslateButton: Button? = null
    private var gptAskButton: Button? = null
    private var gptContinueButton: Button? = null
    private var gptSuggestButton: Button? = null
    private var deepseekSuggestButton: Button? = null
    private var stopGenerationButton: Button? = null
    private var assistantsGptButton: Button? = null
    private var languageSpinner: Spinner? = null
    private var gptModelSpinner: Spinner? = null
    private var preferences: SharedPreferences? = null

    private var telexComposer = TelexComposer()
    private var pinyinComposer = PinyinComposer()
    private var japaneseComposer = JapaneseComposer()
    private var koreanComposer = KoreanComposer()
    private var frenchComposer = FrenchComposer()
    private var germanComposer = GermanComposer()
    private var spanishComposer = SpanishComposer()
    private var italianComposer = ItalianComposer()
    private var russianComposer = RussianComposer()
    private var arabicComposer = ArabicComposer()
    private var thaiComposer = ThaiComposer()
    private var hindiComposer = HindiComposer()
    private var vietnameseInputBuffer = StringBuilder()
    private var smartVietnameseProcessor: SmartVietnameseProcessor? = null
    private var assistantsAPI: AssistantsAPI? = null
    private var textProcessor: TextProcessor? = null

    private var currentLanguage = "Vietnamese"
    private val supportedLanguages = listOf(
        "Vietnamese", "English", "Chinese", "Japanese", "Korean",
        "French", "German", "Spanish", "Russian", "Italian"
    )

    private val gptModels = listOf(
        "gpt-3.5-turbo",
        "gpt-3.5-turbo-1106",
        "o3-mini-2025-01-31",
        "gpt-4o-2024-11-20",
        "gpt-4o-mini-2024-07-18",
        "o1-2024-12-17",
        "o1-preview-2024-09-12",
        "gpt-4.5-preview-2025-02-27"
    )

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var textToSpeech: TextToSpeech? = null
    private var lastDetectedLanguage = "vi"
    private var isVietnameseMode = true
    private var isSmartProcessorEnabled = true // Bật/tắt SmartVietnameseProcessor
    private val requestMutex = Mutex()

    private var gptAPI: GPTAPI? = null
    private var deepSeekAPI: DeepSeekAPI? = null
    private val clipboardManager: ClipboardManager by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private val handler = Handler(Looper.getMainLooper())

    private var btnTinhToan: Button? = null
    private var calculatorKeyboard: View? = null
    private var calculatorResult: TextView? = null
    private var calculatorExpression = StringBuilder()

    // Âm thanh gõ phím
    private lateinit var soundPool: SoundPool
    private var soundCach = 0
    private var soundShift = 0
    private var soundThuong = 0
    private var soundXoa = 0
    private var isSoundEnabled = true // Đọc từ SharedPreferences
    private var lastCalculationResult: Double? = null
    private var calculatorPopup: PopupWindow? = null

    private val calculatorKeys = arrayOf(
        "7", "8", "9", "÷",
        "4", "5", "6", "×",
        "1", "2", "3", "-",
        "0", ".", "=", "+"
    )

    private lateinit var keyboardView: SmartKeyboardView
    private lateinit var normalKeyboard: Keyboard
    private lateinit var symbolKeyboard1: Keyboard
    private lateinit var symbolKeyboard2: Keyboard
    private var currentKeyboard: Keyboard? = null

    private var shiftMode = 0 // 0: off, 1: on, 2: caps lock
    private var lastShiftClickTime = 0L
    private val DOUBLE_CLICK_THRESHOLD = 300L

    private var speechRecognizer: SpeechRecognizer? = null

    // Dictionary Suggestions
    private var dictionaryManager: SimpleDictionaryManager? = null
    private var suggestionsContainer: View? = null
    private var dictionarySuggestionsContainer: View? = null
    private var singleWordRecyclerView: RecyclerView? = null
    private var phraseRecyclerView: RecyclerView? = null
    private var nextWordRecyclerView: RecyclerView? = null
    private var singleWordAdapter: SuggestionAdapter? = null
    private var phraseAdapter: SuggestionAdapter? = null
    private var nextWordAdapter: SuggestionAdapter? = null
    private var currentInputWord = StringBuilder()
    private var isSuggestionsVisible = false
    private var suggestionsJob: Job? = null
    private var isListening = false
    private val timeoutRunnable = Runnable { stopListening() }
    private var isSpeechRecognitionActive = false
    private var originalInputText = ""
    private var temporarySpeechText = ""
    private var lastRecognizedText: String? = null
    private var lastCursorPosition = 0 // Track cursor position

    private var isFromApp = false

    private var keyPopupWindow: PopupWindow? = null
    private var keyPopupView: View? = null

    private var currentThreadId: String? = null
    private var lastGptFunction: String? = null
    private var lastTranslateLanguage: String? = null
    private var thinkingTextLength = 0 // Track the length of "Thinking..." text

    // Thêm Job để quản lý tất cả các quá trình tạo nội dung
    private var generationJob: Job? = null

    private val CLIPBOARD_HISTORY_KEY = "clipboard_history"

    private var olamaAskButton: Button? = null
    private var btnGptSpellCheck: Button? = null
    private var btnDeepSeekSpellCheck: Button? = null
    private var btnAskDeepSeek: Button? = null
    private var btnOlamaTrans: Button? = null
    private var btnVoiceToText: Button? = null
    private var btnStopVoiceToText: Button? = null
    private var voiceToTextManager: VoiceToTextManager? = null
    private var isVoiceRecording = false
    private var isVoiceProcessing = false
    private lateinit var promptManager: PromptManager
    private lateinit var languageManager: LanguageManager

    // Biến cho bàn phím nổi



    override fun onCreate() {
        super.onCreate()
        try {
        Logger.initialize(this)

        } catch (e: Exception) {
            Log.e("AIKeyboard", "Failed to initialize Logger: ${e.message}")
        }
        try {
        initializeAPIs()
        } catch (e: Exception) {
    
        }
        try {
        preferences = getSharedPreferences("AIKeyboardPrefs", MODE_PRIVATE)
        } catch (e: Exception) {
    
        }
        // Lắng nghe thay đổi SharedPreferences để cập nhật realtime trạng thái âm thanh
        try {
        preferences?.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "sound_enabled") {
                isSoundEnabled = sharedPreferences.getBoolean("sound_enabled", true)
            }
        }
    
        } catch (e: Exception) {
    
        }
        try {
        loadClipboardHistoryFromPrefs() // <-- Load khi khởi động
        } catch (e: Exception) {
    
        }
        try {
        tts = TextToSpeech(this, this)
        textToSpeech = TextToSpeech(this, this)
    
        } catch (e: Exception) {
    
        }

        // clipboardManager.addPrimaryClipChangedListener(this) - sẽ được gọi sau khi clipboardManager được khởi tạo
        try {
        setupCalculatorKeyboard()
        setupSpeechRecognition()
        setupKeyPopup()
        initializeKeyboards()
        initializeVoiceToText()
    
        } catch (e: Exception) {
    
        }
        
        // Initialize PromptManager and LanguageManager
        try {
        promptManager = PromptManager(this)
        languageManager = LanguageManager(this)
    
        } catch (e: Exception) {
    
            // Fallback initialization
            promptManager = PromptManager(this)
            languageManager = LanguageManager(this)
        }
        
        // Initialize TextProcessor - sẽ được khởi tạo khi có InputConnection
        // textProcessor sẽ được khởi tạo trong onStartInputView
        
        // Initialize SmartVietnameseProcessor
        try {
            smartVietnameseProcessor = SmartVietnameseProcessor(this)
    
        } catch (e: Exception) {
    
            smartVietnameseProcessor = null
        }
        
        // Load SmartVietnameseProcessor settings
        isSmartProcessorEnabled = true // Luôn bật SmartVietnameseProcessor
        
        // Refresh UI when language changes
        try {
        refreshUIForLanguage()
        } catch (e: Exception) {
    
        }
        
        // Register broadcast receiver for language changes
        try {
        val filter = IntentFilter("LANGUAGE_CHANGED")
        registerReceiver(languageChangeReceiver, filter)
    
        } catch (e: Exception) {
    
        }

        // Khởi tạo SoundPool và load âm thanh gõ phím
        try {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()
        soundCach = soundPool.load(this, R.raw.phim_cach, 1)
        soundShift = soundPool.load(this, R.raw.phim_shift, 1)
        soundThuong = soundPool.load(this, R.raw.phim_thuong, 1)
        soundXoa = soundPool.load(this, R.raw.phim_xoa, 1)
        isSoundEnabled = preferences?.getBoolean("sound_enabled", true) ?: true
    
        } catch (e: Exception) {
    
        }
    }

    private fun initializeKeyboards() {
        normalKeyboard = Keyboard(this, R.xml.keyboard_normal)
        symbolKeyboard1 = Keyboard(this, R.xml.keyboard_symbols_1)
        symbolKeyboard2 = Keyboard(this, R.xml.keyboard_symbols_2)
        currentKeyboard = normalKeyboard
    }

    private fun setupSpeechRecognition() {
        // Speech recognizer sẽ được khởi tạo khi cần thiết

    }

    private fun initializeSpeechRecognizer() {
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    isListening = true
                    isSpeechRecognitionActive = true
            
                }

                override fun onBeginningOfSpeech() {
            
                }

                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    stopListening()
                }

                override fun onError(error: Int) {
            
                    resetMicState()
                    cleanupSpeechRecognizer()
                    Toast.makeText(this@AIKeyboardService, "Speech recognition error: $error", Toast.LENGTH_SHORT).show()
                }

                override fun onResults(results: Bundle) {
                    val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                
                        currentInputConnection?.finishComposingText()
                    } else {
                        Toast.makeText(this@AIKeyboardService, "No speech recognized.", Toast.LENGTH_SHORT).show()
                    }
                    stopListening()
                    lastRecognizedText = null
                    temporarySpeechText = ""
                    lastCursorPosition = 0
                }

                override fun onPartialResults(partialResults: Bundle) {
                    val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (matches != null && matches.isNotEmpty()) {
                        val currentText = matches[0]
                        if (currentText != lastRecognizedText) {
                            val textBeforeCursorBeforeUpdate = currentInputConnection?.getTextBeforeCursor(1000, 0)?.toString() ?: ""
                            val userEditedText = lastRecognizedText != null && textBeforeCursorBeforeUpdate != (currentInputConnection?.getTextBeforeCursor(1000, 0)?.toString() ?: "")

                            

                            if (lastRecognizedText == null) {
                        
                                currentInputConnection?.commitText(" ", 1)
                                currentInputConnection?.commitText(currentText, 1)
                            } else if (!userEditedText) {
                        
                                if (temporarySpeechText.isNotEmpty()) {
                                    currentInputConnection?.deleteSurroundingText(temporarySpeechText.length, 0)
                                }
                                currentInputConnection?.commitText(currentText, 1)
                            } else {
                        
                                currentInputConnection?.commitText(" $currentText", 1)
                                lastRecognizedText = textBeforeCursorBeforeUpdate + " $currentText"
                                temporarySpeechText = " $currentText"
                            }

                            lastRecognizedText = currentText
                            temporarySpeechText = currentText
                            lastCursorPosition = textBeforeCursorBeforeUpdate.length

                            
                        }
                    }
                    handler.removeCallbacks(timeoutRunnable)
                    handler.postDelayed(timeoutRunnable, 2000)
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
    
        } catch (e: Exception) {
    
            Toast.makeText(this, "Failed to initialize speech recognition", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cleanupSpeechRecognizer() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
    
        } catch (e: Exception) {
    
        }
    }

    private fun ensureSpeechRecognizer() {
        if (speechRecognizer == null) {
    
            cleanupSpeechRecognizer()
            initializeSpeechRecognizer()
        }
    }

    private fun startListeningMic() {
        if (!isListening) {
            ensureSpeechRecognizer()
            lastCursorPosition = currentInputConnection?.getTextBeforeCursor(1000, 0)?.length ?: 0
            clipboardManager.removePrimaryClipChangedListener(this)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            speechRecognizer?.startListening(intent)
        }
    }

    private fun stopListening() {
        if (isListening) {
            isListening = false
            speechRecognizer?.stopListening()
            resetMicState()
    
        }
    }

    private fun resetMicState() {
        isListening = false
        isSpeechRecognitionActive = false
        btnMic?.setImageResource(R.drawable.ic_mic)
        btnMic?.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.key_special_background, theme))
        handler.removeCallbacks(timeoutRunnable)
        clipboardManager.addPrimaryClipChangedListener(this)
        lastCursorPosition = 0

    }

    private fun checkMicrophonePermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestMicrophonePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Gửi intent để mở SettingsActivity để yêu cầu quyền
            val intent = Intent(this, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun onMicButtonClick(view: View) {

        if (!checkMicrophonePermission()) {
            showToast("Microphone permission required")
            requestMicrophonePermission()
            return
        }

        if (!isListening) {
    
            startListeningMic()
            btnMic?.setImageResource(R.drawable.ic_mic_active)
            btnMic?.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.button_running_background, theme))
        } else {
    
            stopListening()
        }
    }
    


    private fun onVoiceToTextButtonClick(view: View) {

        if (!checkMicrophonePermission()) {
            showToast("Microphone permission required")
            requestMicrophonePermission()
            return
        }

        // Kiểm tra xem có đang ghi âm hoặc xử lý không
        if (isVoiceRecording || isVoiceProcessing) {
    
            showToast("Already recording or processing")
            return
        }


        val success = voiceToTextManager?.startRecording()
        if (success == true) {
    
            // Nút Voice→Text chuyển sang màu vàng khi đang ghi âm
            setButtonRunningState(btnVoiceToText, true)
            btnVoiceToText?.text = getString(R.string.recording_text)
        } else {
    
            showToast("Failed to start recording")
            // Reset trạng thái nếu thất bại
            resetVoiceRecordingState()
        }
    }

    private fun onStopVoiceToTextButtonClick(view: View) {

        
        // Kiểm tra xem có đang ghi âm không
        if (!isVoiceRecording) {
    
            showToast("Not recording")
            return
        }
        
        // Kiểm tra xem có đang xử lý không
        if (isVoiceProcessing) {
    
            showToast("Processing in progress")
            return
        }
        
        voiceToTextManager?.stopRecording()
    }

    private fun initializeAPIs() {

        preferences = getSharedPreferences("AIKeyboardPrefs", MODE_PRIVATE)

        val gptApiKey = preferences?.getString("gpt_api_key", "") ?: ""
        val gptModel = preferences?.getString("selected_gpt_model", "gpt-3.5-turbo") ?: "gpt-3.5-turbo"
        val assistantId = preferences?.getString("gpt_assistants_id", "") ?: "" // Lấy từ key đúng

        if (gptApiKey.isNotEmpty()) {
            try {
                gptAPI = GPTAPI(gptApiKey, gptModel)
        
            } catch (e: Exception) {
            }
        } else {
        }

        val deepSeekApiKey = preferences?.getString("deepseek_api_key", "") ?: ""
        if (deepSeekApiKey.isNotEmpty()) {
            try {
                deepSeekAPI = DeepSeekAPI(deepSeekApiKey)
        
            } catch (e: Exception) {
            }
        } else {
    
        }

        if (gptApiKey.isNotEmpty() && assistantId.isNotEmpty()) {
            try {
                assistantsAPI = AssistantsAPI(gptApiKey, assistantId)
            } catch (e: Exception) {
            }
        } else {
        }
    }

    private fun setupSuggestionButtons() {
        gptSuggestButton?.setOnClickListener {
            setButtonRunningState(gptSuggestButton, true)
            val selectedText = getSelectedText()
            val clipboardText = getClipboardText() ?: ""
            val textToProcess = if (selectedText.isNotEmpty()) selectedText else clipboardText

            if (textToProcess.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("AI Keyboard Copy", textToProcess))
                addTextToClipboardHistory(textToProcess)

                val prompt = promptManager.getSuggestPrompt(textToProcess)

                // Lưu vị trí con trỏ ban đầu (cuối văn bản đang có)
                val originalTextBeforeCursor = currentInputConnection?.getTextBeforeCursor(1000, 0) ?: ""
                val originalCursorPosition = originalTextBeforeCursor.length
                
                currentInputConnection?.commitText("\n", 1)
                val thinkingText = promptManager.getThinkingText()
                currentInputConnection?.commitText(thinkingText, 1)
                thinkingTextLength = thinkingText.length
                stopGenerationButton?.visibility = View.VISIBLE

                generationJob?.cancel() // Hủy job cũ nếu có
                generationJob = CoroutineScope(Dispatchers.Main).launch {
                    try {
                        var fullResponse = StringBuilder()
                        lastGptFunction = "suggest"

                        val ic = currentInputConnection
                        gptAPI?.streamChatCompletion(prompt, false, currentInputConnection)?.collect { response ->
                            if (thinkingTextLength > 0) {
                                deleteThinkingText()
                            }
                            currentInputConnection?.commitText(response, 1)
                            fullResponse.append(response)
                        }

                        if (gptAPI?.getLastFinishReason() == "length") {
                            gptContinueButton?.visibility = View.VISIBLE
                        } else {
                            gptContinueButton?.visibility = View.GONE
                        }
                        captureGPTResponse(fullResponse.toString())
                    } catch (e: Exception) {
                
                        if (thinkingTextLength > 0) {
                            deleteThinkingText()
                        }
                        if (e !is CancellationException) {
                            showToast("Error: ${e.message}")
                        }
                    } finally {
                        stopGenerationButton?.visibility = View.GONE
                        setButtonRunningState(gptSuggestButton, false)
                    }
                }
            } else {
                setButtonRunningState(gptSuggestButton, false)
            }
        }

        deepseekSuggestButton?.setOnClickListener {
            setButtonRunningState(deepseekSuggestButton, true)
            val selectedText = getSelectedText()
            val clipboardText = getClipboardText() ?: ""
            val textToProcess = if (selectedText.isNotEmpty()) selectedText else clipboardText

            if (textToProcess.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("AI Keyboard Copy", textToProcess))
                addTextToClipboardHistory(textToProcess)

                val prompt = promptManager.getSuggestPrompt(textToProcess)

                // Lưu vị trí con trỏ ban đầu (cuối văn bản đang có)
                val originalTextBeforeCursor = currentInputConnection?.getTextBeforeCursor(1000, 0) ?: ""
                val originalCursorPosition = originalTextBeforeCursor.length
                
                currentInputConnection?.commitText("\n", 1)
                val thinkingText = promptManager.getThinkingText()
                currentInputConnection?.commitText(thinkingText, 1)
                thinkingTextLength = thinkingText.length
                stopGenerationButton?.visibility = View.VISIBLE

                generationJob?.cancel() // Hủy job cũ nếu có
                generationJob = CoroutineScope(Dispatchers.Main).launch {
                    try {
                        var fullResponse = StringBuilder()

                        deepSeekAPI?.streamTranslate(prompt, "Vietnamese", currentInputConnection, thinkingTextLength)?.collect { response ->
                            if (thinkingTextLength > 0) {
                                deleteThinkingText()
                            }
                            currentInputConnection?.commitText(response, 1)
                            fullResponse.append(response)
                        }

                        captureGPTResponse(fullResponse.toString())
                    } catch (e: Exception) {
                
                        if (thinkingTextLength > 0) {
                            deleteThinkingText()
                        }
                        if (e !is CancellationException) {
                            showToast("Error: ${e.message}")
                        }
                    } finally {
                        stopGenerationButton?.visibility = View.GONE
                        setButtonRunningState(deepseekSuggestButton, false)
                    }
                }
            } else {
                setButtonRunningState(deepseekSuggestButton, false)
            }
        }
    }

    override fun onCreateInputView(): View {
        keyboard = layoutInflater.inflate(R.layout.keyboard_layout, null)
        keyboardView = keyboard?.findViewById(R.id.keyboard) as SmartKeyboardView
        keyboardView.keyboard = currentKeyboard
        keyboardView.setOnKeyboardActionListener(this)
        keyboardView.isPreviewEnabled = false

        initializeViews()
        setupSmartbar()

        gptModelSpinner = keyboard?.findViewById(R.id.gptModelSpinner)
        gptModelSpinner?.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, gptModels)
        gptModelSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedModel = gptModels[position]
                preferences?.edit()?.putString("selected_gpt_model", selectedModel)?.apply()
                val apiKey = preferences?.getString("gpt_api_key", "") ?: ""
                val assistantsId = preferences?.getString("gpt_assistants_id", "") ?: ""
                if (apiKey.isNotEmpty()) {
                    gptAPI = GPTAPI(apiKey, selectedModel)
                    if (assistantsId.isNotEmpty()) {
                        assistantsAPI = AssistantsAPI(apiKey, assistantsId)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        gptContinueButton = keyboard?.findViewById(R.id.gptContinueButton)
        gptContinueButton?.setOnClickListener {
    
            handleGptContinueGenerating()
        }

        return keyboard as View
    }

    private fun initializeViews() {
        translateButton = keyboard?.findViewById(R.id.deepseekTranslateButton)
        askButton = keyboard?.findViewById(R.id.askButton)
        gptTranslateButton = keyboard?.findViewById(R.id.gptTranslateButton)
        gptAskButton = keyboard?.findViewById(R.id.gptAskButton)
        gptContinueButton = keyboard?.findViewById(R.id.gptContinueButton)
        gptSuggestButton = keyboard?.findViewById(R.id.gptSuggestButton)
        deepseekSuggestButton = keyboard?.findViewById(R.id.deepseekSuggestButton)
        stopGenerationButton = keyboard?.findViewById(R.id.stopGenerationButton)
        assistantsGptButton = keyboard?.findViewById(R.id.assistantsGptButton)
        olamaAskButton = keyboard?.findViewById(R.id.olamaAskButton)
        btnGptSpellCheck = keyboard?.findViewById(R.id.btnGptSpellCheck)
        btnDeepSeekSpellCheck = keyboard?.findViewById(R.id.btnDeepSeekSpellCheck)
        btnAskDeepSeek = keyboard?.findViewById(R.id.btnAskDeepSeek)
        btnOlamaTrans = keyboard?.findViewById(R.id.btnOlamaTrans)
        btnVoiceToText = keyboard?.findViewById(R.id.btnVoiceToText)
        btnStopVoiceToText = keyboard?.findViewById(R.id.btnStopVoiceToText)

        // Khởi tạo smartbar container và toggle button
        smartbarContainer = keyboard?.findViewById(R.id.smartbarContainer)
        btnToggleSmartbar = keyboard?.findViewById(R.id.btnToggleSmartbar)
        smartbarTopScrollView = keyboard?.findViewById(R.id.smartbarTopScrollView)
        smartbarBottomScrollView = keyboard?.findViewById(R.id.smartbarBottomScrollView)

        setupSuggestionButtons()
        btnPasteAndRead = keyboard?.findViewById(R.id.btnPasteAndRead)
        btnStopTts = keyboard?.findViewById(R.id.btnStopTts)
        btnMic = keyboard?.findViewById(R.id.btnMic)
        
        // Khởi tạo Dictionary Suggestions
        initializeDictionarySuggestions()
        
        // Thiết lập trạng thái ban đầu - thu gọn smartbar
        setupSmartbarCollapse()
    }

    private fun setupSmartbar() {
        setupLanguageSpinner()
        setupSmartbarButtons()
        setupTTSButtons()
        setupLanguageToggleButton()
        setupClipboardHistorySpinner()
        setupCalculatorButton()
        setupSwitchKeyboardButton()
        setupSmartbarToggle()
    }

    private fun setupSmartbarButtons() {
        translateButton?.setOnClickListener { handleDeepSeekTranslate() }
        askButton?.setOnClickListener { handleDeepSeekAsk() }
        gptTranslateButton?.setOnClickListener { handleGptTranslate() }
        gptAskButton?.setOnClickListener { processGPTAsk() }
        gptContinueButton?.setOnClickListener {
            handleGptContinueGenerating()
        }
        stopGenerationButton?.setOnClickListener {
            handleStopGeneration()
        }
        assistantsGptButton?.setOnClickListener {
            handleAssistantsGpt()
        }
        btnMic?.setOnClickListener {
            onMicButtonClick(it)
        }
        btnVoiceToText?.setOnClickListener {
            onVoiceToTextButtonClick(it)
        }
        btnStopVoiceToText?.setOnClickListener {
            onStopVoiceToTextButtonClick(it)
        }
        olamaAskButton?.setOnClickListener { handleOlamaAsk() }
        btnGptSpellCheck?.setOnClickListener {
            setButtonRunningState(btnGptSpellCheck, true)
            val selectedText = getSelectedText()
            val clipboardText = getClipboardText() ?: ""
            val textToProcess = if (selectedText.isNotEmpty()) selectedText else clipboardText
            if (textToProcess.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("AI Keyboard Copy", textToProcess))
                addTextToClipboardHistory(textToProcess)
                val prompt = promptManager.getSpellCheckPrompt(textToProcess)
                
                // Lưu vị trí con trỏ ban đầu (cuối văn bản đang có)
                val originalTextBeforeCursor = currentInputConnection?.getTextBeforeCursor(1000, 0) ?: ""
                val originalCursorPosition = originalTextBeforeCursor.length
                
                currentInputConnection?.commitText("\n", 1)
                val thinkingText = promptManager.getThinkingText()
                currentInputConnection?.commitText(thinkingText, 1)
                thinkingTextLength = thinkingText.length
                stopGenerationButton?.visibility = View.VISIBLE
                generationJob?.cancel()
                generationJob = CoroutineScope(Dispatchers.Main).launch {
                    try {
                        var fullResponse = StringBuilder()
                        gptAPI?.streamChatCompletion(prompt, false, currentInputConnection)?.collect { response ->
                            if (thinkingTextLength > 0) {
                                deleteThinkingText()
                            }
                            currentInputConnection?.commitText(response, 1)
                            fullResponse.append(response)
                        }
                        currentInputConnection?.commitText("\n", 1)
                        if (gptAPI?.getLastFinishReason() == "length") {
                            gptContinueButton?.visibility = View.VISIBLE
                        } else {
                            gptContinueButton?.visibility = View.GONE
                        }
                        captureGPTResponse(fullResponse.toString())
                    } catch (e: Exception) {
                
                        if (thinkingTextLength > 0) {
                            deleteThinkingText()
                        }
                        if (e !is CancellationException) {
                            showToast("Error: ${e.message}")
                        }
                    } finally {
                        stopGenerationButton?.visibility = View.GONE
                        setButtonRunningState(btnGptSpellCheck, false)
                    }
                }
            } else {
                setButtonRunningState(btnGptSpellCheck, false)
            }
        }
        btnDeepSeekSpellCheck?.setOnClickListener {
            setButtonRunningState(btnDeepSeekSpellCheck, true)
            val selectedText = getSelectedText()
            val clipboardText = getClipboardText() ?: ""
            val textToProcess = if (selectedText.isNotEmpty()) selectedText else clipboardText
            if (textToProcess.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("AI Keyboard Copy", textToProcess))
                addTextToClipboardHistory(textToProcess)
                val prompt = promptManager.getSpellCheckPrompt(textToProcess)
                
                // Lưu vị trí con trỏ ban đầu (cuối văn bản đang có)
                val originalTextBeforeCursor = currentInputConnection?.getTextBeforeCursor(1000, 0) ?: ""
                val originalCursorPosition = originalTextBeforeCursor.length
                
                currentInputConnection?.commitText("\n", 1)
                val thinkingText = promptManager.getThinkingText()
                currentInputConnection?.commitText(thinkingText, 1)
                thinkingTextLength = thinkingText.length
                stopGenerationButton?.visibility = View.VISIBLE
                generationJob?.cancel()
                generationJob = CoroutineScope(Dispatchers.Main).launch {
                    try {
                        var fullResponse = StringBuilder()
                        deepSeekAPI?.streamTranslate(prompt, "Vietnamese", currentInputConnection, thinkingTextLength)?.collect { response ->
                            if (thinkingTextLength > 0) {
                                deleteThinkingText()
                            }
                            currentInputConnection?.commitText(response, 1)
                            fullResponse.append(response)
                        }
                        currentInputConnection?.commitText("\n", 1)
                        captureGPTResponse(fullResponse.toString())
                    } catch (e: Exception) {
                
                        if (thinkingTextLength > 0) {
                            deleteThinkingText()
                        }
                        if (e !is CancellationException) {
                            showToast("Error: ${e.message}")
                        }
                    } finally {
                        stopGenerationButton?.visibility = View.GONE
                        setButtonRunningState(btnDeepSeekSpellCheck, false)
                    }
                }
            } else {
                setButtonRunningState(btnDeepSeekSpellCheck, false)
            }
        }
        btnAskDeepSeek?.setOnClickListener {
            setButtonRunningState(btnAskDeepSeek, true)
            val selectedText = getSelectedText()
            val clipboardText = getClipboardText() ?: ""
            val textToProcess = if (selectedText.isNotEmpty()) selectedText else clipboardText
            if (textToProcess.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("AI Keyboard Copy", textToProcess))
                addTextToClipboardHistory(textToProcess)
                val prompt = textToProcess // Hỏi đáp tự do
                
                // Lưu vị trí con trỏ ban đầu (cuối văn bản đang có)
                val originalTextBeforeCursor = currentInputConnection?.getTextBeforeCursor(1000, 0) ?: ""
                val originalCursorPosition = originalTextBeforeCursor.length
                
                currentInputConnection?.commitText("\n", 1)
                val thinkingText = promptManager.getThinkingText()
                currentInputConnection?.commitText(thinkingText, 1)
                thinkingTextLength = thinkingText.length
                stopGenerationButton?.visibility = View.VISIBLE
                generationJob?.cancel()
                generationJob = CoroutineScope(Dispatchers.Main).launch {
                    try {
                        var fullResponse = StringBuilder()
                        deepSeekAPI?.streamChat(prompt, currentInputConnection!!, thinkingTextLength)?.collect { response ->
                            if (thinkingTextLength > 0) {
                                deleteThinkingText()
                            }
                            currentInputConnection?.commitText(response, 1)
                            fullResponse.append(response)
                        }
                        currentInputConnection?.commitText("\n", 1)
                        captureGPTResponse(fullResponse.toString())
                    } catch (e: Exception) {
                
                        if (thinkingTextLength > 0) {
                            deleteThinkingText()
                        }
                        if (e !is CancellationException) {
                            showToast("Error: ${e.message}")
                        }
                    } finally {
                        stopGenerationButton?.visibility = View.GONE
                        setButtonRunningState(btnAskDeepSeek, false)
                    }
                }
            } else {
                setButtonRunningState(btnAskDeepSeek, false)
            }
        }
        btnOlamaTrans?.setOnClickListener {
            setButtonRunningState(btnOlamaTrans, true)
            val selectedText = getSelectedText()
            val clipboardText = getClipboardText() ?: ""
            val textToProcess = if (selectedText.isNotEmpty()) selectedText else clipboardText
            if (textToProcess.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("AI Keyboard Copy", textToProcess))
                addTextToClipboardHistory(textToProcess)
                val targetLanguage = languageSpinner?.selectedItem?.toString() ?: "English"
                val prompt = promptManager.getTranslatePrompt(textToProcess, targetLanguage)
                
                // Lưu vị trí con trỏ ban đầu (cuối văn bản đang có)
                val originalTextBeforeCursor = currentInputConnection?.getTextBeforeCursor(1000, 0) ?: ""
                val originalCursorPosition = originalTextBeforeCursor.length
                
                currentInputConnection?.commitText("\n", 1)
                val thinkingText = promptManager.getThinkingText()
                currentInputConnection?.commitText(thinkingText, 1)
                thinkingTextLength = thinkingText.length
                stopGenerationButton?.visibility = View.VISIBLE
                generationJob?.cancel()
                generationJob = CoroutineScope(Dispatchers.Main).launch {
                    try {
                        var fullResponse = StringBuilder()
                        val olamaUrl = preferences?.getString("olama_url", "") ?: ""
                        val olamaModel = preferences?.getString("olama_model", "") ?: ""
                        val olama = OlamaServe(olamaUrl, olamaModel)
                        olama.streamAskQuestion(prompt, currentInputConnection!!, thinkingTextLength)?.collect { chunk ->
                            if (thinkingTextLength > 0) {
                                deleteThinkingText()
                            }
                            currentInputConnection?.commitText(chunk, 1)
                            fullResponse.append(chunk)
                        }
                        currentInputConnection?.commitText("\n", 1)
                        captureGPTResponse(fullResponse.toString())
                    } catch (e: Exception) {
                
                        if (thinkingTextLength > 0) {
                            deleteThinkingText()
                        }
                        if (e !is CancellationException) {
                            showToast("Error: ${e.message}")
                        }
                    } finally {
                        stopGenerationButton?.visibility = View.GONE
                        setButtonRunningState(btnOlamaTrans, false)
                    }
                }
            } else {
                setButtonRunningState(btnOlamaTrans, false)
            }
        }
    }

    private fun handleAssistantsGpt() {

        setButtonRunningState(assistantsGptButton, true)
        val prefs = getSharedPreferences("AIKeyboardPrefs", MODE_PRIVATE)
        val gptApiKey = prefs.getString("gpt_api_key", "") ?: ""
        val assistantId = prefs.getString("gpt_assistants_id", "") ?: ""

        if (gptApiKey.isEmpty() || assistantId.isEmpty()) {
    
            showToast("Please set your GPT API key and Assistant ID in settings")
            setButtonRunningState(assistantsGptButton, false)
            return
        }

        // Luôn khởi tạo lại để đảm bảo sử dụng key và ID mới nhất
        try {
            assistantsAPI = AssistantsAPI(gptApiKey, assistantId)
    
        } catch (e: Exception) {
    
            showToast("Error initializing Assistants API: ${e.message}")
            setButtonRunningState(assistantsGptButton, false)
            return
        }

        val clipboardText = getClipboardText()
        if (clipboardText.isNullOrEmpty()) {

            setButtonRunningState(assistantsGptButton, false)
            return
        }

        lastGptFunction = "assistants"
        
        // Lưu vị trí con trỏ ban đầu (cuối văn bản đang có)
        val originalTextBeforeCursor = currentInputConnection?.getTextBeforeCursor(1000, 0) ?: ""
        val originalCursorPosition = originalTextBeforeCursor.length
        
        currentInputConnection?.commitText("\n", 1)
        val thinkingText = promptManager.getThinkingText()
        currentInputConnection?.commitText(thinkingText, 1)
        thinkingTextLength = thinkingText.length
        stopGenerationButton?.visibility = View.VISIBLE

        generationJob?.cancel() // Hủy job cũ nếu có
        generationJob = CoroutineScope(Dispatchers.Main).launch {
            try {
        
                var fullResponse = StringBuilder()

                assistantsAPI?.sendMessage(clipboardText, currentInputConnection!!, thinkingTextLength)?.collect { chunk ->
                    if (thinkingTextLength > 0) {
                        deleteThinkingText()
                    }
                    if (chunk.startsWith("Error: ")) {
                        // Hiển thị lỗi riêng biệt
                        currentInputConnection?.commitText("\n$chunk\n", 1)
                    } else {
                        currentInputConnection?.commitText(chunk, 1)
                        fullResponse.append(chunk)
                    }
                }
                currentInputConnection?.commitText("\n", 1)

                val lastFinishReason = assistantsAPI?.getLastFinishReason()
        
                when (lastFinishReason) {
                    "completed" -> {
                        gptContinueButton?.visibility = View.GONE
                        captureGPTResponse(fullResponse.toString())
                    }
                    "failed", "expired" -> {
                        gptContinueButton?.visibility = View.GONE
                        assistantsAPI?.clearConversation() // Làm mới thread nếu thất bại hoặc hết hạn
                    }
                    else -> {
                        gptContinueButton?.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
        
                if (thinkingTextLength > 0) {
                    deleteThinkingText()
                }
                if (e !is CancellationException) {
                    currentInputConnection?.commitText("\nAssistants error: ${e.message ?: "Unknown error"}\n", 1)
                }
                gptContinueButton?.visibility = View.GONE
            } finally {
                stopGenerationButton?.visibility = View.GONE
                setButtonRunningState(assistantsGptButton, false)
            }
        }
    }

    private fun setupSwitchKeyboardButton() {
        val btnSwitchKeyboard = keyboard?.findViewById<ImageButton>(R.id.btnSwitchKeyboard)
        btnSwitchKeyboard?.setOnClickListener {
    
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }
        

    }

    private fun setupLanguageSpinner() {
        languageSpinner = keyboard?.findViewById(R.id.languageSpinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, supportedLanguages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner?.adapter = adapter

        languageSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentLanguage = supportedLanguages[position]
                (view as? TextView)?.text = currentLanguage
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupTTSButtons() {
        btnPasteAndRead?.setOnClickListener {
    
            pasteAndReadText()
        }
        btnStopTts?.setOnClickListener {
    
            stopTts()
        }
    }

    private fun setupLanguageToggleButton() {
        val languageButtonContainer = keyboard?.findViewById<View>(R.id.languageButtonContainer)
        languageButtonContainer?.setOnClickListener { toggleLanguage() }
    }

    private fun toggleLanguage() {
        val languageButtonContainer = keyboard?.findViewById<View>(R.id.languageButtonContainer)
        val languageCodeText = languageButtonContainer?.findViewById<TextView>(R.id.languageCode)
        val currentLanguage = languageManager.getCurrentLanguage()
        
        // Cycle through languages
        val nextLanguage = when (currentLanguage) {
            Language.VIETNAMESE -> Language.ENGLISH
            Language.ENGLISH -> Language.CHINESE
            Language.CHINESE -> Language.JAPANESE
            Language.JAPANESE -> Language.KOREAN
            Language.KOREAN -> Language.FRENCH
            Language.FRENCH -> Language.GERMAN
            Language.GERMAN -> Language.SPANISH
            Language.SPANISH -> Language.ITALIAN
            Language.ITALIAN -> Language.RUSSIAN
            Language.RUSSIAN -> Language.ARABIC
            Language.ARABIC -> Language.THAI
            Language.THAI -> Language.HINDI
            Language.HINDI -> Language.VIETNAMESE
        }
        
        // Update language
        languageManager.setLanguage(nextLanguage)
        
        // Refresh entire system for new language
        refreshSystemForLanguage(nextLanguage)
        
        // Show toast notification
        showToast("Switched to ${nextLanguage.nativeName}")
        

    }

    private fun refreshSystemForLanguage(language: Language) {

        
        try {
            // 1. Update button text
            val languageButtonContainer = keyboard?.findViewById<View>(R.id.languageButtonContainer)
            val languageCodeText = languageButtonContainer?.findViewById<TextView>(R.id.languageCode)
            languageCodeText?.text = language.displayCode
            
            // 2. Update composer in TextProcessor
            textProcessor?.setLanguage(language)
            
            // 3. Update Vietnamese mode flag
            isVietnameseMode = (language == Language.VIETNAMESE)
            
            // 4. Refresh keyboard layout
            refreshKeyboardForLanguage(language)
            
            // 5. Refresh smartbar language
            refreshSmartbarForLanguage(language)
            
            // 6. Refresh prompt system
            refreshPromptSystemForLanguage(language)
            
            // 7. Refresh suggestions system
            refreshSuggestionsForLanguage(language)
            
            // 8. Clear any composing text
            currentInputConnection?.finishComposingText()
            
    
            
        } catch (e: Exception) {
    
        }
    }

    private fun refreshKeyboardForLanguage(language: Language) {
        try {
            // Force keyboard to redraw
            keyboardView.invalidateAllKeys()
            
            // Update keyboard layout if needed
            when (language) {
                Language.ARABIC -> {
                    // Arabic is right-to-left
                    keyboardView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                }
                Language.HINDI -> {
                    // Hindi might need special layout
                    keyboardView.layoutDirection = View.LAYOUT_DIRECTION_LTR
                }
                else -> {
                    // Default left-to-right
                    keyboardView.layoutDirection = View.LAYOUT_DIRECTION_LTR
                }
            }
            
    
            
        } catch (e: Exception) {
    
        }
    }

    private fun refreshSmartbarForLanguage(language: Language) {
        try {
            // Update all smartbar buttons text with correct names
            
            // Basic buttons
            translateButton?.text = getLocalizedString("translate", language)
            askButton?.text = getLocalizedString("ask", language)
            btnTinhToan?.text = getLocalizedString("calculator", language)
            
            // GPT buttons
            gptTranslateButton?.text = getLocalizedString("gpt_translate", language)
            gptAskButton?.text = getLocalizedString("gpt_ask", language)
            gptSuggestButton?.text = getLocalizedString("gpt_suggest", language)
            gptContinueButton?.text = getLocalizedString("continue", language)
            
            // DeepSeek buttons
            deepseekSuggestButton?.text = getLocalizedString("deepseek_suggest", language)
            btnAskDeepSeek?.text = getLocalizedString("ask_deepseek", language)
            btnDeepSeekSpellCheck?.text = getLocalizedString("spell_check", language)
            
            // Olama buttons
            olamaAskButton?.text = getLocalizedString("olama_ask", language)
            btnOlamaTrans?.text = getLocalizedString("olama_translate", language)
            
            // Voice buttons - Updated with correct names
            btnVoiceToText?.text = getLocalizedString("voice_to_text", language)
            btnStopVoiceToText?.text = getLocalizedString("stop_voice", language)
            
            // Other buttons
            btnPasteAndRead?.text = getLocalizedString("paste_and_read", language)
            btnStopTts?.text = getLocalizedString("stop_tts", language)
            stopGenerationButton?.text = getLocalizedString("stop_generation", language)
            assistantsGptButton?.text = getLocalizedString("assistants_gpt", language)
            btnGptSpellCheck?.text = getLocalizedString("gpt_spell_check", language)
            
            // Toggle smartbar button
            btnToggleSmartbar?.text = getLocalizedString("toggle_smartbar", language)
            
    
            
        } catch (e: Exception) {
    
        }
    }

    private fun refreshPromptSystemForLanguage(language: Language) {
        try {
            // Update prompt manager with new language
            promptManager = PromptManager(this)
            
            // Update language-specific prompts
            when (language) {
                Language.VIETNAMESE -> {
                    // Vietnamese-specific prompts
                }
                Language.CHINESE -> {
                    // Chinese-specific prompts
                }
                Language.JAPANESE -> {
                    // Japanese-specific prompts
                }
                Language.KOREAN -> {
                    // Korean-specific prompts
                }
                else -> {
                    // Default prompts
                }
            }
            
    
            
        } catch (e: Exception) {
    
        }
    }

    private fun refreshSuggestionsForLanguage(language: Language) {
        try {
            // Clear current suggestions
            clearSuggestions()
            
            // Update dictionary manager for new language
            // Note: DictionaryManager doesn't have setLanguage method
            // We'll just clear suggestions for now
    
            
        } catch (e: Exception) {
    
        }
    }

    private fun clearSuggestions() {
        try {
            singleWordAdapter?.clearSuggestions()
            phraseAdapter?.clearSuggestions()
            nextWordAdapter?.clearSuggestions()
    
        } catch (e: Exception) {
    
        }
    }

    private fun getLocalizedString(key: String, language: Language): String {
        return when (language) {
            Language.VIETNAMESE -> when (key) {
                "translate" -> "Dịch"
                "ask" -> "Định dạng văn bản"
                "calculator" -> "Máy tính"
                "gpt_translate" -> "GPT Dịch"
                "gpt_ask" -> "GPT Hỏi"
                "gpt_suggest" -> "GPT Gợi ý"
                "continue" -> "Tiếp tục"
                "deepseek_suggest" -> "DeepSeek Gợi ý"
                "ask_deepseek" -> "Hỏi DeepSeek"
                "spell_check" -> "Kiểm tra chính tả"
                "olama_ask" -> "Olama Hỏi"
                "olama_translate" -> "Olama Dịch"
                "voice_to_text" -> "Giọng nói → Văn bản"
                "stop_voice" -> "Dừng ghi âm → Văn bản"
                "paste_and_read" -> "Dán & Đọc"
                "stop_tts" -> "Dừng TTS"
                "stop_generation" -> "Dừng tạo"
                "assistants_gpt" -> "GPT Trợ lý"
                "gpt_spell_check" -> "GPT Kiểm tra"
                "toggle_smartbar" -> "Ẩn/Hiện"
                else -> key
            }
            Language.CHINESE -> when (key) {
                "translate" -> "翻译"
                "ask" -> "格式化文本"
                "calculator" -> "计算器"
                "gpt_translate" -> "GPT翻译"
                "gpt_ask" -> "GPT询问"
                "gpt_suggest" -> "GPT建议"
                "continue" -> "继续"
                "deepseek_suggest" -> "DeepSeek建议"
                "ask_deepseek" -> "询问DeepSeek"
                "spell_check" -> "拼写检查"
                "olama_ask" -> "Olama询问"
                "olama_translate" -> "Olama翻译"
                "voice_to_text" -> "语音→文字"
                "stop_voice" -> "停止录音→文字"
                "paste_and_read" -> "粘贴并朗读"
                "stop_tts" -> "停止TTS"
                "stop_generation" -> "停止生成"
                "assistants_gpt" -> "GPT助手"
                "gpt_spell_check" -> "GPT拼写检查"
                "toggle_smartbar" -> "隐藏/显示"
                else -> key
            }
            Language.JAPANESE -> when (key) {
                "translate" -> "翻訳"
                "ask" -> "テキストフォーマット"
                "calculator" -> "計算機"
                "gpt_translate" -> "GPT翻訳"
                "gpt_ask" -> "GPT質問"
                "gpt_suggest" -> "GPT提案"
                "continue" -> "続行"
                "deepseek_suggest" -> "DeepSeek提案"
                "ask_deepseek" -> "DeepSeek質問"
                "spell_check" -> "スペルチェック"
                "olama_ask" -> "Olama質問"
                "olama_translate" -> "Olama翻訳"
                "voice_to_text" -> "音声→文字"
                "stop_voice" -> "録音停止→文字"
                "paste_and_read" -> "貼付・読み上げ"
                "stop_tts" -> "TTS停止"
                "stop_generation" -> "生成停止"
                "assistants_gpt" -> "GPTアシスタント"
                "gpt_spell_check" -> "GPTスペルチェック"
                "toggle_smartbar" -> "非表示/表示"
                else -> key
            }
            Language.KOREAN -> when (key) {
                "translate" -> "번역"
                "ask" -> "텍스트 포맷"
                "calculator" -> "계산기"
                "gpt_translate" -> "GPT번역"
                "gpt_ask" -> "GPT질문"
                "gpt_suggest" -> "GPT제안"
                "continue" -> "계속"
                "deepseek_suggest" -> "DeepSeek제안"
                "ask_deepseek" -> "DeepSeek질문"
                "spell_check" -> "맞춤법검사"
                "olama_ask" -> "Olama질문"
                "olama_translate" -> "Olama번역"
                "voice_to_text" -> "음성→텍스트"
                "stop_voice" -> "녹음중지→텍스트"
                "paste_and_read" -> "붙여넣기&읽기"
                "stop_tts" -> "TTS중지"
                "stop_generation" -> "생성중지"
                "assistants_gpt" -> "GPT어시스턴트"
                "gpt_spell_check" -> "GPT맞춤법검사"
                "toggle_smartbar" -> "숨김/표시"
                else -> key
            }
            Language.FRENCH -> when (key) {
                "translate" -> "Traduire"
                "ask" -> "Formater le texte"
                "calculator" -> "Calculatrice"
                "gpt_translate" -> "GPT Traduire"
                "gpt_ask" -> "GPT Demander"
                "gpt_suggest" -> "GPT Suggérer"
                "continue" -> "Continuer"
                "deepseek_suggest" -> "DeepSeek Suggérer"
                "ask_deepseek" -> "Demander DeepSeek"
                "spell_check" -> "Vérification orthographique"
                "olama_ask" -> "Olama Demander"
                "olama_translate" -> "Olama Traduire"
                "voice_to_text" -> "Voix→Texte"
                "stop_voice" -> "Arrêter enregistrement→Texte"
                "paste_and_read" -> "Coller et Lire"
                "stop_tts" -> "Arrêter TTS"
                "stop_generation" -> "Arrêter la génération"
                "assistants_gpt" -> "GPT Assistant"
                "gpt_spell_check" -> "GPT Vérification"
                "toggle_smartbar" -> "Masquer/Afficher"
                else -> key
            }
            Language.GERMAN -> when (key) {
                "translate" -> "Übersetzen"
                "ask" -> "Text formatieren"
                "calculator" -> "Taschenrechner"
                "gpt_translate" -> "GPT Übersetzen"
                "gpt_ask" -> "GPT Fragen"
                "gpt_suggest" -> "GPT Vorschlagen"
                "continue" -> "Fortsetzen"
                "deepseek_suggest" -> "DeepSeek Vorschlagen"
                "ask_deepseek" -> "DeepSeek Fragen"
                "spell_check" -> "Rechtschreibprüfung"
                "olama_ask" -> "Olama Fragen"
                "olama_translate" -> "Olama Übersetzen"
                "voice_to_text" -> "Sprache→Text"
                "stop_voice" -> "Aufnahme stoppen→Text"
                "paste_and_read" -> "Einfügen & Lesen"
                "stop_tts" -> "TTS stoppen"
                "stop_generation" -> "Generierung stoppen"
                "assistants_gpt" -> "GPT Assistent"
                "gpt_spell_check" -> "GPT Rechtschreibprüfung"
                "toggle_smartbar" -> "Ausblenden/Anzeigen"
                else -> key
            }
            Language.SPANISH -> when (key) {
                "translate" -> "Traducir"
                "ask" -> "Formatear texto"
                "calculator" -> "Calculadora"
                "gpt_translate" -> "GPT Traducir"
                "gpt_ask" -> "GPT Preguntar"
                "gpt_suggest" -> "GPT Sugerir"
                "continue" -> "Continuar"
                "deepseek_suggest" -> "DeepSeek Sugerir"
                "ask_deepseek" -> "Preguntar DeepSeek"
                "spell_check" -> "Verificación ortográfica"
                "olama_ask" -> "Olama Preguntar"
                "olama_translate" -> "Olama Traducir"
                "voice_to_text" -> "Voz→Texto"
                "stop_voice" -> "Detener grabación→Texto"
                "paste_and_read" -> "Pegar y Leer"
                "stop_tts" -> "Detener TTS"
                "stop_generation" -> "Detener generación"
                "assistants_gpt" -> "GPT Asistente"
                "gpt_spell_check" -> "GPT Verificación"
                "toggle_smartbar" -> "Ocultar/Mostrar"
                else -> key
            }
            Language.ITALIAN -> when (key) {
                "translate" -> "Tradurre"
                "ask" -> "Formatta testo"
                "calculator" -> "Calcolatrice"
                "gpt_translate" -> "GPT Tradurre"
                "gpt_ask" -> "GPT Chiedere"
                "gpt_suggest" -> "GPT Suggerire"
                "continue" -> "Continuare"
                "deepseek_suggest" -> "DeepSeek Suggerire"
                "ask_deepseek" -> "Chiedere DeepSeek"
                "spell_check" -> "Controllo ortografico"
                "olama_ask" -> "Olama Chiedere"
                "olama_translate" -> "Olama Tradurre"
                "voice_to_text" -> "Voce→Testo"
                "stop_voice" -> "Fermare registrazione→Testo"
                "paste_and_read" -> "Incolla e Leggi"
                "stop_tts" -> "Fermare TTS"
                "stop_generation" -> "Fermare generazione"
                "assistants_gpt" -> "GPT Assistente"
                "gpt_spell_check" -> "GPT Controllo"
                "toggle_smartbar" -> "Nascondi/Mostra"
                else -> key
            }
            Language.RUSSIAN -> when (key) {
                "translate" -> "Перевести"
                "ask" -> "Форматировать текст"
                "calculator" -> "Калькулятор"
                "gpt_translate" -> "GPT Перевести"
                "gpt_ask" -> "GPT Спросить"
                "gpt_suggest" -> "GPT Предложить"
                "continue" -> "Продолжить"
                "deepseek_suggest" -> "DeepSeek Предложить"
                "ask_deepseek" -> "Спросить DeepSeek"
                "spell_check" -> "Проверка орфографии"
                "olama_ask" -> "Olama Спросить"
                "olama_translate" -> "Olama Перевести"
                "voice_to_text" -> "Голос→Текст"
                "stop_voice" -> "Остановить запись→Текст"
                "paste_and_read" -> "Вставить и Читать"
                "stop_tts" -> "Остановить TTS"
                "stop_generation" -> "Остановить генерацию"
                "assistants_gpt" -> "GPT Ассистент"
                "gpt_spell_check" -> "GPT Проверка"
                "toggle_smartbar" -> "Скрыть/Показать"
                else -> key
            }
            Language.ARABIC -> when (key) {
                "translate" -> "ترجمة"
                "ask" -> "تنسيق النص"
                "calculator" -> "حاسبة"
                "gpt_translate" -> "GPT ترجمة"
                "gpt_ask" -> "GPT سؤال"
                "gpt_suggest" -> "GPT اقتراح"
                "continue" -> "متابعة"
                "deepseek_suggest" -> "DeepSeek اقتراح"
                "ask_deepseek" -> "سؤال DeepSeek"
                "spell_check" -> "فحص الإملاء"
                "olama_ask" -> "Olama سؤال"
                "olama_translate" -> "Olama ترجمة"
                "voice_to_text" -> "صوت→نص"
                "stop_voice" -> "إيقاف التسجيل→نص"
                "paste_and_read" -> "لصق وقراءة"
                "stop_tts" -> "إيقاف TTS"
                "stop_generation" -> "إيقاف التوليد"
                "assistants_gpt" -> "GPT مساعد"
                "gpt_spell_check" -> "GPT فحص"
                "toggle_smartbar" -> "إخفاء/إظهار"
                else -> key
            }
            Language.THAI -> when (key) {
                "translate" -> "แปล"
                "ask" -> "จัดรูปแบบข้อความ"
                "calculator" -> "เครื่องคิดเลข"
                "gpt_translate" -> "GPT แปล"
                "gpt_ask" -> "GPT ถาม"
                "gpt_suggest" -> "GPT แนะนำ"
                "continue" -> "ต่อ"
                "deepseek_suggest" -> "DeepSeek แนะนำ"
                "ask_deepseek" -> "ถาม DeepSeek"
                "spell_check" -> "ตรวจสอบการสะกด"
                "olama_ask" -> "Olama ถาม"
                "olama_translate" -> "Olama แปล"
                "voice_to_text" -> "เสียง→ข้อความ"
                "stop_voice" -> "หยุดบันทึก→ข้อความ"
                "paste_and_read" -> "วางและอ่าน"
                "stop_tts" -> "หยุด TTS"
                "stop_generation" -> "หยุดการสร้าง"
                "assistants_gpt" -> "GPT ผู้ช่วย"
                "gpt_spell_check" -> "GPT ตรวจสอบ"
                "toggle_smartbar" -> "ซ่อน/แสดง"
                else -> key
            }
            Language.HINDI -> when (key) {
                "translate" -> "अनुवाद"
                "ask" -> "टेक्स्ट फॉर्मेट"
                "calculator" -> "कैलकुलेटर"
                "gpt_translate" -> "GPT अनुवाद"
                "gpt_ask" -> "GPT पूछें"
                "gpt_suggest" -> "GPT सुझाव"
                "continue" -> "जारी रखें"
                "deepseek_suggest" -> "DeepSeek सुझाव"
                "ask_deepseek" -> "DeepSeek पूछें"
                "spell_check" -> "वर्तनी जांच"
                "olama_ask" -> "Olama पूछें"
                "olama_translate" -> "Olama अनुवाद"
                "voice_to_text" -> "आवाज→टेक्स्ट"
                "stop_voice" -> "रिकॉर्डिंग रोकें→टेक्स्ट"
                "paste_and_read" -> "पेस्ट और पढ़ें"
                "stop_tts" -> "TTS रोकें"
                "stop_generation" -> "जनरेशन रोकें"
                "assistants_gpt" -> "GPT सहायक"
                "gpt_spell_check" -> "GPT जांच"
                "toggle_smartbar" -> "छिपाएं/दिखाएं"
                else -> key
            }
            else -> when (key) {
                "translate" -> "Translate"
                "ask" -> "Format Text"
                "calculator" -> "Calculator"
                "gpt_translate" -> "GPT Translate"
                "gpt_ask" -> "GPT Ask"
                "gpt_suggest" -> "GPT Suggest"
                "continue" -> "Continue"
                "deepseek_suggest" -> "DeepSeek Suggest"
                "ask_deepseek" -> "Ask DeepSeek"
                "spell_check" -> "Spell Check"
                "olama_ask" -> "Olama Ask"
                "olama_translate" -> "Olama Translate"
                "voice_to_text" -> "Voice→Text"
                "stop_voice" -> "Stop Recording→Text"
                "paste_and_read" -> "Paste & Read"
                "stop_tts" -> "Stop TTS"
                "stop_generation" -> "Stop Generation"
                "assistants_gpt" -> "GPT Assistant"
                "gpt_spell_check" -> "GPT Spell Check"
                "toggle_smartbar" -> "Hide/Show"
                else -> key
            }
        }
    }

    private fun setupClipboardHistorySpinner() {
        val clipboardHistorySpinner = keyboard?.findViewById<Spinner>(R.id.clipboardHistorySpinner) ?: return
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, clipboardHistory)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        clipboardHistorySpinner.adapter = adapter

        var isUserInitiatedSelection = false
        clipboardHistorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isUserInitiatedSelection && position >= 0 && position < clipboardHistory.size) {
                    val selectedText = clipboardHistory[position]
                    currentInputConnection?.commitText(selectedText, 1)
                    showToast("Inserted: ${selectedText.take(20)}...")
                }
                isUserInitiatedSelection = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        clipboardHistorySpinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                isUserInitiatedSelection = true
            }
            false
        }

        clipboardHistorySpinner.setOnLongClickListener {
            val textToCopy = getSelectedText().ifBlank { getClipboardText() ?: "" }
            if (textToCopy.isNotBlank()) {
                addTextToClipboardHistory(textToCopy)
            }
            true
        }
    }

    private fun setupCalculatorButton() {
        btnTinhToan = keyboard?.findViewById(R.id.btnTinhToan)
        btnTinhToan?.setOnClickListener {
            if (calculatorPopup?.isShowing == true) {
                calculatorPopup?.dismiss()
            } else {
                calculatorExpression.clear()
                calculatorResult?.text = "0"
                calculatorPopup?.showAtLocation(keyboard, Gravity.BOTTOM, 0, 0)
            }
        }
    }

    private fun setupCalculatorKeyboard() {
        calculatorKeyboard = layoutInflater.inflate(R.layout.calculator_keyboard, null)
        calculatorResult = calculatorKeyboard?.findViewById(R.id.calculatorResult)
        val keyboardContainer = calculatorKeyboard?.findViewById<LinearLayout>(R.id.calculatorKeyboardContainer)

        calculatorPopup = PopupWindow(calculatorKeyboard, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            isOutsideTouchable = true
            isFocusable = true
        }

        val btnQuayLai = calculatorKeyboard?.findViewById<Button>(R.id.btnQuayLai)
        btnQuayLai?.setOnClickListener { calculatorPopup?.dismiss() }

        val btnInVanBan = calculatorKeyboard?.findViewById<Button>(R.id.btnInVanBan)
        btnInVanBan?.setOnClickListener {
            val resultText = calculatorResult?.text.toString()
            currentInputConnection?.commitText(resultText, 1)
            calculatorPopup?.dismiss()
        }

        val rows = 4
        val cols = 4
        for (row in 0 until rows) {
            val rowLayout = LinearLayout(this)
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            for (col in 0 until cols) {
                val index = row * cols + col
                val button = Button(this)
                button.text = calculatorKeys[index]
                button.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                button.setOnClickListener { handleCalculatorButtonClick(calculatorKeys[index]) }
                rowLayout.addView(button)
            }
            keyboardContainer?.addView(rowLayout)
        }

        val clearButton = calculatorKeyboard?.findViewById<Button>(R.id.btnClear)
        clearButton?.setOnClickListener {
            if (calculatorExpression.isNotEmpty()) {
                calculatorExpression.deleteCharAt(calculatorExpression.length - 1)
                calculatorResult?.text = calculatorExpression.toString().ifEmpty { "0" }
            } else {
                calculatorResult?.text = "0"
            }
        }
    }

    private fun handleCalculatorButtonClick(key: String) {
        when (key) {
            "=" -> {
                try {
                    val result = evaluateExpression(calculatorExpression.toString())
                    lastCalculationResult = result
                    calculatorResult?.text = "${calculatorExpression} = ${formatResult(result)}"
                    calculatorExpression.append(" = $result")
                } catch (e: Exception) {
                    calculatorResult?.text = "Error"
                }
            }
            "C" -> {
                if (calculatorExpression.isNotEmpty()) {
                    calculatorExpression.deleteCharAt(calculatorExpression.length - 1)
                    calculatorResult?.text = calculatorExpression.toString().ifEmpty { "0" }
                }
            }
            in listOf("+", "-", "×", "÷") -> {
                if (lastCalculationResult != null) {
                    calculatorExpression.clear()
                    calculatorExpression.append(lastCalculationResult)
                    lastCalculationResult = null
                }
                calculatorExpression.append(when (key) { "×" -> "*"; "÷" -> "/"; else -> key })
                calculatorResult?.text = calculatorExpression.toString()
            }
            else -> {
                if (lastCalculationResult != null) {
                    calculatorExpression.clear()
                    lastCalculationResult = null
                }
                calculatorExpression.append(key)
                calculatorResult?.text = calculatorExpression.toString()
            }
        }
    }

    private fun evaluateExpression(expression: String): Double {
        return try {
            val cleanedExpression = expression.replace("×", "*").replace("÷", "/")
            val result = object : Any() {
                var pos = -1
                var ch = 0

                fun nextChar() {
                    ch = if (++pos < cleanedExpression.length) cleanedExpression[pos].toInt() else -1
                }

                fun eat(charToEat: Char): Boolean {
                    while (ch == ' '.toInt()) nextChar()
                    if (ch == charToEat.toInt()) {
                        nextChar()
                        return true
                    }
                    return false
                }

                fun parse(): Double {
                    nextChar()
                    val x = parseExpression()
                    if (pos < cleanedExpression.length) throw RuntimeException("Unexpected: ${ch.toChar()}")
                    return x
                }

                fun parseExpression(): Double {
                    var x = parseTerm()
                    while (true) {
                        when {
                            eat('+') -> x += parseTerm()
                            eat('-') -> x -= parseTerm()
                            else -> return x
                        }
                    }
                }

                fun parseTerm(): Double {
                    var x = parseFactor()
                    while (true) {
                        when {
                            eat('*') -> x *= parseFactor()
                            eat('/') -> x /= parseFactor()
                            else -> return x
                        }
                    }
                }

                fun parseFactor(): Double {
                    if (eat('+')) return parseFactor()
                    if (eat('-')) return -parseFactor()

                    var x: Double
                    val startPos = pos
                    if (eat('(')) {
                        x = parseExpression()
                        eat(')')
                    } else if ((ch in '0'.toInt()..'9'.toInt()) || ch == '.'.toInt()) {
                        while ((ch in '0'.toInt()..'9'.toInt()) || ch == '.'.toInt()) nextChar()
                        x = cleanedExpression.substring(startPos, pos).toDouble()
                    } else {
                        throw RuntimeException("Unexpected: ${ch.toChar()}")
                    }
                    return x
                }
            }.parse()
            result
        } catch (e: Exception) {
            throw RuntimeException("Invalid expression: ${e.message}")
        }
    }

    private fun formatResult(result: Double): String {
        return if (result % 1.0 == 0.0) result.toLong().toString() else "%.2f".format(result)
    }

    private var isSpeaking = false
    private fun pasteAndReadText() {

        setButtonRunningState(btnPasteAndRead, true)
        if (isSpeaking) {
    
            setButtonRunningState(btnPasteAndRead, false)
            return
        }
        try {
            val clipboardText = getClipboardText()
    
            if (!clipboardText.isNullOrEmpty()) {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        speakText(clipboardText)
                        setButtonRunningState(btnPasteAndRead, false)
                    } catch (e: Exception) {

                        showToast("Error speaking text: ${e.message}")
                        setButtonRunningState(btnPasteAndRead, false)
                    }
                }
            } else {
        
                showToast("No text to read")
                setButtonRunningState(btnPasteAndRead, false)
            }
        } catch (e: Exception) {
    

            setButtonRunningState(btnPasteAndRead, false)
        }
    }

    private val MAX_TTS_LENGTH = 4000
    private suspend fun speakText(text: String) {
        if (!isTtsInitialized) {
    
            showToast("Text-to-Speech not initialized")
            return
        }


        isSpeaking = true
        try {
            val segments = mutableListOf<String>()
            var remainingText = text
            while (remainingText.isNotEmpty()) {
                if (remainingText.length <= MAX_TTS_LENGTH) {
                    segments.add(remainingText)
                    break
                } else {
                    var cutIndex = remainingText.lastIndexOf(' ', MAX_TTS_LENGTH - 1)
                    if (cutIndex == -1) cutIndex = MAX_TTS_LENGTH
                    segments.add(remainingText.substring(0, cutIndex))
                    remainingText = remainingText.substring(cutIndex).trim()
                }
            }

    
            for (segment in segments) {
                if (segment.isNotEmpty()) {
            
                    val speakJob = CoroutineScope(Dispatchers.IO).async {
                        val detectedLanguage = withContext(Dispatchers.IO) {
                            detectLanguage(segment)
                        }
                
                        val locale = withContext(Dispatchers.IO) {
                            getLocaleForLanguage(detectedLanguage)
                        }
                        val speakCompleted = CompletableDeferred<Unit>()

                        withContext(Dispatchers.Main) {
                            tts?.language = locale
                            tts?.setOnUtteranceCompletedListener {
                        
                                speakCompleted.complete(Unit)
                            }
                            tts?.speak(
                                segment,
                                TextToSpeech.QUEUE_FLUSH,
                                Bundle().apply {
                                    putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "segment")
                                },
                                "segment"
                            )
                        }
                        speakCompleted.await()
                    }
                    speakJob.await()
                }
            }
    
        } catch (e: Exception) {
    
            showToast("Error speaking text")
        } finally {
            isSpeaking = false
        }
    }

    private fun stopTts() {

        try {
            textToSpeech?.stop()
            tts?.stop()
            isSpeaking = false
            tts?.shutdown()
            textToSpeech?.shutdown()
            tts = TextToSpeech(this, this)
            textToSpeech = TextToSpeech(this, this)
    
            // Reset button state to normal (green) when stopping TTS
            setButtonRunningState(btnPasteAndRead, false)
        } catch (e: Exception) {
    
            // Also reset button state in case of error
            setButtonRunningState(btnPasteAndRead, false)
        }
    }

    private fun handleGptTranslate() {

        setButtonRunningState(gptTranslateButton, true)
        val prefs = getSharedPreferences("AIKeyboardPrefs", MODE_PRIVATE)
        val gptApiKey = prefs.getString("gpt_api_key", "") ?: ""

        if (gptApiKey.isEmpty()) {
    
            showToast("Please set your GPT API key in settings")
            setButtonRunningState(gptTranslateButton, false)
            return
        }

        if (gptAPI == null) {
            try {
                gptAPI = GPTAPI(gptApiKey)
            } catch (e: Exception) {
        
                showToast("Error initializing GPT API")
                setButtonRunningState(gptTranslateButton, false)
                return
            }
        }

        val clipboardText = getClipboardText()
        if (clipboardText.isNullOrEmpty()) {
            setButtonRunningState(gptTranslateButton, false)
            return
        }

        val targetLanguage = languageSpinner?.selectedItem?.toString() ?: "English"
        lastTranslateLanguage = targetLanguage
        lastGptFunction = "translate"

        // Lưu vị trí con trỏ ban đầu (cuối văn bản đang có)
        val originalTextBeforeCursor = currentInputConnection?.getTextBeforeCursor(1000, 0) ?: ""
        val originalCursorPosition = originalTextBeforeCursor.length
        
        currentInputConnection?.commitText("\n", 1)
        val thinkingText = promptManager.getThinkingText()
        currentInputConnection?.commitText(thinkingText, 1)
        thinkingTextLength = thinkingText.length
        stopGenerationButton?.visibility = View.VISIBLE

        generationJob?.cancel() // Hủy job cũ nếu có
        generationJob = CoroutineScope(Dispatchers.Main).launch {
            try {
        
                deleteThinkingText()
                var fullResponse = StringBuilder()

                val ic = currentInputConnection
                gptAPI?.streamTranslate(clipboardText, targetLanguage, currentInputConnection)?.collect { chunk ->
                    ic?.commitText(chunk, 1)
                    fullResponse.append(chunk)
                }
                ic?.commitText("\n", 1)

                if (gptAPI?.getLastFinishReason() == "length") {
                    gptContinueButton?.visibility = View.VISIBLE
                } else {
                    gptContinueButton?.visibility = View.GONE
                }
                captureGPTResponse(fullResponse.toString())
            } catch (e: Exception) {
        
                deleteThinkingText()
                if (e !is CancellationException) {
                    currentInputConnection?.commitText("\nTranslation error: ${e.message}\n", 1)
                }
                gptContinueButton?.visibility = View.GONE
            } finally {
                stopGenerationButton?.visibility = View.GONE
                setButtonRunningState(gptTranslateButton, false)
            }
        }
    }

    private fun processGPTAsk() {

        setButtonRunningState(gptAskButton, true)
        val clipboardText = getClipboardText()
        if (clipboardText.isNullOrEmpty()) {
            setButtonRunningState(gptAskButton, false)
            return
        }

        val gptApiKey = preferences?.getString("gpt_api_key", "") ?: ""


        if (gptApiKey.isEmpty()) {
            showToast("Please set your GPT API key in settings")
            setButtonRunningState(gptAskButton, false)
            return
        }

        if (gptAPI == null) {
            try {
                val model = preferences?.getString("selected_gpt_model", "gpt-3.5-turbo") ?: "gpt-3.5-turbo"
                gptAPI = GPTAPI(gptApiKey, model)
        
            } catch (e: Exception) {
        
                showToast("Error initializing GPT API")
                setButtonRunningState(gptAskButton, false)
                return
            }
        }

        // Lưu vị trí con trỏ ban đầu (cuối văn bản đang có)
        val originalTextBeforeCursor = currentInputConnection?.getTextBeforeCursor(1000, 0) ?: ""
        val originalCursorPosition = originalTextBeforeCursor.length
        
        currentInputConnection?.commitText("\n", 1)
        val thinkingText = promptManager.getThinkingText()
        currentInputConnection?.commitText(thinkingText, 1)
        thinkingTextLength = thinkingText.length
        stopGenerationButton?.visibility = View.VISIBLE

        generationJob?.cancel() // Hủy job cũ nếu có
        generationJob = CoroutineScope(Dispatchers.Main).launch {
            try {
        
                deleteThinkingText()
                var fullResponse = StringBuilder()
                lastGptFunction = "ask"

                val ic = currentInputConnection
                gptAPI?.streamAskQuestion(clipboardText, currentInputConnection)?.collect { chunk ->
                    ic?.commitText(chunk, 1)
                    fullResponse.append(chunk)
                }
                ic?.commitText("\n", 1)

                if (gptAPI?.getLastFinishReason() == "length") {
            
                    gptContinueButton?.visibility = View.VISIBLE
                } else {
            
                    gptContinueButton?.visibility = View.GONE
                }
                captureGPTResponse(fullResponse.toString())
            } catch (e: Exception) {
        
                deleteThinkingText()
                if (e !is CancellationException) {
                    currentInputConnection?.commitText("\nError: ${e.message}\n", 1)
                }
                gptContinueButton?.visibility = View.GONE
            } finally {
                stopGenerationButton?.visibility = View.GONE
                setButtonRunningState(gptAskButton, false)
            }
        }
    }

    private fun handleStopGeneration() {
        generationJob?.cancel() // Hủy tất cả các quá trình tạo nội dung
        gptAPI?.clearConversation()
        deepSeekAPI?.clearConversation()
        assistantsAPI?.clearConversation()
        deleteThinkingText()
        currentInputConnection?.commitText("\nGeneration stopped.", 1)
        stopGenerationButton?.visibility = View.GONE
        gptContinueButton?.visibility = View.GONE
        thinkingTextLength = 0 // Reset độ dài văn bản "Thinking..."
    }

    private fun handleGptContinueGenerating() {
        currentInputConnection?.commitText("\n", 1)
        val thinkingText = promptManager.getThinkingText()
        currentInputConnection?.commitText(thinkingText, 1)
        thinkingTextLength = thinkingText.length
        stopGenerationButton?.visibility = View.VISIBLE

        generationJob?.cancel() // Hủy job cũ nếu có
        generationJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                var fullResponse = StringBuilder()
                gptAPI?.streamContinueGeneration(currentInputConnection)?.collect { response ->
                    currentInputConnection?.commitText(response, 1)
                    fullResponse.append(response)
                }
                currentInputConnection?.commitText("\n", 1)
                captureGPTResponse(fullResponse.toString())
            } catch (e: Exception) {
        
                deleteThinkingText()
                if (e !is CancellationException) {
                    currentInputConnection?.commitText("\nError continuing generation: ${e.message}\n", 1)
                }
                gptContinueButton?.visibility = View.GONE
            } finally {
                stopGenerationButton?.visibility = View.GONE
            }
        }
    }

    private fun captureGPTResponse(response: String) {
        if (response.isNotBlank()) {
            isFromApp = true
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("GPT Response", response))
            addTextToClipboardHistory(response)
        }
    }

    fun getSelectedText(): String {
        val ic = currentInputConnection ?: return ""
        return ic.getSelectedText(0)?.toString() ?: ""
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Log.e("AIKeyboard", "Error: $message")
        currentInputConnection?.commitText("\nError: $message\n", 1)
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, "AIKeyboard Error: $message", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        if (restarting) {
            currentThreadId = null
        }
        
        // Initialize clipboard listener
        try {
            clipboardManager.addPrimaryClipChangedListener(this)
    
        } catch (e: Exception) {
    
        }
        
        // Initialize TextProcessor when InputConnection is available
        currentInputConnection?.let { ic ->
            textProcessor = TextProcessor(ic)
            textProcessor?.setLanguage(languageManager.getCurrentLanguage())
    
        }
        
        // Refresh entire system for current language on startup
        val currentLanguage = languageManager.getCurrentLanguage()

        refreshSystemForLanguage(currentLanguage)
        
        // Refresh UI when keyboard is shown
        refreshUIForLanguage()
    }



    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
    
        } else {
            Log.e("AIKeyboard", "TTS initialization failed with status: $status")
        }
    }

    private val clipboardHistory = mutableListOf<String>()

    private fun saveClipboardHistoryToPrefs() {
        preferences?.edit()?.putStringSet(CLIPBOARD_HISTORY_KEY, clipboardHistory.toSet())?.apply()
    }

    private fun loadClipboardHistoryFromPrefs() {
        val saved = preferences?.getStringSet(CLIPBOARD_HISTORY_KEY, null)
        clipboardHistory.clear()
        if (saved != null) {
            clipboardHistory.addAll(saved)
        }
    }

    private fun clearClipboardHistory() {
        clipboardHistory.clear()
        saveClipboardHistoryToPrefs() // <-- Lưu lại mỗi khi xoá
        (keyboard?.findViewById<Spinner>(R.id.clipboardHistorySpinner)?.adapter as? ArrayAdapter<*>)?.notifyDataSetChanged()
    }

    override fun onPrimaryClipChanged() {
        if (isFromApp) {
            isFromApp = false
            return
        }

        val clipText = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()?.trim()
        if (clipText != null) {
            addTextToClipboardHistory(clipText, false)
        }
    }

    // Thêm cấu trúc để theo dõi clipboard thông minh
    private data class SmartClipboardItem(
        val text: String,
        val type: ClipboardType,
        val frequency: Int = 1,
        val lastUsed: Long = System.currentTimeMillis(),
        val context: String = "" // Context khi được copy
    )
    
    private enum class ClipboardType {
        TEXT, EMAIL, URL, PHONE, NUMBER, WORD, PHRASE, UNKNOWN
    }
    
    private val smartClipboardHistory = mutableListOf<SmartClipboardItem>()
    
    private fun classifyClipboardText(text: String): ClipboardType {
        return when {
            text.contains("@") && text.contains(".") -> ClipboardType.EMAIL
            text.startsWith("http") || text.startsWith("www") -> ClipboardType.URL
            text.matches(Regex("\\d+")) -> ClipboardType.NUMBER
            text.matches(Regex("\\d{10,11}")) -> ClipboardType.PHONE
            text.contains(" ") -> ClipboardType.PHRASE
            text.length <= 20 -> ClipboardType.WORD
            else -> ClipboardType.TEXT
        }
    }
    
    private fun getCurrentContext(): String {
        return try {
            val textBeforeCursor = currentInputConnection?.getTextBeforeCursor(100, 0) ?: ""
            val words = textBeforeCursor.split(" ").filter { it.isNotEmpty() }
            words.takeLast(3).joinToString(" ") // Lấy 3 từ cuối làm context
        } catch (e: Exception) {
            ""
        }
    }
    
    private fun addTextToClipboardHistory(text: String, showToast: Boolean = true) {
        val trimmedText = text.trim()


        
        if (trimmedText.isBlank()) {
    
            return
        }
        
        // Phân loại text
        val type = classifyClipboardText(trimmedText)
        val context = getCurrentContext()

        
        // Kiểm tra xem đã tồn tại chưa
        val existingIndex = smartClipboardHistory.indexOfFirst { it.text == trimmedText }
        if (existingIndex != -1) {
            // Cập nhật frequency và lastUsed
            val existing = smartClipboardHistory[existingIndex]
            smartClipboardHistory[existingIndex] = existing.copy(
                frequency = existing.frequency + 1,
                lastUsed = System.currentTimeMillis()
            )
    
        } else {
            // Thêm mới
            smartClipboardHistory.add(SmartClipboardItem(trimmedText, type, 1, System.currentTimeMillis(), context))
    
        }
        
        // Sắp xếp theo frequency và lastUsed
        smartClipboardHistory.sortByDescending { it.frequency }
        smartClipboardHistory.sortByDescending { it.lastUsed }
        
        // Cập nhật clipboardHistory cũ để tương thích
        clipboardHistory.clear()
        clipboardHistory.addAll(smartClipboardHistory.map { it.text })
        
        saveClipboardHistoryToPrefs()
        (keyboard?.findViewById<Spinner>(R.id.clipboardHistorySpinner)?.adapter as? ArrayAdapter<*>)?.notifyDataSetChanged()


    }

    fun getClipboardText(): String? {
        return clipboardManager.primaryClip?.let { clip ->
            if (clip.itemCount > 0) clip.getItemAt(0).text?.toString()?.trim() else null
        }
    }

    override fun onPress(primaryCode: Int) {
        keyboardView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        showKeyPreview(primaryCode)
    }

    override fun onRelease(primaryCode: Int) {
        hideKeyPreview()
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        // Phát âm thanh tuỳ loại phím nếu bật
        if (isSoundEnabled) {
            when (primaryCode) {
                32 -> soundPool.play(soundCach, 1f, 1f, 0, 0, 1f) // Space
                -1 -> soundPool.play(soundShift, 1f, 1f, 0, 0, 1f) // Shift
                -5 -> soundPool.play(soundXoa, 1f, 1f, 0, 0, 1f)   // Xoá
                else -> soundPool.play(soundThuong, 1f, 1f, 0, 0, 1f) // Phím thường
            }
        }
        when (primaryCode) {
            -2 -> {
                when (currentKeyboard) {
                    normalKeyboard -> {
                        currentKeyboard = symbolKeyboard1
                        keyboardView.keyboard = symbolKeyboard1
                    }
                    symbolKeyboard1, symbolKeyboard2 -> {
                        currentKeyboard = normalKeyboard
                        keyboardView.keyboard = normalKeyboard
                    }
                }
            }
            -3 -> {
                when (currentKeyboard) {
                    symbolKeyboard1 -> {
                        currentKeyboard = symbolKeyboard2
                        keyboardView.keyboard = symbolKeyboard2
                    }
                    symbolKeyboard2 -> {
                        currentKeyboard = symbolKeyboard1
                        keyboardView.keyboard = symbolKeyboard1
                    }
                }
            }
            -1 -> {
                val now = System.currentTimeMillis()
                if (now - lastShiftClickTime < DOUBLE_CLICK_THRESHOLD) {
                    shiftMode = if (shiftMode == 2) 0 else 2
                } else {
                    shiftMode = if (shiftMode == 0) 1 else 0
                }
                lastShiftClickTime = now
                updateShiftState()
            }
            else -> {
                val ic = currentInputConnection
                if (ic != null) {
                    when (primaryCode) {
                        -5 -> {
                            val selectedText = ic.getSelectedText(0)
                            if (selectedText.isNullOrEmpty()) {
                                ic.deleteSurroundingText(1, 0)
                                // Cập nhật suggestions sau khi xóa
                                updateSuggestionsAfterInput()
                            } else {
                                ic.commitText("", 1)
                            }
                        }
                        -4 -> {
                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                            
                            // Học từ text đã nhập khi nhấn Enter
                            val textBeforeCursor = ic.getTextBeforeCursor(200, 0)?.toString() ?: ""
                            if (textBeforeCursor.isNotEmpty()) {
                                dictionaryManager?.learnFromText(textBeforeCursor)
                        
                            }
                            
                            // Không ẩn suggestions khi nhấn Enter
                        }
                        else -> {
                            val currentLang = languageManager.getCurrentLanguage()
                            when (currentLang) {
                                Language.VIETNAMESE -> processVietnameseInput(primaryCode.toChar())
                                Language.CHINESE -> processChineseInput(primaryCode.toChar())
                                Language.JAPANESE -> processJapaneseInput(primaryCode.toChar())
                                Language.KOREAN -> processKoreanInput(primaryCode.toChar())
                                Language.FRENCH -> processFrenchInput(primaryCode.toChar())
                                Language.GERMAN -> processGermanInput(primaryCode.toChar())
                                Language.SPANISH -> processSpanishInput(primaryCode.toChar())
                                Language.ITALIAN -> processItalianInput(primaryCode.toChar())
                                Language.RUSSIAN -> processRussianInput(primaryCode.toChar())
                                Language.ARABIC -> processArabicInput(primaryCode.toChar())
                                Language.THAI -> processThaiInput(primaryCode.toChar())
                                Language.HINDI -> processHindiInput(primaryCode.toChar())
                                else -> {
                                    // Default English processing
                                val code = primaryCode.toChar()
                                val text = when {
                                    shiftMode == 2 -> code.uppercase()
                                    shiftMode == 1 -> {
                                        shiftMode = 0
                                        updateShiftState()
                                        code.uppercase()
                                    }
                                    else -> code.toString()
                                }
                                    
                                    currentInputConnection?.commitText(text, 1)
                                
                                // Không ẩn suggestions khi nhấn space
                                if (code == ' ') {
                                    // Cập nhật suggestions thay vì ẩn
                                    updateSuggestionsAfterInput()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onText(text: CharSequence?) {
        val ic = currentInputConnection
        if (ic != null && text != null) {
            ic.commitText(text, 1)
            
            // Cập nhật suggestions ngay lập tức
            updateSuggestionsAfterInput()
        }
    }

    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {}
    override fun swipeUp() {}

    fun updateShiftState() {
        keyboardView.isShifted = shiftMode > 0
        keyboardView.invalidateAllKeys()
    }

    private fun processVietnameseInput(char: Char) {
        val precedingText = currentInputConnection?.getTextBeforeCursor(10, 0)?.toString() ?: ""
        val (deleteCount, newText) = telexComposer.getActions(precedingText, char.lowercaseChar().toString())
        if (deleteCount > 0) currentInputConnection?.deleteSurroundingText(deleteCount, 0)

        val finalText = when {
            shiftMode == 2 -> newText.uppercase()
            shiftMode == 1 -> newText.replaceFirstChar { it.uppercase() }
            else -> newText
        }

        // THÊM: VietnameseTextEnhancer enhancement
        val enhancedText = enhanceVietnameseWord(finalText)

        currentInputConnection?.commitText(enhancedText, 1)

        if (shiftMode == 1) {
            shiftMode = 0
            updateShiftState()
        }
        
        // Cập nhật suggestions ngay lập tức
        updateSuggestionsAfterInput()
    }

    private fun processChineseInput(char: Char) {
        val precedingText = currentInputConnection?.getTextBeforeCursor(10, 0)?.toString() ?: ""
        val (deleteCount, newText) = pinyinComposer.getActions(precedingText, char.lowercaseChar().toString())
        if (deleteCount > 0) currentInputConnection?.deleteSurroundingText(deleteCount, 0)

        val finalText = when {
            shiftMode == 2 -> newText.uppercase()
            shiftMode == 1 -> newText.replaceFirstChar { it.uppercase() }
            else -> newText
        }

        currentInputConnection?.commitText(finalText, 1)

        if (shiftMode == 1) {
            shiftMode = 0
            updateShiftState()
        }
        
        updateSuggestionsAfterInput()
    }

    private fun processJapaneseInput(char: Char) {
        val precedingText = currentInputConnection?.getTextBeforeCursor(10, 0)?.toString() ?: ""
        val (deleteCount, newText) = japaneseComposer.getActions(precedingText, char.lowercaseChar().toString())
        if (deleteCount > 0) currentInputConnection?.deleteSurroundingText(deleteCount, 0)

        val finalText = when {
            shiftMode == 2 -> newText.uppercase()
            shiftMode == 1 -> newText.replaceFirstChar { it.uppercase() }
            else -> newText
        }

        currentInputConnection?.commitText(finalText, 1)

        if (shiftMode == 1) {
            shiftMode = 0
            updateShiftState()
        }
        
        updateSuggestionsAfterInput()
    }

    private fun processKoreanInput(char: Char) {
        val precedingText = currentInputConnection?.getTextBeforeCursor(10, 0)?.toString() ?: ""
        val (deleteCount, newText) = koreanComposer.getActions(precedingText, char.lowercaseChar().toString())
        if (deleteCount > 0) currentInputConnection?.deleteSurroundingText(deleteCount, 0)

        val finalText = when {
            shiftMode == 2 -> newText.uppercase()
            shiftMode == 1 -> newText.replaceFirstChar { it.uppercase() }
            else -> newText
        }

        currentInputConnection?.commitText(finalText, 1)

        if (shiftMode == 1) {
            shiftMode = 0
            updateShiftState()
        }
        
        updateSuggestionsAfterInput()
    }

    private fun processFrenchInput(char: Char) {
        val precedingText = currentInputConnection?.getTextBeforeCursor(10, 0)?.toString() ?: ""
        val (deleteCount, newText) = frenchComposer.getActions(precedingText, char.lowercaseChar().toString())
        if (deleteCount > 0) currentInputConnection?.deleteSurroundingText(deleteCount, 0)

        val finalText = when {
            shiftMode == 2 -> newText.uppercase()
            shiftMode == 1 -> newText.replaceFirstChar { it.uppercase() }
            else -> newText
        }

        currentInputConnection?.commitText(finalText, 1)

        if (shiftMode == 1) {
            shiftMode = 0
            updateShiftState()
        }
        
        updateSuggestionsAfterInput()
    }

    private fun processGermanInput(char: Char) {
        val precedingText = currentInputConnection?.getTextBeforeCursor(10, 0)?.toString() ?: ""
        val (deleteCount, newText) = germanComposer.getActions(precedingText, char.lowercaseChar().toString())
        if (deleteCount > 0) currentInputConnection?.deleteSurroundingText(deleteCount, 0)

        val finalText = when {
            shiftMode == 2 -> newText.uppercase()
            shiftMode == 1 -> newText.replaceFirstChar { it.uppercase() }
            else -> newText
        }

        currentInputConnection?.commitText(finalText, 1)

        if (shiftMode == 1) {
            shiftMode = 0
            updateShiftState()
        }
        
        updateSuggestionsAfterInput()
    }

    private fun processSpanishInput(char: Char) {
        val precedingText = currentInputConnection?.getTextBeforeCursor(10, 0)?.toString() ?: ""
        val (deleteCount, newText) = spanishComposer.getActions(precedingText, char.lowercaseChar().toString())
        if (deleteCount > 0) currentInputConnection?.deleteSurroundingText(deleteCount, 0)

        val finalText = when {
            shiftMode == 2 -> newText.uppercase()
            shiftMode == 1 -> newText.replaceFirstChar { it.uppercase() }
            else -> newText
        }

        currentInputConnection?.commitText(finalText, 1)

        if (shiftMode == 1) {
            shiftMode = 0
            updateShiftState()
        }
        
        updateSuggestionsAfterInput()
    }

    private fun processItalianInput(char: Char) {
        val precedingText = currentInputConnection?.getTextBeforeCursor(10, 0)?.toString() ?: ""
        val (deleteCount, newText) = italianComposer.getActions(precedingText, char.lowercaseChar().toString())
        if (deleteCount > 0) currentInputConnection?.deleteSurroundingText(deleteCount, 0)

        val finalText = when {
            shiftMode == 2 -> newText.uppercase()
            shiftMode == 1 -> newText.replaceFirstChar { it.uppercase() }
            else -> newText
        }

        currentInputConnection?.commitText(finalText, 1)

        if (shiftMode == 1) {
            shiftMode = 0
            updateShiftState()
        }
        
        updateSuggestionsAfterInput()
    }

    private fun processRussianInput(char: Char) {
        val precedingText = currentInputConnection?.getTextBeforeCursor(10, 0)?.toString() ?: ""
        val (deleteCount, newText) = russianComposer.getActions(precedingText, char.lowercaseChar().toString())
        if (deleteCount > 0) currentInputConnection?.deleteSurroundingText(deleteCount, 0)

        val finalText = when {
            shiftMode == 2 -> newText.uppercase()
            shiftMode == 1 -> newText.replaceFirstChar { it.uppercase() }
            else -> newText
        }

        currentInputConnection?.commitText(finalText, 1)

        if (shiftMode == 1) {
            shiftMode = 0
            updateShiftState()
        }
        
        updateSuggestionsAfterInput()
    }

    private fun processArabicInput(char: Char) {
        val precedingText = currentInputConnection?.getTextBeforeCursor(10, 0)?.toString() ?: ""
        val (deleteCount, newText) = arabicComposer.getActions(precedingText, char.lowercaseChar().toString())
        if (deleteCount > 0) currentInputConnection?.deleteSurroundingText(deleteCount, 0)

        val finalText = when {
            shiftMode == 2 -> newText.uppercase()
            shiftMode == 1 -> newText.replaceFirstChar { it.uppercase() }
            else -> newText
        }

        currentInputConnection?.commitText(finalText, 1)

        if (shiftMode == 1) {
            shiftMode = 0
            updateShiftState()
        }
        
        updateSuggestionsAfterInput()
    }

    private fun processThaiInput(char: Char) {
        val precedingText = currentInputConnection?.getTextBeforeCursor(10, 0)?.toString() ?: ""
        val (deleteCount, newText) = thaiComposer.getActions(precedingText, char.lowercaseChar().toString())
        if (deleteCount > 0) currentInputConnection?.deleteSurroundingText(deleteCount, 0)

        val finalText = when {
            shiftMode == 2 -> newText.uppercase()
            shiftMode == 1 -> newText.replaceFirstChar { it.uppercase() }
            else -> newText
        }

        currentInputConnection?.commitText(finalText, 1)

        if (shiftMode == 1) {
            shiftMode = 0
            updateShiftState()
        }
        
        updateSuggestionsAfterInput()
    }

    private fun processHindiInput(char: Char) {
        val precedingText = currentInputConnection?.getTextBeforeCursor(10, 0)?.toString() ?: ""
        val (deleteCount, newText) = hindiComposer.getActions(precedingText, char.lowercaseChar().toString())
        if (deleteCount > 0) currentInputConnection?.deleteSurroundingText(deleteCount, 0)

        val finalText = when {
            shiftMode == 2 -> newText.uppercase()
            shiftMode == 1 -> newText.replaceFirstChar { it.uppercase() }
            else -> newText
        }

        currentInputConnection?.commitText(finalText, 1)

        if (shiftMode == 1) {
            shiftMode = 0
            updateShiftState()
        }
        
        updateSuggestionsAfterInput()
    }

    suspend fun detectLanguage(text: String): String = withContext(Dispatchers.IO) {
        if (text.isEmpty()) return@withContext lastDetectedLanguage

        var hasVietnamese = false
        var hasChinese = false
        var hasJapanese = false
        var hasKorean = false
        var hasLatin = false
        var hasMalay = false
        var hasThai = false
        var hasHindi = false
        var hasId = false
        var hasArabic = false
        var hasRussian = false
        var hasItalian = false
        var hasGerman = false
        var hasPortuguese = false
        var hasFrench = false
        var hasSpanish = false

        for (char in text) {
            when {
                char in '\u0300'..'\u036F' || char in '\u1EA0'..'\u1EF9' -> hasVietnamese = true
                char in '\u4E00'..'\u9FFF' -> hasChinese = true
                char in '\u3040'..'\u309F' || char in '\u30A0'..'\u30FF' -> hasJapanese = true
                char in '\uAC00'..'\uD7AF' -> hasKorean = true
                char in 'A'..'Z' || char in 'a'..'z' -> hasLatin = true
                char in '\u0600'..'\u06FF' -> hasArabic = true
                char in '\u0900'..'\u097F' -> hasHindi = true
                char in '\u0E00'..'\u0E7F' -> hasThai = true
                char in '\u0100'..'\u017F' -> hasId = true
                char in '\u0400'..'\u04FF' -> hasRussian = true
                char in '\u0100'..'\u017F' -> hasItalian = true
                char in '\u00C0'..'\u00FF' -> hasGerman = true
                char in '\u0100'..'\u017F' -> hasPortuguese = true
                char in '\u00C0'..'\u00FF' -> hasFrench = true
                char in '\u00C0'..'\u00FF' -> hasSpanish = true
                char in '\u0100'..'\u017F' -> hasMalay = true
            }
        }

        return@withContext when {
            hasVietnamese -> "vi"
            hasChinese -> "zh"
            hasJapanese -> "ja"
            hasKorean -> "ko"
            hasLatin -> "en"
            hasMalay -> "ms"
            hasThai -> "th"
            hasHindi -> "hi"
            hasId -> "id"
            hasArabic -> "ar"
            hasRussian -> "ru"
            hasItalian -> "it"
            hasGerman -> "de"
            hasPortuguese -> "pt"
            hasFrench -> "fr"
            hasSpanish -> "es"
            else -> "vi"
        }
    }

    suspend fun getLocaleForLanguage(lang: String): Locale = withContext(Dispatchers.IO) {
        return@withContext when (lang) {
            "vi" -> Locale("vi", "VN")
            "zh" -> Locale.CHINESE
            "ja" -> Locale.JAPANESE
            "ko" -> Locale.KOREAN
            "en" -> Locale.US
            "fr" -> Locale.FRANCE
            "es" -> Locale("es", "ES")
            "it" -> Locale.ITALIAN
            "de" -> Locale.GERMAN
            "pt" -> Locale("pt", "PT")
            "ru" -> Locale("ru", "RU")
            "ar" -> Locale("ar", "SA")
            "hi" -> Locale("hi", "IN")
            "th" -> Locale("th", "TH")
            "id" -> Locale("id", "ID")
            "ms" -> Locale("ms", "MY")
            else -> Locale.getDefault()
        }
    }

    private fun setupKeyPopup() {
        keyPopupView = layoutInflater.inflate(R.layout.custom_key_popup, null)
        keyPopupWindow = PopupWindow(
            keyPopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            isClippingEnabled = false
            isTouchable = false
            isOutsideTouchable = false
        }
    }

    private fun initializeVoiceToText() {
        voiceToTextManager = VoiceToTextManager(this)
        voiceToTextManager?.setCallback(object : VoiceToTextManager.VoiceToTextCallback {
            override fun onRecordingStarted() {
                handler.post {
                    isVoiceRecording = true
                    // Nút Voice→Text đã chuyển màu vàng trong onVoiceToTextButtonClick
                    // Hiện nút Stop với màu xanh (có thể bấm)
                    btnStopVoiceToText?.visibility = View.VISIBLE
                    setButtonRunningState(btnStopVoiceToText, false)
                    btnStopVoiceToText?.text = getString(R.string.stop_recording_text)
                }
            }

            override fun onRecordingStopped() {
                handler.post {
                    isVoiceRecording = false
                    isVoiceProcessing = true
                    // Nút Voice→Text chuyển về màu xanh
                    setButtonRunningState(btnVoiceToText, false)
                    btnVoiceToText?.text = getLocalizedString("voice_to_text", languageManager.getCurrentLanguage())
                    // Nút Stop chuyển sang màu vàng (đang thực thi)
                    setButtonRunningState(btnStopVoiceToText, true)
                    btnStopVoiceToText?.text = getString(R.string.processing_text)
                }
            }

            override fun onTranscriptionStarted() {
                handler.post {
                    isVoiceProcessing = true
                    // Nút Stop vẫn màu vàng, đang thực thi
                    setButtonRunningState(btnStopVoiceToText, true)
                    btnStopVoiceToText?.text = getString(R.string.transcribing_text)
                }
            }

            override fun onTranscriptionCompleted(text: String) {
                handler.post {
                    isVoiceProcessing = false
                    // Ẩn nút Stop, reset trạng thái
                    btnStopVoiceToText?.visibility = View.GONE
                    setButtonRunningState(btnStopVoiceToText, false)
                    
                    // Chèn văn bản vào input
                    currentInputConnection?.commitText(text, 1)
                }
            }

            override fun onError(message: String) {
                handler.post {
                    resetVoiceRecordingState()
                    Toast.makeText(this@AIKeyboardService, message, Toast.LENGTH_SHORT).show()
            
                }
            }
        })
    }

    private fun showKeyPreview(primaryCode: Int) {
        val key = currentKeyboard?.keys?.find { it.codes.contains(primaryCode) } ?: return
        val popupText = keyPopupView?.findViewById<TextView>(R.id.popupText)
        popupText?.text = key.label ?: key.codes[0].toChar().toString()

        popupText?.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val keyboardLocation = IntArray(2)
        keyboardView.getLocationInWindow(keyboardLocation)

        val x = keyboardLocation[0] + key.x + (key.width - popupText?.measuredWidth!!) / 2
        val y = keyboardLocation[1] + key.y - popupText.measuredHeight - 2

        keyPopupWindow?.dismiss()
        keyPopupWindow?.showAtLocation(keyboardView, Gravity.NO_GRAVITY, x, y)
    }

    private fun hideKeyPreview() {
        keyPopupWindow?.dismiss()
    }
    
    private fun refreshUIForLanguage() {

        
        // Force refresh resources for current language
        val currentLanguage = languageManager.getCurrentLanguage()
        val locale = Locale(currentLanguage.code)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        
        // Refresh all button texts in smartbar
        btnPasteAndRead?.text = getLocalizedString("paste_and_read", languageManager.getCurrentLanguage())
        btnStopTts?.text = getLocalizedString("stop_tts", languageManager.getCurrentLanguage())
        btnVoiceToText?.text = getLocalizedString("voice_to_text", languageManager.getCurrentLanguage())
        btnStopVoiceToText?.text = getLocalizedString("stop_voice", languageManager.getCurrentLanguage())
        btnTinhToan?.text = getLocalizedString("calculator", languageManager.getCurrentLanguage())
        
        // Find and update language button
        val languageButtonContainer = keyboard?.findViewById<View>(R.id.languageButtonContainer)
        val languageCodeText = languageButtonContainer?.findViewById<TextView>(R.id.languageCode)
        languageCodeText?.text = getString(R.string.language_code_vn)
        
        gptAskButton?.text = getLocalizedString("gpt_ask", languageManager.getCurrentLanguage())
        assistantsGptButton?.text = getLocalizedString("assistants_gpt", languageManager.getCurrentLanguage())
        olamaAskButton?.text = getLocalizedString("olama_ask", languageManager.getCurrentLanguage())
        stopGenerationButton?.text = getLocalizedString("stop_generation", languageManager.getCurrentLanguage())
        gptTranslateButton?.text = getLocalizedString("gpt_translate", languageManager.getCurrentLanguage())
        translateButton?.text = getLocalizedString("translate", languageManager.getCurrentLanguage())
        gptContinueButton?.text = getLocalizedString("continue", languageManager.getCurrentLanguage())
        gptSuggestButton?.text = getLocalizedString("gpt_suggest", languageManager.getCurrentLanguage())
        deepseekSuggestButton?.text = getLocalizedString("deepseek_suggest", languageManager.getCurrentLanguage())
        askButton?.text = getLocalizedString("ask", languageManager.getCurrentLanguage())
        btnGptSpellCheck?.text = getLocalizedString("gpt_spell_check", languageManager.getCurrentLanguage())
        btnDeepSeekSpellCheck?.text = getLocalizedString("spell_check", languageManager.getCurrentLanguage())
        btnAskDeepSeek?.text = getLocalizedString("ask_deepseek", languageManager.getCurrentLanguage())
        btnOlamaTrans?.text = getLocalizedString("olama_translate", languageManager.getCurrentLanguage())
        

    }
    
    private val languageChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "LANGUAGE_CHANGED") {
                handler.post {
                    refreshUIForLanguage()
                }
            }
        }
    }

    private fun handleDeepSeekTranslate() {

        setButtonRunningState(translateButton, true)
        val clipboardText = getClipboardText()
        if (clipboardText.isNullOrEmpty()) {
            setButtonRunningState(translateButton, false)
            return
        }

        val deepSeekApiKey = preferences?.getString("deepseek_api_key", "") ?: ""
        if (deepSeekApiKey.isEmpty()) {
            showToast("Please set your DeepSeek API key in settings")
            setButtonRunningState(translateButton, false)
            return
        }

        if (deepSeekAPI == null) {
            try {
                deepSeekAPI = DeepSeekAPI(deepSeekApiKey)
            } catch (e: Exception) {
                showToast("Error initializing DeepSeek API")
                setButtonRunningState(translateButton, false)
                return
            }
        }

        val targetLanguage = languageSpinner?.selectedItem?.toString() ?: "English"

        // Lưu vị trí con trỏ ban đầu (cuối văn bản đang có)
        val originalTextBeforeCursor = currentInputConnection?.getTextBeforeCursor(1000, 0) ?: ""
        val originalCursorPosition = originalTextBeforeCursor.length
        
        currentInputConnection?.commitText("\n", 1)
        val thinkingText = promptManager.getThinkingText()
        currentInputConnection?.commitText(thinkingText, 1)
        thinkingTextLength = thinkingText.length
        stopGenerationButton?.visibility = View.VISIBLE

        generationJob?.cancel() // Hủy job cũ nếu có
        generationJob = CoroutineScope(Dispatchers.Main).launch {
            try {
        
                deleteThinkingText()
                var fullResponse = StringBuilder()

                deepSeekAPI?.streamTranslate(clipboardText, targetLanguage, currentInputConnection, thinkingTextLength)?.collect { chunk ->
                    currentInputConnection?.commitText(chunk, 1)
                    fullResponse.append(chunk)
                }
                currentInputConnection?.commitText("\n", 1)

                captureGPTResponse(fullResponse.toString())
            } catch (e: Exception) {
        
                deleteThinkingText()
                if (e !is CancellationException) {
                    currentInputConnection?.commitText("\nTranslation error: ${e.message}\n", 1)
                }
            } finally {
                stopGenerationButton?.visibility = View.GONE
                setButtonRunningState(translateButton, false)
            }
        }
    }

    private fun handleDeepSeekAsk() {

        setButtonRunningState(askButton, true)
        val clipboardText = getClipboardText()
        if (clipboardText.isNullOrEmpty()) {
            setButtonRunningState(askButton, false)
            return
        }

        val deepSeekApiKey = preferences?.getString("deepseek_api_key", "") ?: ""
        if (deepSeekApiKey.isEmpty()) {
            showToast("Please set your DeepSeek API key in settings")
            setButtonRunningState(askButton, false)
            return
        }

        if (deepSeekAPI == null) {
            try {
                deepSeekAPI = DeepSeekAPI(deepSeekApiKey)
            } catch (e: Exception) {
                showToast("Error initializing DeepSeek API")
                setButtonRunningState(askButton, false)
                return
            }
        }

        // Lưu vị trí con trỏ ban đầu (cuối văn bản đang có)
        val originalTextBeforeCursor = currentInputConnection?.getTextBeforeCursor(1000, 0) ?: ""
        val originalCursorPosition = originalTextBeforeCursor.length
        
        currentInputConnection?.commitText("\n", 1)
        val thinkingText = promptManager.getThinkingText()
        currentInputConnection?.commitText(thinkingText, 1)
        thinkingTextLength = thinkingText.length
        stopGenerationButton?.visibility = View.VISIBLE

        generationJob?.cancel() // Hủy job cũ nếu có
        generationJob = CoroutineScope(Dispatchers.Main).launch {
            try {
        
                deleteThinkingText()
                var fullResponse = StringBuilder()

                deepSeekAPI?.streamAskQuestion(clipboardText, currentInputConnection!!, thinkingTextLength)?.collect { chunk ->
                    currentInputConnection?.commitText(chunk, 1)
                    fullResponse.append(chunk)
                }
                currentInputConnection?.commitText("\n", 1)

                captureGPTResponse(fullResponse.toString())
            } catch (e: Exception) {
        
                deleteThinkingText()
                if (e !is CancellationException) {
                    currentInputConnection?.commitText("\nError: ${e.message}\n", 1)
                }
            } finally {
                stopGenerationButton?.visibility = View.GONE
                setButtonRunningState(askButton, false)
            }
        }
    }

    private fun handleOlamaAsk() {

        setButtonRunningState(olamaAskButton, true)
        val prefs = getSharedPreferences("AIKeyboardPrefs", MODE_PRIVATE)
        val olamaUrl = prefs.getString("olama_url", "") ?: ""
        val olamaModel = prefs.getString("olama_model", "") ?: ""
        if (olamaUrl.isEmpty() || olamaModel.isEmpty()) {
            showToast("Please set your Olama URL and model in settings")
            setButtonRunningState(olamaAskButton, false)
            return
        }
        val clipboardText = getClipboardText()
        if (clipboardText.isNullOrEmpty()) {
            setButtonRunningState(olamaAskButton, false)
            return
        }
        
        // Lưu vị trí con trỏ ban đầu (cuối văn bản đang có)
        val originalTextBeforeCursor = currentInputConnection?.getTextBeforeCursor(1000, 0) ?: ""
        val originalCursorPosition = originalTextBeforeCursor.length
        
        currentInputConnection?.commitText("\n", 1)
        val thinkingText = promptManager.getThinkingText()
        currentInputConnection?.commitText(thinkingText, 1)
        thinkingTextLength = thinkingText.length
        generationJob?.cancel()
        generationJob = CoroutineScope(Dispatchers.Main).launch {
            try {
        
                deleteThinkingText()
                val olama = OlamaServe(olamaUrl, olamaModel)
                val ic = currentInputConnection
                var isFirstChunk = true
                if (ic != null) {
                    olama.streamAskQuestion(clipboardText, currentInputConnection!!, thinkingTextLength)?.collect { chunk ->
                        ic.commitText(chunk, 1)
                    }
                }
            } catch (e: Exception) {
        
                deleteThinkingText()
                if (e !is CancellationException) {
                    currentInputConnection?.commitText("\nError: ${e.message}\n", 1)
                }
            } finally {
                stopGenerationButton?.visibility = View.GONE
                setButtonRunningState(olamaAskButton, false)
            }
        }
    }

    private fun setButtonRunningState(button: Button?, running: Boolean) {
        if (running) {
            button?.isEnabled = false
            button?.backgroundTintList = android.content.res.ColorStateList.valueOf(resources.getColor(R.color.button_running_background, theme))
        } else {
            button?.isEnabled = true
            button?.backgroundTintList = android.content.res.ColorStateList.valueOf(resources.getColor(R.color.key_special_background, theme))
        }
    }

    // Helper function để xóa chính xác thinking text theo ngôn ngữ
    private fun deleteThinkingText() {
        if (thinkingTextLength > 0) {
            // Tìm và xóa chính xác thinking text hiện tại
            val textBeforeCursor = currentInputConnection?.getTextBeforeCursor(1000, 0) ?: ""
            val thinkingText = promptManager.getThinkingText()
            val thinkingIndex = textBeforeCursor.lastIndexOf(thinkingText)
            if (thinkingIndex >= 0) {
                // Xóa từ vị trí thinking text đến cuối
                val charsToDelete = textBeforeCursor.length - thinkingIndex
                currentInputConnection?.deleteSurroundingText(charsToDelete, 0)
            } else {
                // Fallback: xóa theo cách cũ
                currentInputConnection?.deleteSurroundingText(thinkingTextLength, 0)
            }
            thinkingTextLength = 0
        }
    }

    private fun setupSmartbarToggle() {
        btnToggleSmartbar?.setOnClickListener {
            if (isSmartbarExpanded) {
                collapseSmartbar()
            } else {
                expandSmartbar()
            }
        }
    }

    private fun setupSmartbarCollapse() {
        // Trạng thái ban đầu: thu gọn (chỉ hiện tầng dưới)
        smartbarContainer?.visibility = View.GONE
        btnToggleSmartbar?.text = "▲"
        isSmartbarExpanded = false
    }

    private fun expandSmartbar() {
        smartbarContainer?.visibility = View.VISIBLE
        btnToggleSmartbar?.text = "▼"
        isSmartbarExpanded = true
    }

    private fun collapseSmartbar() {
        smartbarContainer?.visibility = View.GONE
        btnToggleSmartbar?.text = "▲"
        isSmartbarExpanded = false
    }

    // Dictionary Suggestions Functions
    private fun initializeDictionarySuggestions() {
        try {
            // Khởi tạo Dictionary Manager
    
            try {
                dictionaryManager = SimpleDictionaryManager(this)
                // Set clipboard history provider
                dictionaryManager?.setClipboardHistoryProvider {
                    clipboardHistory.toList()
                }
        
            } catch (e: Exception) {
        
                e.printStackTrace()
                return
            }
            
            // Khởi tạo views
    
            
            // Tìm dictionary suggestions container từ smartbar
            this.dictionarySuggestionsContainer = keyboard?.findViewById<View>(R.id.dictionarySuggestionsContainer)
    
            
            // Set references cho dictionary suggestions
            suggestionsContainer = this.dictionarySuggestionsContainer
            singleWordRecyclerView = this.dictionarySuggestionsContainer?.findViewById(R.id.singleWordRecyclerView)
            phraseRecyclerView = this.dictionarySuggestionsContainer?.findViewById(R.id.phraseRecyclerView)
            nextWordRecyclerView = this.dictionarySuggestionsContainer?.findViewById(R.id.nextWordRecyclerView)
            
            // Khởi tạo smartbar controls
            val smartbarView = keyboard?.findViewById<View>(R.id.smartbar)
            btnToggleSmartbar = smartbarView?.findViewById(R.id.btnToggleSmartbar)
            smartbarContainer = smartbarView?.findViewById(R.id.smartbarContainer)
            
            
            
            // Thiết lập nút toggle smartbar
            btnToggleSmartbar?.setOnClickListener {
                val currentVisibility = smartbarContainer?.visibility
                if (currentVisibility == View.VISIBLE) {
                    smartbarContainer?.visibility = View.GONE
                    btnToggleSmartbar?.text = "▼"
                } else {
                    smartbarContainer?.visibility = View.VISIBLE
                    btnToggleSmartbar?.text = "▲"
                }
            }
            
            // Thiết lập nút toggle dictionary suggestions
            val btnToggleDictionary = keyboard?.findViewById<Button>(R.id.btnToggleDictionary)
            btnToggleDictionary?.setOnClickListener {
                val currentVisibility = dictionarySuggestionsContainer?.visibility
                if (currentVisibility == View.VISIBLE) {
                    dictionarySuggestionsContainer?.visibility = View.GONE
                    btnToggleDictionary.text = "→"
            
                } else {
                    dictionarySuggestionsContainer?.visibility = View.VISIBLE
                    btnToggleDictionary.text = "←"
            
                }
            }
            
            // Thiết lập RecyclerView cho từ đơn
            singleWordRecyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            singleWordAdapter = SuggestionAdapter(emptyList()) { suggestion ->
                onSuggestionClicked(suggestion)
            }
            singleWordRecyclerView?.adapter = singleWordAdapter
            
            // Thiết lập RecyclerView cho từ đôi
            phraseRecyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            phraseAdapter = SuggestionAdapter(emptyList()) { suggestion ->
                onSuggestionClicked(suggestion)
            }
            phraseRecyclerView?.adapter = phraseAdapter
            
            // Thiết lập RecyclerView cho từ tiếp theo
            nextWordRecyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            nextWordAdapter = SuggestionAdapter(emptyList()) { suggestion ->
                onSuggestionClicked(suggestion)
            }
            nextWordRecyclerView?.adapter = nextWordAdapter
            
            // Không cần nút đóng nữa vì đã loại bỏ
            

            
    
        } catch (e: Exception) {
    
        }
    }

    private fun onSuggestionClicked(suggestion: Suggestion) {
        try {
    
            
            // Kiểm tra xem suggestion này có phải từ tiếp theo không
            val isNextWordSuggestion = nextWordAdapter?.getSuggestions()?.contains(suggestion) == true
            val isSingleWordSuggestion = singleWordAdapter?.getSuggestions()?.contains(suggestion) == true
            val isPhraseSuggestion = phraseAdapter?.getSuggestions()?.contains(suggestion) == true
            
    
            
            if (isNextWordSuggestion) {
                // Nếu là từ tiếp theo: thêm cách ra và in từ đó
                currentInputConnection?.commitText(" ${suggestion.word}", 1)
        
                
                // Cập nhật gợi ý từ tiếp theo ngay lập tức
                updateNextWordSuggestions()
            } else if (isSingleWordSuggestion || isPhraseSuggestion) {
                // Nếu là từ đơn hoặc từ đôi: thay thế từ hiện tại
                val wordToReplace = currentInputWord.toString()
        
                
                if (wordToReplace.isNotEmpty()) {
                    // Xóa từ đang nhập
                    currentInputConnection?.deleteSurroundingText(wordToReplace.length, 0)
                    // Thêm từ gợi ý
                    currentInputConnection?.commitText(suggestion.word, 1)
            
                } else {
                    // Nếu không có từ để thay thế, chỉ thêm từ gợi ý
                    currentInputConnection?.commitText(suggestion.word, 1)
            
                }
            } else {
                // Fallback: thêm từ gợi ý trực tiếp
                currentInputConnection?.commitText(suggestion.word, 1)
        
            }
            
            // Học từ suggestion được chọn
            dictionaryManager?.learnFromText(suggestion.word)
            
            // Cập nhật currentInputWord
            currentInputWord.clear()
            currentInputWord.append(suggestion.word)
            
    
        } catch (e: Exception) {
    
            e.printStackTrace()
        }
    }

    private fun showSuggestions(singleWords: List<Suggestion>, phrases: List<Suggestion>, nextWords: List<Suggestion>) {
        try {
    
    
            
            singleWordAdapter?.updateSuggestions(singleWords)
            phraseAdapter?.updateSuggestions(phrases)
            nextWordAdapter?.updateSuggestions(nextWords)
            
            suggestionsContainer?.visibility = View.VISIBLE
            isSuggestionsVisible = true
    
        } catch (e: Exception) {
    
        }
    }

    private fun hideSuggestions() {
        try {
            suggestionsContainer?.visibility = View.GONE
            isSuggestionsVisible = false
            singleWordAdapter?.clearSuggestions()
            phraseAdapter?.clearSuggestions()
            nextWordAdapter?.clearSuggestions()
    
        } catch (e: Exception) {
    
        }
    }

    private fun updateSuggestions(query: String) {
        // Vô hiệu hóa hệ thống cũ để tránh xung đột với updateSuggestionsFast

        return
    }

    private fun updateSuggestionsFast(query: String) {
        // Tìm kiếm nhanh không cần coroutine
        try {
    
            
            // Phần 1: Clipboard history + văn bản liên quan nhất
            val part1Suggestions = mutableListOf<Suggestion>()
            
            // Thêm clipboard history thông minh
    
            
            // Lấy context hiện tại để ưu tiên
            val currentContext = getCurrentContext()
    
            
            // Lọc clipboard theo context và type
            val relevantClipboard = smartClipboardHistory.filter { item ->
                // Ưu tiên items có context tương tự
                item.context.contains(currentContext) || 
                currentContext.contains(item.text) ||
                item.text.contains(currentContext)
            }.take(4).toMutableList()
            
            // Nếu không có items liên quan, lấy theo frequency
            if (relevantClipboard.isEmpty()) {
                relevantClipboard.addAll(smartClipboardHistory.take(6))
            }
            
    
            
            // Thêm các từ thường dùng nếu ít
            if (relevantClipboard.size < 3) {
                val commonWords = listOf("tôi", "bạn", "anh", "em", "ông", "bà", "cô", "chú", "bác", "chị", "cậu", "mợ")
                for (word in commonWords) {
                    if (!relevantClipboard.any { it.text == word } && relevantClipboard.size < 8) {
                        relevantClipboard.add(SmartClipboardItem(word, ClipboardType.WORD, 1, System.currentTimeMillis(), ""))
                    }
                }
        
            }
            
            // Thêm vào suggestions với ưu tiên clipboard thông minh
            for (item in relevantClipboard.take(8)) {
                val suggestionType = when (item.type) {
                    ClipboardType.EMAIL -> Suggestion.SuggestionType.CLIPBOARD_HISTORY
                    ClipboardType.URL -> Suggestion.SuggestionType.CLIPBOARD_HISTORY
                    ClipboardType.PHONE -> Suggestion.SuggestionType.CLIPBOARD_HISTORY
                    ClipboardType.PHRASE -> Suggestion.SuggestionType.CLIPBOARD_HISTORY
                    else -> Suggestion.SuggestionType.WORD_SUGGESTION
                }
                part1Suggestions.add(Suggestion(item.text, null, suggestionType))
        
            }
    
            
            // Thêm các từ liên quan đến văn bản đang gõ
            if (smartVietnameseProcessor != null && isSmartProcessorEnabled && query.isNotEmpty()) {
                try {
                    // Lấy các từ có liên quan đến query hiện tại
                    val relatedWords = smartVietnameseProcessor!!.getSmartSuggestions(query, 3)
                    for (suggestion in relatedWords) {
                        if (!part1Suggestions.any { it.word == suggestion.word } && part1Suggestions.size < 10) {
                            part1Suggestions.add(Suggestion(suggestion.word, null, Suggestion.SuggestionType.WORD_SUGGESTION))
                        }
                    }
                } catch (e: Exception) {
            
                }
            }
            
            // Phần 2: Gợi ý từ đôi (phrase suggestions)
            val part2Suggestions = mutableListOf<Suggestion>()
            if (smartVietnameseProcessor != null && isSmartProcessorEnabled) {
                try {
                    // Lấy gợi ý từ đôi từ smartVietnameseProcessor
                    val phraseSuggestions = smartVietnameseProcessor!!.getSmartSuggestions(query, 10)
                    for (suggestion in phraseSuggestions) {
                        // Chỉ lấy các từ có khoảng trắng (từ đôi)
                        if (suggestion.word.contains(" ")) {
                            part2Suggestions.add(suggestion)
                            if (part2Suggestions.size >= 8) break
                        }
                    }
                } catch (e: Exception) {
            
                }
            }
            
            // Phần 3: Gợi ý từ tiếp theo
            val part3Suggestions = mutableListOf<Suggestion>()
            if (smartVietnameseProcessor != null && isSmartProcessorEnabled) {
                try {
                    val nextWords = smartVietnameseProcessor!!.getNextWordSuggestions(query, 5)
                    for (word in nextWords) {
                        part3Suggestions.add(Suggestion(word, null, Suggestion.SuggestionType.WORD_SUGGESTION))
                    }
                } catch (e: Exception) {
            
                }
            }
            
    
            showSuggestions(part1Suggestions, part2Suggestions, part3Suggestions)
        } catch (e: Exception) {
    
            e.printStackTrace()
        }
    }

    private fun extractContextWords(): List<String> {
        try {
            val textBeforeCursor = currentInputConnection?.getTextBeforeCursor(200, 0) ?: ""
            val words = textBeforeCursor.split(" ").filter { it.isNotEmpty() }
            
            // Lấy từ cuối cùng để tạo context
            val lastWord = words.lastOrNull() ?: ""
            
            if (lastWord.isEmpty()) {
                return listOf("tôi", "bạn", "anh", "em", "ông", "bà", "cô", "chú", "bác")
            }
            
            // Sử dụng SimpleDictionaryManager để lấy gợi ý từ tiếp theo
            return dictionaryManager?.getNextWordSuggestions(lastWord, "vi") ?: emptyList()
            
        } catch (e: Exception) {
    
            return emptyList()
        }
    }



    private fun extractCurrentWord(): String {
        try {
            val textBeforeCursor = currentInputConnection?.getTextBeforeCursor(100, 0) ?: ""
            val words = textBeforeCursor.split(" ")
            return words.lastOrNull() ?: ""
        } catch (e: Exception) {
    
            return ""
        }
    }

    private fun updateSuggestionsAfterInput() {
        try {
            val currentWord = extractCurrentWord()
    
            
            // Auto-collapse smartbar khi gõ
            autoCollapseSmartbar()
            
            // Cập nhật currentInputWord
            currentInputWord.clear()
            currentInputWord.append(currentWord)
            
            // Hủy job cũ nếu có
            suggestionsJob?.cancel()
            
            // Tạo job mới với delay ngắn để gợi ý nhanh như chớp
            suggestionsJob = CoroutineScope(Dispatchers.Main).launch {
                delay(100) // Giảm delay từ 300ms xuống 100ms để nhanh hơn
                
                if (currentWord.length >= 2) {
            
                    try {
                        updateSuggestionsFast(currentWord)
                
                    } catch (e: Exception) {
                
                        e.printStackTrace()
                    }
                } else {
            
                    updateNextWordSuggestions()
                }
            }
        } catch (e: Exception) {
    
        }
    }
    
    private fun autoCollapseSmartbar() {
        try {
            // Thu gọn smartbar khi gõ
            smartbarContainer?.visibility = View.GONE
            btnToggleSmartbar?.text = "▼"
            
            // Hiển thị dictionary suggestions (chỉ khi đã được bật thủ công)
            val btnToggleDictionary = keyboard?.findViewById<Button>(R.id.btnToggleDictionary)
            if (btnToggleDictionary?.text == "←") {
                // Nếu dictionary suggestions đang ẩn, không tự động bật ra
        
            } else {
                // Nếu dictionary suggestions đang hiển thị, giữ nguyên
        
            }
            
    
        } catch (e: Exception) {
    
        }
    }

    private fun updateNextWordSuggestions() {
        try {
            val contextWords = extractContextWords()
            val nextWordSuggestions = contextWords.take(5).map { word ->
                Suggestion(word, null, Suggestion.SuggestionType.WORD_SUGGESTION)
            }
            
            // Chỉ cập nhật nếu có suggestions mới và khác với cũ
            val currentSuggestions = nextWordAdapter?.getSuggestions() ?: emptyList()
            if (nextWordSuggestions != currentSuggestions) {
                nextWordAdapter?.updateSuggestions(nextWordSuggestions)
        
            }
        } catch (e: Exception) {
    
        }
    }
    
    /**
     * Xử lý từ không dấu thành từ có dấu sử dụng SmartVietnameseProcessor
     */
    private fun enhanceVietnameseWord(word: String): String {
        return if (smartVietnameseProcessor != null && isSmartProcessorEnabled) {
            try {
                smartVietnameseProcessor!!.processNonAccentedWord(word)
            } catch (e: Exception) {
        
                word
            }
        } else {
            word
        }
    }
    
    /**
     * Bật/tắt SmartVietnameseProcessor
     */
    fun setSmartProcessorEnabled(enabled: Boolean) {
        isSmartProcessorEnabled = enabled
        preferences?.edit()?.putBoolean("smart_processor_enabled", enabled)?.apply()

    }

    override fun onDestroy() {
        super.onDestroy()
        generationJob?.cancel() // Hủy bất kỳ job nào đang chạy
        suggestionsJob?.cancel() // Hủy suggestions job
        stopTts()
        cleanupSpeechRecognizer()
        voiceToTextManager?.release() // Cleanup VoiceToTextManager
        clipboardManager.removePrimaryClipChangedListener(this)
        dictionaryManager?.close() // Đóng dictionary manager
        
        // Unregister broadcast receiver
        try {
            unregisterReceiver(languageChangeReceiver)
        } catch (e: Exception) {
            // Receiver might not be registered
        }
        
        saveClipboardHistoryToPrefs() // <-- Lưu lại khi huỷ

    }

    private fun resetVoiceRecordingState() {
        isVoiceRecording = false
        isVoiceProcessing = false
        setButtonRunningState(btnVoiceToText, false)
        btnVoiceToText?.text = getLocalizedString("voice_to_text", languageManager.getCurrentLanguage())
        btnStopVoiceToText?.visibility = View.GONE
        setButtonRunningState(btnStopVoiceToText, false)

    }


}