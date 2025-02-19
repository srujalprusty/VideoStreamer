package com.example.videosteaming.Screen

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UrlInputDialog(
    onDismiss: () -> Unit,
    onUrlEntered: (String) -> Unit
) {
    var urlText by remember { mutableStateOf(TextFieldValue("")) }
    var isValidUrl by remember { mutableStateOf(true) } // ✅ Track URL validity

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val url = urlText.text.trim()
                    if (isValidVideoUrl(url)) {
                        onUrlEntered(url)
                        onDismiss()  // Close dialog after entering a valid URL
                    } else {
                        isValidUrl = false // ✅ Mark URL as invalid
                    }
                },
                enabled = urlText.text.isNotBlank()
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Enter Video URL") },
        text = {
            Column {
                OutlinedTextField(
                    value = urlText,
                    onValueChange = {
                        urlText = it
                        isValidUrl = true // ✅ Reset error when user types
                    },
                    label = { Text("Paste your URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (!isValidUrl) { // ✅ Show error if URL is invalid
                    Text(
                        text = "Invalid URL. Please enter a valid video link.",
                        color = androidx.compose.ui.graphics.Color.Red,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    )
}

// ✅ Function to validate the URL format
fun isValidVideoUrl(url: String): Boolean {
    val youtubeRegex = "(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+".toRegex()
    val videoFormats = listOf(".mp4", ".m3u8", ".avi", ".mov", ".flv", ".rtsp", ".mkv")

    return url.matches(youtubeRegex) || (Patterns.WEB_URL.matcher(url).matches() && videoFormats.any { url.endsWith(it) })
}

@Preview
@Composable
fun preview() {
    UrlInputDialog(onDismiss = { }, onUrlEntered = {})
}
