package com.alain.epure.notes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alain.epure.notes.R
import com.alain.epure.notes.domain.isEditAllowed
import com.alain.epure.notes.ui.theme.LocalNoteColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop

private const val COMMAND_BAR_DELAY_MS = 3_000L

// Éditeur plein écran : texte brut, marges de 24 dp, barre de commandes en bas (SPEC F-2, F-7).
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onOpenNotes: () -> Unit,
    onOpenTasks: () -> Unit,
    onAddTask: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val textSize by viewModel.textSize.collectAsState()
    val taskAdded by viewModel.taskAdded.collectAsState()
    // Avec une sélection, « > » crée une note au lieu d'ajouter une tâche (DEC-0040).
    var hasSelection by remember { mutableStateOf(false) }
    LaunchedEffect(viewModel.textState) {
        snapshotFlow { !viewModel.textState.selection.collapsed }.collect { hasSelection = it }
    }
    val deletionAllowed by viewModel.deletionAllowed.collectAsState()
    // « Effacer : non » : toute modification qui retire des caractères est annulée (SPEC F-8.3, DEC-0015).
    val inputTransformation = remember(deletionAllowed) {
        InputTransformation {
            if (!isEditAllowed(originalText.length, length, deletionAllowed)) revertAllChanges()
        }
    }

    // La barre disparaît pendant la frappe et réapparaît après 3 s sans frappe (SPEC F-7.4, DEC-0029).
    var barVisible by remember { mutableStateOf(true) }
    LaunchedEffect(viewModel.textState) {
        snapshotFlow { viewModel.textState.text.toString() }
            .drop(1)
            .collectLatest {
                barVisible = false
                delay(COMMAND_BAR_DELAY_MS)
                barVisible = true
            }
    }
    // Un toucher dans le texte, ou la fermeture du clavier, fait réapparaître la barre (SPEC F-7.4).
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press) barVisible = true
        }
    }
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    LaunchedEffect(imeVisible) {
        if (!imeVisible) barVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalNoteColors.current.background)
            .systemBarsPadding()
            .imePadding()
    ) {
        BasicTextField(
            state = viewModel.textState,
            inputTransformation = inputTransformation,
            keyboardOptions = KeyboardOptions(autoCorrectEnabled = deletionAllowed),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            textStyle = TextStyle(color = LocalNoteColors.current.textPrimary, fontSize = textSize.sp),
            cursorBrush = SolidColor(LocalNoteColors.current.textPrimary),
            lineLimits = TextFieldLineLimits.MultiLine(),
            scrollState = rememberScrollState(),
            interactionSource = interactionSource,
            decorator = { innerTextField ->
                Box {
                    if (viewModel.textState.text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.editor_placeholder),
                            style = TextStyle(color = LocalNoteColors.current.textSecondary, fontSize = textSize.sp)
                        )
                    }
                    innerTextField()
                }
            }
        )
        if (barVisible) {
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
                    onClick = viewModel::newNote
                )
                CommandButton(
                    label = stringResource(
                        when {
                            hasSelection -> R.string.action_to_note
                            taskAdded -> R.string.action_added
                            else -> R.string.action_to_task
                        }
                    ),
                    description = stringResource(
                        when {
                            hasSelection -> R.string.cd_to_note
                            taskAdded -> R.string.action_added
                            else -> R.string.cd_to_task
                        }
                    ),
                    // La sélection est relue au moment du clic, pas seulement à l'affichage.
                    onClick = {
                        if (!viewModel.textState.selection.collapsed) viewModel.newNoteFromSelection() else onAddTask()
                    }
                )
                }
                Row {
                CommandButton(
                    label = stringResource(R.string.action_open_notes),
                    description = stringResource(R.string.action_open_notes),
                    onClick = onOpenNotes
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
    }
}
