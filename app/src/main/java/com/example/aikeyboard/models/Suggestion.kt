package com.example.aikeyboard.models

data class Suggestion(
    val word: String,
    val definition: String? = null,
    val type: SuggestionType = SuggestionType.WORD_SUGGESTION
) {
    enum class SuggestionType {
        WORD_SUGGESTION,    // Gợi ý từ
        DEFINITION,         // Định nghĩa
        TRANSLATION,        // Dịch nghĩa
        SPELLING_CORRECTION, // Sửa lỗi chính tả
        CLIPBOARD_HISTORY   // Lịch sử clipboard
    }
} 