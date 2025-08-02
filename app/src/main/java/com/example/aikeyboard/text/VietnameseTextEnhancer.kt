package com.example.aikeyboard.text

import android.content.Context
import org.json.JSONObject
import java.io.IOException

class VietnameseTextEnhancer(private val context: Context) {
    
    private var accentRules: Map<String, List<String>> = emptyMap()
    private var autoCompleteRules: Map<String, List<String>> = emptyMap()
    private var wordFrequency: Map<String, Int> = emptyMap()
    
    init {
        loadAccentRules()
        loadAutoCompleteRules()
        loadWordFrequency()
    }
    
    private fun loadAccentRules() {
        try {
            val inputStream = context.assets.open("dictionaries/accent_rules.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            
            val rules = mutableMapOf<String, List<String>>()
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val values = jsonObject.getJSONArray(key)
                val list = mutableListOf<String>()
                for (i in 0 until values.length()) {
                    list.add(values.getString(i))
                }
                rules[key] = list
            }
            accentRules = rules
        } catch (e: IOException) {
            // Fallback to empty map if file not found
            accentRules = emptyMap()
        }
    }
    
    private fun loadAutoCompleteRules() {
        try {
            val inputStream = context.assets.open("dictionaries/telex_enhancements.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val autoCompleteObject = jsonObject.getJSONObject("auto_complete_rules")
            
            val rules = mutableMapOf<String, List<String>>()
            val keys = autoCompleteObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val values = autoCompleteObject.getJSONArray(key)
                val list = mutableListOf<String>()
                for (i in 0 until values.length()) {
                    list.add(values.getString(i))
                }
                rules[key] = list
            }
            autoCompleteRules = rules
        } catch (e: IOException) {
            // Fallback to empty map if file not found
            autoCompleteRules = emptyMap()
        }
    }
    
    private fun loadWordFrequency() {
        try {
            val inputStream = context.assets.open("dictionaries/vietnamese_dict.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            
            val frequency = mutableMapOf<String, Int>()
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val wordObject = jsonObject.getJSONObject(key)
                if (wordObject.has("frequency")) {
                    frequency[key] = wordObject.getInt("frequency")
                }
            }
            wordFrequency = frequency
        } catch (e: IOException) {
            // Fallback to empty map if file not found
            wordFrequency = emptyMap()
        }
    }
    
    /**
     * Lấy danh sách từ có dấu cho một từ không dấu
     */
    fun getAccentedWords(nonAccentedWord: String): List<String> {
        return accentRules[nonAccentedWord] ?: emptyList()
    }
    
    /**
     * Lấy danh sách gợi ý tự động hoàn thành
     */
    fun getAutoCompleteSuggestions(partialWord: String): List<String> {
        return autoCompleteRules[partialWord] ?: emptyList()
    }
    
    /**
     * Lấy từ có tần số cao nhất trong danh sách
     */
    fun getMostFrequentWord(words: List<String>): String? {
        if (words.isEmpty()) return null
        
        return words.maxByOrNull { word ->
            wordFrequency[word] ?: 0
        }
    }
    
    /**
     * Kiểm tra xem một từ có trong từ điển không
     */
    fun isWordInDictionary(word: String): Boolean {
        return wordFrequency.containsKey(word)
    }
    
    /**
     * Lấy tần số của một từ
     */
    fun getWordFrequency(word: String): Int {
        return wordFrequency[word] ?: 0
    }
    
    /**
     * Lấy danh sách từ gợi ý dựa trên từ đang gõ
     */
    fun getSuggestionsForWord(currentWord: String): List<String> {
        val suggestions = mutableListOf<String>()
        
        // 1. Kiểm tra auto-complete rules
        val autoCompleteSuggestions = getAutoCompleteSuggestions(currentWord)
        suggestions.addAll(autoCompleteSuggestions)
        
        // 2. Kiểm tra accent rules
        val accentedSuggestions = getAccentedWords(currentWord)
        suggestions.addAll(accentedSuggestions)
        
        // 3. Sắp xếp theo tần số
        return suggestions.distinct().sortedByDescending { word ->
            getWordFrequency(word)
        }
    }
    
    /**
     * Xử lý từ không dấu thành từ có dấu phù hợp nhất
     */
    fun processNonAccentedWord(nonAccentedWord: String): String {
        val accentedWords = getAccentedWords(nonAccentedWord)
        if (accentedWords.isNotEmpty()) {
            // Trả về từ có tần số cao nhất
            return getMostFrequentWord(accentedWords) ?: accentedWords.first()
        }
        return nonAccentedWord
    }
    
    /**
     * Kiểm tra xem có thể chuyển đổi từ không dấu thành từ có dấu không
     */
    fun canConvertToAccented(nonAccentedWord: String): Boolean {
        return accentRules.containsKey(nonAccentedWord)
    }
} 