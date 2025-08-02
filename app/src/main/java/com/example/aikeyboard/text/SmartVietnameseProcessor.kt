package com.example.aikeyboard.text

import android.content.Context
import android.util.Log
import com.example.aikeyboard.Logger
import com.example.aikeyboard.models.Suggestion
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

/**
 * SmartVietnameseProcessor - Xử lý từ điển thông minh giống Google Gboard
 * Tối ưu hóa cho tốc độ nhanh như chớp
 */
class SmartVietnameseProcessor(private val context: Context) {
    
    private var smartDictionary: JSONObject? = null
    private var accentRules: JSONObject? = null
    private var bigramData: JSONObject? = null
    private var contextPatterns: JSONObject? = null
    
    // Cache siêu nhanh với ConcurrentHashMap
    private val suggestionCache = ConcurrentHashMap<String, List<Suggestion>>()
    private val nextWordCache = ConcurrentHashMap<String, List<String>>()
    private val wordFrequencyCache = ConcurrentHashMap<String, Int>()
    
    // Preload data để tăng tốc
    private val allWords = mutableListOf<String>()
    private val wordFrequencyMap = mutableMapOf<String, Int>()
    
    init {
        loadSmartDictionary()
        preloadData()
    }
    
    private fun loadSmartDictionary() {
        try {
            // Load từ điển chính
            val dictStream = context.assets.open("dictionaries/smart_vietnamese_dict.json")
            val dictContent = dictStream.bufferedReader().use { it.readText() }
            smartDictionary = JSONObject(dictContent)
            dictStream.close()
            
            // Load quy tắc dấu
            val accentStream = context.assets.open("dictionaries/smart_accent_rules.json")
            val accentContent = accentStream.bufferedReader().use { it.readText() }
            accentRules = JSONObject(accentContent)
            accentStream.close()
            
            // Load bigram data
            val bigramStream = context.assets.open("dictionaries/smart_bigram.json")
            val bigramContent = bigramStream.bufferedReader().use { it.readText() }
            bigramData = JSONObject(bigramContent)
            bigramStream.close()
            
            // Load context patterns
            val patternStream = context.assets.open("dictionaries/context_patterns.json")
            val patternContent = patternStream.bufferedReader().use { it.readText() }
            contextPatterns = JSONObject(patternContent)
            patternStream.close()
            
            
            
        } catch (e: Exception) {
    
        }
    }
    
    private fun preloadData() {
        try {
            smartDictionary?.let { dict ->
                val keys = dict.keys()
                while (keys.hasNext()) {
                    val word = keys.next()
                    allWords.add(word)
                    
                    val wordData = dict.getJSONObject(word)
                    val frequency = wordData.optInt("frequency", 0)
                    wordFrequencyMap[word] = frequency
                }
            }
    
        } catch (e: Exception) {
    
        }
    }
    
    /**
     * Xử lý từ không dấu thành từ có dấu - TỐI ƯU NHANH
     */
    fun processNonAccentedWord(word: String): String {
        return try {
            accentRules?.let { rules ->
                if (rules.has(word)) {
                    val accents = rules.getJSONArray(word)
                    if (accents.length() > 0) {
                        return accents.getString(0)
                    }
                }
            }
            word
        } catch (e: Exception) {
            word
        }
    }
    
