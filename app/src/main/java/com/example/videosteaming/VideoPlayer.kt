package com.example.videosteaming

import android.content.Context
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(videoUrl: String, context: Context) {
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) } // ✅ Track ExoPlayer instance
    var webView by remember { mutableStateOf<WebView?>(null) } // ✅ Track WebView instance

    // Stop & release previous player before loading a new one
    LaunchedEffect(videoUrl) {
        exoPlayer?.release()  // ✅ Release ExoPlayer before setting a new video
        webView?.stopLoading() // ✅ Stop previous WebView loading
    }

    // ✅ Ensure function is called correctly
    if (isYouTubeUrl(videoUrl)) {
        val videoId = extractYouTubeVideoId(videoUrl)
        if (!videoId.isNullOrEmpty()) {
            val youtubeEmbedUrl = "https://www.youtube.com/embed/$videoId"
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.mediaPlaybackRequiresUserGesture = false  // ✅ Fix autoplay issues
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                Log.d("WebView", "Page Loaded: $url")
                            }
                        }

                        webChromeClient = WebChromeClient()
                        loadUrl("https://www.youtube.com/embed/${extractYouTubeVideoId(videoUrl)}")
                    }
                }
            )
            }
        }
     else {
        // Play RTSP or MP4 using ExoPlayer
        val newExoPlayer = ExoPlayer.Builder(context)
            .setRenderersFactory(DefaultRenderersFactory(context).setEnableDecoderFallback(true))
            .build().apply {
                val mediaItem = MediaItem.Builder()
                    .setUri(Uri.parse(videoUrl))
                    .build()
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true  // Auto-play
            }
        exoPlayer = newExoPlayer // ✅ Assign new player instance

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = newExoPlayer
                    useController = true  // Show Play/Pause controls
                }
            }
        )
    }
}

// ✅ Ensure this function is properly defined
fun isYouTubeUrl(url: String): Boolean {
    return url.contains("youtube.com") || url.contains("youtu.be")
}

// ✅ Ensure this function correctly extracts YouTube Video ID
@OptIn(UnstableApi::class)
fun extractYouTubeVideoId(url: String): String? {
    val regex = Regex("(?:v=|youtu\\.be/|embed/|watch\\?v=|\\/v\\/|\\/e\\/|watch\\?feature=player_embedded&v=|shorts/)([a-zA-Z0-9_-]{11})")
    val match = regex.find(url)
    val videoId = match?.groupValues?.get(1)?.split("?")?.get(0)  // Remove extra parameters
    Log.d("YouTube ID", "Extracted ID: $videoId")  // Debugging
    return videoId
}


