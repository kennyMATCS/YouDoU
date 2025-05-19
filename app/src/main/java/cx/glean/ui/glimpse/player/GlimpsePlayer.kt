package cx.glean.ui.glimpse.player

import android.view.LayoutInflater
import android.view.Window
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import cx.glean.R
import cx.glean.ui.glimpse.Glimpse
import cx.glean.ui.glimpse.getUri

data class WatchingInfo(var watching: Boolean, var glimpseWatching: Glimpse?)

fun MutableState<WatchingInfo>.clear() {
    value = value.copy(watching = false, glimpseWatching = null)
}

@Composable
fun GlimpsePlayer(
    modifier: Modifier,
    window: Window,
    watchingInfo: MutableState<WatchingInfo>
) {
    val context = LocalContext.current
    var exoPlayer = ExoPlayer.Builder(context)
        .setHandleAudioBecomingNoisy(true)
        .build()
    var windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

    exoPlayer.addListener(object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                watchingInfo.clear()
            }
        }
    })

    var mediaItem = MediaItem.fromUri(watchingInfo.value.glimpseWatching?.video!!.getUri
        (context))

    LaunchedEffect(mediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            watchingInfo.value = watchingInfo.value.copy(watching = false, glimpseWatching = null)

            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    // TODO: view reuse for performance
    AndroidView(
        factory = { context ->
            (LayoutInflater.from(context).inflate(R.layout.glimpse_player_view,
                null, false) as PlayerView).apply {
                player = exoPlayer
                // black background
            }
        },
        modifier = modifier
            .fillMaxSize()
    )
}