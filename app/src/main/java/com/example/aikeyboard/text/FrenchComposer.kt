package com.example.aikeyboard.text

import com.example.aikeyboard.text.composing.Composer

class FrenchComposer : Composer {
    override val id = "french"
    override val label = "French"
    override val toRead = 3

    // French accent rules
    private val accentRules = mapOf(
        // Acute accent (é)
        "e'" to "é", "a'" to "á", "i'" to "í", "o'" to "ó", "u'" to "ú",
        "E'" to "É", "A'" to "Á", "I'" to "Í", "O'" to "Ó", "U'" to "Ú",
        
        // Grave accent (è)
        "e`" to "è", "a`" to "à", "i`" to "ì", "o`" to "ò", "u`" to "ù",
        "E`" to "È", "A`" to "À", "I`" to "Ì", "O`" to "Ò", "U`" to "Ù",
        
        // Circumflex (ê)
        "e^" to "ê", "a^" to "â", "i^" to "î", "o^" to "ô", "u^" to "û",
        "E^" to "Ê", "A^" to "Â", "I^" to "Î", "O^" to "Ô", "U^" to "Û",
        
        // Diaeresis (ë)
        "e\"" to "ë", "a\"" to "ä", "i\"" to "ï", "o\"" to "ö", "u\"" to "ü",
        "E\"" to "Ë", "A\"" to "Ä", "I\"" to "Ï", "O\"" to "Ö", "U\"" to "Ü",
        
        // Cedilla (ç)
        "c," to "ç", "C," to "Ç",
        
        // Common French words with accents
        "etre" to "être", "etat" to "état", "cafe" to "café",
        "deja" to "déjà", "voila" to "voilà", "a" to "à",
        "ou" to "où", "la" to "là", "ca" to "ça",
        "francais" to "français", "francaise" to "française",
        "garcon" to "garçon", "facade" to "façade",
        "naif" to "naïf", "naive" to "naïve",
        "coeur" to "cœur", "soeur" to "sœur",
        "noel" to "Noël", "mael" to "maël",
        "aigue" to "aiguë", "ambigue" to "ambiguë",
        "contigue" to "contiguë", "exigue" to "exiguë",
        "fatigue" to "fatiguë", "longue" to "longue",
        "rogue" to "rogue", "vague" to "vague",
        
        // Common French phrases
        "bonjour" to "bonjour", "au revoir" to "au revoir",
        "merci" to "merci", "s'il vous plait" to "s'il vous plaît",
        "excusez-moi" to "excusez-moi", "je suis" to "je suis",
        "je vais" to "je vais", "je peux" to "je peux",
        "je veux" to "je veux", "je dois" to "je dois",
        "c'est" to "c'est", "il y a" to "il y a",
        "qu'est-ce que" to "qu'est-ce que", "comment allez-vous" to "comment allez-vous",
        
        // Numbers in French
        "un" to "un", "deux" to "deux", "trois" to "trois",
        "quatre" to "quatre", "cinq" to "cinq", "six" to "six",
        "sept" to "sept", "huit" to "huit", "neuf" to "neuf",
        "dix" to "dix", "onze" to "onze", "douze" to "douze",
        "treize" to "treize", "quatorze" to "quatorze", "quinze" to "quinze",
        "seize" to "seize", "dix-sept" to "dix-sept", "dix-huit" to "dix-huit",
        "dix-neuf" to "dix-neuf", "vingt" to "vingt",
        
        // Days of the week
        "lundi" to "lundi", "mardi" to "mardi", "mercredi" to "mercredi",
        "jeudi" to "jeudi", "vendredi" to "vendredi", "samedi" to "samedi",
        "dimanche" to "dimanche",
        
        // Months
        "janvier" to "janvier", "fevrier" to "février", "mars" to "mars",
        "avril" to "avril", "mai" to "mai", "juin" to "juin",
        "juillet" to "juillet", "aout" to "août", "septembre" to "septembre",
        "octobre" to "octobre", "novembre" to "novembre", "decembre" to "décembre",
        
        // Colors
        "rouge" to "rouge", "bleu" to "bleu", "vert" to "vert",
        "jaune" to "jaune", "noir" to "noir", "blanc" to "blanc",
        "gris" to "gris", "marron" to "marron", "orange" to "orange",
        "rose" to "rose", "violet" to "violet", "turquoise" to "turquoise"
    )

    override fun getActions(precedingText: String, toInsert: String): Pair<Int, String> {
        val input = precedingText + toInsert
        
        // Check for accent rules
        for ((pattern, replacement) in accentRules) {
            if (input.endsWith(pattern)) {
                return Pair(pattern.length - toInsert.length, replacement)
            }
        }
        
        // No conversion found, just append the character
        return Pair(0, toInsert)
    }
} 