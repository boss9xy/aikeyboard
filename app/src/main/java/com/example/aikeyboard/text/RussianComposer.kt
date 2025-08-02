package com.example.aikeyboard.text

import com.example.aikeyboard.text.composing.Composer

class RussianComposer : Composer {
    override val id = "russian"
    override val label = "Russian"
    override val toRead = 4

    // Transliteration to Cyrillic mapping
    private val transliterationToCyrillic = mapOf(
        // Basic vowels
        "a" to "а", "e" to "е", "i" to "и", "o" to "о", "u" to "у", "y" to "ы",
        
        // Basic consonants
        "b" to "б", "v" to "в", "g" to "г", "d" to "д", "zh" to "ж", "z" to "з",
        "k" to "к", "l" to "л", "m" to "м", "n" to "н", "p" to "п", "r" to "р",
        "s" to "с", "t" to "т", "f" to "ф", "kh" to "х", "ts" to "ц", "ch" to "ч",
        "sh" to "ш", "shch" to "щ", "y" to "й", "e" to "э", "yu" to "ю", "ya" to "я",
        
        // Common words
        "privet" to "привет", "zdravstvuyte" to "здравствуйте", "spasibo" to "спасибо",
        "pozhalusta" to "пожалуйста", "izvinite" to "извините", "prosti" to "прости",
        "da" to "да", "net" to "нет", "mozhet" to "может", "vozmozhno" to "возможно",
        "konechno" to "конечно", "pravda" to "правда", "lozh" to "ложь", "verno" to "верно",
        "nepravilno" to "неправильно", "khorosho" to "хорошо", "plokho" to "плохо",
        "luchshe" to "лучше", "khuzhe" to "хуже", "krasivo" to "красиво", "ugly" to "уродливо",
        "bolshoi" to "большой", "malenki" to "маленький", "vysokii" to "высокий", "nizkii" to "низкий",
        "dlinnyi" to "длинный", "korotkii" to "короткий", "shirokii" to "широкий", "uzkii" to "узкий",
        "tolstyi" to "толстый", "tonkii" to "тонкий", "tyazhelyi" to "тяжелый", "legkii" to "легкий",
        "staryi" to "старый", "molodoi" to "молодой", "novyi" to "новый", "sovremennyi" to "современный",
        "drevnii" to "древний", "klassicheskii" to "классический", "sovremennyi" to "современный",
        
        // Numbers
        "odin" to "один", "dva" to "два", "tri" to "три", "chetyre" to "четыре",
        "pyat" to "пять", "shest" to "шесть", "sem" to "семь", "vosem" to "восемь",
        "devyat" to "девять", "desyat" to "десять", "odinnadtsat" to "одиннадцать",
        "dvenadtsat" to "двенадцать", "trinadtsat" to "тринадцать", "chetirnadtsat" to "четырнадцать",
        "pyatnadtsat" to "пятнадцать", "shestnadtsat" to "шестнадцать", "semnadtsat" to "семнадцать",
        "vosemnadtsat" to "восемнадцать", "devyatnadtsat" to "девятнадцать", "dvadtsat" to "двадцать",
        "sto" to "сто", "tysyacha" to "тысяча", "million" to "миллион", "milliard" to "миллиард",
        
        // Days of the week
        "ponedelnik" to "понедельник", "vtornik" to "вторник", "sreda" to "среда",
        "chetverg" to "четверг", "pyatnitsa" to "пятница", "subbota" to "суббота",
        "voskresene" to "воскресенье", "nedelya" to "неделя", "vikend" to "выходные",
        
        // Months
        "yanvar" to "январь", "fevral" to "февраль", "mart" to "март",
        "aprel" to "апрель", "mai" to "май", "iyun" to "июнь",
        "iyul" to "июль", "avgust" to "август", "sentyabr" to "сентябрь",
        "oktyabr" to "октябрь", "noyabr" to "ноябрь", "dekabr" to "декабрь",
        
        // Colors
        "krasnyi" to "красный", "sinii" to "синий", "zelenyi" to "зеленый", "zheltyi" to "желтый",
        "chernyi" to "черный", "belyi" to "белый", "seryi" to "серый", "korichnevyi" to "коричневый",
        "oranževyi" to "оранжевый", "rozovyi" to "розовый", "fioletovyi" to "фиолетовый", "goluboi" to "голубой",
        "pink" to "розовый", "fioletovyi" to "фиолетовый", "beževyi" to "бежевый", "serebryanyi" to "серебряный",
        "zolotoi" to "золотой", "purpurnyi" to "пурпурный", "indigo" to "индиго", "tsian" to "циан",
        
        // Food
        "khleb" to "хлеб", "moloko" to "молоко", "voda" to "вода", "kofe" to "кофе",
        "chai" to "чай", "vino" to "вино", "pivo" to "пиво", "ris" to "рис",
        "kuritsa" to "курица", "myaso" to "мясо", "ryba" to "рыба", "ovoshchi" to "овощи",
        "frukty" to "фрукты", "pomidor" to "помидор", "luk" to "лук", "chesnok" to "чеснок",
        "kartoshka" to "картошка", "morkov" to "морковь", "salat" to "салат", "yabloko" to "яблоко",
        "apelsin" to "апельсин", "banan" to "банан", "vinograd" to "виноград", "klubnika" to "клубника",
        "limon" to "лимон", "grusha" to "груша", "persik" to "персик", "arbuz" to "арбуз",
        
        // Family
        "otets" to "отец", "mat" to "мать", "ded" to "дед", "babushka" to "бабушка",
        "brat" to "брат", "sestra" to "сестра", "dyadya" to "дядя", "tyotya" to "тётя",
        "dвоюродный" to "двоюродный", "brat" to "брат", "syn" to "сын", "doch" to "дочь",
        "vnuk" to "внук", "vnuchka" to "внучка", "plemyannik" to "племянник", "plemyannitsa" to "племянница",
        "shurin" to "шурин", "svoyak" to "свояк", "svekor" to "свекор", "svekrov" to "свекровь",
        
        // Common verbs
        "byt" to "быть", "est" to "есть", "imet" to "иметь", "delat" to "делать",
        "idti" to "идти", "prikhodit" to "приходить", "videt" to "видеть", "znat" to "знать",
        "moch" to "мочь", "dolzhen" to "должен", "khotet" to "хотеть", "nravitsya" to "нравиться",
        "govorit" to "говорить", "slushat" to "слушать", "chitat" to "читать", "pisat" to "писать",
        "est" to "есть", "pit" to "пить", "spat" to "спать", "rabotat" to "работать",
        "uchitsya" to "учиться", "izuchat" to "изучать", "prepodavat" to "преподавать",
        "pomogat" to "помогать", "nuzhdat" to "нуждаться", "iskat" to "искать",
        "nakhodit" to "находить", "poluchat" to "получать", "brat" to "брать",
        "dat" to "давать", "prinosit" to "приносить", "nosit" to "носить", "pokupat" to "покупать",
        "prodavat" to "продавать", "platit" to "платить", "stoit" to "стоить", "ekonomit" to "экономить",
        "zarabatyvat" to "зарабатывать", "tratit" to "тратить", "poluchat" to "получать", "platit" to "платить",
        "schitat" to "считать", "schitat" to "считать", "schitat" to "считать", "schitat" to "считать"
    )

    override fun getActions(precedingText: String, toInsert: String): Pair<Int, String> {
        val input = precedingText + toInsert
        
        // Check for transliteration to Cyrillic conversion
        for ((transliteration, cyrillic) in transliterationToCyrillic) {
            if (input.endsWith(transliteration)) {
                return Pair(transliteration.length - toInsert.length, cyrillic)
            }
        }
        
        // No conversion found, just append the character
        return Pair(0, toInsert)
    }
} 