package com.alain.epure.notes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alain.epure.notes.R
import com.alain.epure.notes.ui.theme.Black
import com.alain.epure.notes.ui.theme.EpureNotesTheme
import com.alain.epure.notes.ui.theme.TextPrimary

// Écran provisoire du jalon 0 : fond noir et nom de l'app. Remplacé par l'éditeur au jalon 1.
@Composable
fun PlaceholderScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.app_name),
            color = TextPrimary,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlaceholderScreenPreview() {
    EpureNotesTheme {
        PlaceholderScreen()
    }
}
