package com.example.videosteaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.exoplayer.ExoPlayer
import com.example.videosteaming.Screen.homeScreen
import com.example.videosteaming.ui.theme.VideoSteamingTheme

class MainActivity : ComponentActivity() {
    private lateinit var exoPlayer: ExoPlayer
    private val streamingViewModel: StreamingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ExoPlayer
        exoPlayer = ExoPlayer.Builder(this).build()

        setContent {
            VideoSteamingTheme {
                homeScreen(viewModel = streamingViewModel)
            }
        }

        // Observe lifecycle changes for ExoPlayer
        lifecycle.addObserver(PlayerLifecycleObserver(exoPlayer))
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()  // Release the player when the activity is destroyed
    }
}

// ✅ Lifecycle observer to pause and resume ExoPlayer properly
class PlayerLifecycleObserver(private val exoPlayer: ExoPlayer) : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
            Lifecycle.Event.ON_RESUME -> exoPlayer.play()
            Lifecycle.Event.ON_DESTROY -> exoPlayer.release()
            else -> {}
        }
    }
}
