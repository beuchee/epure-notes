package com.alain.epure.notes.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.time.LocalDateTime

class NoteStoreTest {

    @get:Rule
    val folder = TemporaryFolder()

    private fun store() = NoteStore(folder.root)

    @Test
    fun createdIdIsNotWrittenUntilTextIsSaved() {
        val store = store()
        val id = store.create(LocalDateTime.of(2026, 10, 28, 16, 30, 12))
        assertEquals("2026-10-28-163012", id)
        assertNull(store.latestId())
    }

    @Test
    fun savedTextIsReadBack() {
        val store = store()
        val id = store.create()
        store.save(id, "Bonjour\nla suite")
        assertEquals("Bonjour\nla suite", store.read(id))
    }

    @Test
    fun savingAgainReplacesText() {
        val store = store()
        val id = store.create()
        store.save(id, "Premier")
        store.save(id, "Second")
        assertEquals("Second", store.read(id))
    }

    @Test
    fun blankTextIsNeverKept() {
        val store = store()
        val id = store.create()
        store.save(id, "Un texte")
        store.save(id, "   \n  ")
        assertNull(store.latestId())
        assertEquals("", store.read(id))
    }

    @Test
    fun latestIdIsTheMostRecentlyModifiedNote() {
        val store = store()
        val older = store.create(LocalDateTime.of(2026, 10, 1, 9, 0, 0))
        val newer = store.create(LocalDateTime.of(2026, 10, 2, 9, 0, 0))
        store.save(older, "ancienne")
        store.save(newer, "récente")
        File(folder.root, "$older.md").setLastModified(1_000_000L)
        File(folder.root, "$newer.md").setLastModified(2_000_000L)
        assertEquals(newer, store.latestId())
    }

    @Test
    fun deleteRemovesTheNote() {
        val store = store()
        val id = store.create()
        store.save(id, "à supprimer")
        store.delete(id)
        assertNull(store.latestId())
    }

    @Test
    fun summariesAreSortedFromNewestModified() {
        val store = store()
        val first = store.create(LocalDateTime.of(2026, 10, 1, 9, 0, 0))
        val second = store.create(LocalDateTime.of(2026, 10, 2, 9, 0, 0))
        store.save(first, "première")
        store.save(second, "deuxième")
        File(folder.root, "$first.md").setLastModified(3_000_000L)
        File(folder.root, "$second.md").setLastModified(2_000_000L)
        assertEquals(listOf(first, second), store.summaries().map { it.id })
        assertEquals("première", store.summaries().first().text)
    }

    @Test
    fun sameSecondGivesDifferentIds() {
        val store = store()
        val now = LocalDateTime.of(2026, 10, 28, 16, 30, 12)
        val first = store.create(now)
        store.save(first, "occupée")
        assertEquals("2026-10-28-163012-2", store.create(now))
    }
}
