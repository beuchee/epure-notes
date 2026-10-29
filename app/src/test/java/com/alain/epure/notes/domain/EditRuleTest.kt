package com.alain.epure.notes.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EditRuleTest {

    @Test
    fun everythingAllowedWhenDeletionIsOn() {
        assertTrue(isEditAllowed(oldLength = 10, newLength = 9, deletionAllowed = true))
    }

    @Test
    fun deletionRefusedWhenOff() {
        assertFalse(isEditAllowed(oldLength = 10, newLength = 9, deletionAllowed = false))
    }

    @Test
    fun insertionAllowedWhenOff() {
        assertTrue(isEditAllowed(oldLength = 10, newLength = 11, deletionAllowed = false))
    }

    @Test
    fun sameLengthReplacementAllowedWhenOff() {
        assertTrue(isEditAllowed(oldLength = 10, newLength = 10, deletionAllowed = false))
    }
}
