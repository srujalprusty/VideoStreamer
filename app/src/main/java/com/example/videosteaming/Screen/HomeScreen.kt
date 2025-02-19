package com.example.videosteaming.Screen

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.videosteaming.StreamingViewModel
import com.example.videosteaming.ui.theme.biege
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.videosteaming.VideoPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun homeScreen(viewModel: StreamingViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val videoUrl by viewModel.videoUrl.collectAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Video Streaming") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFf5f5dc), // Beige color
                        titleContentColor = Color.Black
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = Color(0xFFf5f5dc),
                    contentColor = Color.Black
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                if (!videoUrl.isNullOrEmpty()) {
                    if (isValidUrl(videoUrl!!)) {
                        VideoPlayer(videoUrl!!, context)
                    } else {
                        errorMessage = "Invalid URL! Please enter a valid YouTube or RTSP link."
                    }
                } else {
                    Text("Paste a URL to start streaming", style = MaterialTheme.typography.bodyLarge)
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }

            if (showDialog) {
                UrlInputDialog(
                    onDismiss = { showDialog = false },
                    onUrlEntered = { url ->
                        if (isValidUrl(url)) {
                            viewModel.setVideoUrl(url)
                            errorMessage = null // Reset error message on success
                        } else {
                            errorMessage = "Invalid URL! Please enter a valid video link."
                        }
                        showDialog = false
                    }
                )
            }
        }
    }
}

fun isValidUrl(url: String): Boolean {
    return url.isNotEmpty() && Patterns.WEB_URL.matcher(url).matches()
}

@Preview
@Composable
fun HomeScreenPreview(){
    homeScreen()
}