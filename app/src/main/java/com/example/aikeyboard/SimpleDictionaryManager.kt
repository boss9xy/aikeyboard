package com.example.aikeyboard

import android.content.Context
import android.util.Log
import com.example.aikeyboard.models.Suggestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException

class SimpleDictionaryManager(private val context: Context) {
    
    companion object {
        private const val TAG = "SimpleDictionaryManager"
    }

    // Cache cho các từ điển
    private var vietnameseDict: JSONObject? = null
    private var englishDict: JSONObject? = null
    private var contextPatterns: JSONObject? = null
    
    // Learning Manager
    private val learningManager = LearningManager(context)
    
    // Clipboard History Manager
    private var clipboardHistoryProvider: (() -> List<String>)? = null
    
    // Cache cho suggestions
    private val suggestionCache = mutableMapOf<String, List<Suggestion>>()
    private val cacheSize = 50 // Giảm cache size cho nhẹ hơn
    
    // Trạng thái load
    private var isVietnameseLoaded = false
    private var isEnglishLoaded = false
    private var isContextLoaded = false

    init {
        loadDictionaries()
    }

    private fun loadDictionaries() {
        try {
    
            
            // Load từ điển tiếng Việt
            val vietnameseStream = context.assets.open("dictionaries/vietnamese_dict.json")
            val vietnameseContent = vietnameseStream.bufferedReader().use { it.readText() }
            vietnameseDict = JSONObject(vietnameseContent)
            isVietnameseLoaded = true
    
            
            // Load từ điển tiếng Anh
            val englishStream = context.assets.open("dictionaries/english_dict.json")
            val englishContent = englishStream.bufferedReader().use { it.readText() }
            englishDict = JSONObject(englishContent)
            isEnglishLoaded = true
    
            
            // Load context patterns
            val contextStream = context.assets.open("dictionaries/context_patterns.json")
            val contextContent = contextStream.bufferedReader().use { it.readText() }
            contextPatterns = JSONObject(contextContent)
            isContextLoaded = true
    
            
    
            
        } catch (e: IOException) {
            Log.e(TAG, "❌ Error loading dictionaries: ${e.message}")
            e.printStackTrace()
        }
    }

    fun getSuggestionsFast(query: String, maxResults: Int = 10): List<Suggestion> {
        val suggestions = mutableListOf<Suggestion>()
        if (query.length < 2) return suggestions
        

        
        try {
            // 1. Kiểm tra cache trước
            val cacheKey = "${query}_$maxResults"
            suggestionCache[cacheKey]?.let { cached ->
        
                return cached
            }
            
            // 2. Tìm kiếm trong learned data (ưu tiên cao)
            val learnedSuggestions = learningManager.getLearnedSuggestions(query, maxResults / 3)
    
            suggestions.addAll(learnedSuggestions)
            
            // 3. Tìm kiếm trong clipboard history (ưu tiên cao)
            val clipboardSuggestions = getClipboardHistorySuggestions(query, maxResults / 3)
    
            suggestions.addAll(clipboardSuggestions)
            
            // 4. Tìm kiếm trong từ điển tiếng Việt
            if (suggestions.size < maxResults && isVietnameseLoaded && vietnameseDict != null) {
                val vietnameseMatches = findMatchesInDict(vietnameseDict!!, query, (maxResults - suggestions.size) / 2)
        
                vietnameseMatches.forEach { word ->
                    suggestions.add(Suggestion(word, null, Suggestion.SuggestionType.WORD_SUGGESTION))
                }
            } else {
        
            }
            
            // 5. Tìm kiếm trong từ điển tiếng Anh
            if (suggestions.size < maxResults && isEnglishLoaded && englishDict != null) {
                val englishMatches = findMatchesInDict(englishDict!!, query, maxResults - suggestions.size)
        
                englishMatches.forEach { word ->
                    suggestions.add(Suggestion(word, null, Suggestion.SuggestionType.WORD_SUGGESTION))
                }
            } else {
        
            }
            
            // 5. Cache kết quả
            val result = suggestions.distinctBy { it.word }.take(maxResults)
            if (suggestionCache.size >= cacheSize) {
                val oldestKey = suggestionCache.keys.first()
                suggestionCache.remove(oldestKey)
            }
            suggestionCache[cacheKey] = result
            
    
            return result
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting suggestions fast: ${e.message}")
            e.printStackTrace()
        }
        return suggestions.distinctBy { it.word }.take(maxResults)
    }

