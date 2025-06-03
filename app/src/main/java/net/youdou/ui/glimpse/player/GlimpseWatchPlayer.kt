package net.youdou.ui.glimpse.player

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import net.youdou.R
import net.youdou.ui.glimpse.Glimpse
import net.youdou.ui.glimpse.getUri

@SuppressLint("InflateParams")
@Composable
fun GlimpseWatchPlayer(
    glimpse: Glimpse,
    onVideoEndOrClose: () -> Unit
) {
    val context = LocalContext.current
    var exoPlayer = ExoPlayer.Builder(context)
        .setHandleAudioBecomingNoisy(true)
        .build()

    exoPlayer.addListener(object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                onVideoEndOrClose()
            }
        }
    })

    var mediaItem = MediaItem.fromUri(glimpse.video.getUri
        (context))

    LaunchedEffect(mediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // TODO: view reuse for performance
    AndroidView(
        factory = { context ->
            (LayoutInflater.from(context).inflate(R.layout.glimpse_watch_player_view,
                null, false) as PlayerView).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxSize()
    )
}