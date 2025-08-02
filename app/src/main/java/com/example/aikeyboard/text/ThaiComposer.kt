package com.example.aikeyboard.text

import com.example.aikeyboard.text.composing.Composer

class ThaiComposer : Composer {
    override val id = "thai"
    override val label = "Thai"
    override val toRead = 4

    // Romanization to Thai mapping
    private val romanizationToThai = mapOf(
        // Basic vowels
        "a" to "ะ", "aa" to "า", "i" to "ิ", "ii" to "ี", "u" to "ุ", "uu" to "ู",
        "e" to "เ", "ee" to "แ", "o" to "โ", "oo" to "อ", "ue" to "ื", "uee" to "ือ",
        
        // Basic consonants
        "k" to "ก", "kh" to "ข", "kh" to "ค", "kh" to "ฆ", "ng" to "ง",
        "c" to "จ", "ch" to "ฉ", "ch" to "ช", "ch" to "ซ", "y" to "ญ",
        "d" to "ด", "t" to "ต", "th" to "ถ", "th" to "ท", "th" to "ธ", "n" to "น",
        "b" to "บ", "p" to "ป", "ph" to "ผ", "ph" to "พ", "ph" to "ภ", "f" to "ฟ",
        "m" to "ม", "r" to "ร", "l" to "ล", "w" to "ว", "s" to "ส", "h" to "ห",
        
        // Common words
        "sawaddee" to "สวัสดี", "khop" to "ขอบ", "khun" to "คุณ", "kha" to "ค่ะ",
        "mai" to "ไม่", "chai" to "ใช่", "dai" to "ได้", "mai" to "ไม่",
        "khoi" to "ข้อย", "khun" to "คุณ", "phom" to "ผม", "chan" to "ฉัน",
        "rao" to "เรา", "khun" to "คุณ", "phuak" to "พวก", "khun" to "คุณ",
        "khao" to "เขา", "man" to "มัน", "wan" to "วัน", "wan" to "วัน",
        "ni" to "นี้", "nan" to "นั้น", "yon" to "ย้อน", "yon" to "ย้อน",
        "ni" to "นี้", "nan" to "นั้น", "yon" to "ย้อน", "yon" to "ย้อน",
        
        // Numbers
        "neung" to "หนึ่ง", "song" to "สอง", "sam" to "สาม", "si" to "สี่",
        "ha" to "ห้า", "hok" to "หก", "jet" to "เจ็ด", "paet" to "แปด",
        "kao" to "เก้า", "sip" to "สิบ", "sip" to "สิบ", "sip" to "สิบ",
        "sip" to "สิบ", "sip" to "สิบ", "sip" to "สิบ", "sip" to "สิบ",
        "sip" to "สิบ", "sip" to "สิบ", "sip" to "สิบ", "sip" to "สิบ",
        "roi" to "ร้อย", "phan" to "พัน", "larn" to "ล้าน", "khoi" to "ข้อย",
        
        // Days of the week
        "wan" to "วัน", "jan" to "จัน", "wan" to "วัน", "jan" to "จัน",
        "wan" to "วัน", "jan" to "จัน", "wan" to "วัน", "jan" to "จัน",
        "wan" to "วัน", "jan" to "จัน", "wan" to "วัน", "jan" to "จัน",
        "wan" to "วัน", "jan" to "จัน", "wan" to "วัน", "jan" to "จัน",
        
        // Colors
        "si" to "สี", "daeng" to "แดง", "nam" to "น้ำ", "ngoen" to "เงิน",
        "khao" to "ขาว", "dam" to "ดำ", "khiao" to "เขียว", "si" to "สี",
        "daeng" to "แดง", "nam" to "น้ำ", "ngoen" to "เงิน", "khao" to "ขาว",
        "dam" to "ดำ", "khiao" to "เขียว", "si" to "สี", "daeng" to "แดง",
        "nam" to "น้ำ", "ngoen" to "เงิน", "khao" to "ขาว", "dam" to "ดำ"
    )

    override fun getActions(precedingText: String, toInsert: String): Pair<Int, String> {
        val input = precedingText + toInsert
        
        // Check for romanization to Thai conversion
        for ((romanization, thai) in romanizationToThai) {
            if (input.endsWith(romanization)) {
                return Pair(romanization.length - toInsert.length, thai)
            }
        }
        
        // No conversion found, just append the character
        return Pair(0, toInsert)
    }
} 