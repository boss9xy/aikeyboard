package com.example.aikeyboard

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.widget.EditText
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import com.example.aikeyboard.R
import com.example.aikeyboard.models.Language
import com.example.aikeyboard.LanguageManager

class PromptCustomizationActivity : AppCompatActivity() {
    
    // AI Assistant
    private lateinit var aiAssistantPromptEditText: EditText
    private lateinit var aiAssistantButtonNameEditText: EditText
    private lateinit var aiAssistantPromptSwitch: SwitchCompat
    
    // GPT Ask
    private lateinit var gptAskPromptEditText: EditText
    private lateinit var gptAskButtonNameEditText: EditText
    private lateinit var gptAskPromptSwitch: SwitchCompat
    
    // DeepSeek Ask
    private lateinit var deepseekAskPromptEditText: EditText
    private lateinit var deepseekAskButtonNameEditText: EditText
    private lateinit var deepseekAskPromptSwitch: SwitchCompat
    
    // Olama Ask
    private lateinit var olamaAskPromptEditText: EditText
    private lateinit var olamaAskButtonNameEditText: EditText
    private lateinit var olamaAskPromptSwitch: SwitchCompat
    
               // GPT Continue
           private lateinit var gptContinuePromptEditText: EditText
           private lateinit var gptContinueButtonNameEditText: EditText
           private lateinit var gptContinuePromptSwitch: SwitchCompat

           // DeepSeek Translate
           private lateinit var deepseekTranslatePromptEditText: EditText
           private lateinit var deepseekTranslateButtonNameEditText: EditText
           private lateinit var deepseekTranslatePromptSwitch: SwitchCompat

           // GPT Suggest
           private lateinit var gptSuggestPromptEditText: EditText
           private lateinit var gptSuggestButtonNameEditText: EditText
           private lateinit var gptSuggestPromptSwitch: SwitchCompat

           // DeepSeek Suggest
           private lateinit var deepseekSuggestPromptEditText: EditText
           private lateinit var deepseekSuggestButtonNameEditText: EditText
           private lateinit var deepseekSuggestPromptSwitch: SwitchCompat

               // Text Format Button (Convert văn bản)
    private lateinit var askButtonPromptEditText: EditText
    private lateinit var askButtonNameEditText: EditText
    private lateinit var askButtonPromptSwitch: SwitchCompat

           // Olama Translate
           private lateinit var olamaTranslatePromptEditText: EditText
           private lateinit var olamaTranslateButtonNameEditText: EditText
           private lateinit var olamaTranslatePromptSwitch: SwitchCompat

           // GPT Translate
           private lateinit var gptTranslatePromptEditText: EditText
           private lateinit var gptTranslateButtonNameEditText: EditText
           private lateinit var gptTranslatePromptSwitch: SwitchCompat

           // GPT Spell Check
           private lateinit var gptSpellCheckPromptEditText: EditText
           private lateinit var gptSpellCheckButtonNameEditText: EditText
           private lateinit var gptSpellCheckPromptSwitch: SwitchCompat

           // DeepSeek Spell Check
           private lateinit var deepseekSpellCheckPromptEditText: EditText
           private lateinit var deepseekSpellCheckButtonNameEditText: EditText
           private lateinit var deepseekSpellCheckPromptSwitch: SwitchCompat
    
    // Buttons
    private lateinit var savePromptsButton: Button
    private lateinit var resetPromptsButton: Button
    
