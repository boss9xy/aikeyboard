package com.example.aikeyboard.text

import com.example.aikeyboard.text.composing.Composer

class JapaneseComposer : Composer {
    override val id = "japanese"
    override val label = "Japanese"
    override val toRead = 4

    // Romaji to Hiragana mapping
    private val romajiToHiragana = mapOf(
        // Basic vowels
        "a" to "あ", "i" to "い", "u" to "う", "e" to "え", "o" to "お",
        
        // K series
        "ka" to "か", "ki" to "き", "ku" to "く", "ke" to "け", "ko" to "こ",
        "ga" to "が", "gi" to "ぎ", "gu" to "ぐ", "ge" to "げ", "go" to "ご",
        
        // S series
        "sa" to "さ", "shi" to "し", "su" to "す", "se" to "せ", "so" to "そ",
        "za" to "ざ", "ji" to "じ", "zu" to "ず", "ze" to "ぜ", "zo" to "ぞ",
        
        // T series
        "ta" to "た", "chi" to "ち", "tsu" to "つ", "te" to "て", "to" to "と",
        "da" to "だ", "di" to "ぢ", "du" to "づ", "de" to "で", "do" to "ど",
        
        // N series
        "na" to "な", "ni" to "に", "nu" to "ぬ", "ne" to "ね", "no" to "の",
        
        // H series
        "ha" to "は", "hi" to "ひ", "fu" to "ふ", "he" to "へ", "ho" to "ほ",
        "ba" to "ば", "bi" to "び", "bu" to "ぶ", "be" to "べ", "bo" to "ぼ",
        "pa" to "ぱ", "pi" to "ぴ", "pu" to "ぷ", "pe" to "ぺ", "po" to "ぽ",
        
        // M series
        "ma" to "ま", "mi" to "み", "mu" to "む", "me" to "め", "mo" to "も",
        
        // Y series
        "ya" to "や", "yu" to "ゆ", "yo" to "よ",
        
        // R series
        "ra" to "ら", "ri" to "り", "ru" to "る", "re" to "れ", "ro" to "ろ",
        
        // W series
        "wa" to "わ", "wo" to "を",
        
        // N
        "n" to "ん",
        
        // Small characters
        "xa" to "ぁ", "xi" to "ぃ", "xu" to "ぅ", "xe" to "ぇ", "xo" to "ぉ",
        "xya" to "ゃ", "xyu" to "ゅ", "xyo" to "ょ",
        "xtsu" to "っ",
        
        // Long vowels
        "aa" to "ああ", "ii" to "いい", "uu" to "うう", "ee" to "ええ", "oo" to "おお",
        
        // Common words
        "watashi" to "わたし", "anata" to "あなた", "kare" to "かれ", "kanojo" to "かのじょ",
        "watashitachi" to "わたしたち", "anatatachi" to "あなたたち", "karera" to "かれら",
        "karejotachi" to "かのじょたち", "hito" to "ひと", "kodomo" to "こども",
        "otoko" to "おとこ", "onna" to "おんな", "sensei" to "せんせい",
        "gakusei" to "がくせい", "tomodachi" to "ともだち", "kazoku" to "かぞく",
        "ie" to "いえ", "uchi" to "うち", "gakko" to "がっこう", "kaisha" to "かいしゃ",
        "shigoto" to "しごと", "yasumi" to "やすみ", "tabemono" to "たべもの",
        "nomimono" to "のみもの", "mizu" to "みず", "ocha" to "おちゃ",
        "kohi" to "コーヒー", "biiru" to "ビール", "sake" to "さけ",
        "pan" to "パン", "gohan" to "ごはん", "asagohan" to "あさごはん",
        "hirugohan" to "ひるごはん", "bangohan" to "ばんごはん",
        "ohayou" to "おはよう", "konnichiwa" to "こんにちは", "konbanwa" to "こんばんは",
        "oyasuminasai" to "おやすみなさい", "sayounara" to "さようなら",
        "arigatou" to "ありがとう", "doumo" to "どうも", "sumimasen" to "すみません",
        "gomennasai" to "ごめんなさい", "onegai" to "おねがい", "kudasai" to "ください",
        "douzo" to "どうぞ", "itadakimasu" to "いただきます", "gochisousama" to "ごちそうさま",
        "tadaima" to "ただいま", "okaeri" to "おかえり", "ittekimasu" to "いってきます",
        "itterasshai" to "いってらっしゃい", "otsukaresama" to "おつかれさま",
        "ganbatte" to "がんばって", "ganbare" to "がんばれ", "daijoubu" to "だいじょうぶ",
        "wakarimasu" to "わかります", "wakarimasen" to "わかりません",
        "suki" to "すき", "kirai" to "きらい", "ii" to "いい", "warui" to "わるい",
        "ookii" to "おおきい", "chiisai" to "ちいさい", "takai" to "たかい", "yasui" to "やすい",
        "atarashii" to "あたらしい", "furui" to "ふるい", "kirei" to "きれい", "utsukushii" to "うつくしい",
        "kawaii" to "かわいい", "kakkoii" to "かっこいい", "sugoi" to "すごい", "subarashii" to "すばらしい",
        "muzukashii" to "むずかしい", "yasashii" to "やさしい", "omoshiroi" to "おもしろい", "tanoshii" to "たのしい",
        "tsumaranai" to "つまらない", "tsurai" to "つらい", "kanashii" to "かなしい", "ureshii" to "うれしい",
        "ureshii" to "うれしい", "tanoshii" to "たのしい", "omoshiroi" to "おもしろい", "sugoi" to "すごい",
        "subarashii" to "すばらしい", "sugoi" to "すごい", "kakkoii" to "かっこいい", "kawaii" to "かわいい",
        "utsukushii" to "うつくしい", "kirei" to "きれい", "furui" to "ふるい", "atarashii" to "あたらしい",
        "yasui" to "やすい", "takai" to "たかい", "chiisai" to "ちいさい", "ookii" to "おおきい",
        "warui" to "わるい", "ii" to "いい", "kirai" to "きらい", "suki" to "すき",
        "wakarimasen" to "わかりません", "wakarimasu" to "わかります", "daijoubu" to "だいじょうぶ",
        "ganbare" to "がんばれ", "ganbatte" to "がんばって", "otsukaresama" to "おつかれさま",
        "itterasshai" to "いってらっしゃい", "ittekimasu" to "いってきます", "okaeri" to "おかえり",
        "tadaima" to "ただいま", "gochisousama" to "ごちそうさま", "itadakimasu" to "いただきます",
        "douzo" to "どうぞ", "kudasai" to "ください", "onegai" to "おねがい", "gomennasai" to "ごめんなさい",
        "sumimasen" to "すみません", "doumo" to "どうも", "arigatou" to "ありがとう", "sayounara" to "さようなら",
        "oyasuminasai" to "おやすみなさい", "konbanwa" to "こんばんは", "konnichiwa" to "こんにちは", "ohayou" to "おはよう",
        "bangohan" to "ばんごはん", "hirugohan" to "ひるごはん", "asagohan" to "あさごはん", "gohan" to "ごはん",
        "pan" to "パン", "sake" to "さけ", "biiru" to "ビール", "kohi" to "コーヒー", "ocha" to "おちゃ",
        "mizu" to "みず", "nomimono" to "のみもの", "tabemono" to "たべもの", "yasumi" to "やすみ", "shigoto" to "しごと",
        "kaisha" to "かいしゃ", "gakko" to "がっこう", "uchi" to "うち", "ie" to "いえ", "kazoku" to "かぞく",
        "tomodachi" to "ともだち", "gakusei" to "がくせい", "sensei" to "せんせい", "onna" to "おんな", "otoko" to "おとこ",
        "kodomo" to "こども", "hito" to "ひと", "karejotachi" to "かのじょたち", "karera" to "かれら", "anatatachi" to "あなたたち",
        "watashitachi" to "わたしたち", "kanojo" to "かのじょ", "kare" to "かれ", "anata" to "あなた", "watashi" to "わたし"
    )

