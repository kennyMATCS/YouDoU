package net.youdou.ui.screens.tale.player

import android.net.Uri
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

// TODO: check all TODO's we need to do lol
@Composable
fun TalePlayer(
    uri: Uri,
    exoPlayer: ExoPlayer,
    @LayoutRes layout: Int,
    onVideoEndOrClose: () -> Unit
) {
    var mediaItem = MediaItem.fromUri(uri)

    exoPlayer.addListener(object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                onVideoEndOrClose()
            }
        }
    })

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

    // TODO: is this null okay in inflate
    // TODO: view reuse for performance
    AndroidView(
        factory = { context ->
            (LayoutInflater.from(context).inflate(
                layout,
                null, false
            ) as PlayerView).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxSize()
    )
}