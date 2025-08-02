package com.example.aikeyboard.text

import com.example.aikeyboard.text.composing.Composer
import com.example.aikeyboard.models.Language

object ComposerFactory {
    
    private val composers = mapOf(
        Language.VIETNAMESE to TelexComposer(),
        Language.ENGLISH to TelexComposer(), // English uses default
        Language.CHINESE to PinyinComposer(),
        Language.JAPANESE to JapaneseComposer(),
        Language.KOREAN to KoreanComposer(),
        Language.FRENCH to FrenchComposer(),
        Language.GERMAN to GermanComposer(),
        Language.SPANISH to SpanishComposer(),
        Language.ITALIAN to ItalianComposer(),
        Language.RUSSIAN to RussianComposer(),
        Language.ARABIC to ArabicComposer(),
        Language.THAI to ThaiComposer(),
        Language.HINDI to HindiComposer(),
    )
    
    fun getComposer(language: Language): Composer {
        return composers[language] ?: TelexComposer() // Default to Telex
    }
    
    fun getComposer(languageCode: String): Composer {
        val language = Language.fromCode(languageCode)
        return getComposer(language)
    }
    
    fun getAvailableComposers(): List<Composer> {
        return composers.values.toList()
    }
    
    fun getComposerById(id: String): Composer? {
        return composers.values.find { it.id == id }
    }
} 