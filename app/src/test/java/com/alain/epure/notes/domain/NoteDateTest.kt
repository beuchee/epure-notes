package com.alain.epure.notes.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class NoteDateTest {

    private val now = LocalDateTime.of(2026, 10, 28, 18, 0)

    @Test
    fun todayShowsTime() {
        assertEquals("16:30", formatNoteDate(LocalDateTime.of(2026, 10, 28, 16, 30), now))
    }

    @Test
    fun earlierThisYearShowsDayAndMonth() {
        assertEquals("3 mars", formatNoteDate(LocalDateTime.of(2026, 3, 3, 9, 0), now))
    }

    @Test
    fun previousYearShowsYearToo() {
        assertEquals("28 oct. 2025", formatNoteDate(LocalDateTime.of(2025, 10, 28, 9, 0), now))
    }

    @Test
    fun midnightTodayStillShowsTime() {
        assertEquals("00:05", formatNoteDate(LocalDateTime.of(2026, 10, 28, 0, 5), now))
    }
}