    private lateinit var languageManager: LanguageManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_prompt_customization_test)
            
            languageManager = LanguageManager(this)
            
            initViews()
            
            loadSavedPrompts()
            
            setupButtons()
            
        } catch (e: Exception) {
            // Critical error in onCreate
            finish()
        }
    }
    
    private fun initViews() {
        try {
            // AI Assistant
            aiAssistantPromptEditText = findViewById<EditText>(R.id.aiAssistantPromptEditText)
            aiAssistantButtonNameEditText = findViewById<EditText>(R.id.aiAssistantButtonNameEditText)
            aiAssistantPromptSwitch = findViewById<SwitchCompat>(R.id.aiAssistantPromptSwitch)
            
            // GPT Ask
            gptAskPromptEditText = findViewById<EditText>(R.id.gptAskPromptEditText)
            gptAskButtonNameEditText = findViewById<EditText>(R.id.gptAskButtonNameEditText)
            gptAskPromptSwitch = findViewById<SwitchCompat>(R.id.gptAskPromptSwitch)
            
            // DeepSeek Ask
            deepseekAskPromptEditText = findViewById<EditText>(R.id.deepseekAskPromptEditText)
            deepseekAskButtonNameEditText = findViewById<EditText>(R.id.deepseekAskButtonNameEditText)
            deepseekAskPromptSwitch = findViewById<SwitchCompat>(R.id.deepseekAskPromptSwitch)
            
            // Olama Ask
            olamaAskPromptEditText = findViewById<EditText>(R.id.olamaAskPromptEditText)
            olamaAskButtonNameEditText = findViewById<EditText>(R.id.olamaAskButtonNameEditText)
            olamaAskPromptSwitch = findViewById<SwitchCompat>(R.id.olamaAskPromptSwitch)
            
            // GPT Continue
            gptContinuePromptEditText = findViewById<EditText>(R.id.gptContinuePromptEditText)
            gptContinueButtonNameEditText = findViewById<EditText>(R.id.gptContinueButtonNameEditText)
            gptContinuePromptSwitch = findViewById<SwitchCompat>(R.id.gptContinuePromptSwitch)
            
            // DeepSeek Translate
            deepseekTranslatePromptEditText = findViewById<EditText>(R.id.deepseekTranslatePromptEditText)
            deepseekTranslateButtonNameEditText = findViewById<EditText>(R.id.deepseekTranslateButtonNameEditText)
            deepseekTranslatePromptSwitch = findViewById<SwitchCompat>(R.id.deepseekTranslatePromptSwitch)
            
            // GPT Suggest
            gptSuggestPromptEditText = findViewById<EditText>(R.id.gptSuggestPromptEditText)
            gptSuggestButtonNameEditText = findViewById<EditText>(R.id.gptSuggestButtonNameEditText)
            gptSuggestPromptSwitch = findViewById<SwitchCompat>(R.id.gptSuggestPromptSwitch)
            
            // DeepSeek Suggest
            deepseekSuggestPromptEditText = findViewById<EditText>(R.id.deepseekSuggestPromptEditText)
            deepseekSuggestButtonNameEditText = findViewById<EditText>(R.id.deepseekSuggestButtonNameEditText)
            deepseekSuggestPromptSwitch = findViewById<SwitchCompat>(R.id.deepseekSuggestPromptSwitch)
            
            // Text Format Button (Convert văn bản)
            askButtonPromptEditText = findViewById<EditText>(R.id.askButtonPromptEditText)
            askButtonNameEditText = findViewById<EditText>(R.id.askButtonNameEditText)
            askButtonPromptSwitch = findViewById<SwitchCompat>(R.id.askButtonPromptSwitch)
            
            // Olama Translate
            olamaTranslatePromptEditText = findViewById<EditText>(R.id.olamaTranslatePromptEditText)
            olamaTranslateButtonNameEditText = findViewById<EditText>(R.id.olamaTranslateButtonNameEditText)
            olamaTranslatePromptSwitch = findViewById<SwitchCompat>(R.id.olamaTranslatePromptSwitch)
            
            // GPT Translate
            gptTranslatePromptEditText = findViewById<EditText>(R.id.gptTranslatePromptEditText)
            gptTranslateButtonNameEditText = findViewById<EditText>(R.id.gptTranslateButtonNameEditText)
            gptTranslatePromptSwitch = findViewById<SwitchCompat>(R.id.gptTranslatePromptSwitch)
            
            // GPT Spell Check
            gptSpellCheckPromptEditText = findViewById<EditText>(R.id.gptSpellCheckPromptEditText)
            gptSpellCheckButtonNameEditText = findViewById<EditText>(R.id.gptSpellCheckButtonNameEditText)
            gptSpellCheckPromptSwitch = findViewById<SwitchCompat>(R.id.gptSpellCheckPromptSwitch)
            
            // DeepSeek Spell Check
            deepseekSpellCheckPromptEditText = findViewById<EditText>(R.id.deepseekSpellCheckPromptEditText)
            deepseekSpellCheckButtonNameEditText = findViewById<EditText>(R.id.deepseekSpellCheckButtonNameEditText)
            deepseekSpellCheckPromptSwitch = findViewById<SwitchCompat>(R.id.deepseekSpellCheckPromptSwitch)
            
            // Buttons
            savePromptsButton = findViewById<Button>(R.id.savePromptsButton)
            resetPromptsButton = findViewById<Button>(R.id.resetPromptsButton)
            
        } catch (e: Exception) {
            // Error in initViews
            throw e
        }
    }
    
    private fun loadSavedPrompts() {
        try {
            val prefs = getSharedPreferences("AIKeyboardPrefs", MODE_PRIVATE)
            
            // Load AI Assistant
            val aiAssistantPrompt = prefs.getString("prompt_ai_assistant", getDefaultAIAssistantPrompt())
            aiAssistantPromptEditText.setText(aiAssistantPrompt)
            val aiAssistantButtonName = prefs.getString("button_name_ai_assistant", getDefaultAIAssistantButtonName())
            aiAssistantButtonNameEditText.setText(aiAssistantButtonName)
            val aiAssistantPromptEnabled = prefs.getBoolean("prompt_enabled_ai_assistant", false)
            aiAssistantPromptSwitch.isChecked = aiAssistantPromptEnabled
            
            // Load GPT Ask
            val gptAskPrompt = prefs.getString("prompt_gpt_ask_button_text", getDefaultGPTAskPrompt())
            gptAskPromptEditText.setText(gptAskPrompt)
            val gptAskButtonName = prefs.getString("button_name_gpt_ask_button_text", getDefaultGPTAskButtonName())
            gptAskButtonNameEditText.setText(gptAskButtonName)
            val gptAskPromptEnabled = prefs.getBoolean("prompt_enabled_gpt_ask_button_text", false)
            gptAskPromptSwitch.isChecked = gptAskPromptEnabled
            
            // Load DeepSeek Ask
            val deepseekAskPrompt = prefs.getString("prompt_deepseek_ask", getDefaultDeepSeekAskPrompt())
            deepseekAskPromptEditText.setText(deepseekAskPrompt)
            val deepseekAskButtonName = prefs.getString("button_name_deepseek_ask", getDefaultDeepSeekAskButtonName())
            deepseekAskButtonNameEditText.setText(deepseekAskButtonName)
            val deepseekAskPromptEnabled = prefs.getBoolean("prompt_enabled_deepseek_ask", false)
            deepseekAskPromptSwitch.isChecked = deepseekAskPromptEnabled
            
            // Load Olama Ask
            val olamaAskPrompt = prefs.getString("prompt_olama_ask", getDefaultOlamaAskPrompt())
            olamaAskPromptEditText.setText(olamaAskPrompt)
            val olamaAskButtonName = prefs.getString("button_name_olama_ask", getDefaultOlamaAskButtonName())
            olamaAskButtonNameEditText.setText(olamaAskButtonName)
            val olamaAskPromptEnabled = prefs.getBoolean("prompt_enabled_olama_ask", false)
            olamaAskPromptSwitch.isChecked = olamaAskPromptEnabled
            
            // Load GPT Continue
            val gptContinuePrompt = prefs.getString("prompt_gpt_continue", getDefaultGPTContinuePrompt())
            gptContinuePromptEditText.setText(gptContinuePrompt)
            val gptContinueButtonName = prefs.getString("button_name_gpt_continue", getDefaultGPTContinueButtonName())
            gptContinueButtonNameEditText.setText(gptContinueButtonName)
            val gptContinuePromptEnabled = prefs.getBoolean("prompt_enabled_gpt_continue", false)
            gptContinuePromptSwitch.isChecked = gptContinuePromptEnabled
            
            // Load DeepSeek Translate
            val deepseekTranslatePrompt = prefs.getString("prompt_deepseek_translate", getDefaultDeepSeekTranslatePrompt())
            deepseekTranslatePromptEditText.setText(deepseekTranslatePrompt)
            val deepseekTranslateButtonName = prefs.getString("button_name_deepseek_translate", getDefaultDeepSeekTranslateButtonName())
            deepseekTranslateButtonNameEditText.setText(deepseekTranslateButtonName)
            val deepseekTranslatePromptEnabled = prefs.getBoolean("prompt_enabled_deepseek_translate", false)
            deepseekTranslatePromptSwitch.isChecked = deepseekTranslatePromptEnabled
            
            // Load GPT Suggest
            val gptSuggestPrompt = prefs.getString("prompt_gpt_suggest", getDefaultGPTSuggestPrompt())
            gptSuggestPromptEditText.setText(gptSuggestPrompt)
            val gptSuggestButtonName = prefs.getString("button_name_gpt_suggest", getDefaultGPTSuggestButtonName())
            gptSuggestButtonNameEditText.setText(gptSuggestButtonName)
            val gptSuggestPromptEnabled = prefs.getBoolean("prompt_enabled_gpt_suggest", false)
            gptSuggestPromptSwitch.isChecked = gptSuggestPromptEnabled
            
            // Load DeepSeek Suggest
            val deepseekSuggestPrompt = prefs.getString("prompt_deepseek_suggest", getDefaultDeepSeekSuggestPrompt())
            deepseekSuggestPromptEditText.setText(deepseekSuggestPrompt)
            val deepseekSuggestButtonName = prefs.getString("button_name_deepseek_suggest", getDefaultDeepSeekSuggestButtonName())
            deepseekSuggestButtonNameEditText.setText(deepseekSuggestButtonName)
            val deepseekSuggestPromptEnabled = prefs.getBoolean("prompt_enabled_deepseek_suggest", false)
            deepseekSuggestPromptSwitch.isChecked = deepseekSuggestPromptEnabled
            
            // Load Ask Button
            val askButtonPrompt = prefs.getString("prompt_ask_button", getDefaultAskButtonPrompt())
            askButtonPromptEditText.setText(askButtonPrompt)
            val askButtonName = prefs.getString("button_name_ask_button", getDefaultAskButtonName())
            askButtonNameEditText.setText(askButtonName)
            val askButtonPromptEnabled = prefs.getBoolean("prompt_enabled_ask_button", false)
            askButtonPromptSwitch.isChecked = askButtonPromptEnabled
            
            // Load Olama Translate
            val olamaTranslatePrompt = prefs.getString("prompt_olama_translate", getDefaultOlamaTranslatePrompt())
            olamaTranslatePromptEditText.setText(olamaTranslatePrompt)
            val olamaTranslateButtonName = prefs.getString("button_name_olama_translate", getDefaultOlamaTranslateButtonName())
            olamaTranslateButtonNameEditText.setText(olamaTranslateButtonName)
            val olamaTranslatePromptEnabled = prefs.getBoolean("prompt_enabled_olama_translate", false)
            olamaTranslatePromptSwitch.isChecked = olamaTranslatePromptEnabled
            
            // Load GPT Translate
            val gptTranslatePrompt = prefs.getString("prompt_gpt_translate", getDefaultGPTTranslatePrompt())
            gptTranslatePromptEditText.setText(gptTranslatePrompt)
            val gptTranslateButtonName = prefs.getString("button_name_gpt_translate", getDefaultGPTTranslateButtonName())
            gptTranslateButtonNameEditText.setText(gptTranslateButtonName)
            val gptTranslatePromptEnabled = prefs.getBoolean("prompt_enabled_gpt_translate", false)
            gptTranslatePromptSwitch.isChecked = gptTranslatePromptEnabled
            
            // Load GPT Spell Check
            val gptSpellCheckPrompt = prefs.getString("prompt_gpt_spell_check", getDefaultGPTSpellCheckPrompt())
            gptSpellCheckPromptEditText.setText(gptSpellCheckPrompt)
            val gptSpellCheckButtonName = prefs.getString("button_name_gpt_spell_check", getDefaultGPTSpellCheckButtonName())
            gptSpellCheckButtonNameEditText.setText(gptSpellCheckButtonName)
            val gptSpellCheckPromptEnabled = prefs.getBoolean("prompt_enabled_gpt_spell_check", false)
            gptSpellCheckPromptSwitch.isChecked = gptSpellCheckPromptEnabled
            
            // Load DeepSeek Spell Check
            val deepseekSpellCheckPrompt = prefs.getString("prompt_deepseek_spell_check", getDefaultDeepSeekSpellCheckPrompt())
            deepseekSpellCheckPromptEditText.setText(deepseekSpellCheckPrompt)
            val deepseekSpellCheckButtonName = prefs.getString("button_name_deepseek_spell_check", getDefaultDeepSeekSpellCheckButtonName())
            deepseekSpellCheckButtonNameEditText.setText(deepseekSpellCheckButtonName)
            val deepseekSpellCheckPromptEnabled = prefs.getBoolean("prompt_enabled_deepseek_spell_check", false)
            deepseekSpellCheckPromptSwitch.isChecked = deepseekSpellCheckPromptEnabled
            
        } catch (e: Exception) {
            // Error in loadSavedPrompts
            throw e
        }
    }
    
    private fun setupButtons() {
        try {
            savePromptsButton.setOnClickListener {
                savePrompts()
            }
            
            resetPromptsButton.setOnClickListener {
                resetToDefaults()
            }
            
        } catch (e: Exception) {
            // Error in setupButtons
            throw e
        }
    }
    
    private fun savePrompts() {
        try {
            val prefs = getSharedPreferences("AIKeyboardPrefs", MODE_PRIVATE)
            val editor = prefs.edit()
            
            // Save AI Assistant
            editor.putString("prompt_ai_assistant", aiAssistantPromptEditText.text.toString())
            editor.putString("button_name_ai_assistant", aiAssistantButtonNameEditText.text.toString())
            editor.putBoolean("prompt_enabled_ai_assistant", aiAssistantPromptSwitch.isChecked)
            
            // Save GPT Ask
            editor.putString("prompt_gpt_ask_button_text", gptAskPromptEditText.text.toString())
            editor.putString("button_name_gpt_ask_button_text", gptAskButtonNameEditText.text.toString())
            editor.putBoolean("prompt_enabled_gpt_ask_button_text", gptAskPromptSwitch.isChecked)
            
            // Save DeepSeek Ask
            editor.putString("prompt_deepseek_ask", deepseekAskPromptEditText.text.toString())
            editor.putString("button_name_deepseek_ask", deepseekAskButtonNameEditText.text.toString())
            editor.putBoolean("prompt_enabled_deepseek_ask", deepseekAskPromptSwitch.isChecked)
            
            // Save Olama Ask
            editor.putString("prompt_olama_ask", olamaAskPromptEditText.text.toString())
            editor.putString("button_name_olama_ask", olamaAskButtonNameEditText.text.toString())
            editor.putBoolean("prompt_enabled_olama_ask", olamaAskPromptSwitch.isChecked)
            
            // Save GPT Continue
            editor.putString("prompt_gpt_continue", gptContinuePromptEditText.text.toString())
            editor.putString("button_name_gpt_continue", gptContinueButtonNameEditText.text.toString())
            editor.putBoolean("prompt_enabled_gpt_continue", gptContinuePromptSwitch.isChecked)
            
            // Save DeepSeek Translate
            editor.putString("prompt_deepseek_translate", deepseekTranslatePromptEditText.text.toString())
            editor.putString("button_name_deepseek_translate", deepseekTranslateButtonNameEditText.text.toString())
            editor.putBoolean("prompt_enabled_deepseek_translate", deepseekTranslatePromptSwitch.isChecked)
            
            // Save GPT Suggest
            editor.putString("prompt_gpt_suggest", gptSuggestPromptEditText.text.toString())
            editor.putString("button_name_gpt_suggest", gptSuggestButtonNameEditText.text.toString())
            editor.putBoolean("prompt_enabled_gpt_suggest", gptSuggestPromptSwitch.isChecked)
            
            // Save DeepSeek Suggest
            editor.putString("prompt_deepseek_suggest", deepseekSuggestPromptEditText.text.toString())
            editor.putString("button_name_deepseek_suggest", deepseekSuggestButtonNameEditText.text.toString())
            editor.putBoolean("prompt_enabled_deepseek_suggest", deepseekSuggestPromptSwitch.isChecked)
            
            // Save Ask Button
            editor.putString("prompt_ask_button", askButtonPromptEditText.text.toString())
            editor.putString("button_name_ask_button", askButtonNameEditText.text.toString())
            editor.putBoolean("prompt_enabled_ask_button", askButtonPromptSwitch.isChecked)
            
            // Save Olama Translate
            editor.putString("prompt_olama_translate", olamaTranslatePromptEditText.text.toString())
            editor.putString("button_name_olama_translate", olamaTranslateButtonNameEditText.text.toString())
            editor.putBoolean("prompt_enabled_olama_translate", olamaTranslatePromptSwitch.isChecked)
            
            // Save GPT Translate
            editor.putString("prompt_gpt_translate", gptTranslatePromptEditText.text.toString())
            editor.putString("button_name_gpt_translate", gptTranslateButtonNameEditText.text.toString())
            editor.putBoolean("prompt_enabled_gpt_translate", gptTranslatePromptSwitch.isChecked)
            
            // Save GPT Spell Check
            editor.putString("prompt_gpt_spell_check", gptSpellCheckPromptEditText.text.toString())
            editor.putString("button_name_gpt_spell_check", gptSpellCheckButtonNameEditText.text.toString())
            editor.putBoolean("prompt_enabled_gpt_spell_check", gptSpellCheckPromptSwitch.isChecked)
            
            // Save DeepSeek Spell Check
            editor.putString("prompt_deepseek_spell_check", deepseekSpellCheckPromptEditText.text.toString())
            editor.putString("button_name_deepseek_spell_check", deepseekSpellCheckButtonNameEditText.text.toString())
            editor.putBoolean("prompt_enabled_deepseek_spell_check", deepseekSpellCheckPromptSwitch.isChecked)
            
            editor.apply()
            
            Toast.makeText(this, "Đã lưu prompts thành công!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Error in savePrompts
            Toast.makeText(this, "Lỗi lưu prompts: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun resetToDefaults() {
        try {
            // Reset AI Assistant
            aiAssistantPromptEditText.setText(getDefaultAIAssistantPrompt())
            aiAssistantButtonNameEditText.setText(getDefaultAIAssistantButtonName())
            aiAssistantPromptSwitch.isChecked = false
            
            // Reset GPT Ask
            gptAskPromptEditText.setText(getDefaultGPTAskPrompt())
            gptAskButtonNameEditText.setText(getDefaultGPTAskButtonName())
            gptAskPromptSwitch.isChecked = false
            
            // Reset DeepSeek Ask
            deepseekAskPromptEditText.setText(getDefaultDeepSeekAskPrompt())
            deepseekAskButtonNameEditText.setText(getDefaultDeepSeekAskButtonName())
            deepseekAskPromptSwitch.isChecked = false
            
            // Reset Olama Ask
            olamaAskPromptEditText.setText(getDefaultOlamaAskPrompt())
            olamaAskButtonNameEditText.setText(getDefaultOlamaAskButtonName())
            olamaAskPromptSwitch.isChecked = false
            
            // Reset GPT Continue
            gptContinuePromptEditText.setText(getDefaultGPTContinuePrompt())
            gptContinueButtonNameEditText.setText(getDefaultGPTContinueButtonName())
            gptContinuePromptSwitch.isChecked = false
            
            // Reset DeepSeek Translate
            deepseekTranslatePromptEditText.setText(getDefaultDeepSeekTranslatePrompt())
            deepseekTranslateButtonNameEditText.setText(getDefaultDeepSeekTranslateButtonName())
            deepseekTranslatePromptSwitch.isChecked = false
            
            // Reset GPT Suggest
            gptSuggestPromptEditText.setText(getDefaultGPTSuggestPrompt())
            gptSuggestButtonNameEditText.setText(getDefaultGPTSuggestButtonName())
            gptSuggestPromptSwitch.isChecked = false
            
            // Reset DeepSeek Suggest
            deepseekSuggestPromptEditText.setText(getDefaultDeepSeekSuggestPrompt())
            deepseekSuggestButtonNameEditText.setText(getDefaultDeepSeekSuggestButtonName())
            deepseekSuggestPromptSwitch.isChecked = false
            
            // Reset Ask Button
            askButtonPromptEditText.setText(getDefaultAskButtonPrompt())
            askButtonNameEditText.setText(getDefaultAskButtonName())
            askButtonPromptSwitch.isChecked = false
            
            // Reset Olama Translate
            olamaTranslatePromptEditText.setText(getDefaultOlamaTranslatePrompt())
            olamaTranslateButtonNameEditText.setText(getDefaultOlamaTranslateButtonName())
            olamaTranslatePromptSwitch.isChecked = false
            
            // Reset GPT Translate
            gptTranslatePromptEditText.setText(getDefaultGPTTranslatePrompt())
            gptTranslateButtonNameEditText.setText(getDefaultGPTTranslateButtonName())
            gptTranslatePromptSwitch.isChecked = false
            
            // Reset GPT Spell Check
            gptSpellCheckPromptEditText.setText(getDefaultGPTSpellCheckPrompt())
            gptSpellCheckButtonNameEditText.setText(getDefaultGPTSpellCheckButtonName())
            gptSpellCheckPromptSwitch.isChecked = false
            
            // Reset DeepSeek Spell Check
            deepseekSpellCheckPromptEditText.setText(getDefaultDeepSeekSpellCheckPrompt())
            deepseekSpellCheckButtonNameEditText.setText(getDefaultDeepSeekSpellCheckButtonName())
            deepseekSpellCheckPromptSwitch.isChecked = false
            
            Toast.makeText(this, "Đã khôi phục mặc định!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Error in resetToDefaults
            Toast.makeText(this, "Lỗi khôi phục mặc định: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    // Default prompts
    private fun getDefaultAIAssistantPrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một trợ lý AI thông minh và hữu ích. Hãy trả lời câu hỏi của người dùng một cách ngắn gọn, chính xác và dễ hiểu. Nếu câu hỏi không rõ ràng, hãy yêu cầu làm rõ thêm."
            Language.ENGLISH -> "You are a smart and helpful AI assistant. Please answer the user's question concisely, accurately and understandably. If the question is unclear, please ask for clarification."
            else -> "You are a smart and helpful AI assistant. Please answer the user's question concisely, accurately and understandably. If the question is unclear, please ask for clarification."
        }
    }
    
    private fun getDefaultGPTAskPrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI thông minh được phát triển bởi OpenAI. Hãy trả lời câu hỏi của người dùng một cách chính xác, hữu ích và an toàn."
            Language.ENGLISH -> "You are an intelligent AI developed by OpenAI. Please answer the user's question accurately, helpfully and safely."
            else -> "You are an intelligent AI developed by OpenAI. Please answer the user's question accurately, helpfully and safely."
        }
    }
    
    private fun getDefaultDeepSeekAskPrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là DeepSeek AI, một AI thông minh và hiểu biết sâu rộng. Hãy trả lời câu hỏi của người dùng một cách chi tiết và chính xác."
            Language.ENGLISH -> "You are DeepSeek AI, an intelligent and knowledgeable AI. Please answer the user's question in detail and accurately."
            else -> "You are DeepSeek AI, an intelligent and knowledgeable AI. Please answer the user's question in detail and accurately."
        }
    }
    
    private fun getDefaultOlamaAskPrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI chạy trên Olama. Hãy trả lời câu hỏi của người dùng một cách hữu ích và chính xác."
            Language.ENGLISH -> "You are an AI running on Olama. Please answer the user's question helpfully and accurately."
            else -> "You are an AI running on Olama. Please answer the user's question helpfully and accurately."
        }
    }

    private fun getDefaultAskButtonPrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI chuyển đổi văn bản. Chuyển đổi văn bản được cung cấp sang kiểu chữ 𝒃𝒐𝒍𝒅 𝒊𝒕𝒂𝒍𝒊𝒄. Chỉ trả về văn bản đã chuyển đổi mà không có giải thích hoặc ngữ cảnh bổ sung."
            Language.ENGLISH -> "You are a text converter. Convert the provided text to 𝒃𝒐𝒍𝒅 𝒊𝒕𝒂𝒍𝒊𝒄 font style. Only output the converted text without any additional explanation or context."
            else -> "You are a text converter. Convert the provided text to 𝒃𝒐𝒍𝒅 𝒊𝒕𝒂𝒍𝒊𝒄 font style. Only output the converted text without any additional explanation or context."
        }
    }
    
    private fun getDefaultSpellCheckPrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI kiểm tra chính tả. Hãy kiểm tra và sửa lỗi chính tả trong văn bản được cung cấp. Chỉ trả về văn bản đã được sửa lỗi."
            Language.ENGLISH -> "You are a spell-checking AI. Please check and correct spelling errors in the provided text. Return only the corrected text."
            else -> "You are a spell-checking AI. Please check and correct spelling errors in the provided text. Return only the corrected text."
        }
    }
    
    private fun getDefaultGrammarCheckPrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI kiểm tra ngữ pháp. Hãy kiểm tra và sửa lỗi ngữ pháp trong văn bản được cung cấp. Chỉ trả về văn bản đã được sửa lỗi."
            Language.ENGLISH -> "You are a grammar-checking AI. Please check and correct grammar errors in the provided text. Return only the corrected text."
            else -> "You are a grammar-checking AI. Please check and correct grammar errors in the provided text. Return only the corrected text."
        }
    }
    
    private fun getDefaultTranslatePrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI dịch thuật. Hãy dịch văn bản được cung cấp sang tiếng Anh. Chỉ trả về bản dịch."
            Language.ENGLISH -> "You are a translation AI. Please translate the provided text to Vietnamese. Return only the translation."
            else -> "You are a translation AI. Please translate the provided text to Vietnamese. Return only the translation."
        }
    }
    
    private fun getDefaultSummarizePrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI tóm tắt. Hãy tóm tắt văn bản được cung cấp một cách ngắn gọn và chính xác. Chỉ trả về bản tóm tắt."
            Language.ENGLISH -> "You are a summarization AI. Please summarize the provided text concisely and accurately. Return only the summary."
            else -> "You are a summarization AI. Please summarize the provided text concisely and accurately. Return only the summary."
        }
    }
    
    private fun getDefaultRephrasePrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI viết lại. Hãy viết lại văn bản được cung cấp với cách diễn đạt khác nhưng giữ nguyên ý nghĩa. Chỉ trả về văn bản đã viết lại."
            Language.ENGLISH -> "You are a rephrasing AI. Please rephrase the provided text with different wording while maintaining the same meaning. Return only the rephrased text."
            else -> "You are a rephrasing AI. Please rephrase the provided text with different wording while maintaining the same meaning. Return only the rephrased text."
        }
    }
    
    private fun getDefaultExpandPrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI mở rộng. Hãy mở rộng văn bản được cung cấp với thêm chi tiết và thông tin bổ sung. Chỉ trả về văn bản đã mở rộng."
            Language.ENGLISH -> "You are an expansion AI. Please expand the provided text with additional details and information. Return only the expanded text."
            else -> "You are an expansion AI. Please expand the provided text with additional details and information. Return only the expanded text."
        }
    }
    
    private fun getDefaultFormalPrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI chuyển đổi văn phong. Hãy chuyển đổi văn bản được cung cấp sang văn phong trang trọng và chuyên nghiệp. Chỉ trả về văn bản đã chuyển đổi."
            Language.ENGLISH -> "You are a formal writing AI. Please convert the provided text to formal and professional writing style. Return only the converted text."
            else -> "You are a formal writing AI. Please convert the provided text to formal and professional writing style. Return only the converted text."
        }
    }
    
    private fun getDefaultCasualPrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI chuyển đổi văn phong. Hãy chuyển đổi văn bản được cung cấp sang văn phong thân thiện và không trang trọng. Chỉ trả về văn bản đã chuyển đổi."
            Language.ENGLISH -> "You are a casual writing AI. Please convert the provided text to friendly and informal writing style. Return only the converted text."
            else -> "You are a casual writing AI. Please convert the provided text to friendly and informal writing style. Return only the converted text."
        }
    }

    // Default prompts for new buttons
    private fun getDefaultGPTContinuePrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI tiếp tục văn bản. Hãy tiếp tục văn bản được cung cấp một cách tự nhiên và mạch lạc. Chỉ trả về phần tiếp tục."
            Language.ENGLISH -> "You are a text continuation AI. Please continue the provided text naturally and coherently. Return only the continuation."
            else -> "You are a text continuation AI. Please continue the provided text naturally and coherently. Return only the continuation."
        }
    }

    private fun getDefaultDeepSeekTranslatePrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI dịch thuật. Hãy dịch văn bản được cung cấp sang ngôn ngữ mục tiêu. Chỉ trả về bản dịch."
            Language.ENGLISH -> "You are a translation AI. Please translate the provided text to the target language. Return only the translation."
            else -> "You are a translation AI. Please translate the provided text to the target language. Return only the translation."
        }
    }

    private fun getDefaultGPTSuggestPrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI gợi ý. Hãy đưa ra các gợi ý hữu ích dựa trên văn bản được cung cấp. Chỉ trả về các gợi ý."
            Language.ENGLISH -> "You are a suggestion AI. Please provide helpful suggestions based on the provided text. Return only the suggestions."
            else -> "You are a suggestion AI. Please provide helpful suggestions based on the provided text. Return only the suggestions."
        }
    }

    private fun getDefaultDeepSeekSuggestPrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI gợi ý. Hãy đưa ra các gợi ý hữu ích dựa trên văn bản được cung cấp. Chỉ trả về các gợi ý."
            Language.ENGLISH -> "You are a suggestion AI. Please provide helpful suggestions based on the provided text. Return only the suggestions."
            else -> "You are a suggestion AI. Please provide helpful suggestions based on the provided text. Return only the suggestions."
        }
    }



    private fun getDefaultOlamaTranslatePrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI dịch thuật. Hãy dịch văn bản được cung cấp sang ngôn ngữ mục tiêu. Chỉ trả về bản dịch."
            Language.ENGLISH -> "You are a translation AI. Please translate the provided text to the target language. Return only the translation."
            else -> "You are a translation AI. Please translate the provided text to the target language. Return only the translation."
        }
    }

    private fun getDefaultGPTTranslatePrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI dịch thuật. Hãy dịch văn bản được cung cấp sang ngôn ngữ mục tiêu. Chỉ trả về bản dịch."
            Language.ENGLISH -> "You are a translation AI. Please translate the provided text to the target language. Return only the translation."
            else -> "You are a translation AI. Please translate the provided text to the target language. Return only the translation."
        }
    }

    private fun getDefaultGPTSpellCheckPrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI kiểm tra chính tả. Hãy kiểm tra và sửa lỗi chính tả trong văn bản được cung cấp. Chỉ trả về văn bản đã sửa."
            Language.ENGLISH -> "You are a spell check AI. Please check and correct spelling errors in the provided text. Return only the corrected text."
            else -> "You are a spell check AI. Please check and correct spelling errors in the provided text. Return only the corrected text."
        }
    }

    private fun getDefaultDeepSeekSpellCheckPrompt(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI kiểm tra chính tả. Hãy kiểm tra và sửa lỗi chính tả trong văn bản được cung cấp. Chỉ trả về văn bản đã sửa."
            Language.ENGLISH -> "You are a spell check AI. Please check and correct spelling errors in the provided text. Return only the corrected text."
            else -> "You are a spell check AI. Please check and correct spelling errors in the provided text. Return only the corrected text."
        }
    }
    
    // Default button names - khớp chính xác với smartbar
    private fun getDefaultAIAssistantButtonName(): String {
        return getString(R.string.assistants_gpt_button)
    }
    
    private fun getDefaultGPTAskButtonName(): String {
        return getString(R.string.gpt_ask_button_text)
    }
    
    private fun getDefaultDeepSeekAskButtonName(): String {
        return getString(R.string.ask_deepseek_button)
    }
    
    private fun getDefaultOlamaAskButtonName(): String {
        return getString(R.string.olama_ask_button)
    }

    private fun getDefaultAskButtonName(): String {
        return getString(R.string.ask_button_text) // "Convert văn bản"
    }
    
    private fun getDefaultGPTContinueButtonName(): String {
        return getString(R.string.gpt_continue_button)
    }
    
    private fun getDefaultDeepSeekTranslateButtonName(): String {
        return getString(R.string.deepseek_translate_button)
    }
    
    private fun getDefaultGPTSuggestButtonName(): String {
        return getString(R.string.gpt_suggest_button)
    }
    
    private fun getDefaultDeepSeekSuggestButtonName(): String {
        return getString(R.string.deepseek_suggest_button)
    }
    
    private fun getDefaultOlamaTranslateButtonName(): String {
        return getString(R.string.olama_translate_button)
    }
    
    private fun getDefaultGPTTranslateButtonName(): String {
        return getString(R.string.gpt_translate_button_text)
    }
    
    private fun getDefaultGPTSpellCheckButtonName(): String {
        return getString(R.string.gpt_spell_check_button)
    }
    
    private fun getDefaultDeepSeekSpellCheckButtonName(): String {
        return getString(R.string.deepseek_spell_check_button)
    }
    
        companion object {
        fun getPrompt(context: android.content.Context, promptType: String): String {
            val prefs = context.getSharedPreferences("AIKeyboardPrefs", android.content.Context.MODE_PRIVATE)
            val isEnabled = prefs.getBoolean("prompt_enabled_$promptType", false)
            if (!isEnabled) return "" // Return empty if disabled
            
            return when (promptType) {
                "ai_assistant" -> prefs.getString("prompt_ai_assistant", "") ?: ""
                "gpt_ask_button_text" -> prefs.getString("prompt_gpt_ask_button_text", "") ?: ""
                "deepseek_ask" -> prefs.getString("prompt_deepseek_ask", "") ?: ""
                "olama_ask" -> prefs.getString("prompt_olama_ask", "") ?: ""
                "gpt_continue" -> prefs.getString("prompt_gpt_continue", "") ?: ""
                "deepseek_translate" -> prefs.getString("prompt_deepseek_translate", "") ?: ""
                "gpt_suggest" -> prefs.getString("prompt_gpt_suggest", "") ?: ""
                "deepseek_suggest" -> prefs.getString("prompt_deepseek_suggest", "") ?: ""
                "ask_button" -> prefs.getString("prompt_ask_button", "") ?: ""
                "olama_translate" -> prefs.getString("prompt_olama_translate", "") ?: ""
                "gpt_translate" -> prefs.getString("prompt_gpt_translate", "") ?: ""
                "gpt_spell_check" -> prefs.getString("prompt_gpt_spell_check", "") ?: ""
                "deepseek_spell_check" -> prefs.getString("prompt_deepseek_spell_check", "") ?: ""
                else -> ""
            }
        }

        fun getButtonName(context: android.content.Context, buttonType: String): String {
            val prefs = context.getSharedPreferences("AIKeyboardPrefs", android.content.Context.MODE_PRIVATE)
            return when (buttonType) {
                "ai_assistant" -> prefs.getString("button_name_ai_assistant", "AI Assistant") ?: "AI Assistant"
                "gpt_ask_button_text" -> prefs.getString("button_name_gpt_ask_button_text", "GPT Ask") ?: "GPT Ask"
                "deepseek_ask" -> prefs.getString("button_name_deepseek_ask", "DeepSeek Ask") ?: "DeepSeek Ask"
                "olama_ask" -> prefs.getString("button_name_olama_ask", "Olama Ask") ?: "Olama Ask"
                "gpt_continue" -> prefs.getString("button_name_gpt_continue", "GPT Continue") ?: "GPT Continue"
                "deepseek_translate" -> prefs.getString("button_name_deepseek_translate", "DeepSeek Translate") ?: "DeepSeek Translate"
                "gpt_suggest" -> prefs.getString("button_name_gpt_suggest", "GPT Suggest") ?: "GPT Suggest"
                "deepseek_suggest" -> prefs.getString("button_name_deepseek_suggest", "DeepSeek Suggest") ?: "DeepSeek Suggest"
                "ask_button" -> prefs.getString("button_name_ask_button", "Convert văn bản") ?: "Convert văn bản"
                "olama_translate" -> prefs.getString("button_name_olama_translate", "Olama Translate") ?: "Olama Translate"
                "gpt_translate" -> prefs.getString("button_name_gpt_translate", "GPT Translate") ?: "GPT Translate"
                "gpt_spell_check" -> prefs.getString("button_name_gpt_spell_check", "GPT Spell Check") ?: "GPT Spell Check"
                "deepseek_spell_check" -> prefs.getString("button_name_deepseek_spell_check", "DeepSeek Spell Check") ?: "DeepSeek Spell Check"
                else -> ""
            }
        }
    }
} 