package com.example.aikeyboard.text

import android.content.Context
import android.view.inputmethod.InputConnection
import com.example.aikeyboard.text.composing.Composer

class EnhancedTextProcessor(
    private val context: Context,
    private val inputConnection: InputConnection,
    private val composer: Composer = TelexComposer()
) {
    private var composingText = StringBuilder()
    private var isComposing = false
    private val vietnameseEnhancer = VietnameseTextEnhancer(context)
    
    fun processText(char: Char): Boolean {
        // Nếu không phải chữ cái và không đang compose
        if (!char.isLetter() && !isComposing) {
            inputConnection.commitText(char.toString(), 1)
            return true
        }

        // Bắt đầu composing nếu chưa
        if (!isComposing) {
            isComposing = true
            composingText.clear()
        }

        // Xử lý text với composer (TelexComposer)
        val result = composer.getActions(composingText.toString(), char.toString())

        // Nếu có thay đổi, cập nhật composing text
        if (result.first > 0) {
            composingText.delete(composingText.length - result.first, composingText.length)
        }
        composingText.append(result.second)

        // Kiểm tra xem có thể cải thiện từ đang gõ không
        val enhancedText = enhanceVietnameseText(composingText.toString())
        
        // Hiển thị text đang compose
        inputConnection.setComposingText(enhancedText, 1)

        return true
    }
    
    private fun enhanceVietnameseText(text: String): String {
        // Tách từ cuối cùng để xử lý
        val words = text.split(" ")
        if (words.isEmpty()) return text
        
        val lastWord = words.last()
        val previousWords = words.dropLast(1)
        
        // Kiểm tra xem từ cuối có thể được cải thiện không
        val enhancedLastWord = enhanceWord(lastWord)
        
        return if (previousWords.isNotEmpty()) {
            "${previousWords.joinToString(" ")} $enhancedLastWord"
        } else {
            enhancedLastWord
        }
    }
    
    private fun enhanceWord(word: String): String {
        // 1. Kiểm tra xem có thể chuyển đổi từ không dấu thành có dấu không
        if (vietnameseEnhancer.canConvertToAccented(word)) {
            return vietnameseEnhancer.processNonAccentedWord(word)
        }
        
        // 2. Kiểm tra auto-complete suggestions
        val suggestions = vietnameseEnhancer.getAutoCompleteSuggestions(word)
        if (suggestions.isNotEmpty()) {
            // Trả về gợi ý đầu tiên (có tần số cao nhất)
            return suggestions.first()
        }
        
        // 3. Nếu không có cải thiện, trả về từ gốc
        return word
    }
    
    fun getSuggestions(): List<String> {
        val currentWord = composingText.toString().split(" ").lastOrNull() ?: ""
        return vietnameseEnhancer.getSuggestionsForWord(currentWord)
    }
    
    fun reset() {
        if (isComposing) {
            // Commit text hiện tại
            inputConnection.finishComposingText()
            composingText.clear()
            isComposing = false
        }
    }

    fun clear() {
        composingText.clear()
        isComposing = false
        inputConnection.finishComposingText()
    }

    fun commitText() {
        if(isComposing){
            inputConnection.finishComposingText()
            composingText.clear()
            isComposing = false
        }
    }

    fun deleteLastCharacter(): Boolean {
        if (composingText.isNotEmpty()) {
            composingText.deleteCharAt(composingText.length - 1)
            if (composingText.isEmpty()) {
                isComposing = false
                inputConnection.finishComposingText()
            } else {
                val enhancedText = enhanceVietnameseText(composingText.toString())
                inputConnection.setComposingText(enhancedText, 1)
            }
            return true
        }
        return false
    }
    
    fun getCurrentComposingText(): String {
        return composingText.toString()
    }
    
    fun isCurrentlyComposing(): Boolean {
        return isComposing
    }
} 