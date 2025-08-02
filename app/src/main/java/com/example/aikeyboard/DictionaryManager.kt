package com.example.aikeyboard

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.aikeyboard.models.Suggestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class DictionaryManager(private val context: Context) {
    
    companion object {
        private const val DATABASE_NAME = "en_vi_dict.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "en_vi_dict"
        private const val COLUMN_WORD = "word"
        private const val COLUMN_MEANING = "meaning"
        private const val COLUMN_PRONOUNCE = "pronounce"
    }

    private var database: SQLiteDatabase? = null
    private var isDatabaseLoaded = false

    // Từ điển tiếng Việt khổng lồ (312,528 từ)
    private var vietnameseWords = mutableSetOf<String>()
    private var isVietnameseWordsLoaded = false

    init {
        loadDatabase()
        loadVietnameseWords()
    }

    private fun loadDatabase() {
        try {
            val dbFile = File(context.getExternalFilesDir(null), DATABASE_NAME)
            
            // Copy database từ assets nếu chưa có
            if (!dbFile.exists()) {
                copyDatabaseFromAssets()
            }
            
            database = SQLiteDatabase.openDatabase(
                dbFile.absolutePath,
                null,
                SQLiteDatabase.OPEN_READONLY
            )
            isDatabaseLoaded = true
    
        } catch (e: Exception) {
            Log.e("DictionaryManager", "Error loading database: ${e.message}")
            isDatabaseLoaded = false
        }
    }

    private fun copyDatabaseFromAssets() {
        try {
            val inputStream = context.assets.open("databases/$DATABASE_NAME")
            val dbFile = File(context.getExternalFilesDir(null), DATABASE_NAME)
            val outputStream = FileOutputStream(dbFile)
            
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            
    
        } catch (e: Exception) {
            Log.e("DictionaryManager", "Error copying database: ${e.message}")
        }
    }

    private fun loadVietnameseWords() {
        if (isVietnameseWordsLoaded) return
        
        try {
            val inputStream = context.assets.open("vietnamese_words_android.txt")
            val reader = inputStream.bufferedReader()
            
            var count = 0
            reader.useLines { lines ->
                for (line in lines) {
                    val word = line.trim()
                    if (word.isNotEmpty() && word.length >= 2) {
                        vietnameseWords.add(word)
                        count++
                    }
                }
            }
            
            isVietnameseWordsLoaded = true
    
        } catch (e: Exception) {
            Log.e("DictionaryManager", "Error loading Vietnamese words: ${e.message}")
            // Fallback to basic words if file not found
            loadBasicVietnameseWords()
        }
    }

    private fun loadBasicVietnameseWords() {
        val basicWords = listOf(
            "xin chào", "cảm ơn", "tạm biệt", "xin lỗi", "không sao", "được rồi", "tốt lắm", "rất tốt", "không tốt", "tệ quá",
            "tôi", "bạn", "anh", "chị", "em", "ông", "bà", "cô", "chú", "bác",
            "người", "nhà", "xe", "sách", "bút", "bàn", "ghế", "cửa", "tường", "sàn",
            "ăn", "uống", "ngủ", "đi", "đến", "về", "ra", "vào", "lên", "xuống",
            "lớn", "nhỏ", "cao", "thấp", "dài", "ngắn", "rộng", "hẹp", "đẹp", "xấu",
            "nóng", "lạnh", "ấm", "mát", "nhanh", "chậm", "dễ", "khó", "đúng", "sai",
            "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín", "mười",
            "hôm nay", "hôm qua", "ngày mai", "tuần này", "tháng này", "năm nay",
            "nắng", "mưa", "gió", "lạnh", "nóng", "ấm", "mát", "ẩm", "khô",
            "vui", "buồn", "giận", "sợ", "ngạc nhiên", "thích thú", "chán", "mệt", "khỏe", "ốm"
        )
        
        vietnameseWords.addAll(basicWords)
        isVietnameseWordsLoaded = true

    }

    suspend fun getSuggestions(query: String, maxResults: Int = 10): List<Suggestion> {
        return withContext(Dispatchers.IO) {
            val suggestions = mutableListOf<Suggestion>()
            
            if (query.length < 2) return@withContext suggestions

            try {
                // Chiến lược thông minh: Ưu tiên in-memory trước, database sau
                
                // 1. Tìm kiếm nhanh trong từ điển tiếng Việt in-memory (ưu tiên cao)
                val vietnameseMatches = vietnameseWords.filter { 
                    it.contains(query, ignoreCase = true) 
                }.take(maxResults / 2) // Chỉ lấy 50% từ in-memory
                
                vietnameseMatches.forEach { word ->
                    suggestions.add(Suggestion(word, null, Suggestion.SuggestionType.WORD_SUGGESTION))
                }

                // 2. Tìm kiếm từ tiếng Anh trong database (chỉ khi cần)
                if (suggestions.size < maxResults && isDatabaseLoaded && database != null) {
                    val remainingSlots = maxResults - suggestions.size
                    
                    // Chỉ query database nếu còn slot và query đủ dài
                    if (query.length >= 3) {
                        val englishCursor = database?.query(
                            "dictionary",
                            arrayOf("word", "meaning"),
                            "word LIKE ?",
                            arrayOf("$query%"),
                            null,
                            null,
                            null,
                            remainingSlots.toString()
                        )

                        englishCursor?.use {
                            while (it.moveToNext()) {
                                val word = it.getString(0)
                                val meaning = it.getString(1)
                                suggestions.add(Suggestion(word, meaning, Suggestion.SuggestionType.TRANSLATION))
                            }
                        }
                    }
                }

                // 3. Thêm gợi ý chính tả cho tiếng Việt (chỉ khi cần)
                if (suggestions.size < maxResults && query.length >= 3) {
                    val spellingSuggestions = getSpellingSuggestions(query)
                    suggestions.addAll(spellingSuggestions.take(maxResults - suggestions.size))
                }

            } catch (e: Exception) {
                Log.e("DictionaryManager", "Error getting suggestions: ${e.message}")
            }

            suggestions.distinctBy { it.word }.take(maxResults)
        }
    }

    // Cache cho các query gần đây
    private val suggestionCache = mutableMapOf<String, List<Suggestion>>()
    private val cacheSize = 100 // Giới hạn cache size
    
    fun getSuggestionsFast(query: String, maxResults: Int = 10): List<Suggestion> {
        val suggestions = mutableListOf<Suggestion>()
        if (query.length < 2) return suggestions
        
        try {
            // 1. Kiểm tra cache trước
            val cacheKey = "${query}_$maxResults"
            suggestionCache[cacheKey]?.let { cached ->
                return cached
            }
            
            // 2. Tìm kiếm trong memory với tối ưu hóa
            val vietnameseMatches = vietnameseWords.asSequence()
                .filter { it.contains(query, ignoreCase = true) }
                .take(maxResults)
                .toList()
            
            vietnameseMatches.forEach { word ->
                suggestions.add(Suggestion(word, null, Suggestion.SuggestionType.WORD_SUGGESTION))
            }
            
            // 3. Thêm từ phổ biến nếu cần (chỉ khi query ngắn)
            if (suggestions.size < maxResults && query.length <= 2) {
                val commonWords = listOf("tôi", "bạn", "anh", "em", "ông", "bà", "cô", "chú", "bác", "là", "của", "với", "cho", "từ", "về")
                for (word in commonWords) {
                    if (word.contains(query, ignoreCase = true) && !suggestions.any { it.word == word }) {
                        suggestions.add(Suggestion(word, null, Suggestion.SuggestionType.WORD_SUGGESTION))
                        if (suggestions.size >= maxResults) break
                    }
                }
            }
            
            // 4. Cache kết quả
            val result = suggestions.distinctBy { it.word }.take(maxResults)
            if (suggestionCache.size >= cacheSize) {
                // Xóa cache cũ nhất
                val oldestKey = suggestionCache.keys.first()
                suggestionCache.remove(oldestKey)
            }
            suggestionCache[cacheKey] = result
            
            return result
            
        } catch (e: Exception) {
            Log.e("DictionaryManager", "Error getting suggestions fast: ${e.message}")
        }
        return suggestions.distinctBy { it.word }.take(maxResults)
    }

    private fun extractVietnameseWords(text: String): List<String> {
        // Tách các từ tiếng Việt từ text
        return text.split(Regex("[\\s,;.()]+"))
            .filter { it.length >= 2 && it.any { char -> char in 'à'..'ỹ' || char in 'a'..'z' } }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    private fun getSpellingSuggestions(query: String): List<Suggestion> {
        val suggestions = mutableListOf<Suggestion>()
        
        // Gợi ý sửa lỗi chính tả đơn giản
        val commonMistakes = mapOf(
            "không" to "không",
            "được" to "được", 
            "của" to "của",
            "với" to "với",
            "cho" to "cho",
            "từ" to "từ",
            "về" to "về",
            "trong" to "trong",
            "ngoài" to "ngoài",
            "trên" to "trên",
            "dưới" to "dưới"
        )

        // Kiểm tra các lỗi chính tả phổ biến
        for ((correct, _) in commonMistakes) {
            if (isSimilar(query, correct)) {
                suggestions.add(Suggestion(correct, null, Suggestion.SuggestionType.SPELLING_CORRECTION))
            }
        }

        return suggestions
    }

    private fun isSimilar(str1: String, str2: String): Boolean {
        if (str1.length < 3 || str2.length < 3) return false
        
        val distance = levenshteinDistance(str1.lowercase(), str2.lowercase())
        return distance <= 2 && distance < str1.length.coerceAtMost(str2.length) / 2
    }

    private fun levenshteinDistance(str1: String, str2: String): Int {
        val matrix = Array(str1.length + 1) { IntArray(str2.length + 1) }
        
        for (i in 0..str1.length) matrix[i][0] = i
        for (j in 0..str2.length) matrix[0][j] = j
        
        for (i in 1..str1.length) {
            for (j in 1..str2.length) {
                val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1
                matrix[i][j] = minOf(
                    matrix[i - 1][j] + 1,      // deletion
                    matrix[i][j - 1] + 1,      // insertion
                    matrix[i - 1][j - 1] + cost // substitution
                )
            }
        }
        
        return matrix[str1.length][str2.length]
    }

    fun close() {
        database?.close()
        database = null
        isDatabaseLoaded = false
        suggestionCache.clear() // Clear cache khi đóng
    }
    
    fun clearCache() {
        suggestionCache.clear()

    }
} 