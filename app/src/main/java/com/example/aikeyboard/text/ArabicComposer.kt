package com.example.aikeyboard.text

import com.example.aikeyboard.text.composing.Composer

class ArabicComposer : Composer {
    override val id = "arabic"
    override val label = "Arabic"
    override val toRead = 4

    // Transliteration to Arabic mapping
    private val transliterationToArabic = mapOf(
        // Basic vowels
        "a" to "ا", "i" to "ي", "u" to "و", "e" to "ي", "o" to "و",
        
        // Basic consonants
        "b" to "ب", "t" to "ت", "th" to "ث", "j" to "ج", "h" to "ح",
        "kh" to "خ", "d" to "د", "dh" to "ذ", "r" to "ر", "z" to "ز",
        "s" to "س", "sh" to "ش", "s" to "ص", "d" to "ض", "t" to "ط",
        "z" to "ظ", "gh" to "غ", "f" to "ف", "q" to "ق", "k" to "ك",
        "l" to "ل", "m" to "م", "n" to "ن", "h" to "ه", "w" to "و", "y" to "ي",
        
        // Common words
        "marhaba" to "مرحبا", "ahlan" to "أهلا", "shukran" to "شكرا",
        "afwan" to "عفوا", "min" to "من", "fadlik" to "من فضلك",
        "la" to "لا", "naam" to "نعم", "mumkin" to "ممكن", "mumtaz" to "ممتاز",
        "jayyid" to "جيد", "sayyid" to "سيء", "ahsan" to "أحسن", "aswa" to "أسوأ",
        "jamil" to "جميل", "qabih" to "قبيح", "kabir" to "كبير", "saghir" to "صغير",
        "tawil" to "طويل", "qasir" to "قصر", "arid" to "عريض", "dayiq" to "ضيق",
        "samik" to "سميك", "raqiq" to "رفيق", "thaqil" to "ثقيل", "khafif" to "خفيف",
        "qadim" to "قديم", "jadid" to "جديد", "shabab" to "شاب", "hadith" to "حديث",
        "qadim" to "قديم", "jadid" to "جديد", "hadith" to "حديث", "qadim" to "قديم",
        
        // Numbers
        "wahid" to "واحد", "ithnan" to "اثنان", "thalatha" to "ثلاثة", "arba'a" to "أربعة",
        "khamsa" to "خمسة", "sitta" to "ستة", "sab'a" to "سبعة", "thamaniya" to "ثمانية",
        "tis'a" to "تسعة", "ashara" to "عشرة", "ahad" to "أحد", "ithnan" to "اثنان",
        "thalatha" to "ثلاثة", "arba'a" to "أربعة", "khamsa" to "خمسة", "sitta" to "ستة",
        "sab'a" to "سبعة", "thamaniya" to "ثمانية", "tis'a" to "تسعة", "ashara" to "عشرة",
        "mi'a" to "مئة", "alf" to "ألف", "million" to "مليون", "milliard" to "مليار",
        
        // Days of the week
        "al-ahad" to "الأحد", "al-ithnayn" to "الاثنين", "al-thulatha" to "الثلاثاء",
        "al-arba'a" to "الأربعاء", "al-khamis" to "الخميس", "al-jum'a" to "الجمعة",
        "al-sabt" to "السبت", "usbu'" to "أسبوع", "nihayat" to "نهاية", "usbu'" to "أسبوع",
        
        // Months
        "yanayir" to "يناير", "febrayir" to "فبراير", "maris" to "مارس",
        "abril" to "أبريل", "mayu" to "مايو", "yunyu" to "يونيو",
        "yulyu" to "يوليو", "aghusus" to "أغسطس", "sibtambar" to "سبتمبر",
        "uktubar" to "أكتوبر", "nufambar" to "نوفمبر", "disambar" to "ديسمبر",
        
        // Colors
        "ahmar" to "أحمر", "azraq" to "أزرق", "akhdar" to "أخضر", "asfar" to "أصفر",
        "aswad" to "أسود", "abyad" to "أبيض", "ramadi" to "رمادي", "bunni" to "بني",
        "burtuqali" to "برتقالي", "wardi" to "وردي", "banafsaji" to "بنفسجي", "turquzi" to "تركوازي",
        "pink" to "وردي", "banafsaji" to "بنفسجي", "beige" to "بيج", "fiddi" to "فضي",
        "dhahabi" to "ذهبي", "purpura" to "أرجواني", "indigo" to "نيلي", "cyan" to "سماوي",
        
        // Food
        "khubz" to "خبز", "halib" to "حليب", "ma'" to "ماء", "qahwa" to "قهوة",
        "shai" to "شاي", "nabidh" to "نبيذ", "bira" to "بيرة", "aruz" to "أرز",
        "dajaj" to "دجاج", "lahm" to "لحم", "samak" to "سمك", "khudrawat" to "خضروات",
        "fawakih" to "فواكه", "tamatin" to "طماطم", "basal" to "بصل", "thum" to "ثوم",
        "batata" to "بطاطا", "jazar" to "جزر", "khas" to "خس", "tuffah" to "تفاح",
        "burtuqal" to "برتقال", "mawz" to "موز", "inab" to "عنب", "farawla" to "فراولة",
        "laymun" to "ليمون", "kummathra" to "كمثرى", "khawkh" to "خوخ", "batikh" to "بطيخ",
        
        // Family
        "ab" to "أب", "umm" to "أم", "jadd" to "جد", "jadda" to "جدة",
        "akh" to "أخ", "ukht" to "أخت", "amm" to "عم", "khal" to "خال",
        "ibn" to "ابن", "bint" to "بنت", "ibn" to "ابن", "bint" to "بنت",
        "hafid" to "حفيد", "hafida" to "حفيدة", "ibn" to "ابن", "bint" to "بنت",
        "sihr" to "صهر", "sihr" to "صهر", "ham" to "حم", "ham" to "حم",
        
        // Common verbs
        "kana" to "كان", "yakun" to "يكون", "sara" to "صار", "yusir" to "يصير",
        "ra'a" to "رأى", "yara" to "يرى", "alima" to "علم", "ya'lam" to "يعلم",
        "qadara" to "قدر", "yaqdir" to "يقدر", "yajib" to "يجب", "arada" to "أراد",
        "yurid" to "يريد", "habba" to "حب", "yuhibb" to "يحب", "kariha" to "كره",
        "yakrah" to "يكره", "qala" to "قال", "yaqul" to "يقول", "sami'a" to "سمع",
        "yasma'" to "يسمع", "qara'a" to "قرأ", "yaqra'" to "يقرأ", "kataba" to "كتب",
        "yaktub" to "يكتب", "akala" to "أكل", "ya'kul" to "يأكل", "shariba" to "شرب",
        "yashrab" to "يشرب", "nama" to "نام", "yanam" to "ينام", "amila" to "عمل",
        "ya'mal" to "يعمل", "ta'allama" to "تعلم", "yata'allam" to "يتعلم", "darasa" to "درس",
        "yadrus" to "يدرس", "allama" to "علم", "yu'allim" to "يعلم", "sa'ada" to "ساعد",
        "yusa'id" to "يساعد", "ihtaja" to "احتاج", "yahtaj" to "يحتاج", "bahatha" to "بحث",
        "yabhath" to "يبحث", "wajada" to "وجد", "yajid" to "يجد", "hasala" to "حصل",
        "yahsul" to "يحصل", "akhadha" to "أخذ", "ya'khudh" to "يأخذ", "a'ta" to "أعطى",
        "yu'ti" to "يعطي", "jab" to "جاب", "yajib" to "يجيب", "hamala" to "حمل",
        "yahmil" to "يحمل", "ishtara" to "اشترى", "yashtari" to "يشتري", "ba'a" to "باع",
        "yabi'" to "يبيع", "dafa'a" to "دفع", "yadfa'" to "يدفع", "sama" to "سما",
        "yasum" to "يسوم", "akhfada" to "أخفض", "yukhfid" to "يخفض", "kasaba" to "كسب",
        "yaksib" to "يكسب", "anfaqa" to "أنفق", "yunfiq" to "ينفق", "hasala" to "حصل",
        "yahsul" to "يحصل", "dafa'a" to "دفع", "yadfa'" to "يدفع", "hasaba" to "حسب",
        "yahsub" to "يحسب", "adda" to "عد", "ya'udd" to "يعد", "adda" to "عد",
        "ya'udd" to "يعد", "adda" to "عد", "ya'udd" to "يعد", "adda" to "عد",
        "ya'udd" to "يعد"
    )

    override fun getActions(precedingText: String, toInsert: String): Pair<Int, String> {
        val input = precedingText + toInsert
        
        // Check for transliteration to Arabic conversion
        for ((transliteration, arabic) in transliterationToArabic) {
            if (input.endsWith(transliteration)) {
                return Pair(transliteration.length - toInsert.length, arabic)
            }
        }
        
        // No conversion found, just append the character
        return Pair(0, toInsert)
    }
} 