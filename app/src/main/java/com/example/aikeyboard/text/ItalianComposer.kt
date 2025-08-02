package com.example.aikeyboard.text

import com.example.aikeyboard.text.composing.Composer

class ItalianComposer : Composer {
    override val id = "italian"
    override val label = "Italian"
    override val toRead = 3

    // Italian accent rules
    private val italianRules = mapOf(
        // Accents
        "a'" to "à", "e'" to "è", "i'" to "ì", "o'" to "ò", "u'" to "ù",
        "A'" to "À", "E'" to "È", "I'" to "Ì", "O'" to "Ò", "U'" to "Ù",
        
        // Common Italian words with accents
        "perche" to "perché", "come" to "come", "dove" to "dove", "quando" to "quando",
        "chi" to "chi", "cosa" to "cosa", "quale" to "quale", "quanto" to "quanto",
        "citta" to "città", "universita" to "università", "facolta" to "facoltà",
        "eta" to "età", "liberta" to "libertà", "verita" to "verità",
        "bonta" to "bontà", "bevuta" to "bevuta", "causa" to "causa",
        "casa" to "casa", "famiglia" to "famiglia", "lavoro" to "lavoro",
        "scuola" to "scuola", "universita" to "università", "ospedale" to "ospedale",
        "negozio" to "negozio", "ristorante" to "ristorante", "albergo" to "albergo",
        "banca" to "banca", "ufficio" to "ufficio", "stazione" to "stazione",
        "aeroporto" to "aeroporto", "porto" to "porto", "chiesa" to "chiesa",
        "teatro" to "teatro", "cinema" to "cinema", "museo" to "museo",
        "biblioteca" to "biblioteca", "parco" to "parco", "giardino" to "giardino",
        "strada" to "strada", "via" to "via", "piazza" to "piazza",
        "ponte" to "ponte", "torre" to "torre", "castello" to "castello",
        "palazzo" to "palazzo", "villa" to "villa", "casa" to "casa",
        
        // Common phrases
        "ciao" to "ciao", "buongiorno" to "buongiorno", "buonasera" to "buonasera",
        "buonanotte" to "buonanotte", "arrivederci" to "arrivederci", "addio" to "addio",
        "grazie" to "grazie", "prego" to "prego", "scusa" to "scusa", "mi" to "mi",
        "dispiace" to "dispiace", "perdono" to "perdono", "niente" to "niente",
        "problema" to "problema", "tutto" to "tutto", "bene" to "bene", "male" to "male",
        "si" to "sì", "no" to "no", "forse" to "forse", "possibile" to "possibile",
        "impossibile" to "impossibile", "vero" to "vero", "falso" to "falso",
        "giusto" to "giusto", "sbagliato" to "sbagliato", "buono" to "buono",
        "cattivo" to "cattivo", "migliore" to "migliore", "peggiore" to "peggiore",
        "bello" to "bello", "brutto" to "brutto", "grande" to "grande", "piccolo" to "piccolo",
        "alto" to "alto", "basso" to "basso", "lungo" to "lungo", "corto" to "corto",
        "largo" to "largo", "stretto" to "stretto", "spesso" to "spesso", "sottile" to "sottile",
        "pesante" to "pesante", "leggero" to "leggero", "vecchio" to "vecchio", "nuovo" to "nuovo",
        "giovane" to "giovane", "moderno" to "moderno", "antico" to "antico", "classico" to "classico",
        "contemporaneo" to "contemporaneo", "tradizionale" to "tradizionale", "originale" to "originale",
        
        // Numbers
        "uno" to "uno", "due" to "due", "tre" to "tre", "quattro" to "quattro",
        "cinque" to "cinque", "sei" to "sei", "sette" to "sette", "otto" to "otto",
        "nove" to "nove", "dieci" to "dieci", "undici" to "undici", "dodici" to "dodici",
        "tredici" to "tredici", "quattordici" to "quattordici", "quindici" to "quindici",
        "sedici" to "sedici", "diciassette" to "diciassette", "diciotto" to "diciotto",
        "diciannove" to "diciannove", "venti" to "venti", "cento" to "cento",
        "mille" to "mille", "milione" to "milione", "miliardo" to "miliardo",
        
        // Days of the week
        "lunedi" to "lunedì", "martedi" to "martedì", "mercoledi" to "mercoledì",
        "giovedi" to "giovedì", "venerdi" to "venerdì", "sabato" to "sabato",
        "domenica" to "domenica", "settimana" to "settimana", "fine" to "fine", "settimana" to "settimana",
        
        // Months
        "gennaio" to "gennaio", "febbraio" to "febbraio", "marzo" to "marzo",
        "aprile" to "aprile", "maggio" to "maggio", "giugno" to "giugno",
        "luglio" to "luglio", "agosto" to "agosto", "settembre" to "settembre",
        "ottobre" to "ottobre", "novembre" to "novembre", "dicembre" to "dicembre",
        
        // Colors
        "rosso" to "rosso", "blu" to "blu", "verde" to "verde", "giallo" to "giallo",
        "nero" to "nero", "bianco" to "bianco", "grigio" to "grigio", "marrone" to "marrone",
        "arancione" to "arancione", "rosa" to "rosa", "viola" to "viola", "turchese" to "turchese",
        "pink" to "rosa", "viola" to "viola", "beige" to "beige", "argento" to "argento",
        "oro" to "oro", "porpora" to "porpora", "indaco" to "indaco", "ciano" to "ciano"
    )

    override fun getActions(precedingText: String, toInsert: String): Pair<Int, String> {
        val input = precedingText + toInsert
        
        // Check for Italian character rules
        for ((pattern, replacement) in italianRules) {
            if (input.endsWith(pattern)) {
                return Pair(pattern.length - toInsert.length, replacement)
            }
        }
        
        // No conversion found, just append the character
        return Pair(0, toInsert)
    }
} 