package com.urmilabs.shield.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import com.urmilabs.shield.core.ui.R

@Composable
fun TranscriptionOverlay(transcribedText: String, suspiciousKeyword: String?) {
    val annotatedText = buildAnnotatedString {
        append(transcribedText)
        if (suspiciousKeyword != null) {
            val startIndex = transcribedText.indexOf(suspiciousKeyword, ignoreCase = true)
            if (startIndex != -1) {
                addStyle(
                    style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold),
                    start = startIndex,
                    end = startIndex + suspiciousKeyword.length
                )
            }
        }
    }

    val context = LocalContext.current
    val description = if (suspiciousKeyword != null) {
        context.getString(R.string.desc_live_transcription_warning, suspiciousKeyword)
    } else {
        context.getString(R.string.desc_live_transcription)
    }

    Box(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(16.dp)
            .semantics {
                contentDescription = description
                liveRegion = LiveRegionMode.Polite
            }
    ) {
        Text(
            text = annotatedText,
            color = Color.White
        )
    }
}
