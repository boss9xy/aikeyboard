package com.example.aikeyboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.net.Uri
import androidx.appcompat.app.AlertDialog

class SettingsActivity : AppCompatActivity() {
    private lateinit var deepseekApiKeyEditText: EditText
    private lateinit var gptApiKeyEditText: EditText
    private lateinit var gptAssistantsIdEditText: EditText

    private lateinit var saveButton: Button
    private lateinit var enableKeyboardButton: Button
    private lateinit var selectKeyboardButton: Button
    private lateinit var olamaUrlEditText: EditText
    private lateinit var olamaModelEditText: EditText
    private lateinit var checkOlamaModelButton: Button
    private lateinit var saveOlamaButton: Button

    private lateinit var appLanguageSpinner: Spinner
    private lateinit var languageManager: LanguageManager
    private lateinit var voiceToTextButton: Button
    private lateinit var customizePromptsButton: Button

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val PERMISSION_REQUEST_CODE_2 = 101
        private const val STORAGE_PERMISSION_CODE = 100
        private const val MICROPHONE_PERMISSION_CODE = 1
    }

    private val permissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        // Android 13+ sử dụng READ_MEDIA_* permissions
        arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        // Android < 13 sử dụng READ_EXTERNAL_STORAGE
        arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
    private var currentPermissionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        initViews()
        setupButtons()
        loadSavedSettings()
        
        // Kiểm tra quyền đã được cấp chưa, nếu đã cấp thì không hiển thị popup
        if (areAllPermissionsGranted()) {
            // Tất cả quyền đã được cấp, không cần hiển thị popup
            return
        }
        
        // Chỉ hiển thị popup nếu chưa cấp đủ quyền
        checkPermissions()
    }
    
    private fun areAllPermissionsGranted(): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun initViews() {
        Logger.initialize(this)


        deepseekApiKeyEditText = findViewById(R.id.deepseekApiKeyEditText)
        gptApiKeyEditText = findViewById(R.id.gptApiKeyEditText)
        gptAssistantsIdEditText = findViewById(R.id.gptAssistantsIdEditText)

        saveButton = findViewById(R.id.saveButton)
        enableKeyboardButton = findViewById(R.id.enableKeyboardButton)
        selectKeyboardButton = findViewById(R.id.selectKeyboardButton)
        olamaUrlEditText = findViewById(R.id.olamaUrlEditText)
        olamaModelEditText = findViewById(R.id.olamaModelEditText)
        checkOlamaModelButton = findViewById(R.id.checkOlamaModelButton)
        saveOlamaButton = findViewById(R.id.saveOlamaButton)

        appLanguageSpinner = findViewById(R.id.appLanguageSpinner)
        voiceToTextButton = findViewById(R.id.voiceToTextButton)
        customizePromptsButton = findViewById(R.id.customizePromptsButton)
        
        // Initialize LanguageManager
        languageManager = LanguageManager(this)
    }

    private fun setupButtons() {
        // Xử lý Switch bật/tắt âm thanh gõ phím
        val switchSound = findViewById<Switch>(R.id.switchSound)
        val prefs = getSharedPreferences("AIKeyboardPrefs", MODE_PRIVATE)
        switchSound.isChecked = prefs.getBoolean("sound_enabled", true)
        switchSound.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("sound_enabled", isChecked).apply()
        }
        


        saveButton.setOnClickListener {
            val deepseekApiKey = deepseekApiKeyEditText.text.toString().trim()
            val gptApiKey = gptApiKeyEditText.text.toString().trim()
            val gptAssistantsId = gptAssistantsIdEditText.text.toString().trim()
            

            prefs.edit().apply {
                putString("deepseek_api_key", deepseekApiKey)
                putString("gpt_api_key", gptApiKey)
                putString("gpt_assistants_id", gptAssistantsId)
                apply()
            }

            Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()
    
        }

        enableKeyboardButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }

        selectKeyboardButton.setOnClickListener {
            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
        }

        checkOlamaModelButton.setOnClickListener {
            val url = olamaUrlEditText.text.toString().trim()
            if (url.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_enter_olama_url), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            checkOlamaModelButton.isEnabled = false
            checkOlamaModelButton.text = getString(R.string.checking_model_text)
            // Gọi API lấy model name
            Thread {
                try {
                    val modelName = fetchOlamaModel(url)
                    runOnUiThread {
                        olamaModelEditText.setText(modelName)
                        Toast.makeText(this, getString(R.string.model_found, modelName), Toast.LENGTH_SHORT).show()
                        checkOlamaModelButton.text = getString(R.string.check_olama_model_button)
                        checkOlamaModelButton.isEnabled = true
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        olamaModelEditText.setText("")
                        Toast.makeText(this, getString(R.string.cannot_get_model, e.message), Toast.LENGTH_SHORT).show()
                        checkOlamaModelButton.text = getString(R.string.check_olama_model_button)
                        checkOlamaModelButton.isEnabled = true
                    }
                }
            }.start()
        }

        saveOlamaButton.setOnClickListener {
            val url = olamaUrlEditText.text.toString().trim()
            val model = olamaModelEditText.text.toString().trim()
            prefs.edit().apply {
                putString("olama_url", url)
                putString("olama_model", model)
                apply()
            }
            Toast.makeText(this, getString(R.string.olama_saved), Toast.LENGTH_SHORT).show()
        }
        
        // Voice to Text
        voiceToTextButton.setOnClickListener {
            val intent = Intent(this, VoiceToTextActivity::class.java)
            startActivity(intent)
        }

        // Customize Prompts
        customizePromptsButton.setOnClickListener {
            Log.d("SettingsActivity", "🔵 Bấm nút Tùy chỉnh Prompts")
            try {
                Log.d("SettingsActivity", "🔵 Tạo Intent cho PromptCustomizationActivity")
                val intent = Intent(this, PromptCustomizationActivity::class.java)
                Log.d("SettingsActivity", "🔵 Intent tạo thành công, bắt đầu startActivity")
                startActivity(intent)
                Log.d("SettingsActivity", "🔵 startActivity thành công")
            } catch (e: Exception) {
                Log.e("SettingsActivity", "❌ Lỗi trong startActivity: ${e.message}", e)
                e.printStackTrace()
                Toast.makeText(this, "Lỗi mở tùy chỉnh prompts: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun loadSavedSettings() {
        val prefs = getSharedPreferences("AIKeyboardPrefs", MODE_PRIVATE)
        
        deepseekApiKeyEditText.setText(prefs.getString("deepseek_api_key", ""))
        gptApiKeyEditText.setText(prefs.getString("gpt_api_key", ""))
        gptAssistantsIdEditText.setText(prefs.getString("gpt_assistants_id", ""))


        olamaUrlEditText.setText(prefs.getString("olama_url", ""))
        olamaModelEditText.setText(prefs.getString("olama_model", ""))

        // Setup Voice-to-Text language spinner

        
        // Setup app language spinner
        setupAppLanguageSpinner()
    }

    private fun checkPermissions() {
        if (currentPermissionIndex >= permissions.size) return
        val permission = permissions[currentPermissionIndex]
        
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            // Đã được cấp, chuyển sang quyền tiếp theo
            currentPermissionIndex++
            checkPermissions()
        } else {
            // Bỏ qua tất cả quyền, không hiển thị popup tự động
            currentPermissionIndex++
            checkPermissions()
        }
    }
    
    private fun isPermissionEssential(permission: String): Boolean {
        return when (permission) {
            Manifest.permission.RECORD_AUDIO -> true // Cần cho các nút AI khác
            Manifest.permission.READ_EXTERNAL_STORAGE -> false // Không cần thiết
            Manifest.permission.READ_MEDIA_IMAGES -> false // Không cần thiết
            Manifest.permission.READ_MEDIA_VIDEO -> false // Không cần thiết
            Manifest.permission.READ_MEDIA_AUDIO -> false // Không cần thiết
            else -> false
        }
    }

    private fun showRationaleDialog(permission: String) {
        val message = when (permission) {
            Manifest.permission.RECORD_AUDIO ->
                "Ứng dụng cần quyền micro để sử dụng tính năng ghi âm và chuyển giọng nói thành văn bản."
            else -> "Ứng dụng cần quyền này để hoạt động."
        }
        AlertDialog.Builder(this)
            .setTitle("Cần quyền để tiếp tục")
            .setMessage(message)
            .setPositiveButton("Cấp quyền") { _, _ ->
                ActivityCompat.requestPermissions(this, arrayOf(permission), currentPermissionIndex)
            }
            .setNegativeButton("Hủy", null)
            .setCancelable(false)
            .show()
    }

    private fun showGoToSettingsDialog() {
        val permissionName = when (permissions[currentPermissionIndex]) {
            Manifest.permission.RECORD_AUDIO -> "Ghi âm"
            Manifest.permission.READ_EXTERNAL_STORAGE -> "Đọc file"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "Ghi file"
            Manifest.permission.READ_MEDIA_IMAGES -> "Truy cập ảnh"
            Manifest.permission.READ_MEDIA_VIDEO -> "Truy cập video"
            Manifest.permission.READ_MEDIA_AUDIO -> "Truy cập âm thanh"
            else -> "Quyền này"
        }
        
        AlertDialog.Builder(this)
            .setTitle("Quyền bị từ chối")
            .setMessage("Bạn đã từ chối quyền $permissionName. Để ứng dụng hoạt động đầy đủ, vui lòng vào Cài đặt để cấp quyền.")
            .setPositiveButton("Mở Cài đặt") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("Đóng") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode in this.permissions.indices) {
            val permission = this.permissions[requestCode]
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        
                currentPermissionIndex++
                checkPermissions()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    // User đã chọn "Don't ask again"
                    showGoToSettingsDialog()
                } else {
                    // User chỉ từ chối tạm thời, xin lại
                    checkPermissions()
                }
            }
        }
    }

    // Hàm gọi API lấy model name từ Olama
    private fun fetchOlamaModel(url: String): String {
        // Giả sử API Olama trả về JSON: { "model": "tên-model" }
        val apiUrl = if (url.endsWith("/")) url + "v1/models" else url + "/v1/models"
        val connection = java.net.URL(apiUrl).openConnection() as java.net.HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        val code = connection.responseCode
        if (code != 200) throw Exception("HTTP $code")
        val stream = connection.inputStream.bufferedReader().use { it.readText() }
        // Tìm model đầu tiên trong danh sách
        val json = org.json.JSONObject(stream)
        val data = json.optJSONArray("data")
        if (data != null && data.length() > 0) {
            val first = data.getJSONObject(0)
            return first.optString("id", "")
        }
        // Nếu không có mảng data, thử lấy trực tiếp key "model"
        if (json.has("model")) return json.getString("model")
        throw Exception("Không tìm thấy model")
    }



    private fun setupAppLanguageSpinner() {
        val languages = languageManager.getAllLanguages()
        val languageNames = languages.map { language ->
            "${language.nativeName} (${language.code})"
        }.toTypedArray()
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        appLanguageSpinner.adapter = adapter
        
        // Set current language selection
        val currentLanguage = languageManager.getCurrentLanguage()
        val currentIndex = languages.indexOf(currentLanguage)
        if (currentIndex >= 0) {
            appLanguageSpinner.setSelection(currentIndex)
        }
        
        // Handle language selection
        appLanguageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedLanguage = languages[position]
                if (selectedLanguage != languageManager.getCurrentLanguage()) {
                    languageManager.setLanguage(selectedLanguage)
                    Toast.makeText(this@SettingsActivity, "Language changed to ${selectedLanguage.nativeName}", Toast.LENGTH_SHORT).show()
                    
                    // Send broadcast to notify AIKeyboardService about language change
                    val intent = Intent("LANGUAGE_CHANGED")
                    sendBroadcast(intent)
                    
                    // Restart activity to apply language changes
                    recreate()
                    
                    // Force refresh keyboard UI by toggling input method
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showPermissionRationale() {
        val message = when (permissions[currentPermissionIndex]) {
            Manifest.permission.RECORD_AUDIO -> 
                "Ứng dụng cần quyền ghi âm để chuyển đổi giọng nói thành văn bản."
            Manifest.permission.READ_EXTERNAL_STORAGE -> 
                "Ứng dụng cần quyền đọc file để import tài liệu từ bộ nhớ thiết bị."
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> 
                "Ứng dụng cần quyền ghi file để lưu trữ dữ liệu và tải model."
            Manifest.permission.READ_MEDIA_IMAGES -> 
                "Ứng dụng cần quyền truy cập ảnh để xử lý và phân tích hình ảnh."
            Manifest.permission.READ_MEDIA_VIDEO -> 
                "Ứng dụng cần quyền truy cập video để xử lý và phân tích video."
            Manifest.permission.READ_MEDIA_AUDIO -> 
                "Ứng dụng cần quyền truy cập file âm thanh để xử lý audio."
            else -> "Ứng dụng cần quyền này để hoạt động đầy đủ."
        }
        
        AlertDialog.Builder(this)
            .setTitle("Cần cấp quyền")
            .setMessage(message)
            .setPositiveButton("Cấp quyền") { _, _ ->
                requestCurrentPermission()
            }
            .setNegativeButton("Hủy") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun requestCurrentPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permissions[currentPermissionIndex]),
            PERMISSION_REQUEST_CODE
        )
    }
}