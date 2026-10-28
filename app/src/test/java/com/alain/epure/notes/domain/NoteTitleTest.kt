package com.alain.epure.notes.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class NoteTitleTest {

    @Test
    fun emptyNoteHasEmptyTitle() {
        assertEquals("", noteTitle(""))
    }

    @Test
    fun blankNoteHasEmptyTitle() {
        assertEquals("", noteTitle("   \n\t\n  "))
    }

    @Test
    fun titleIsFirstLine() {
        assertEquals("Courses", noteTitle("Courses\nlait\npain"))
    }

    @Test
    fun leadingBlankLinesAreSkipped() {
        assertEquals("Réunion", noteTitle("\n\n  \nRéunion\nordre du jour"))
    }

    @Test
    fun surroundingSpacesAreTrimmed() {
        assertEquals("Élève ECOS", noteTitle("   Élève ECOS   \nsuite"))
    }

    @Test
    fun windowsLineEndingsAreHandled() {
        assertEquals("Titre", noteTitle("Titre\r\nCorps"))
    }

    @Test
    fun singleLineNoteIsItsOwnTitle() {
        assertEquals("Idée", noteTitle("Idée"))
    }

    @Test
    fun longFirstLineIsKeptWhole() {
        val line = "a".repeat(500)
        assertEquals(line, noteTitle("$line\nsuite"))
    }
}