    /**
     * Lấy gợi ý thông minh - TỐI ƯU NHANH NHƯ CHỚP
     */
    fun getSmartSuggestions(query: String, maxResults: Int = 15): List<Suggestion> {
        if (query.isEmpty()) return emptyList()
        
        // Kiểm tra cache trước - NHANH NHẤT
        val cacheKey = "${query}_$maxResults"
        suggestionCache[cacheKey]?.let { return it }
        
        val suggestions = mutableListOf<Suggestion>()
        
        try {
            // 1. Tìm từ chính xác - NHANH
            if (wordFrequencyMap.containsKey(query)) {
                suggestions.add(Suggestion(query, null, Suggestion.SuggestionType.WORD_SUGGESTION))
            }
            
            // 2. Tìm từ bắt đầu bằng query - TỐI ƯU
            val matchingWords = allWords.filter { it.startsWith(query) && it != query }
                .sortedByDescending { wordFrequencyMap[it] ?: 0 }
                .take(maxResults)
            
            for (word in matchingWords) {
                suggestions.add(Suggestion(word, null, Suggestion.SuggestionType.WORD_SUGGESTION))
            }
            
            // 3. Thêm suggestions từ accent rules - NHANH
            accentRules?.let { rules ->
                if (rules.has(query)) {
                    val accents = rules.getJSONArray(query)
                    for (i in 0 until accents.length()) {
                        val accent = accents.getString(i)
                        if (accent != query && !suggestions.any { it.word == accent }) {
                            suggestions.add(Suggestion(accent, null, Suggestion.SuggestionType.WORD_SUGGESTION))
                        }
                    }
                }
            }
            
            // 4. Tìm từ tương tự (similar words) - CẢI THIỆN SPELL CHECK
            val similarWords = findSimilarWords(query, 5)
            for (word in similarWords) {
                if (!suggestions.any { it.word == word }) {
                    suggestions.add(Suggestion(word, null, Suggestion.SuggestionType.WORD_SUGGESTION))
                }
            }
            
            // 5. Sắp xếp theo tần suất - NHANH
            suggestions.sortByDescending { suggestion ->
                wordFrequencyMap[suggestion.word] ?: 0
            }
            
        } catch (e: Exception) {
    
        }
        
        val result = suggestions.take(maxResults)
        suggestionCache[cacheKey] = result
        return result
    }
    
    /**
     * Tìm từ tương tự dựa trên độ tương đồng - CẢI THIỆN SPELL CHECK
     */
    private fun findSimilarWords(query: String, maxResults: Int): List<String> {
        val similarWords = mutableListOf<String>()
        
        try {
            // 1. Kiểm tra mapping từ dễ nhầm lẫn
            val confusionMap = getConfusionMap()
            if (confusionMap.containsKey(query)) {
                similarWords.addAll(confusionMap[query]!!)
            }
            
            // 2. Tìm từ có độ tương đồng cao
            for (word in allWords) {
                if (word != query && calculateSimilarity(query, word) > 0.7) {
                    similarWords.add(word)
                    if (similarWords.size >= maxResults) break
                }
            }
            
            // 3. Sắp xếp theo độ tương đồng và tần suất
            similarWords.sortWith(compareByDescending<String> { word ->
                calculateSimilarity(query, word) * (wordFrequencyMap[word] ?: 0)
            })
            
        } catch (e: Exception) {
    
        }
        
        return similarWords.take(maxResults)
    }
    
    /**
     * Mapping các từ dễ nhầm lẫn khi gõ
     */
    private fun getConfusionMap(): Map<String, List<String>> {
        return mapOf(
            "chứ" to listOf("xóa", "chức", "chứa"),
            "xóa" to listOf("chứ", "xóa bỏ", "xóa sạch"),
            "toi" to listOf("tôi", "tối", "tội"),
            "tôi" to listOf("toi", "tối", "tội"),
            "ban" to listOf("bạn", "bàn", "bán"),
            "bạn" to listOf("ban", "bàn", "bán"),
            "lam" to listOf("làm", "lâm", "lầm"),
            "làm" to listOf("lam", "lâm", "lầm"),
            "hoc" to listOf("học", "hộc", "hốc"),
            "học" to listOf("hoc", "hộc", "hốc"),
            "doc" to listOf("đọc", "dốc", "độc"),
            "đọc" to listOf("doc", "dốc", "độc"),
            "viet" to listOf("viết", "việt", "việc"),
            "viết" to listOf("viet", "việt", "việc"),
            "noi" to listOf("nói", "nội", "nơi"),
            "nói" to listOf("noi", "nội", "nơi")
        )
    }
    
