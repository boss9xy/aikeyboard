package com.example.aikeyboard

import android.content.Context
import com.example.aikeyboard.models.Language

class PromptManager(private val context: Context) {
    
    private val languageManager = LanguageManager(context)
    
    // Get custom prompt or fallback to default
    private fun getCustomPrompt(promptType: String): String {
        val prefs = context.getSharedPreferences("AIKeyboardPrefs", Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean("prompt_enabled_$promptType", false)
        if (!isEnabled) return ""
        return prefs.getString("prompt_$promptType", "") ?: ""
    }
    
    fun getSuggestPrompt(text: String): String {
        // Try to get custom prompt first
        val customPrompt = getCustomPrompt("gpt_suggest")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nVăn bản cần gợi ý: '$text'"
        }
        
        // Fallback to default
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Dựa trên cuộc trò chuyện này: '$text'\n\nTạo 3 đề xuất phản hồi khác nhau cho kiểu sau:\n1. Hài hước mix với styles Thế hệ Z\nLàm cho mỗi đề xuất trở nên độc đáo và phù hợp với ngữ cảnh, phong cách ngôn ngữ của văn bản đề xuất nên tương tự với nội dung cuộc trò chuyện. Khi trả lời chỉ in văn bản đã chỉnh sửa không in chú thích hoặc các tag không liên quan khác, đây là nội dung:"
            Language.ENGLISH -> "Based on this conversation: '$text'\n\nCreate 3 different response suggestions for the following type:\n1. Humorous mixed with Gen Z styles\nMake each suggestion unique and appropriate for the context, the language style of the suggestion text should be similar to the conversation content. When answering, only print the modified text, do not print comments or other unrelated tags, this is the content:"
            Language.CHINESE -> "基于这段对话：'$text'\n\n为以下类型创建3个不同的回复建议：\n1. 幽默混合Z世代风格\n让每个建议都独特且符合上下文，建议文本的语言风格与对话内容相似。回答时只输出修改后的文本，不要输出注释或其他无关标签，这是内容："
            Language.JAPANESE -> "この会話に基づいて：'$text'\n\n以下のタイプで3つの異なる返信提案を作成してください：\n1. ユーモアとZ世代スタイルのミックス\n各提案をユニークで文脈に適したものにし、提案テキストの言語スタイルを会話内容と似たものにしてください。回答時は修正されたテキストのみを出力し、コメントやその他の無関係なタグは出力しないでください。これが内容です："
            Language.KOREAN -> "이 대화를 바탕으로：'$text'\n\n다음 유형에 대해 3가지 다른 답변 제안을 생성하세요：\n1. 유머와 Z세대 스타일 혼합\n각 제안을 독특하고 맥락에 적합하게 만들고, 제안 텍스트의 언어 스타일을 대화 내용과 유사하게 만드세요. 답변할 때는 수정된 텍스트만 출력하고, 주석이나 기타 관련 없는 태그는 출력하지 마세요. 이것이 내용입니다："
            Language.FRENCH -> "Basé sur cette conversation : '$text'\n\nCréez 3 suggestions de réponses différentes pour le type suivant :\n1. Humoristique mélangé avec les styles Gen Z\nRendez chaque suggestion unique et appropriée au contexte, le style linguistique du texte suggéré doit être similaire au contenu de la conversation. Lors de la réponse, n'outputez que le texte modifié, n'outputez pas de commentaires ou d'autres balises non liées, voici le contenu :"
            Language.GERMAN -> "Basierend auf diesem Gespräch: '$text'\n\nErstellen Sie 3 verschiedene Antwortvorschläge für den folgenden Typ:\n1. Humorvoll gemischt mit Gen Z Stilen\nMachen Sie jeden Vorschlag einzigartig und passend zum Kontext, der Sprachstil des vorgeschlagenen Textes sollte dem Gesprächsinhalt ähnlich sein. Beim Antworten nur den modifizierten Text ausgeben, keine Kommentare oder andere unbezogene Tags ausgeben, das ist der Inhalt:"
            Language.SPANISH -> "Basado en esta conversación: '$text'\n\nCrea 3 sugerencias de respuesta diferentes para el siguiente tipo:\n1. Humorístico mezclado con estilos Gen Z\nHaz cada sugerencia única y apropiada al contexto, el estilo lingüístico del texto sugerido debe ser similar al contenido de la conversación. Al responder, solo emite el texto modificado, no emita comentarios u otras etiquetas no relacionadas, este es el contenido:"
            Language.ITALIAN -> "Basato su questa conversazione: '$text'\n\nCrea 3 suggerimenti di risposta diversi per il seguente tipo:\n1. Umoristico mescolato con stili Gen Z\nRendi ogni suggerimento unico e appropriato al contesto, lo stile linguistico del testo suggerito deve essere simile al contenuto della conversazione. Quando rispondi, emetti solo il testo modificato, non emettere commenti o altri tag non correlati, questo è il contenuto:"
            Language.RUSSIAN -> "Основываясь на этом разговоре: '$text'\n\nСоздайте 3 различных предложения ответов для следующего типа:\n1. Юмористический смешанный со стилями Gen Z\nСделайте каждое предложение уникальным и подходящим к контексту, языковой стиль предлагаемого текста должен быть похож на содержание разговора. При ответе выводите только измененный текст, не выводите комментарии или другие несвязанные теги, это содержание:"
            Language.ARABIC -> "بناءً على هذه المحادثة: '$text'\n\nأنشئ 3 اقتراحات رد مختلفة للنوع التالي:\n1. فكاهي مختلط مع أنماط الجيل Z\nاجعل كل اقتراح فريداً ومناسباً للسياق، يجب أن يكون النمط اللغوي للنص المقترح مشابهاً لمحتوى المحادثة. عند الرد، اطبع النص المعدل فقط، لا تطبع التعليقات أو العلامات الأخرى غير المرتبطة، هذا هو المحتوى:"
            Language.THAI -> "จากบทสนทนานี้: '$text'\n\nสร้างข้อเสนอแนะการตอบกลับ 3 แบบที่แตกต่างกันสำหรับประเภทต่อไปนี้:\n1. อารมณ์ขันผสมกับสไตล์ Gen Z\nทำให้แต่ละข้อเสนอแนะเป็นเอกลักษณ์และเหมาะสมกับบริบท สไตล์ภาษาของข้อความที่แนะนำควรคล้ายกับเนื้อหาการสนทนา เมื่อตอบกลับ ให้แสดงเฉพาะข้อความที่แก้ไขแล้ว อย่าแสดงความคิดเห็นหรือแท็กอื่นๆ ที่ไม่เกี่ยวข้อง นี่คือเนื้อหา:"
            Language.HINDI -> "इस बातचीत के आधार पर: '$text'\n\nनिम्नलिखित प्रकार के लिए 3 अलग-अलग प्रतिक्रिया सुझाव बनाएं:\n1. जेन Z स्टाइल के साथ हास्य मिश्रित\nप्रत्येक सुझाव को अद्वितीय और संदर्भ के लिए उपयुक्त बनाएं, सुझाए गए पाठ की भाषा शैली बातचीत की सामग्री के समान होनी चाहिए। जब जवाब देते हैं, तो केवल संशोधित पाठ आउटपुट करें, टिप्पणियां या अन्य असंबंधित टैग आउटपुट न करें, यह सामग्री है:"
        }
    }
    
    fun getSpellCheckPrompt(text: String): String {
        // Try to get custom prompt first
        val customPrompt = getCustomPrompt("gpt_spell_check")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nVăn bản cần kiểm tra: '$text'"
        }
        
        // Fallback to default
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Hãy sửa lỗi chính tả cho văn bản sau (giữ nguyên ý nghĩa, ngôn ngữ văn bản gốc, chỉ sửa lỗi chính tả, thêm dấu câu phù hợp), khi trả lời chỉ in văn bản đã sửa đổi không in chú thích hoặc các tag không liên quan khác, đây là nội dung cần sửa: '$text'"
            Language.ENGLISH -> "Please correct the spelling errors in the following text (maintain the original meaning, maintain the original text language, only correct spelling errors, add appropriate punctuation), when answering only print the modified text, do not print comments or other unrelated tags, this is the content that needs to be corrected: '$text'"
            Language.CHINESE -> "请修正以下文本的拼写错误（保持原意，保持原文语言，只修正拼写错误，添加适当的标点符号），回答时只输出修改后的文本，不要输出注释或其他无关标签，这是需要修正的内容：'$text'"
            Language.JAPANESE -> "以下のテキストのスペルミスを修正してください（意味を保持し、元のテキスト言語を保持し、スペルミスのみを修正し、適切な句読点を追加）、回答時は修正されたテキストのみを出力し、コメントやその他の無関係なタグは出力しないでください。これが修正が必要な内容です：'$text'"
            Language.KOREAN -> "다음 텍스트의 맞춤법 오류를 수정하세요（의미를 유지하고, 원본 텍스트 언어를 유지하며, 맞춤법 오류만 수정하고, 적절한 문장 부호를 추가）、답변할 때는 수정된 텍스트만 출력하고, 주석이나 기타 관련 없는 태그는 출력하지 마세요. 이것이 수정이 필요한 내용입니다：'$text'"
            Language.FRENCH -> "Veuillez corriger les erreurs d'orthographe dans le texte suivant (maintenir le sens original, maintenir la langue du texte original, corriger uniquement les erreurs d'orthographe, ajouter une ponctuation appropriée), lors de la réponse, n'outputez que le texte modifié, n'outputez pas de commentaires ou d'autres balises non liées, voici le contenu qui doit être corrigé : '$text'"
            Language.GERMAN -> "Bitte korrigieren Sie die Rechtschreibfehler im folgenden Text (behalten Sie die ursprüngliche Bedeutung bei, behalten Sie die ursprüngliche Textsprache bei, korrigieren Sie nur Rechtschreibfehler, fügen Sie angemessene Interpunktion hinzu), beim Antworten geben Sie nur den modifizierten Text aus, geben Sie keine Kommentare oder andere unbezogene Tags aus, das ist der Inhalt, der korrigiert werden muss: '$text'"
            Language.SPANISH -> "Por favor, corrija los errores ortográficos en el siguiente texto (mantenga el significado original, mantenga el idioma del texto original, corrija solo errores ortográficos, agregue puntuación apropiada), al responder solo emita el texto modificado, no emita comentarios u otras etiquetas no relacionadas, este es el contenido que necesita ser corregido: '$text'"
            Language.ITALIAN -> "Per favore, correggi gli errori di ortografia nel seguente testo (mantieni il significato originale, mantieni la lingua del testo originale, correggi solo errori di ortografia, aggiungi punteggiatura appropriata), quando rispondi emetti solo il testo modificato, non emettere commenti o altri tag non correlati, questo è il contenuto che deve essere corretto: '$text'"
            Language.RUSSIAN -> "Пожалуйста, исправьте орфографические ошибки в следующем тексте (сохраните исходное значение, сохраните исходный язык текста, исправьте только орфографические ошибки, добавьте соответствующую пунктуацию), при ответе выводите только измененный текст, не выводите комментарии или другие несвязанные теги, это содержание, которое нужно исправить: '$text'"
            Language.ARABIC -> "يرجى تصحيح الأخطاء الإملائية في النص التالي (الحفاظ على المعنى الأصلي، الحفاظ على لغة النص الأصلية، تصحيح الأخطاء الإملائية فقط، إضافة علامات الترقيم المناسبة)، عند الرد اطبع النص المعدل فقط، لا تطبع التعليقات أو العلامات الأخرى غير المرتبطة، هذا هو المحتوى الذي يحتاج إلى تصحيح: '$text'"
            Language.THAI -> "กรุณาแก้ไขข้อผิดพลาดในการสะกดในข้อความต่อไปนี้（รักษาความหมายเดิม รักษาภาษาข้อความเดิม แก้ไขข้อผิดพลาดในการสะกดเท่านั้น เพิ่มเครื่องหมายวรรคตอนที่เหมาะสม）เมื่อตอบกลับ ให้แสดงเฉพาะข้อความที่แก้ไขแล้ว อย่าแสดงความคิดเห็นหรือแท็กอื่นๆ ที่ไม่เกี่ยวข้อง นี่คือเนื้อหาที่ต้องแก้ไข: '$text'"
            Language.HINDI -> "कृपया निम्नलिखित पाठ में वर्तनी त्रुटियों को सुधारें (मूल अर्थ बनाए रखें, मूल पाठ भाषा बनाए रखें, केवल वर्तनी त्रुटियों को सुधारें, उचित विराम चिह्न जोड़ें), जब जवाब देते हैं तो केवल संशोधित पाठ आउटपुट करें, टिप्पणियां या अन्य असंबंधित टैग आउटपुट न करें, यह वह सामग्री है जिसे सुधारने की आवश्यकता है: '$text'"
        }
    }
    
    fun getTranslatePrompt(text: String, targetLanguage: String): String {
        // Try to get custom prompt first
        val customPrompt = getCustomPrompt("gpt_translate")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nVăn bản cần dịch: '$text'"
        }
        
        // Fallback to default
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Dịch đoạn văn sau sang $targetLanguage: '$text'"
            Language.ENGLISH -> "Translate the following paragraph to $targetLanguage: '$text'"
            Language.CHINESE -> "将以下段落翻译成$targetLanguage：'$text'"
            Language.JAPANESE -> "以下の段落を${targetLanguage}に翻訳してください：'$text'"
            Language.KOREAN -> "다음 단락을 ${targetLanguage}로 번역하세요：'$text'"
            Language.FRENCH -> "Traduisez le paragraphe suivant en $targetLanguage : '$text'"
            Language.GERMAN -> "Übersetzen Sie den folgenden Absatz in $targetLanguage: '$text'"
            Language.SPANISH -> "Traduce el siguiente párrafo a $targetLanguage: '$text'"
            Language.ITALIAN -> "Traduci il seguente paragrafo in $targetLanguage: '$text'"
            Language.RUSSIAN -> "Переведите следующий абзац на $targetLanguage: '$text'"
            Language.ARABIC -> "ترجم الفقرة التالية إلى $targetLanguage: '$text'"
            Language.THAI -> "แปลย่อหน้าต่อไปนี้เป็น $targetLanguage: '$text'"
            Language.HINDI -> "निम्नलिखित अनुच्छेद को $targetLanguage में अनुवाद करें: '$text'"
        }
    }
    
    fun getAIAssistantPrompt(text: String): String {
        // Try to get custom prompt first
        val customPrompt = getCustomPrompt("ai_assistant")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nCâu hỏi của người dùng: '$text'"
        }
        
        // Fallback to default
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một trợ lý AI thông minh và hữu ích. Hãy trả lời câu hỏi của người dùng một cách ngắn gọn, chính xác và dễ hiểu. Nếu câu hỏi không rõ ràng, hãy yêu cầu làm rõ thêm.\n\nCâu hỏi: '$text'"
            Language.ENGLISH -> "You are a smart and helpful AI assistant. Please answer the user's question concisely, accurately and understandably. If the question is unclear, please ask for clarification.\n\nQuestion: '$text'"
            else -> "You are a smart and helpful AI assistant. Please answer the user's question concisely, accurately and understandably. If the question is unclear, please ask for clarification.\n\nQuestion: '$text'"
        }
    }
    
    fun getVoiceToTextPrompt(audioText: String): String {
        // Try to get custom prompt first
        val customPrompt = getCustomPrompt("voice_to_text")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nVăn bản âm thanh: '$audioText'"
        }
        
        // Fallback to default
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Hãy chuyển đổi giọng nói thành văn bản chính xác. Giữ nguyên dấu câu, ngữ điệu và cấu trúc câu. Nếu có từ không rõ, hãy đánh dấu [không rõ].\n\nVăn bản âm thanh: '$audioText'"
            Language.ENGLISH -> "Please convert speech to text accurately. Maintain punctuation, intonation and sentence structure. If there are unclear words, mark them as [unclear].\n\nAudio text: '$audioText'"
            else -> "Please convert speech to text accurately. Maintain punctuation, intonation and sentence structure. If there are unclear words, mark them as [unclear].\n\nAudio text: '$audioText'"
        }
    }
    
    // New methods for all AI buttons
    fun getGPTAskPrompt(text: String): String {
        val customPrompt = getCustomPrompt("gpt_ask")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nCâu hỏi: '$text'"
        }
        
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một trợ lý AI thông minh và hữu ích. Hãy trả lời câu hỏi của người dùng một cách ngắn gọn, chính xác và dễ hiểu.\n\nCâu hỏi: '$text'"
            Language.ENGLISH -> "You are a smart and helpful AI assistant. Please answer the user's question concisely, accurately and understandably.\n\nQuestion: '$text'"
            else -> "You are a smart and helpful AI assistant. Please answer the user's question concisely, accurately and understandably.\n\nQuestion: '$text'"
        }
    }
    
    fun getOlamaAskPrompt(text: String): String {
        val customPrompt = getCustomPrompt("olama_ask")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nCâu hỏi: '$text'"
        }
        
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một trợ lý AI thông minh và hữu ích. Hãy trả lời câu hỏi của người dùng một cách ngắn gọn, chính xác và dễ hiểu.\n\nCâu hỏi: '$text'"
            Language.ENGLISH -> "You are a smart and helpful AI assistant. Please answer the user's question concisely, accurately and understandably.\n\nQuestion: '$text'"
            else -> "You are a smart and helpful AI assistant. Please answer the user's question concisely, accurately and understandably.\n\nQuestion: '$text'"
        }
    }
    
    fun getDeepSeekTranslatePrompt(text: String, targetLanguage: String): String {
        val customPrompt = getCustomPrompt("deepseek_translate")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nVăn bản cần dịch: '$text'"
        }
        
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Dịch đoạn văn sau sang $targetLanguage: '$text'"
            Language.ENGLISH -> "Translate the following paragraph to $targetLanguage: '$text'"
            else -> "Translate the following paragraph to $targetLanguage: '$text'"
        }
    }
    
    fun getAskDeepSeekPrompt(text: String): String {
        val customPrompt = getCustomPrompt("ask_deepseek")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nCâu hỏi: '$text'"
        }
        
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một trợ lý AI thông minh và hữu ích. Hãy trả lời câu hỏi của người dùng một cách ngắn gọn, chính xác và dễ hiểu.\n\nCâu hỏi: '$text'"
            Language.ENGLISH -> "You are a smart and helpful AI assistant. Please answer the user's question concisely, accurately and understandably.\n\nQuestion: '$text'"
            else -> "You are a smart and helpful AI assistant. Please answer the user's question concisely, accurately and understandably.\n\nQuestion: '$text'"
        }
    }

    fun getAskButtonPrompt(text: String): String {
        val customPrompt = getCustomPrompt("ask_button")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nVăn bản cần chuyển đổi: '$text'"
        }
        
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một AI chuyển đổi văn bản. Chuyển đổi văn bản được cung cấp sang kiểu chữ 𝒃𝒐𝒍𝒅 𝒊𝒕𝒂𝒍𝒊𝒄. Chỉ trả về văn bản đã chuyển đổi mà không có giải thích hoặc ngữ cảnh bổ sung.\n\nVăn bản: '$text'"
            Language.ENGLISH -> "You are a text converter. Convert the provided text to 𝒃𝒐𝒍𝒅 𝒊𝒕𝒂𝒍𝒊𝒄 font style. Only output the converted text without any additional explanation or context.\n\nText: '$text'"
            else -> "You are a text converter. Convert the provided text to 𝒃𝒐𝒍𝒅 𝒊𝒕𝒂𝒍𝒊𝒄 font style. Only output the converted text without any additional explanation or context.\n\nText: '$text'"
        }
    }
    
    fun getGPTSpellCheckPrompt(text: String): String {
        val customPrompt = getCustomPrompt("gpt_spell_check")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nVăn bản cần kiểm tra: '$text'"
        }
        
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Hãy sửa lỗi chính tả cho văn bản sau (giữ nguyên ý nghĩa, ngôn ngữ văn bản gốc, chỉ sửa lỗi chính tả, thêm dấu câu phù hợp), khi trả lời chỉ in văn bản đã sửa đổi không in chú thích hoặc các tag không liên quan khác, đây là nội dung cần sửa: '$text'"
            Language.ENGLISH -> "Please correct the spelling errors in the following text (maintain the original meaning, maintain the original text language, only correct spelling errors, add appropriate punctuation), when answering only print the modified text, do not print comments or other unrelated tags, this is the content that needs to be corrected: '$text'"
            else -> "Please correct the spelling errors in the following text (maintain the original meaning, maintain the original text language, only correct spelling errors, add appropriate punctuation), when answering only print the modified text, do not print comments or other unrelated tags, this is the content that needs to be corrected: '$text'"
        }
    }
    
    fun getDeepSeekSpellCheckPrompt(text: String): String {
        val customPrompt = getCustomPrompt("deepseek_spell_check")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nVăn bản cần kiểm tra: '$text'"
        }
        
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Hãy sửa lỗi chính tả cho văn bản sau (giữ nguyên ý nghĩa, ngôn ngữ văn bản gốc, chỉ sửa lỗi chính tả, thêm dấu câu phù hợp), khi trả lời chỉ in văn bản đã sửa đổi không in chú thích hoặc các tag không liên quan khác, đây là nội dung cần sửa: '$text'"
            Language.ENGLISH -> "Please correct the spelling errors in the following text (maintain the original meaning, maintain the original text language, only correct spelling errors, add appropriate punctuation), when answering only print the modified text, do not print comments or other unrelated tags, this is the content that needs to be corrected: '$text'"
            else -> "Please correct the spelling errors in the following text (maintain the original meaning, maintain the original text language, only correct spelling errors, add appropriate punctuation), when answering only print the modified text, do not print comments or other unrelated tags, this is the content that needs to be corrected: '$text'"
        }
    }
    
    fun getDeepSeekSuggestPrompt(text: String): String {
        val customPrompt = getCustomPrompt("deepseek_suggest")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nVăn bản cần gợi ý: '$text'"
        }
        
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Dựa trên cuộc trò chuyện này: '$text'\n\nTạo 3 đề xuất phản hồi khác nhau cho kiểu sau:\n1. Hài hước mix với styles Thế hệ Z\nLàm cho mỗi đề xuất trở nên độc đáo và phù hợp với ngữ cảnh, phong cách ngôn ngữ của văn bản đề xuất nên tương tự với nội dung cuộc trò chuyện. Khi trả lời chỉ in văn bản đã chỉnh sửa không in chú thích hoặc các tag không liên quan khác, đây là nội dung:"
            Language.ENGLISH -> "Based on this conversation: '$text'\n\nCreate 3 different response suggestions for the following type:\n1. Humorous mixed with Gen Z styles\nMake each suggestion unique and appropriate for the context, the language style of the suggestion text should be similar to the conversation content. When answering, only print the modified text, do not print comments or other unrelated tags, this is the content:"
            else -> "Based on this conversation: '$text'\n\nCreate 3 different response suggestions for the following type:\n1. Humorous mixed with Gen Z styles\nMake each suggestion unique and appropriate for the context, the language style of the suggestion text should be similar to the conversation content. When answering, only print the modified text, do not print comments or other unrelated tags, this is the content:"
        }
    }
    
    fun getAskPrompt(text: String): String {
        val customPrompt = getCustomPrompt("ask")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nCâu hỏi: '$text'"
        }
        
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Bạn là một trợ lý AI thông minh và hữu ích. Hãy trả lời câu hỏi của người dùng một cách ngắn gọn, chính xác và dễ hiểu.\n\nCâu hỏi: '$text'"
            Language.ENGLISH -> "You are a smart and helpful AI assistant. Please answer the user's question concisely, accurately and understandably.\n\nQuestion: '$text'"
            else -> "You are a smart and helpful AI assistant. Please answer the user's question concisely, accurately and understandably.\n\nQuestion: '$text'"
        }
    }
    
    fun getOlamaTranslatePrompt(text: String, targetLanguage: String): String {
        val customPrompt = getCustomPrompt("olama_translate")
        if (customPrompt.isNotEmpty()) {
            return "$customPrompt\n\nVăn bản cần dịch: '$text'"
        }
        
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Dịch đoạn văn sau sang $targetLanguage: '$text'"
            Language.ENGLISH -> "Translate the following paragraph to $targetLanguage: '$text'"
            else -> "Translate the following paragraph to $targetLanguage: '$text'"
        }
    }
    
    fun getThinkingText(): String {
        val currentLanguage = languageManager.getCurrentLanguage()
        return when (currentLanguage) {
            Language.VIETNAMESE -> "Đang suy nghĩ..."
            Language.ENGLISH -> "Thinking..."
            Language.CHINESE -> "正在思考..."
            Language.JAPANESE -> "考え中..."
            Language.KOREAN -> "생각 중..."
            Language.FRENCH -> "Réflexion..."
            Language.GERMAN -> "Nachdenken..."
            Language.SPANISH -> "Pensando..."
            Language.ITALIAN -> "Pensando..."
            Language.RUSSIAN -> "Думаю..."
            Language.ARABIC -> "أفكر..."
            Language.THAI -> "กำลังคิด..."
            Language.HINDI -> "सोच रहा हूं..."
        }
    }
} 