package net.youdou.ui.glimpse.player

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import net.youdou.R

@OptIn(UnstableApi::class)
@SuppressLint("InflateParams")
@Composable
fun GlimpseRecordPlayer(
    uri: Uri,
) {
    val context = LocalContext.current

    val exoPlayer = ExoPlayer.Builder(context)
        .setHandleAudioBecomingNoisy(true)
        .build()

    var mediaItem = MediaItem.fromUri(uri)

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

    AndroidView(
        factory = { context ->
            (LayoutInflater.from(context).inflate(
                R.layout.glimpse_record_player_view,
                null, false
            ) as PlayerView).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxSize()
    )
}