package com.alain.epure.notes.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class SharedTextTest {

    @Test
    fun textWithoutSubjectIsKeptAsIs() {
        assertEquals("Bonjour", sharedNoteText(null, "Bonjour"))
    }

    @Test
    fun subjectBecomesFirstLine() {
        assertEquals("Réunion\nordre du jour", sharedNoteText("Réunion", "ordre du jour"))
    }

    @Test
    fun blankSubjectIsIgnored() {
        assertEquals("texte", sharedNoteText("   ", "texte"))
    }

    @Test
    fun subjectAloneWhenTextIsBlank() {
        assertEquals("Sujet", sharedNoteText("Sujet", "  "))
    }
}
