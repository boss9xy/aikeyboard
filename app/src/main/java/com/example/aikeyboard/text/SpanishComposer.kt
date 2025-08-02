package com.example.aikeyboard.text

import com.example.aikeyboard.text.composing.Composer

class SpanishComposer : Composer {
    override val id = "spanish"
    override val label = "Spanish"
    override val toRead = 3

    // Spanish accent and special character rules
    private val spanishRules = mapOf(
        // Accents
        "a'" to "á", "e'" to "é", "i'" to "í", "o'" to "ó", "u'" to "ú",
        "A'" to "Á", "E'" to "É", "I'" to "Í", "O'" to "Ó", "U'" to "Ú",
        
        // Tilde (ñ)
        "n~" to "ñ", "N~" to "Ñ",
        
        // Common Spanish words with accents
        "año" to "año", "mañana" to "mañana", "español" to "español",
        "niño" to "niño", "niña" to "niña", "años" to "años",
        "señor" to "señor", "señora" to "señora", "señorita" to "señorita",
        "caña" to "caña", "baño" to "baño", "daño" to "daño",
        "paño" to "paño", "raña" to "raña", "saña" to "saña",
        "taña" to "taña", "vaña" to "vaña", "zaña" to "zaña",
        "año" to "año", "eño" to "eño", "iño" to "iño", "oño" to "oño", "uño" to "uño",
        "aña" to "aña", "eña" to "eña", "iña" to "iña", "oña" to "oña", "uña" to "uña",
        
        // Common Spanish phrases
        "hola" to "hola", "buenos" to "buenos", "dias" to "días", "tardes" to "tardes",
        "noches" to "noches", "adios" to "adiós", "hasta" to "hasta", "luego" to "luego",
        "gracias" to "gracias", "por" to "por", "favor" to "favor", "de" to "de",
        "nada" to "nada", "perdon" to "perdón", "disculpe" to "disculpe", "lo" to "lo",
        "siento" to "siento", "mucho" to "mucho", "bien" to "bien", "mal" to "mal",
        "si" to "sí", "no" to "no", "tal" to "tal", "vez" to "vez",
        "que" to "que", "como" to "cómo", "donde" to "dónde", "cuando" to "cuándo",
        "porque" to "porque", "quien" to "quién", "cual" to "cuál", "cuanto" to "cuánto",
        "que" to "qué", "como" to "cómo", "donde" to "dónde", "cuando" to "cuándo",
        "porque" to "por qué", "quien" to "quién", "cual" to "cuál", "cuanto" to "cuánto",
        
        // Numbers
        "uno" to "uno", "dos" to "dos", "tres" to "tres", "cuatro" to "cuatro",
        "cinco" to "cinco", "seis" to "seis", "siete" to "siete", "ocho" to "ocho",
        "nueve" to "nueve", "diez" to "diez", "once" to "once", "doce" to "doce",
        "trece" to "trece", "catorce" to "catorce", "quince" to "quince",
        "dieciseis" to "dieciséis", "diecisiete" to "diecisiete", "dieciocho" to "dieciocho",
        "diecinueve" to "diecinueve", "veinte" to "veinte", "cien" to "cien",
        "mil" to "mil", "millon" to "millón", "millones" to "millones",
        
        // Days of the week
        "lunes" to "lunes", "martes" to "martes", "miercoles" to "miércoles",
        "jueves" to "jueves", "viernes" to "viernes", "sabado" to "sábado",
        "domingo" to "domingo", "semana" to "semana", "fin" to "fin", "de" to "de",
        "semana" to "semana",
        
        // Months
        "enero" to "enero", "febrero" to "febrero", "marzo" to "marzo",
        "abril" to "abril", "mayo" to "mayo", "junio" to "junio",
        "julio" to "julio", "agosto" to "agosto", "septiembre" to "septiembre",
        "octubre" to "octubre", "noviembre" to "noviembre", "diciembre" to "diciembre",
        
        // Colors
        "rojo" to "rojo", "azul" to "azul", "verde" to "verde", "amarillo" to "amarillo",
        "negro" to "negro", "blanco" to "blanco", "gris" to "gris", "marron" to "marrón",
        "naranja" to "naranja", "rosa" to "rosa", "morado" to "morado", "turquesa" to "turquesa",
        "pink" to "rosa", "violeta" to "violeta", "beige" to "beige", "plateado" to "plateado",
        "dorado" to "dorado", "purpura" to "púrpura", "indigo" to "índigo", "cian" to "cian",
        
        // Food
        "pan" to "pan", "leche" to "leche", "agua" to "agua", "cafe" to "café",
        "te" to "té", "vino" to "vino", "cerveza" to "cerveza", "arroz" to "arroz",
        "pollo" to "pollo", "carne" to "carne", "pescado" to "pescado", "verduras" to "verduras",
        "frutas" to "frutas", "tomate" to "tomate", "cebolla" to "cebolla", "ajo" to "ajo",
        "patata" to "patata", "zanahoria" to "zanahoria", "lechuga" to "lechuga", "manzana" to "manzana",
        "naranja" to "naranja", "platano" to "plátano", "uva" to "uva", "fresa" to "fresa",
        "limon" to "limón", "pera" to "pera", "melocoton" to "melocotón", "sandia" to "sandía",
        
        // Family
        "padre" to "padre", "madre" to "madre", "abuelo" to "abuelo", "abuela" to "abuela",
        "hermano" to "hermano", "hermana" to "hermana", "tio" to "tío", "tia" to "tía",
        "primo" to "primo", "prima" to "prima", "hijo" to "hijo", "hija" to "hija",
        "nieto" to "nieto", "nieta" to "nieta", "sobrino" to "sobrino", "sobrina" to "sobrina",
        "cuñado" to "cuñado", "cuñada" to "cuñada", "suegro" to "suegro", "suegra" to "suegra",
        
        // Common verbs
        "ser" to "ser", "estar" to "estar", "tener" to "tener", "hacer" to "hacer",
        "ir" to "ir", "venir" to "venir", "ver" to "ver", "saber" to "saber",
        "poder" to "poder", "deber" to "deber", "querer" to "querer", "gustar" to "gustar",
        "hablar" to "hablar", "escuchar" to "escuchar", "leer" to "leer", "escribir" to "escribir",
        "comer" to "comer", "beber" to "beber", "dormir" to "dormir", "trabajar" to "trabajar",
        "estudiar" to "estudiar", "aprender" to "aprender", "enseñar" to "enseñar",
        "ayudar" to "ayudar", "necesitar" to "necesitar", "buscar" to "buscar",
        "encontrar" to "encontrar", "obtener" to "obtener", "tomar" to "tomar",
        "dar" to "dar", "traer" to "traer", "llevar" to "llevar", "comprar" to "comprar",
        "vender" to "vender", "pagar" to "pagar", "costar" to "costar", "ahorrar" to "ahorrar",
        "ganar" to "ganar", "gastar" to "gastar", "ingresar" to "ingresar", "pagar" to "pagar",
        "calcular" to "calcular", "contar" to "contar", "contar" to "contar", "contar" to "contar"
    )

    override fun getActions(precedingText: String, toInsert: String): Pair<Int, String> {
        val input = precedingText + toInsert
        
        // Check for Spanish character rules
        for ((pattern, replacement) in spanishRules) {
            if (input.endsWith(pattern)) {
                return Pair(pattern.length - toInsert.length, replacement)
            }
        }
        
        // No conversion found, just append the character
        return Pair(0, toInsert)
    }
} 