    // Romaji to Katakana mapping (for foreign words)
    private val romajiToKatakana = mapOf(
        "amerika" to "アメリカ", "igirisu" to "イギリス", "doitsu" to "ドイツ", "furansu" to "フランス",
        "supein" to "スペイン", "itaria" to "イタリア", "roshia" to "ロシア", "chuugoku" to "チュウゴク",
        "kankoku" to "カンコク", "nihon" to "ニホン", "kanada" to "カナダ", "oosutoraria" to "オーストラリア",
        "nyuuyooku" to "ニューヨーク", "roondon" to "ロンドン", "paris" to "パリ", "berurin" to "ベルリン",
        "madrid" to "マドリード", "roma" to "ローマ", "moscow" to "モスクワ", "pekin" to "ペキン",
        "seoru" to "ソウル", "tokyo" to "トウキョウ", "toronto" to "トロント", "sydney" to "シドニー",
        "koohii" to "コーヒー", "biiru" to "ビール", "wain" to "ワイン", "pan" to "パン",
        "keeki" to "ケーキ", "aisukuriimu" to "アイスクリーム", "hamburger" to "ハンバーガー", "pizza" to "ピザ",
        "sushi" to "スシ", "tempura" to "テンプラ", "ramen" to "ラーメン", "udon" to "ウドン",
        "soba" to "ソバ", "gyoza" to "ギョーザ", "karaage" to "カラアゲ", "tonkatsu" to "トンカツ",
        "curry" to "カレー", "omurice" to "オムライス", "nikujaga" to "ニクジャガ", "miso" to "ミソ",
        "natto" to "ナットウ", "tsukemono" to "ツケモノ", "wasabi" to "ワサビ", "shoyu" to "ショユ",
        "mirin" to "ミリン", "dashi" to "ダシ", "kombu" to "コンブ", "katsuobushi" to "カツオブシ",
        "nori" to "ノリ", "wakame" to "ワカメ", "konnyaku" to "コンニャク", "tofu" to "トウフ",
        "tamago" to "タマゴ", "gyuuniku" to "ギュウニク", "butaniku" to "ブタニク", "toriniku" to "トリニク",
        "sakana" to "サカナ", "ebi" to "エビ", "ika" to "イカ", "tako" to "タコ",
        "hotate" to "ホタテ", "awabi" to "アワビ", "uni" to "ウニ", "ikura" to "イクラ",
        "mentaiko" to "メンタイコ", "tarako" to "タラコ", "kazunoko" to "カズノコ", "konbu" to "コンブ",
        "wakame" to "ワカメ", "nori" to "ノリ", "kombu" to "コンブ", "katsuobushi" to "カツオブシ",
        "dashi" to "ダシ", "mirin" to "ミリン", "shoyu" to "ショユ", "wasabi" to "ワサビ",
        "tsukemono" to "ツケモノ", "natto" to "ナットウ", "miso" to "ミソ", "nikujaga" to "ニクジャガ",
        "omurice" to "オムライス", "curry" to "カレー", "tonkatsu" to "トンカツ", "karaage" to "カラアゲ",
        "gyoza" to "ギョーザ", "soba" to "ソバ", "udon" to "ウドン", "ramen" to "ラーメン",
        "tempura" to "テンプラ", "sushi" to "スシ", "pizza" to "ピザ", "hamburger" to "ハンバーガー",
        "aisukuriimu" to "アイスクリーム", "keeki" to "ケーキ", "pan" to "パン", "wain" to "ワイン",
        "biiru" to "ビール", "koohii" to "コーヒー", "sydney" to "シドニー", "toronto" to "トロント",
        "tokyo" to "トウキョウ", "seoru" to "ソウル", "pekin" to "ペキン", "moscow" to "モスクワ",
        "roma" to "ローマ", "madrid" to "マドリード", "berurin" to "ベルリン", "paris" to "パリ",
        "roondon" to "ロンドン", "nyuuyooku" to "ニューヨーク", "oosutoraria" to "オーストラリア",
        "kanada" to "カナダ", "nihon" to "ニホン", "kankoku" to "カンコク", "chuugoku" to "チュウゴク",
        "roshia" to "ロシア", "itaria" to "イタリア", "supein" to "スペイン", "furansu" to "フランス",
        "doitsu" to "ドイツ", "igirisu" to "イギリス", "amerika" to "アメリカ"
    )

    override fun getActions(precedingText: String, toInsert: String): Pair<Int, String> {
        val input = precedingText + toInsert
        
        // Check for katakana conversion first (for foreign words)
        for ((romaji, katakana) in romajiToKatakana) {
            if (input.endsWith(romaji)) {
                return Pair(romaji.length - toInsert.length, katakana)
            }
        }
        
        // Check for hiragana conversion
        for ((romaji, hiragana) in romajiToHiragana) {
            if (input.endsWith(romaji)) {
                return Pair(romaji.length - toInsert.length, hiragana)
            }
        }
        
        // No conversion found, just append the character
        return Pair(0, toInsert)
    }
} 