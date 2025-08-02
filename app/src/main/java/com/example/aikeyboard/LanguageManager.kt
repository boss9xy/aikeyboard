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
        private const val DISPLAY_LANGUAGE_PREF = "display_language"
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
    
    fun setDisplayLanguage(language: Language) {
        sharedPreferences.edit().putString(DISPLAY_LANGUAGE_PREF, language.code).apply()
    }
    
    fun getCurrentDisplayLanguage(): Language {
        val languageCode = sharedPreferences.getString(DISPLAY_LANGUAGE_PREF, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        return Language.fromCode(languageCode)
    }
    
    fun getAllDisplayLanguages(): List<Language> {
        return Language.values().toList()
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
        return getAvailableLanguages()
    }

    fun getAvailableLanguages(): List<Language> {
        return Language.values().toList()
    }

    fun getLanguageDisplayName(language: Language): String {
        return when (language) {
            Language.VIETNAMESE -> language.nativeName
            Language.ENGLISH -> language.englishName
            else -> language.englishName
        }
    }
} 