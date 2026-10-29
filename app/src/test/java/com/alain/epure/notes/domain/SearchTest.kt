package com.alain.epure.notes.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchTest {

    @Test
    fun searchIgnoresCaseAndAccents() {
        assertTrue(matchesSearch("Élève ECOS", "eleve"))
        assertTrue(matchesSearch("Élève ECOS", "ECOS"))
        assertTrue(matchesSearch("élève", "Élève"))
    }

    @Test
    fun noMatchIsFalse() {
        assertFalse(matchesSearch("Courses\nlait", "pain"))
    }

    @Test
    fun blankQueryMatchesEverything() {
        assertTrue(matchesSearch("texte", "   "))
    }

    @Test
    fun firstMatchRangeIsInOriginalText() {
        assertEquals(8..12, firstMatchRange("Bonjour élève, élève", "ELEVE"))
    }

    @Test
    fun firstMatchRangeIsNullWhenAbsent() {
        assertNull(firstMatchRange("Courses", "pain"))
    }
}
