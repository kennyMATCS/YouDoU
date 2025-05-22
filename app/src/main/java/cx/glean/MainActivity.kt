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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import cx.glean.ui.glimpse.Glimpse
import cx.glean.ui.glimpse.GlimpseGrid
import cx.glean.ui.glimpse.player.GlimpsePlayer
import cx.glean.ui.theme.GleanTheme
import cx.glean.ui.glimpse.previewGlimpses
import cx.glean.ui.glimpse.record.GlimpseCamera
import kotlinx.serialization.Serializable

@Serializable
object MainApp

// TODO: speed up everything. overall we are slow.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: fix white strip above scaffold suite

        enableEdgeToEdge()
        setContent {
            GleanTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = MainApp,
                    enterTransition = {
                        slideInVertically(
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
                        var windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

                        with(windowInsetsController) {
                            show(WindowInsetsCompat.Type.systemBars())
                        }

                        GleanScaffold(
                            modifier = Modifier,
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
                                    glimpse.secondsUntilExpiration,
                                    glimpse.hearts
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
                            navController.navigate(route = MainApp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GleanScaffold(
    modifier: Modifier,
    glimpses: List<Glimpse> = listOf(),
    onClickGlimpse: (Glimpse) -> Unit
) {
    var start = AppDestinations.VIEW
    var currentDestination by rememberSaveable { mutableStateOf(start) }
    var pagerState = rememberPagerState(initialPage = start.pageNumber) {
        AppDestinations.entries.size
    }

    // TODO: proper dark mode. nav bar is white when is should be black

    NavigationSuiteScaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary,
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
                    onClick = {
                        currentDestination = it
                        pagerState.requestScrollToPage(currentDestination.pageNumber)
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                GleanTopBar()
            }
        ) { innerPadding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
            ) { page ->
                when (page) {
                    AppDestinations.RECORD.pageNumber -> {
                        GlimpseCamera(
                            modifier = Modifier,
                            contentPadding = innerPadding
                        )
                    }
                    AppDestinations.VIEW.pageNumber -> {
                        GlimpseGrid(
                            modifier = Modifier,
                            glimpses = glimpses.toMutableList(),
                            contentPadding = innerPadding,
                            onClickGlimpse = onClickGlimpse
                        )
                    }
                }

                // pretty hardcoded, but this should be okay
                currentDestination = AppDestinations.entries[pagerState.currentPage]
            }
        }
    }
}

private fun Color.gradient(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            copy(alpha = 1f),
            copy(alpha = .8f),
            copy(alpha = .6f),
            copy(alpha = .4f),
            copy(alpha = .2f),
            Color.Transparent
        ),
        startY = 0f,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun GleanTopBar() {
    val color = MaterialTheme.colorScheme.primaryContainer

    Surface(
        shadowElevation = 10.dp,
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "glean",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineLarge
                )
            },
            actions = {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier
                .background(color.gradient()),
            expandedHeight = 55.dp
        )
    }

}

@PreviewLightDark
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
                glimpse.secondsUntilExpiration,
                glimpse.hearts
            )
        )
    })
}