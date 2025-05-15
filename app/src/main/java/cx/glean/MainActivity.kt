package cx.glean

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.window.core.layout.WindowSizeClass
import cx.glean.ui.glimpse.DetailPaneBreakpoint
import cx.glean.ui.glimpse.Glimpse
import cx.glean.ui.glimpse.GlimpseGrid
import cx.glean.ui.glimpse.getUri
import cx.glean.ui.theme.GleanTheme
import cx.glean.ui.glimpse.previewGlimpses

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            GleanTheme {
                GleanScaffold(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .background(Color.Black),
                    activity = this,
                    glimpses = previewGlimpses)
            }
        }
    }
}

data class WatchingInfo(var watching: Boolean, var glimpseWatching: Glimpse?)

@OptIn(UnstableApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun GleanScaffold(
    modifier: Modifier,
    glimpses: List<Glimpse> = listOf(),
    activity: Activity?
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    var windowInsetsController = WindowCompat.getInsetsController(activity!!.window, activity.window.decorView)
    // windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())

    val detailPaneBreakpoint: DetailPaneBreakpoint = if (windowSizeClass.isAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND, WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND)) {
        DetailPaneBreakpoint.EXPANDED
    } else if (windowSizeClass.isAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
            WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)) {
        DetailPaneBreakpoint.MEDIUM
    } else {
        DetailPaneBreakpoint.COMPACT
    }

    var watchingInfo = remember { mutableStateOf(WatchingInfo(false, null)) }
    var start = AppDestinations.VIEW
    var currentDestination by rememberSaveable { mutableStateOf(start) }
    val context = LocalContext.current
    var exoPlayer = ExoPlayer.Builder(context)
        .setHandleAudioBecomingNoisy(true)
        .build()

    exoPlayer.addListener(object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying == false) {
                watchingInfo.value = watchingInfo.value.copy(watching = false)
            }
        }
    })

    if (watchingInfo.value.watching) {
        // TODO: scary null call, check on this
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

        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                    setBackgroundResource(R.drawable.black)
                }
            },
            modifier = Modifier
                .fillMaxSize()
        )
    } else {
        NavigationSuiteScaffold(
            modifier = modifier
                .fillMaxSize(),
            navigationSuiteItems = {
                AppDestinations.entries.forEach {
                    item(
                        icon = {
                            Icon(
                                it.icon,
                                contentDescription = stringResource(it.contentDescription)
                            )
                        },
                        label = {
                            Text(stringResource(it.label))
                        },
                        selected = (it == currentDestination),
                        onClick = { currentDestination = it }
                    )
                }
            }
        ) {
            when (currentDestination) {
                AppDestinations.RECORD -> { }
                AppDestinations.VIEW -> {
                    GlimpseGrid(
                        modifier = Modifier,
                        glimpses = glimpses,
                        contentPadding = PaddingValues(10.dp),
                        detailPaneBreakpoint = detailPaneBreakpoint,
                        watchingInfo = watchingInfo,
                    )
                }
                AppDestinations.INFO -> { }
                AppDestinations.SETTINGS -> { }
            }
        }
    }
}

fun Context.getActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

@Preview
@Composable
fun PreviewScaffold() {
    GleanScaffold(Modifier, previewGlimpses, LocalContext.current.getActivity())
}