package com.example.aikeyboard.text

import com.example.aikeyboard.text.composing.Composer

class HindiComposer : Composer {
    override val id = "hindi"
    override val label = "Hindi"
    override val toRead = 4

    // Romanization to Devanagari mapping
    private val romanizationToDevanagari = mapOf(
        // Basic vowels
        "a" to "अ", "aa" to "आ", "i" to "इ", "ii" to "ई", "u" to "उ", "uu" to "ऊ",
        "e" to "ए", "ai" to "ऐ", "o" to "ओ", "au" to "औ", "r" to "ऋ", "rr" to "ॠ",
        
        // Basic consonants
        "k" to "क", "kh" to "ख", "g" to "ग", "gh" to "घ", "ng" to "ङ",
        "ch" to "च", "chh" to "छ", "j" to "ज", "jh" to "झ", "ny" to "ञ",
        "t" to "ट", "th" to "ठ", "d" to "ड", "dh" to "ढ", "n" to "ण",
        "p" to "प", "ph" to "फ", "b" to "ब", "bh" to "भ", "m" to "म",
        "y" to "य", "r" to "र", "l" to "ल", "v" to "व", "w" to "व",
        "sh" to "श", "s" to "स", "h" to "ह", "ksh" to "क्ष", "tr" to "त्र", "gy" to "ज्ञ",
        
        // Common words
        "namaste" to "नमस्ते", "dhanyavaad" to "धन्यवाद", "kripaya" to "कृपया",
        "haan" to "हाँ", "nahi" to "नहीं", "shayad" to "शायद", "sambhav" to "संभव",
        "asambhav" to "असंभव", "sach" to "सच", "jhooth" to "झूठ", "sahi" to "सही",
        "galat" to "गलत", "achha" to "अच्छा", "bura" to "बुरा", "behtar" to "बेहतर",
        "badtar" to "बदतर", "sundar" to "सुंदर", "badsurat" to "बदसूरत", "bada" to "बड़ा",
        "chota" to "छोटा", "lamba" to "लंबा", "chota" to "छोटा", "chaura" to "चौड़ा",
        "patla" to "पतला", "mota" to "मोटा", "patla" to "पतला", "bhari" to "भारी",
        "halka" to "हल्का", "purana" to "पुराना", "naya" to "नया", "jawan" to "जवान",
        "adunik" to "आधुनिक", "puratana" to "पुरातन", "shastriya" to "शास्त्रीय", "samkalin" to "समकालीन",
        
        // Numbers
        "ek" to "एक", "do" to "दो", "teen" to "तीन", "char" to "चार",
        "paanch" to "पाँच", "cheh" to "छह", "saat" to "सात", "aath" to "आठ",
        "nau" to "नौ", "das" to "दस", "gyarah" to "ग्यारह", "barah" to "बारह",
        "terah" to "तेरह", "chaudah" to "चौदह", "pandrah" to "पंद्रह",
        "solah" to "सोलह", "satrah" to "सत्रह", "athaarah" to "अठारह",
        "unnees" to "उन्नीस", "bees" to "बीस", "sau" to "सौ", "hazaar" to "हज़ार",
        "lakh" to "लाख", "karod" to "करोड़",
        
        // Days of the week
        "ravivar" to "रविवार", "somvar" to "सोमवार", "mangalvar" to "मंगलवार",
        "budhvar" to "बुधवार", "guruvar" to "गुरुवार", "shukravar" to "शुक्रवार",
        "shanivar" to "शनिवार", "hafta" to "हफ्ता", "saptah" to "सप्ताह",
        
        // Months
        "janvari" to "जनवरी", "farvari" to "फरवरी", "march" to "मार्च",
        "april" to "अप्रैल", "may" to "मई", "june" to "जून",
        "july" to "जुलाई", "agast" to "अगस्त", "saptambar" to "सितंबर",
        "aktoobar" to "अक्टूबर", "navambar" to "नवंबर", "disambar" to "दिसंबर",
        
        // Colors
        "laal" to "लाल", "neela" to "नीला", "hara" to "हरा", "peela" to "पीला",
        "kaala" to "काला", "safed" to "सफेद", "surahi" to "सुराही", "bhura" to "भूरा",
        "narangi" to "नारंगी", "gulabi" to "गुलाबी", "jamuni" to "जामुनी", "firozi" to "फिरोज़ी",
        "pink" to "गुलाबी", "jamuni" to "जामुनी", "beige" to "बेज", "chaandi" to "चाँदी",
        "sona" to "सोना", "jamuni" to "जामुनी", "indigo" to "इंडिगो", "cyan" to "सायन",
        
        // Food
        "roti" to "रोटी", "doodh" to "दूध", "paani" to "पानी", "chai" to "चाय",
        "sharab" to "शराब", "daaru" to "दारू", "chawal" to "चावल", "murga" to "मुर्गा",
        "gosht" to "गोश्त", "machhli" to "मछली", "sabzi" to "सब्जी", "phal" to "फल",
        "tamatar" to "टमाटर", "pyaaz" to "प्याज", "lahsun" to "लहसुन", "aalu" to "आलू",
        "gajar" to "गाजर", "salad" to "सलाद", "seb" to "सेब", "santra" to "संतरा",
        "kela" to "केला", "angur" to "अंगूर", "strawberry" to "स्ट्रॉबेरी", "nimbu" to "नींबू",
        "nashpati" to "नाशपाती", "aadu" to "आड़ू", "tarbuj" to "तरबूज",
        
        // Family
        "pita" to "पिता", "mata" to "माता", "dada" to "दादा", "dadi" to "दादी",
        "bhai" to "भाई", "behen" to "बहन", "chacha" to "चाचा", "chachi" to "चाची",
        "mama" to "मामा", "mami" to "मामी", "beta" to "बेटा", "beti" to "बेटी",
        "pota" to "पोता", "poti" to "पोती", "bhatija" to "भतीजा", "bhatiji" to "भतीजी",
        "jija" to "जीजा", "sali" to "साली", "sasur" to "ससुर", "saas" to "सास",
        
        // Common verbs
        "hona" to "होना", "karna" to "करना", "dena" to "देना", "lena" to "लेना",
        "jana" to "जाना", "aana" to "आना", "dekhna" to "देखना", "jaanna" to "जानना",
        "sakna" to "सकना", "chahiye" to "चाहिए", "chaahna" to "चाहना", "pasand" to "पसंद",
        "bolna" to "बोलना", "sunna" to "सुनना", "padhna" to "पढ़ना", "likhna" to "लिखना",
        "khana" to "खाना", "peena" to "पीना", "sona" to "सोना", "kaam" to "काम",
        "seekhna" to "सीखना", "sikhaana" to "सिखाना", "madad" to "मदद", "zarurat" to "ज़रूरत",
        "dhoondhna" to "ढूंढना", "milna" to "मिलना", "paana" to "पाना", "uthana" to "उठाना",
        "rakhna" to "रखना", "laana" to "लाना", "le" to "ले", "kharidna" to "खरीदना",
        "bechna" to "बेचना", "dena" to "देना", "kharcha" to "खर्च", "bchanat" to "बचत",
        "kamaana" to "कमाना", "kharch" to "खर्च", "paana" to "पाना", "dena" to "देना",
        "ginna" to "गिनना", "ginna" to "गिनना", "ginna" to "गिनना", "ginna" to "गिनना"
    )

    override fun getActions(precedingText: String, toInsert: String): Pair<Int, String> {
        val input = precedingText + toInsert
        
        // Check for romanization to Devanagari conversion
        for ((romanization, devanagari) in romanizationToDevanagari) {
            if (input.endsWith(romanization)) {
                return Pair(romanization.length - toInsert.length, devanagari)
            }
        }
        
        // No conversion found, just append the character
        return Pair(0, toInsert)
    }
} 