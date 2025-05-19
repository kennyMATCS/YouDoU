package cx.glean

import android.app.Activity
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import cx.glean.ui.glimpse.DetailPaneBreakpoint
import cx.glean.ui.glimpse.Glimpse
import cx.glean.ui.glimpse.GlimpseGrid
import cx.glean.ui.glimpse.player.GlimpsePlayer
import cx.glean.ui.glimpse.player.WatchingInfo
import cx.glean.ui.glimpse.player.clear
import cx.glean.ui.theme.GleanTheme
import cx.glean.ui.glimpse.previewGlimpses

var watchingInfo: MutableState<WatchingInfo>? = null

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


        onBackPressedDispatcher.addCallback {
            watchingInfo?.clear()
        }
    }
}

@Composable
fun GleanScaffold(
    modifier: Modifier,
    glimpses: List<Glimpse> = listOf(),
    activity: Activity
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val detailPaneBreakpoint: DetailPaneBreakpoint = if (windowSizeClass.isAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND, WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND)) {
        DetailPaneBreakpoint.EXPANDED
    } else if (windowSizeClass.isAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
            WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)) {
        DetailPaneBreakpoint.MEDIUM
    } else {
        DetailPaneBreakpoint.COMPACT
    }

    var start = AppDestinations.VIEW
    var currentDestination by rememberSaveable { mutableStateOf(start) }

    watchingInfo = remember { mutableStateOf(WatchingInfo(false, null)) }

    if (watchingInfo!!.value.watching) {
        GlimpsePlayer(
            modifier = Modifier,
            window = activity.window,
            watchingInfo = watchingInfo!!
        )
    } else {
        // TODO: proper dark mode. nav bar is white when is should be black
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
                        watchingInfo = watchingInfo!!,
                    )
                }
                AppDestinations.INFO -> { }
                AppDestinations.SETTINGS -> { }
            }
        }
    }
}

//fun Context.getActivity(): Activity? {
//    var currentContext = this
//    while (currentContext is ContextWrapper) {
//        if (currentContext is Activity) {
//            return currentContext
//        }
//        currentContext = currentContext.baseContext
//    }
//    return null
//}
//
//@Preview
//@Composable
//fun PreviewScaffold() {
//    GleanScaffold(Modifier, previewGlimpses, LocalContext.current.getActivity())
//}