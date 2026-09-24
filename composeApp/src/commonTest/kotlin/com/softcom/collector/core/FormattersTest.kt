package com.softcom.collector.core

import kotlin.test.Test
import kotlin.test.assertEquals

class FormattersTest {
    @Test
    fun formatsValidCnpj() {
        assertEquals("67.949.765/0001-43", formatCnpj("67949765000143"))
    }

    @Test
    fun preservesIncompleteValue() {
        assertEquals("123", formatCnpj("123"))
    }

    @Test
    fun parsesBrazilianDecimalWithComma() {
        assertEquals(0.0, parseLocalizedDouble("0,00"))
        assertEquals(1234.56, parseLocalizedDouble("1.234,56"))
        assertEquals(10.5, parseLocalizedDouble("10,5"))
    }

    @Test
    fun parsesEnglishDecimalWithDot() {
        assertEquals(0.0, parseLocalizedDouble("0.00"))
        assertEquals(1234.56, parseLocalizedDouble("1234.56"))
    }
}
