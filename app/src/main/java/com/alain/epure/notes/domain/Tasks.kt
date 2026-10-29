package com.alain.epure.notes.domain

// Une tâche de la liste « Tâches » (SPEC F-12, DEC-0014).
data class Task(val text: String, val done: Boolean)

// Lit le fichier `taches.md` : une tâche par ligne, « - [ ] texte » ou « - [x] texte ». Les autres lignes sont ignorées.
fun parseTasks(content: String): List<Task> =
    content.lineSequence().mapNotNull { line ->
        val match = TASK_LINE.matchEntire(line.trim()) ?: return@mapNotNull null
        Task(text = match.groupValues[2], done = match.groupValues[1] != " ")
    }.toList()

// Écrit le fichier `taches.md` : une tâche par ligne.
fun formatTasks(tasks: List<Task>): String =
    tasks.joinToString(separator = "") { task ->
        "- [${if (task.done) "x" else " "}] ${task.text}\n"
    }

// Ligne d'une note transformée en tâche (SPEC F-13.3) : sans espaces, sans puce initiale. Null si vide.
fun lineToTaskText(line: String): String? {
    val text = line.trim().replace(LEADING_BULLET, "").trim()
    return text.ifEmpty { null }
}

// Ordre d'affichage (SPEC F-12.3) : les tâches à faire d'abord, les faites en bas ; l'ordre est conservé dans chaque groupe.
fun displayOrder(tasks: List<Task>): List<Int> =
    tasks.indices.sortedBy { index -> if (tasks[index].done) 1 else 0 }

private val TASK_LINE = Regex("""-\s\[( |x|X)]\s(.+)""")
private val LEADING_BULLET = Regex("""^[-*•]\s*""")
