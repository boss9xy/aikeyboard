package com.example.aikeyboard.text

import com.example.aikeyboard.text.composing.Composer

class GermanComposer : Composer {
    override val id = "german"
    override val label = "German"
    override val toRead = 3

    // German umlaut and special character rules
    private val germanRules = mapOf(
        // Umlauts
        "ae" to "ä", "oe" to "ö", "ue" to "ü", "ss" to "ß",
        "AE" to "Ä", "OE" to "Ö", "UE" to "Ü",
        
        // Common German words with umlauts
        "fuer" to "für", "muessen" to "müssen", "koennen" to "können",
        "schoen" to "schön", "gross" to "groß", "strasse" to "Straße",
        "buss" to "Buß", "muss" to "muß", "kuss" to "Kuß",
        "fuss" to "Fuß", "schluss" to "Schluß", "gruss" to "Gruß",
        "spass" to "Spaß", "mass" to "Maß", "lass" to "laß",
        "nass" to "naß", "dass" to "daß", "ass" to "aß",
        "essen" to "essen", "trinken" to "trinken", "schlafen" to "schlafen",
        "arbeiten" to "arbeiten", "lernen" to "lernen", "spielen" to "spielen",
        "gehen" to "gehen", "kommen" to "kommen", "sehen" to "sehen",
        "hoeren" to "hören", "sprechen" to "sprechen", "verstehen" to "verstehen",
        "wissen" to "wissen", "denken" to "denken", "glauben" to "glauben",
        "lieben" to "lieben", "hassen" to "hassen", "mögen" to "mögen",
        "wollen" to "wollen", "sollen" to "sollen", "dürfen" to "dürfen",
        "können" to "können", "müssen" to "müssen", "haben" to "haben",
        "sein" to "sein", "werden" to "werden", "machen" to "machen",
        "geben" to "geben", "nehmen" to "nehmen", "finden" to "finden",
        "sagen" to "sagen", "sprechen" to "sprechen", "hoeren" to "hören",
        "sehen" to "sehen", "gehen" to "gehen", "kommen" to "kommen",
        "bleiben" to "bleiben", "stehen" to "stehen", "liegen" to "liegen",
        "sitzen" to "sitzen", "laufen" to "laufen", "fahren" to "fahren",
        "fliegen" to "fliegen", "schwimmen" to "schwimmen", "tanzen" to "tanzen",
        "singen" to "singen", "spielen" to "spielen", "arbeiten" to "arbeiten",
        "lernen" to "lernen", "studieren" to "studieren", "unterrichten" to "unterrichten",
        "helfen" to "helfen", "brauchen" to "brauchen", "suchen" to "suchen",
        "finden" to "finden", "bekommen" to "bekommen", "nehmen" to "nehmen",
        "geben" to "geben", "bringen" to "bringen", "holen" to "holen",
        "kaufen" to "kaufen", "verkaufen" to "verkaufen", "bezahlen" to "bezahlen",
        "kosten" to "kosten", "sparen" to "sparen", "verdienen" to "verdienen",
        "ausgeben" to "ausgeben", "einnehmen" to "einnehmen", "zahlen" to "zahlen",
        "rechnen" to "rechnen", "zählen" to "zählen", "zählen" to "zählen",
        
        // Common German phrases
        "guten" to "guten", "morgen" to "morgen", "tag" to "Tag", "abend" to "Abend",
        "nacht" to "Nacht", "guten" to "guten", "morgen" to "morgen", "guten" to "guten",
        "tag" to "Tag", "guten" to "guten", "abend" to "Abend", "gute" to "gute",
        "nacht" to "Nacht", "auf" to "auf", "wiedersehen" to "wiedersehen",
        "tschüss" to "tschüss", "bis" to "bis", "später" to "später",
        "danke" to "danke", "bitte" to "bitte", "entschuldigung" to "Entschuldigung",
        "verzeihung" to "Verzeihung", "es" to "es", "tut" to "tut", "mir" to "mir",
        "leid" to "leid", "kein" to "kein", "problem" to "Problem", "keine" to "keine",
        "sorge" to "Sorge", "alles" to "alles", "klar" to "klar", "verstanden" to "verstanden",
        "ja" to "ja", "nein" to "nein", "vielleicht" to "vielleicht", "möglich" to "möglich",
        "unmöglich" to "unmöglich", "wahr" to "wahr", "falsch" to "falsch", "richtig" to "richtig",
        "falsch" to "falsch", "gut" to "gut", "schlecht" to "schlecht", "besser" to "besser",
        "am" to "am", "besten" to "besten", "schlimmsten" to "schlimmsten", "schön" to "schön",
        "hässlich" to "hässlich", "groß" to "groß", "klein" to "klein", "lang" to "lang",
        "kurz" to "kurz", "breit" to "breit", "schmal" to "schmal", "dick" to "dick",
        "dünn" to "dünn", "schwer" to "schwer", "leicht" to "leicht", "alt" to "alt",
        "jung" to "jung", "neu" to "neu", "alt" to "alt", "modern" to "modern",
        "traditionell" to "traditionell", "klassisch" to "klassisch", "zeitgenössisch" to "zeitgenössisch",
        
        // Numbers
        "eins" to "eins", "zwei" to "zwei", "drei" to "drei", "vier" to "vier",
        "fünf" to "fünf", "sechs" to "sechs", "sieben" to "sieben", "acht" to "acht",
        "neun" to "neun", "zehn" to "zehn", "elf" to "elf", "zwölf" to "zwölf",
        "dreizehn" to "dreizehn", "vierzehn" to "vierzehn", "fünfzehn" to "fünfzehn",
        "sechzehn" to "sechzehn", "siebzehn" to "siebzehn", "achtzehn" to "achtzehn",
        "neunzehn" to "neunzehn", "zwanzig" to "zwanzig", "hundert" to "hundert",
        "tausend" to "tausend", "million" to "Million", "milliarde" to "Milliarde",
        
        // Days of the week
        "montag" to "Montag", "dienstag" to "Dienstag", "mittwoch" to "Mittwoch",
        "donnerstag" to "Donnerstag", "freitag" to "Freitag", "samstag" to "Samstag",
        "sonntag" to "Sonntag", "woche" to "Woche", "wochenende" to "Wochenende",
        
        // Months
        "januar" to "Januar", "februar" to "Februar", "märz" to "März",
        "april" to "April", "mai" to "Mai", "juni" to "Juni",
        "juli" to "Juli", "august" to "August", "september" to "September",
        "oktober" to "Oktober", "november" to "November", "dezember" to "Dezember",
        
        // Colors
        "rot" to "rot", "blau" to "blau", "grün" to "grün", "gelb" to "gelb",
        "schwarz" to "schwarz", "weiß" to "weiß", "grau" to "grau", "braun" to "braun",
        "orange" to "orange", "rosa" to "rosa", "lila" to "lila", "türkis" to "türkis",
        "pink" to "pink", "violett" to "violett", "beige" to "beige", "silber" to "silber",
        "gold" to "gold", "purpur" to "purpur", "indigo" to "indigo", "cyan" to "cyan"
    )

    override fun getActions(precedingText: String, toInsert: String): Pair<Int, String> {
        val input = precedingText + toInsert
        
        // Check for German character rules
        for ((pattern, replacement) in germanRules) {
            if (input.endsWith(pattern)) {
                return Pair(pattern.length - toInsert.length, replacement)
            }
        }
        
        // No conversion found, just append the character
        return Pair(0, toInsert)
    }
} 