package cx.glean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import cx.glean.ui.glimpse.Glimpse
import cx.glean.ui.glimpse.GlimpseGrid
import cx.glean.ui.glimpse.player.GlimpsePlayer
import cx.glean.ui.theme.GleanTheme
import cx.glean.ui.glimpse.previewGlimpses
import kotlinx.serialization.Serializable

@Serializable
object MainApp

// TODO: speed up everything. overall we are slow.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            GleanTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = MainApp,
                    enterTransition = {
                        slideInVertically(
                            // Start the slide from 40 (pixels) above where the content is supposed to go, to
                            // produce a parallax effect
                            initialOffsetY = { -40 }
                        ) + expandVertically(expandFrom = Alignment.CenterVertically) +
                                scaleIn(
                                    // Animate scale from 0f to 1f using the top center as the pivot point.
                                    transformOrigin = TransformOrigin(0.5f, 0f)
                                ) +
                                fadeIn(initialAlpha = 0.3f)
                    },
                    exitTransition = {
                        slideOutVertically(
                            // Start the slide from 40 (pixels) above where the content is supposed to go, to
                            // produce a parallax effect
                            targetOffsetY = { -40 }
                        ) + shrinkVertically(shrinkTowards = Alignment.CenterVertically) +
                                scaleOut(
                                    // Animate scale from 0f to 1f using the top center as the pivot point.
                                    transformOrigin = TransformOrigin(0.5f, 0f)
                                ) +
                                fadeOut(targetAlpha = 0.3f)
                    }
                ) {
                    composable<MainApp> {
                        GleanScaffold(
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.statusBars)
                                .background(Color.Black),
                            glimpses = previewGlimpses
                        ) { glimpse ->
                            navController.navigate(
                                route = Glimpse(
                                    glimpse.author,
                                    glimpse.duration,
                                    glimpse.thumbnail,
                                    glimpse.contentDescription,
                                    glimpse.time,
                                    glimpse.video,
                                    glimpse.secondsUntilExpiration
                                )
                            )
                        }
                    }

                    composable<Glimpse> { backStackEntry ->
                        val glimpse: Glimpse = backStackEntry.toRoute()
                        GlimpsePlayer(
                            modifier = Modifier,
                            window = window,
                            glimpse = glimpse,
                        ) {
                            // navController.navigate(route = MainApp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GleanScaffold(
    modifier: Modifier,
    glimpses: List<Glimpse> = listOf(),
    onClickGlimpse: (Glimpse) -> Unit
) {
    var start = AppDestinations.VIEW
    var currentDestination by rememberSaveable { mutableStateOf(start) }

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
            AppDestinations.RECORD -> {}
            AppDestinations.VIEW -> {
                GlimpseGrid(
                    modifier = Modifier,
                    glimpses = glimpses,
                    contentPadding = PaddingValues(10.dp),
                    onClickGlimpse = onClickGlimpse
                )
            }

            AppDestinations.INFO -> {}
            AppDestinations.SETTINGS -> {}
        }
    }
}

@Preview
@Composable
fun PreviewScaffold() {
    val navController = rememberNavController()

    GleanScaffold(Modifier, previewGlimpses, onClickGlimpse = { glimpse ->
        navController.navigate(
            route = Glimpse(
                glimpse.author,
                glimpse.duration,
                glimpse.thumbnail,
                glimpse.contentDescription,
                glimpse.time,
                glimpse.video,
                glimpse.secondsUntilExpiration
            )
        )
    })
}