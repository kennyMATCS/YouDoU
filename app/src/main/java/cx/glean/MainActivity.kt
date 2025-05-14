package cx.glean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.window.core.layout.WindowSizeClass
import cx.glean.ui.glimpse.DetailPaneBreakpoint
import cx.glean.ui.glimpse.Glimpse
import cx.glean.ui.glimpse.GlimpseGrid
import cx.glean.ui.theme.GleanTheme
import cx.glean.ui.glimpse.previewGlimpses

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            GleanTheme {
                GleanScaffold(Modifier)
            }
        }
    }
}

@Composable
fun GleanScaffold(modifier: Modifier, glimpses: List<Glimpse> = listOf()) {
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
                    contentPadding = PaddingValues(7.dp),
                    detailPaneBreakpoint = detailPaneBreakpoint
                )
            }
            AppDestinations.INFO -> { }
            AppDestinations.SETTINGS -> { }
        }
    }
}

@Preview
@Composable
fun PreviewScaffold() {
    GleanScaffold(Modifier, previewGlimpses)
}