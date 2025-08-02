package com.example.aikeyboard

/**
 * Cấu hình cho các model LLM
 */
data class ModelConfig(
    val name: String,
    val displayName: String,
    val provider: String, // "openai", "deepseek"
    val maxTokens: Int,
    val temperature: Double = 0.7,
    val timeoutSeconds: Int = 30
)

/**
 * Quản lý cấu hình model
 */
object ModelManager {
    
    val availableModels = listOf(
        // OpenAI Models
        ModelConfig("gpt-3.5-turbo", "GPT-3.5 Turbo", "openai", 4096, 0.7, 30),
        ModelConfig("gpt-4", "GPT-4", "openai", 8192, 0.7, 60),
        ModelConfig("gpt-4-turbo", "GPT-4 Turbo", "openai", 128000, 0.7, 60),
        
        // DeepSeek Models
        ModelConfig("deepseek-chat", "DeepSeek Chat", "deepseek", 4096, 0.7, 30),
        ModelConfig("deepseek-coder", "DeepSeek Coder", "deepseek", 8192, 0.7, 45),
        ModelConfig("deepseek-chat-33b", "DeepSeek Chat 33B", "deepseek", 32768, 0.7, 60),
        ModelConfig("deepseek-coder-33b", "DeepSeek Coder 33B", "deepseek", 32768, 0.7, 60)
    )
    
    fun getModelByName(name: String): ModelConfig? {
        return availableModels.find { it.name == name }
    }
    
    fun getModelsByProvider(provider: String): List<ModelConfig> {
        return availableModels.filter { it.provider == provider }
    }
    
    fun getDefaultModel(): ModelConfig {
        return availableModels.first()
    }
} 