    private fun findMatchesInDict(dict: JSONObject, query: String, maxResults: Int): List<String> {
        val matches = mutableListOf<String>()
        val keys = dict.keys()
        
        while (keys.hasNext() && matches.size < maxResults) {
            val word = keys.next()
            if (word.contains(query, ignoreCase = true)) {
                matches.add(word)
            }
        }
        
        return matches
    }

    fun getNextWordSuggestions(lastWord: String, language: String = "vi"): List<String> {
        val suggestions = mutableListOf<String>()
        
        try {
            // 1. Tìm trong learned data (ưu tiên cao)
            val learnedNextWords = learningManager.getLearnedNextWords(lastWord, 3)
            suggestions.addAll(learnedNextWords)
            
            // 2. Tìm trong từ điển chính
            val dict = if (language == "vi") vietnameseDict else englishDict
            if (dict != null && dict.has(lastWord)) {
                val wordData = dict.getJSONObject(lastWord)
                if (wordData.has("next")) {
                    val nextArray = wordData.getJSONArray("next")
                    for (i in 0 until nextArray.length()) {
                        val suggestion = nextArray.getString(i)
                        if (!suggestions.contains(suggestion)) {
                            suggestions.add(suggestion)
                        }
                    }
                }
            }
            
            // 3. Tìm trong context patterns
            if (suggestions.size < 5 && isContextLoaded && contextPatterns != null) {
                val langPatterns = contextPatterns?.getJSONObject(language)
                if (langPatterns != null) {
                    val wordData = dict?.getJSONObject(lastWord)
                    val wordType = wordData?.getString("type")
                    
                    if (wordType != null && langPatterns.has(wordType)) {
                        val typeArray = langPatterns.getJSONArray(wordType)
                        for (i in 0 until typeArray.length()) {
                            val suggestion = typeArray.getString(i)
                            if (!suggestions.contains(suggestion)) {
                                suggestions.add(suggestion)
                            }
                        }
                    }
                }
            }
            
            // 4. Fallback nếu chưa đủ
            if (suggestions.size < 3) {
                val fallbackWords = if (language == "vi") {
                    listOf("là", "của", "với", "cho", "từ", "về", "trong", "ngoài", "trên", "dưới")
                } else {
                    listOf("is", "are", "have", "has", "was", "were", "will", "can", "should", "would")
                }
                
                for (word in fallbackWords) {
                    if (!suggestions.contains(word)) {
                        suggestions.add(word)
                    }
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting next word suggestions: ${e.message}")
        }
        
        return suggestions.distinct().take(5)
    }

    fun getSuggestions(query: String, maxResults: Int = 10): List<Suggestion> {
        return getSuggestionsFast(query, maxResults)
    }

    fun clearCache() {
        suggestionCache.clear()

    }

    fun close() {
        suggestionCache.clear()
        vietnameseDict = null
        englishDict = null
        contextPatterns = null
        isVietnameseLoaded = false
        isEnglishLoaded = false
        isContextLoaded = false
    }
    
    // Learning methods
    fun learnFromText(text: String) {

        learningManager.learnFromText(text)
    }
    
    fun getLearningStats(): Map<String, Any> {
        return learningManager.getLearningStats()
    }
    
    fun clearLearnedData() {
        learningManager.clearLearnedData()
    }
    
    fun exportLearnedData(): String {
        return learningManager.exportLearnedData()
    }
    
    // Clipboard History Integration
    fun setClipboardHistoryProvider(provider: () -> List<String>) {
        clipboardHistoryProvider = provider

    }
    
    private fun getClipboardHistorySuggestions(query: String, maxResults: Int): List<Suggestion> {
        val suggestions = mutableListOf<Suggestion>()
        
        try {
            val clipboardHistory = clipboardHistoryProvider?.invoke() ?: emptyList()
    
            
            // Tìm kiếm trong clipboard history
            for (clipText in clipboardHistory) {
                if (clipText.contains(query, ignoreCase = true)) {
                    suggestions.add(Suggestion(clipText, null, Suggestion.SuggestionType.CLIPBOARD_HISTORY))
                    if (suggestions.size >= maxResults) break
                }
            }
            
    
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting clipboard history suggestions: ${e.message}")
        }
        
        return suggestions
    }
} 