    /**
     * Tính độ tương đồng giữa hai từ - SỬ DỤNG LEVENSHTEIN DISTANCE
     */
    private fun calculateSimilarity(word1: String, word2: String): Double {
        val distance = levenshteinDistance(word1, word2)
        val maxLength = maxOf(word1.length, word2.length)
        return if (maxLength == 0) 1.0 else (maxLength - distance).toDouble() / maxLength
    }
    
    /**
     * Tính Levenshtein distance - THUẬT TOÁN TÌM ĐỘ TƯƠNG ĐỒNG
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val matrix = Array(s1.length + 1) { IntArray(s2.length + 1) }
        
        for (i in 0..s1.length) {
            matrix[i][0] = i
        }
        for (j in 0..s2.length) {
            matrix[0][j] = j
        }
        
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                matrix[i][j] = minOf(
                    matrix[i - 1][j] + 1,      // deletion
                    matrix[i][j - 1] + 1,      // insertion
                    matrix[i - 1][j - 1] + cost // substitution
                )
            }
        }
        
        return matrix[s1.length][s2.length]
    }
    
    /**
     * Lấy gợi ý từ tiếp theo - NHANH
     */
    fun getNextWordSuggestions(currentWord: String, maxResults: Int = 8): List<String> {
        if (currentWord.isEmpty()) return emptyList()
        
        // Kiểm tra cache
        val cacheKey = "${currentWord}_$maxResults"
        nextWordCache[cacheKey]?.let { return it }
        
        return try {
            bigramData?.let { bigram ->
                if (bigram.has(currentWord)) {
                    val nextWords = bigram.getJSONArray(currentWord)
                    val result = mutableListOf<String>()
                    
                    for (i in 0 until nextWords.length()) {
                        result.add(nextWords.getString(i))
                        if (result.size >= maxResults) break
                    }
                    
                    nextWordCache[cacheKey] = result
                    return result
                }
            }
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Lấy gợi ý dựa trên ngữ cảnh - NHANH
     */
    fun getContextSuggestions(contextType: String): List<String> {
        return try {
            contextPatterns?.let { patterns ->
                if (patterns.has(contextType)) {
                    val contextWords = patterns.getJSONArray(contextType)
                    val result = mutableListOf<String>()
                    
                    for (i in 0 until contextWords.length()) {
                        result.add(contextWords.getString(i))
                    }
                    
                    return result
                }
            }
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Xác định loại ngữ cảnh từ văn bản - NHANH
     */
    fun detectContext(text: String): String {
        val lowerText = text.lowercase()
        
        return when {
            lowerText.contains("chào") || lowerText.contains("xin") -> "greeting"
            lowerText.contains("gì") || lowerText.contains("sao") -> "question"
            lowerText.contains("làm") || lowerText.contains("học") -> "action"
            lowerText.contains("hôm") || lowerText.contains("ngày") -> "time"
            lowerText.contains("nhà") || lowerText.contains("trường") -> "location"
            lowerText.contains("vui") || lowerText.contains("buồn") -> "emotion"
            lowerText.contains("một") || lowerText.contains("hai") -> "number"
            else -> "general"
        }
    }
    
    /**
     * Lấy tần suất của một từ - NHANH
     */
    fun getWordFrequency(word: String): Int {
        return wordFrequencyMap[word] ?: 0
    }
    
    /**
     * Kiểm tra xem từ có tồn tại trong từ điển không - NHANH
     */
    fun isWordInDictionary(word: String): Boolean {
        return wordFrequencyMap.containsKey(word)
    }
    
    /**
     * Xóa cache để tiết kiệm bộ nhớ
     */
    fun clearCache() {
        suggestionCache.clear()
        nextWordCache.clear()
    }
    
    /**
     * Lấy tất cả từ có tần suất cao - NHANH
     */
    fun getHighFrequencyWords(limit: Int = 20): List<String> {
        return wordFrequencyMap.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key }
    }
} 