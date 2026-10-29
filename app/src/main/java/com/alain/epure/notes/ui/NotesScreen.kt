package com.alain.epure.notes.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.alain.epure.notes.R
import com.alain.epure.notes.ui.theme.LocalNoteColors
import androidx.compose.ui.graphics.SolidColor

// Liste des notes, de la plus récente à la plus ancienne (SPEC F-6).
@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    onOpenNote: (String) -> Unit,
    onDeleteNote: (String) -> Unit,
    onBack: () -> Unit,
    onNewNote: () -> Unit,
    onOpenTasks: () -> Unit,
    onOpenSettings: () -> Unit,
    backupActive: Boolean
) {
    val rows by viewModel.rows.collectAsState()
    val query by viewModel.query.collectAsState()
    val context = LocalContext.current
    var noteToDelete by remember { mutableStateOf<NoteRow?>(null) }

    LaunchedEffect(Unit) { viewModel.refresh() }
    // Retour vide d'abord la recherche, puis revient à l'éditeur (SPEC T-8).
    val goBack = {
        if (query.isNotEmpty()) viewModel.setQuery("") else onBack()
    }
    BackHandler(onBack = goBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalNoteColors.current.background)
            .systemBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.notes_title),
            style = MaterialTheme.typography.titleLarge,
            color = LocalNoteColors.current.textPrimary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
        )
        // Champ « Rechercher » (SPEC F-11.1), sans loupe.
        BasicTextField(
            value = query,
            onValueChange = viewModel::setQuery,
            singleLine = true,
            textStyle = TextStyle(color = LocalNoteColors.current.textPrimary, fontSize = 16.sp),
            cursorBrush = SolidColor(LocalNoteColors.current.textPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 48.dp)
                .padding(horizontal = 24.dp),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_hint),
                            style = TextStyle(color = LocalNoteColors.current.textSecondary, fontSize = 16.sp)
                        )
                    }
                    innerTextField()
                }
            }
        )
        if (rows.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(if (query.isEmpty()) R.string.notes_empty else R.string.search_none),
                    color = LocalNoteColors.current.textSecondary
                )
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(rows, key = { it.id }) { row ->
                    NoteItem(
                        row = row,
                        onOpen = { onOpenNote(row.id) },
                        onShare = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, row.text)
                                putExtra(Intent.EXTRA_SUBJECT, row.title)
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        },
                        onDelete = { noteToDelete = row }
                    )
                }
            }
        }
        HorizontalDivider(color = LocalNoteColors.current.textSecondary, thickness = 1.dp)
        Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                CommandButton(
                    label = stringResource(R.string.action_new_note),
                    description = stringResource(R.string.cd_new_note),
                    onClick = onNewNote
                )
                }
                Row {
                CommandButton(
                    label = stringResource(R.string.action_back),
                    description = stringResource(R.string.action_back),
                    onClick = goBack
                )
                CommandButton(
                    label = stringResource(R.string.action_tasks),
                    description = stringResource(R.string.action_tasks),
                    onClick = onOpenTasks
                )
                CommandButton(
                    label = stringResource(R.string.action_settings),
                    description = stringResource(R.string.cd_settings),
                    onClick = onOpenSettings
                )
                }
            }
    }

    noteToDelete?.let { row ->
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            text = {
                Text(
                    text = stringResource(
                        if (backupActive) R.string.delete_note_question_backup else R.string.delete_note_question
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteNote(row.id)
                    noteToDelete = null
                    viewModel.refresh()
                }) {
                    Text(text = stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun NoteItem(
    row: NoteRow,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onOpen,
                    onLongClick = { menuOpen = true }
                )
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = row.title,
                color = LocalNoteColors.current.textPrimary,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = row.dateLabel,
                color = LocalNoteColors.current.textSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.action_share)) },
                onClick = {
                    menuOpen = false
                    onShare()
                }
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.action_delete)) },
                onClick = {
                    menuOpen = false
                    onDelete()
                }
            )
        }
    }
}
