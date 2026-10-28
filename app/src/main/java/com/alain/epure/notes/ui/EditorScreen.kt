package com.alain.epure.notes.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alain.epure.notes.R
import com.alain.epure.notes.ui.theme.Black
import com.alain.epure.notes.ui.theme.TextPrimary
import com.alain.epure.notes.ui.theme.TextSecondary

// Éditeur plein écran : texte brut, marges de 24 dp, barre de commandes en bas (SPEC F-2, F-7).
@Composable
fun EditorScreen(viewModel: EditorViewModel, onOpenNotes: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .systemBarsPadding()
            .imePadding()
    ) {
        BasicTextField(
            state = viewModel.textState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            textStyle = TextStyle(color = TextPrimary, fontSize = 20.sp),
            cursorBrush = SolidColor(TextPrimary),
            lineLimits = TextFieldLineLimits.MultiLine(),
            scrollState = rememberScrollState(),
            decorator = { innerTextField ->
                Box {
                    if (viewModel.textState.text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.editor_placeholder),
                            style = TextStyle(color = TextSecondary, fontSize = 20.sp)
                        )
                    }
                    innerTextField()
                }
            }
        )
        HorizontalDivider(color = TextSecondary, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            TextButton(
                onClick = viewModel::newNote,
                modifier = Modifier.sizeIn(minHeight = 48.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = TextPrimary)
            ) {
                Text(text = stringResource(R.string.action_new_note))
            }
            TextButton(
                onClick = onOpenNotes,
                modifier = Modifier.sizeIn(minHeight = 48.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = TextPrimary)
            ) {
                Text(text = stringResource(R.string.action_open_notes))
            }
        }
    }
}
