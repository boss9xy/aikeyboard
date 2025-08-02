package com.example.aikeyboard

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import com.example.aikeyboard.models.Language
import java.util.*

class LanguageManager(private val context: Context) {
    
    companion object {
        private const val LANGUAGE_PREF = "app_language"
        private const val DEFAULT_LANGUAGE = "vi" // Vietnamese as default
    }
    
    private val sharedPreferences = context.getSharedPreferences("AIKeyboardPrefs", Context.MODE_PRIVATE)
    
    fun getCurrentLanguage(): Language {
        val languageCode = sharedPreferences.getString(LANGUAGE_PREF, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        return Language.fromCode(languageCode)
    }
    
    fun setLanguage(language: Language) {
        sharedPreferences.edit().putString(LANGUAGE_PREF, language.code).apply()
        updateResources(language.code)
    }
    
    fun setLanguageByCode(languageCode: String) {
        val language = Language.fromCode(languageCode)
        setLanguage(language)
    }
    
        private fun updateResources(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.createConfigurationContext(config)
        
        // Force update resources
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
    
    fun getLocalizedString(resourceId: Int): String {
        val currentLanguage = getCurrentLanguage()
        val resources = getResourcesForLanguage(currentLanguage.code)
        return resources.getString(resourceId)
    }
    
    private fun getResourcesForLanguage(languageCode: String): Resources {
        val locale = Locale(languageCode)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config).resources
    }
    
    fun getAllLanguages(): List<Language> {
        return Language.values().toList()
    }
    
    fun getLanguageDisplayName(language: Language): String {
        return when (getCurrentLanguage()) {
            Language.VIETNAMESE -> language.nativeName
            Language.CHINESE -> language.nativeName
            Language.JAPANESE -> language.nativeName
            Language.KOREAN -> language.nativeName
            Language.ARABIC -> language.nativeName
            Language.THAI -> language.nativeName
            Language.HINDI -> language.nativeName
            else -> language.englishName
        }
    }
} 