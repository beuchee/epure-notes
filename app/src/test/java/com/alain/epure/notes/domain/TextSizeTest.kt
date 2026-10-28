package com.alain.epure.notes.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class TextSizeTest {

    @Test
    fun tapGoesToNextSize() {
        assertEquals(22, nextTextSize(20))
        assertEquals(18, nextTextSize(16))
        assertEquals(14, nextTextSize(12))
    }

    @Test
    fun largestSizeWrapsToSmallest() {
        assertEquals(12, nextTextSize(24))
    }

    @Test
    fun unknownSizeFallsBackToDefault() {
        assertEquals(DEFAULT_TEXT_SIZE, nextTextSize(19))
    }
}
