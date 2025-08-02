package com.example.aikeyboard.text

import com.example.aikeyboard.text.composing.Composer

class KoreanComposer : Composer {
    override val id = "korean"
    override val label = "Korean"
    override val toRead = 4

    // Romaja to Hangul mapping
    private val romajaToHangul = mapOf(
        // Basic vowels
        "a" to "아", "ya" to "야", "eo" to "어", "yeo" to "여", "o" to "오", "yo" to "요",
        "u" to "우", "yu" to "유", "eu" to "으", "i" to "이", "wi" to "위",
        
        // Basic consonants
        "g" to "ㄱ", "n" to "ㄴ", "d" to "ㄷ", "r" to "ㄹ", "m" to "ㅁ", "b" to "ㅂ", "s" to "ㅅ",
        "ng" to "ㅇ", "j" to "ㅈ", "ch" to "ㅊ", "k" to "ㅋ", "t" to "ㅌ", "p" to "ㅍ", "h" to "ㅎ",
        
        // Syllables with initial consonants
        "ga" to "가", "na" to "나", "da" to "다", "ra" to "라", "ma" to "마", "ba" to "바", "sa" to "사",
        "a" to "아", "ja" to "자", "cha" to "차", "ka" to "카", "ta" to "타", "pa" to "파", "ha" to "하",
        
        "ge" to "게", "ne" to "네", "de" to "데", "re" to "레", "me" to "메", "be" to "베", "se" to "세",
        "e" to "에", "je" to "제", "che" to "체", "ke" to "케", "te" to "테", "pe" to "페", "he" to "헤",
        
        "go" to "고", "no" to "노", "do" to "도", "ro" to "로", "mo" to "모", "bo" to "보", "so" to "소",
        "o" to "오", "jo" to "조", "cho" to "초", "ko" to "코", "to" to "토", "po" to "포", "ho" to "호",
        
        "gu" to "구", "nu" to "누", "du" to "두", "ru" to "루", "mu" to "무", "bu" to "부", "su" to "수",
        "u" to "우", "ju" to "주", "chu" to "추", "ku" to "쿠", "tu" to "투", "pu" to "푸", "hu" to "후",
        
        "gi" to "기", "ni" to "니", "di" to "디", "ri" to "리", "mi" to "미", "bi" to "비", "si" to "시",
        "i" to "이", "ji" to "지", "chi" to "치", "ki" to "키", "ti" to "티", "pi" to "피", "hi" to "히",
        
        // Common words
        "annyeong" to "안녕", "haseyo" to "하세요", "gamsahamnida" to "감사합니다",
        "kamsahamnida" to "감사합니다", "gomawo" to "고마워", "komawo" to "고마워",
        "mianhae" to "미안해", "joesonghamnida" to "죄송합니다", "cheonmaneyo" to "천만에요",
        "gwaenchanayo" to "괜찮아요", "kwaenchanayo" to "괜찮아요", "ne" to "네", "aniyo" to "아니요",
        "ye" to "예", "ani" to "아니", "mwo" to "뭐", "eodi" to "어디", "eonje" to "언제",
        "eolma" to "얼마", "wae" to "왜", "nugu" to "누구", "museun" to "무슨", "eotteoke" to "어떻게",
        "jal" to "잘", "johayo" to "좋아요", "joayo" to "좋아요", "sirheoyo" to "싫어요",
        "sirheoyo" to "싫어요", "saranghae" to "사랑해", "saranghamnida" to "사랑합니다",
        "annyeonghaseyo" to "안녕하세요", "annyeonghigeseyo" to "안녕히계세요",
        "annyeonghikaseyo" to "안녕히가세요", "jaljinaeyo" to "잘지내요", "jaljinaeseyo" to "잘지내세요",
        "manaseo" to "만나서", "bangapseumnida" to "반갑습니다", "bangapseumnida" to "반갑습니다",
        "bangapseumnida" to "반갑습니다", "bangapseumnida" to "반갑습니다", "bangapseumnida" to "반갑습니다",
        
        // Numbers
        "hana" to "하나", "dul" to "둘", "set" to "셋", "net" to "넷", "daseot" to "다섯",
        "yeoseot" to "여섯", "ilgop" to "일곱", "yeodeol" to "여덟", "ahop" to "아홉", "yeol" to "열",
        "il" to "일", "i" to "이", "sam" to "삼", "sa" to "사", "o" to "오",
        "yuk" to "육", "chil" to "칠", "pal" to "팔", "gu" to "구", "sip" to "십",
        "baek" to "백", "cheon" to "천", "man" to "만", "eok" to "억",
        
        // Days of the week
        "wol" to "월", "hwa" to "화", "su" to "수", "mok" to "목", "geum" to "금", "to" to "토", "il" to "일",
        "wolyoil" to "월요일", "hwayoil" to "화요일", "suyoil" to "수요일", "mokyoil" to "목요일",
        "geumyoil" to "금요일", "toyoil" to "토요일", "iryoil" to "일요일",
        
        // Months
        "ilwol" to "1월", "iwol" to "2월", "samwol" to "3월", "sawol" to "4월", "owol" to "5월",
        "yukwol" to "6월", "chilwol" to "7월", "palwol" to "8월", "guwol" to "9월",
        "sipwol" to "10월", "sibilwol" to "11월", "sibiwol" to "12월",
        
        // Colors
        "ppalgang" to "빨강", "norang" to "노랑", "parang" to "파랑", "chorok" to "초록",
        "geomjeong" to "검정", "hayang" to "하양", "jusaek" to "주색", "bora" to "보라",
        "jamsaek" to "잠색", "hongsaek" to "홍색", "noksaek" to "녹색", "cheongsaek" to "청색",
        
        // Food
        "bap" to "밥", "guk" to "국", "kimchi" to "김치", "bulgogi" to "불고기",
        "samgyeopsal" to "삼겹살", "galbi" to "갈비", "bibimbap" to "비빔밥",
        "japchae" to "잡채", "tteokbokki" to "떡볶이", "kimbap" to "김밥",
        "ramyeon" to "라면", "jajangmyeon" to "짜장면", "tangsu" to "탕수육",
        "mandu" to "만두", "pajeon" to "파전", "samgyetang" to "삼계탕",
        "galbitang" to "갈비탕", "seolleongtang" to "설렁탕", "samgyetang" to "삼계탕",
        
        // Family
        "appa" to "아빠", "eomma" to "엄마", "harabeoji" to "할아버지", "halmeoni" to "할머니",
        "abeoji" to "아버지", "eomeoni" to "어머니", "hyeong" to "형", "nuna" to "누나",
        "oppa" to "오빠", "dongsaeng" to "동생", "namdongsaeng" to "남동생", "yeodongsaeng" to "여동생",
        "ajussi" to "아저씨", "ajumma" to "아줌마", "sonnim" to "손님", "chin" to "친구",
        
        // Common phrases
        "annyeonghaseyo" to "안녕하세요", "gamsahamnida" to "감사합니다", "joesonghamnida" to "죄송합니다",
        "cheonmaneyo" to "천만에요", "gwaenchanayo" to "괜찮아요", "ne" to "네", "aniyo" to "아니요",
        "mwo" to "뭐", "eodi" to "어디", "eonje" to "언제", "eolma" to "얼마", "wae" to "왜",
        "nugu" to "누구", "museun" to "무슨", "eotteoke" to "어떻게", "jal" to "잘",
        "johayo" to "좋아요", "sirheoyo" to "싫어요", "saranghae" to "사랑해",
        "annyeonghigeseyo" to "안녕히계세요", "annyeonghikaseyo" to "안녕히가세요",
        "jaljinaeyo" to "잘지내요", "manaseo" to "만나서", "bangapseumnida" to "반갑습니다"
    )

    override fun getActions(precedingText: String, toInsert: String): Pair<Int, String> {
        val input = precedingText + toInsert
        
        // Check for romaja to hangul conversion
        for ((romaja, hangul) in romajaToHangul) {
            if (input.endsWith(romaja)) {
                return Pair(romaja.length - toInsert.length, hangul)
            }
        }
        
        // No conversion found, just append the character
        return Pair(0, toInsert)
    }
} 