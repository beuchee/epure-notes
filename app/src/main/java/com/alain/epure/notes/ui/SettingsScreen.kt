package com.alain.epure.notes.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alain.epure.notes.R
import com.alain.epure.notes.ui.theme.Black
import com.alain.epure.notes.ui.theme.TextPrimary
import com.alain.epure.notes.ui.theme.TextSecondary

// Liste de réglages (DEC-0024). Pour l'instant : « À propos ». La sauvegarde arrivera ici.
@Composable
fun SettingsScreen(onOpenAbout: () -> Unit, onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .systemBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
        )
        Text(
            text = stringResource(R.string.settings_about),
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenAbout)
                .sizeIn(minHeight = 48.dp)
                .padding(horizontal = 24.dp, vertical = 12.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(color = TextSecondary, thickness = 1.dp)
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
            TextButton(
                onClick = onBack,
                colors = ButtonDefaults.textButtonColors(contentColor = TextPrimary)
            ) {
                Text(text = stringResource(R.string.action_back))
            }
        }
    }
}
