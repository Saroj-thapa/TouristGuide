package com.example.touristguide

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsUnitTest {
    fun add(a: Int, b: Int) = a + b

    @Test
    fun addition_isCorrect() {
        assertEquals(5, add(2, 3))
    }
}