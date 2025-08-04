package com.example.aikeyboard

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.channels.awaitClose
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import android.view.inputmethod.InputConnection

class OlamaServe(private val baseUrl: String, private val model: String) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun askQuestion(prompt: String): String {
        val url = if (baseUrl.endsWith("/")) baseUrl + "v1/chat/completions" else baseUrl + "/v1/chat/completions"
        val messages = JSONArray().apply {
            put(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            })
        }
        val jsonBody = JSONObject().apply {
            put("model", model)
            put("messages", messages)
            put("stream", false)
        }

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: ""
            
            if (!response.isSuccessful) {
                return "HTTP ${response.code}: $body"
            }
            try {
                val json = JSONObject(body)
                val choices = json.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val message = choices.getJSONObject(0).optJSONObject("message")
                    if (message != null) return message.optString("content", "[No content in message]")
                }
                return "No content: $body"
            } catch (e: Exception) {
                // JSON parse error
                return "JSON parse error: ${e.message}\nRaw: $body"
            }
        }
    }

    fun streamAskQuestion(prompt: String, ic: InputConnection, thinkingTextLength: Int): Flow<String> = callbackFlow {
        val url = if (baseUrl.endsWith("/")) baseUrl + "v1/chat/completions" else baseUrl + "/v1/chat/completions"
        val messages = JSONArray().apply {
            put(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            })
        }
        val jsonBody = JSONObject().apply {
            put("model", model)
            put("messages", messages)
            put("stream", true)
        }

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                trySend("[STREAM] Error: ${e.message}")
                close(e)
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    trySend("[STREAM] HTTP ${response.code}: ${response.body?.string()}")
                    close(IOException("HTTP ${response.code}"))
                    return
                }
                val source = response.body?.source()
                var firstChunk = true
                if (source != null) {
                    try {
                        while (!source.exhausted()) {
                            val line = source.readUtf8Line()
                            if (line != null && line.startsWith("data: ")) {
                                val data = line.removePrefix("data: ").trim()
                                if (data == "[DONE]") break
                                val json = JSONObject(data)
                                val delta = json.optJSONArray("choices")
                                    ?.optJSONObject(0)
                                    ?.optJSONObject("delta")
                                    ?.optString("content")
                                if (!delta.isNullOrEmpty()) {
                                    var currentThinkingTextLength = thinkingTextLength
                                    if (firstChunk && currentThinkingTextLength > 0) {
                                        // AIKeyboardService.kt đã xử lý việc xóa "Thinking..."
                                        // Không cần thêm xuống dòng nào
                                        currentThinkingTextLength = 0
                                        firstChunk = false
                                    }
                                    trySend(delta)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        trySend("[STREAM] Error: ${e.message}")
                    }
                }
                close()
            }
        })
        awaitClose { call.cancel() }
    }.flowOn(Dispatchers.IO)
} 