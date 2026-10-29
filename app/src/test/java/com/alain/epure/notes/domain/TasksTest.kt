package com.alain.epure.notes.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TasksTest {

    @Test
    fun parsesOpenAndDoneTasks() {
        val content = "- [ ] appeler le labo\n- [x] commander\nun commentaire\n"
        assertEquals(
            listOf(Task("appeler le labo", false), Task("commander", true)),
            parseTasks(content)
        )
    }

    @Test
    fun formatThenParseKeepsTasks() {
        val tasks = listOf(Task("un", false), Task("deux", true))
        assertEquals(tasks, parseTasks(formatTasks(tasks)))
    }

    @Test
    fun lineBecomesTaskWithoutBullet() {
        assertEquals("appeler le labo", lineToTaskText("  - appeler le labo  "))
        assertEquals("idée", lineToTaskText("• idée"))
    }

    @Test
    fun blankLineAddsNothing() {
        assertNull(lineToTaskText("   "))
        assertNull(lineToTaskText("-"))
    }

    @Test
    fun doneTasksGoToTheBottomKeepingOrder() {
        val tasks = listOf(Task("a", true), Task("b", false), Task("c", true), Task("d", false))
        assertEquals(listOf(1, 3, 0, 2), displayOrder(tasks))
    }
}
