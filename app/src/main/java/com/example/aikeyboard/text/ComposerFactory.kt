package com.example.aikeyboard.text

import com.example.aikeyboard.text.composing.Composer
import com.example.aikeyboard.models.Language

object ComposerFactory {
    
    private val composers = mapOf(
        Language.VIETNAMESE to TelexComposer(),
        Language.ENGLISH to TelexComposer(), // English uses default
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