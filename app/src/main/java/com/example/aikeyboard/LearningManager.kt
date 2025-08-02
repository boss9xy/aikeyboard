package com.example.aikeyboard

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.aikeyboard.models.Suggestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class LearningManager(private val context: Context) {
    
    companion object {
        private const val TAG = "LearningManager"
        private const val PREFS_NAME = "learning_prefs"
        private const val KEY_LEARNED_WORDS = "learned_words"
        private const val KEY_LEARNED_PHRASES = "learned_phrases"
        private const val KEY_WORD_FREQUENCY = "word_frequency"
        private const val KEY_PHRASE_FREQUENCY = "phrase_frequency"
        private const val KEY_LAST_LEARNED = "last_learned"
        private const val MAX_LEARNED_WORDS = 1000
        private const val MAX_LEARNED_PHRASES = 500
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    // Cache cho learned data
    private var learnedWords = mutableMapOf<String, Int>() // word -> frequency
    private var learnedPhrases = mutableMapOf<String, Int>() // phrase -> frequency
    private var wordContexts = mutableMapOf<String, MutableList<String>>() // word -> next words
    private var phraseContexts = mutableMapOf<String, MutableList<String>>() // phrase -> next words

    init {
        loadLearnedData()
    }

    private fun loadLearnedData() {
        try {
            // Load learned words
            val wordsJson = prefs.getString(KEY_LEARNED_WORDS, "{}")
            val wordsObj = JSONObject(wordsJson ?: "{}")
            val wordsIterator = wordsObj.keys()
            while (wordsIterator.hasNext()) {
                val word = wordsIterator.next()
                val frequency = wordsObj.getInt(word)
                learnedWords[word] = frequency
            }

            // Load learned phrases
            val phrasesJson = prefs.getString(KEY_LEARNED_PHRASES, "{}")
            val phrasesObj = JSONObject(phrasesJson ?: "{}")
            val phrasesIterator = phrasesObj.keys()
            while (phrasesIterator.hasNext()) {
                val phrase = phrasesIterator.next()
                val frequency = phrasesObj.getInt(phrase)
                learnedPhrases[phrase] = frequency
            }

            // Load word contexts
            val wordContextsJson = prefs.getString("word_contexts", "{}")
            val wordContextsObj = JSONObject(wordContextsJson ?: "{}")
            val wordContextsIterator = wordContextsObj.keys()
            while (wordContextsIterator.hasNext()) {
                val word = wordContextsIterator.next()
                val contextsArray = wordContextsObj.getJSONArray(word)
                val contexts = mutableListOf<String>()
                for (i in 0 until contextsArray.length()) {
                    contexts.add(contextsArray.getString(i))
                }
                wordContexts[word] = contexts
            }

            // Load phrase contexts
            val phraseContextsJson = prefs.getString("phrase_contexts", "{}")
            val phraseContextsObj = JSONObject(phraseContextsJson ?: "{}")
            val phraseContextsIterator = phraseContextsObj.keys()
            while (phraseContextsIterator.hasNext()) {
                val phrase = phraseContextsIterator.next()
                val contextsArray = phraseContextsObj.getJSONArray(phrase)
                val contexts = mutableListOf<String>()
                for (i in 0 until contextsArray.length()) {
                    contexts.add(contextsArray.getString(i))
                }
                phraseContexts[phrase] = contexts
            }

    
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading learned data: ${e.message}")
        }
    }

    private fun saveLearnedData() {
        try {
            // Save learned words
            val wordsObj = JSONObject()
            learnedWords.forEach { (word, frequency) ->
                wordsObj.put(word, frequency)
            }
            prefs.edit().putString(KEY_LEARNED_WORDS, wordsObj.toString()).apply()

            // Save learned phrases
            val phrasesObj = JSONObject()
            learnedPhrases.forEach { (phrase, frequency) ->
                phrasesObj.put(phrase, frequency)
            }
            prefs.edit().putString(KEY_LEARNED_PHRASES, phrasesObj.toString()).apply()

            // Save word contexts
            val wordContextsObj = JSONObject()
            wordContexts.forEach { (word, contexts) ->
                val contextsArray = org.json.JSONArray()
                contexts.forEach { context ->
                    contextsArray.put(context)
                }
                wordContextsObj.put(word, contextsArray)
            }
            prefs.edit().putString("word_contexts", wordContextsObj.toString()).apply()

            // Save phrase contexts
            val phraseContextsObj = JSONObject()
            phraseContexts.forEach { (phrase, contexts) ->
                val contextsArray = org.json.JSONArray()
                contexts.forEach { context ->
                    contextsArray.put(context)
                }
                phraseContextsObj.put(phrase, contextsArray)
            }
            prefs.edit().putString("phrase_contexts", phraseContextsObj.toString()).apply()

            // Save last learned timestamp
            prefs.edit().putString(KEY_LAST_LEARNED, dateFormat.format(Date())).apply()

    
            
        } catch (e: Exception) {
            Log.e(TAG, "Error saving learned data: ${e.message}")
        }
    }

    fun learnFromText(text: String) {
        try {
    
            
            val words = text.split(" ").filter { it.isNotEmpty() && it.length >= 2 }
    
            
            // Learn individual words
            var newWords = 0
            words.forEach { word ->
                val cleanWord = word.trim().lowercase()
                if (cleanWord.length >= 2) {
                    val oldCount = learnedWords.getOrDefault(cleanWord, 0)
                    learnedWords[cleanWord] = oldCount + 1
                    if (oldCount == 0) newWords++
                }
            }
    

            // Learn phrases (2-4 words)
            var newPhrases = 0
            for (i in 0 until words.size - 1) {
                val phrase2 = "${words[i]} ${words[i + 1]}".trim()
                if (phrase2.length >= 4) {
                    val oldCount = learnedPhrases.getOrDefault(phrase2.lowercase(), 0)
                    learnedPhrases[phrase2.lowercase()] = oldCount + 1
                    if (oldCount == 0) newPhrases++
                }

                if (i < words.size - 2) {
                    val phrase3 = "${words[i]} ${words[i + 1]} ${words[i + 2]}".trim()
                    if (phrase3.length >= 6) {
                        val oldCount = learnedPhrases.getOrDefault(phrase3.lowercase(), 0)
                        learnedPhrases[phrase3.lowercase()] = oldCount + 1
                        if (oldCount == 0) newPhrases++
                    }
                }

                if (i < words.size - 3) {
                    val phrase4 = "${words[i]} ${words[i + 1]} ${words[i + 2]} ${words[i + 3]}".trim()
                    if (phrase4.length >= 8) {
                        val oldCount = learnedPhrases.getOrDefault(phrase4.lowercase(), 0)
                        learnedPhrases[phrase4.lowercase()] = oldCount + 1
                        if (oldCount == 0) newPhrases++
                    }
                }
            }
    

            // Learn word contexts (what comes after each word)
            var newContexts = 0
            for (i in 0 until words.size - 1) {
                val currentWord = words[i].trim().lowercase()
                val nextWord = words[i + 1].trim().lowercase()
                
                if (currentWord.length >= 2 && nextWord.length >= 2) {
                    if (!wordContexts.containsKey(currentWord)) {
                        wordContexts[currentWord] = mutableListOf()
                    }
                    if (!wordContexts[currentWord]!!.contains(nextWord)) {
                        wordContexts[currentWord]!!.add(nextWord)
                        newContexts++
                    }
                }
            }
    

            // Learn phrase contexts
            var newPhraseContexts = 0
            for (i in 0 until words.size - 2) {
                val currentPhrase = "${words[i]} ${words[i + 1]}".trim().lowercase()
                val nextWord = words[i + 2].trim().lowercase()
                
                if (currentPhrase.length >= 4 && nextWord.length >= 2) {
                    if (!phraseContexts.containsKey(currentPhrase)) {
                        phraseContexts[currentPhrase] = mutableListOf()
                    }
                    if (!phraseContexts[currentPhrase]!!.contains(nextWord)) {
                        phraseContexts[currentPhrase]!!.add(nextWord)
                        newPhraseContexts++
                    }
                }
            }
    

            // Clean up old data if too much
            if (learnedWords.size > MAX_LEARNED_WORDS) {
                val sortedWords = learnedWords.toList().sortedByDescending { it.second }
                learnedWords.clear()
                learnedWords.putAll(sortedWords.take(MAX_LEARNED_WORDS).toMap())
        
            }

            if (learnedPhrases.size > MAX_LEARNED_PHRASES) {
                val sortedPhrases = learnedPhrases.toList().sortedByDescending { it.second }
                learnedPhrases.clear()
                learnedPhrases.putAll(sortedPhrases.take(MAX_LEARNED_PHRASES).toMap())
        
            }

            // Save data
            saveLearnedData()
            
            
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error learning from text: ${e.message}")
            e.printStackTrace()
        }
    }

    fun getLearnedSuggestions(query: String, maxResults: Int = 5): List<Suggestion> {
        val suggestions = mutableListOf<Suggestion>()
        
        try {
            // Find learned words that match query
            val matchingWords = learnedWords.keys
                .filter { it.contains(query, ignoreCase = true) }
                .sortedByDescending { learnedWords[it] ?: 0 }
                .take(maxResults / 2)

            matchingWords.forEach { word ->
                suggestions.add(Suggestion(word, "Learned word", Suggestion.SuggestionType.WORD_SUGGESTION))
            }

            // Find learned phrases that match query
            val matchingPhrases = learnedPhrases.keys
                .filter { it.contains(query, ignoreCase = true) }
                .sortedByDescending { learnedPhrases[it] ?: 0 }
                .take(maxResults - suggestions.size)

            matchingPhrases.forEach { phrase ->
                suggestions.add(Suggestion(phrase, "Learned phrase", Suggestion.SuggestionType.WORD_SUGGESTION))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error getting learned suggestions: ${e.message}")
        }

        return suggestions
    }

    fun getLearnedNextWords(lastWord: String, maxResults: Int = 5): List<String> {
        val nextWords = mutableListOf<String>()
        
        try {
            // Get learned contexts for this word
            val contexts = wordContexts[lastWord.lowercase()]
            if (contexts != null) {
                nextWords.addAll(contexts.take(maxResults))
            }

            // If not enough, get from phrases ending with this word
            if (nextWords.size < maxResults) {
                val phraseContexts = phraseContexts.entries
                    .filter { it.key.endsWith(" $lastWord") }
                    .flatMap { it.value }
                    .distinct()
                    .take(maxResults - nextWords.size)
                
                nextWords.addAll(phraseContexts)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error getting learned next words: ${e.message}")
        }

        return nextWords.distinct()
    }

    fun getLearningStats(): Map<String, Any> {
        return mapOf(
            "learned_words" to learnedWords.size,
            "learned_phrases" to learnedPhrases.size,
            "word_contexts" to wordContexts.size,
            "phrase_contexts" to phraseContexts.size,
            "last_learned" to (prefs.getString(KEY_LAST_LEARNED, "Never") ?: "Never"),
            "total_word_frequency" to learnedWords.values.sum(),
            "total_phrase_frequency" to learnedPhrases.values.sum()
        )
    }

    fun clearLearnedData() {
        learnedWords.clear()
        learnedPhrases.clear()
        wordContexts.clear()
        phraseContexts.clear()
        
        prefs.edit().clear().apply()
        

    }

    fun exportLearnedData(): String {
        return try {
            val exportData = JSONObject().apply {
                put("learned_words", JSONObject(learnedWords))
                put("learned_phrases", JSONObject(learnedPhrases))
                put("word_contexts", JSONObject(wordContexts))
                put("phrase_contexts", JSONObject(phraseContexts))
                put("export_date", dateFormat.format(Date()))
                put("stats", JSONObject(getLearningStats()))
            }
            exportData.toString(2)
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting learned data: ${e.message}")
            "{}"
        }
    }
} 