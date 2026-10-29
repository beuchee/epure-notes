package com.alain.epure.notes.ui

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.alain.epure.notes.ui.theme.LocalNoteColors

// Commande d'une barre du bas : libellé court (symbole ou mot), nom complet pour les lecteurs d'écran (SPEC F-7.5, DEC-0039).
@Composable
fun CommandButton(label: String, description: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .sizeIn(minHeight = 48.dp)
            .semantics { contentDescription = description },
        colors = ButtonDefaults.textButtonColors(contentColor = LocalNoteColors.current.textPrimary)
    ) {
        Text(text = label)
    }
}
