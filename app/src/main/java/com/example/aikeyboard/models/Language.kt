package com.example.aikeyboard.models

enum class Language(
    val code: String,
    val displayCode: String,
    val nativeName: String,
    val englishName: String,
) {
    VIETNAMESE("vi", "VI", "Tiếng Việt", "Vietnamese"),
    ENGLISH("en", "EN", "English", "English"),
    CHINESE("zh", "中", "中文", "Chinese"),
    JAPANESE("ja", "日", "日本語", "Japanese"),
    KOREAN("ko", "한", "한국어", "Korean"),
    FRENCH("fr", "FR", "Français", "French"),
    GERMAN("de", "DE", "Deutsch", "German"),
    SPANISH("es", "ES", "Español", "Spanish"),
    ITALIAN("it", "IT", "Italiano", "Italian"),
    RUSSIAN("ru", "РУ", "Русский", "Russian"),
    ARABIC("ar", "ع", "العربية", "Arabic"),
    THAI("th", "ไท", "ไทย", "Thai"),
    HINDI("hi", "हि", "हिन्दी", "Hindi"),
    ;

    companion object {
        fun fromCode(code: String): Language = values().find { it.code == code } ?: ENGLISH

        fun fromDisplayCode(displayCode: String): Language = values().find { it.displayCode == displayCode } ?: ENGLISH
    }
} 