package net.youdou

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import net.youdou.ui.screens.account.AccountSignInPage
import net.youdou.ui.screens.account.AccountSignUpPage
import net.youdou.ui.screens.account.AccountStartPage
import net.youdou.ui.screens.settings.YouDoUSettings
import net.youdou.ui.screens.tale.Tale
import net.youdou.ui.screens.tale.TaleGrid
import net.youdou.ui.screens.tale.player.TalePlayer
import net.youdou.ui.screens.tale.record.TaleCamera
import net.youdou.ui.theme.YouDoUTheme
import net.youdou.util.AppDestinations
import net.youdou.util.formatTimeSecondsFull
import net.youdou.util.getUri
import net.youdou.util.gradient
import net.youdou.util.previewTales

@Serializable
object MainApp

@Serializable
object Settings

@Serializable
object AccountStartPage

@Serializable
object AccountSignIn

@Serializable
object AccountSignUp

// TODO: multi-window mode
// TODO: go through all suppressions and clear unnecessary ones
// TODO: find a way to test on other phones

class MainActivity : ComponentActivity() {
    lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    var canUseCamera by mutableStateOf(false)
    var canUseCameraAudio by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher. You can use either a val, as shown in this snippet,
        // or a late-init var in your onAttach() or onCreate() method.
        // TODO: can we do this somewhere else in a function
        requestPermissionLauncher =
            registerForActivityResult(
                RequestMultiplePermissions()
            ) { granted ->
                granted.forEach { action ->
                    when (action.key) {
                        Manifest.permission.CAMERA -> {
                            if (action.value) canUseCamera = true
                        }

                        Manifest.permission.RECORD_AUDIO -> {
                            if (action.value) canUseCameraAudio = true
                        }
                    }
                }
            }

        val activity = this

        enableEdgeToEdge()
        setContent {
            YouDoUTheme {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxSize(),
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = AccountStartPage,

                        // TODO: animation manager
                        enterTransition = {
                            slideInVertically(initialOffsetY = { -40 }) + expandVertically(
                                expandFrom = Alignment.CenterVertically
                            ) + scaleIn(
                                transformOrigin = TransformOrigin(
                                    0.5f,
                                    0f
                                )
                            ) + fadeIn(initialAlpha = 0.3f)
                        },

                        exitTransition = {
                            slideOutVertically(targetOffsetY = { -40 }) + shrinkVertically(
                                shrinkTowards = Alignment.CenterVertically
                            ) + scaleOut(
                                transformOrigin = TransformOrigin(
                                    0.5f,
                                    0f
                                )
                            ) + fadeOut(targetAlpha = 0.3f)
                        },

                        ) {

                        var windowInsetsController =
                            WindowCompat.getInsetsController(
                                window,
                                window.decorView
                            )

                        composable<AccountStartPage> {
                            AccountStartPage(
                                navigateSignIn = {
                                    navController.navigate(route = AccountSignIn) {
                                        popUpTo(route = AccountSignIn) {
                                            inclusive = true
                                        }
                                    }
                                },
                                navigateSignUp = {
                                    navController.navigate(route = AccountSignUp) {
                                        popUpTo(route = AccountSignUp) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }

                        composable<AccountSignUp> {
                            AccountSignUpPage {
                                navController.navigate(MainApp) {
                                    popUpTo(0) // clear backstack
                                }
                            }
                        }

                        composable<AccountSignIn> {
                            AccountSignInPage {
                                navController.navigate(MainApp) {
                                    popUpTo(0) // clear backstack
                                }
                            }
                        }

                        composable<MainApp> {
                            with(windowInsetsController) {
                                show(WindowInsetsCompat.Type.systemBars())
                            }

                            var isRecording by remember { mutableStateOf(false) }
                            var recordingTimeout by remember { mutableLongStateOf(0L) }
                            var currentRecordedVideoUri by remember { mutableStateOf<Uri?>(null) }
                            var tales =
                                remember { previewTales.toMutableList() } // TODO: change in full version
                            var lensFacing by remember { mutableIntStateOf(LENS_FACING_FRONT) }

                            LaunchedEffect(recordingTimeout) {
                                if (recordingTimeout > 0) {
                                    delay(1000L)
                                    recordingTimeout--
                                }
                            }
                            YouDoUScaffold(
                                activity = activity,
                                tales = tales.toList(),
                                onClickTale = { tale ->
                                    navController.navigate(
                                        route = tale
                                    ) {
                                        popUpTo(route = tale) {
                                            inclusive = true
                                        }
                                    }
                                },
                                onClickSettings = {
                                    navController.navigate(Settings) {
                                        popUpTo(Settings) {
                                            inclusive = true
                                        }
                                    }
                                },
                                isRecording = isRecording,
                                toggleRecording = { isRecording = !isRecording },
                                recordingTimeout = recordingTimeout,
                                setRecordingTimeout = { recordingTimeout = it },
                                canUseCamera = canUseCamera,
                                canUseCameraAudio = canUseCameraAudio,
                                currentRecordedVideoUri = currentRecordedVideoUri,
                                setCurrentRecordedVideoUri = { currentRecordedVideoUri = it },
                                setCanUseCamera = { canUseCamera = it },
                                setCanUseCameraAudio = { canUseCameraAudio = it },
                                removeTale = { tales.remove(it) },
                                lensFacing = lensFacing,
                                setLensFacing = { lensFacing = it }
                            )
                        }

                        composable<Tale> { backStackEntry ->
                            with(windowInsetsController) {
                                hide(WindowInsetsCompat.Type.systemBars())
                                systemBarsBehavior =
                                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                            }

                            val tale: Tale = backStackEntry.toRoute()
                            val exoPlayer = ExoPlayer.Builder(LocalContext.current)
                                .setHandleAudioBecomingNoisy(true)
                                .build()

                            TalePlayer(
                                uri = tale.video.getUri(LocalContext.current),
                                exoPlayer = exoPlayer,
                                layout = R.layout.tale_watch_player_view,
                                onVideoEndOrClose = {
                                    navController.navigate(route = MainApp)
                                }
                            )
                        }

                        composable<Settings> {
                            // TODO: we will end up needing to pass callables here
                            YouDoUSettings()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouDoUScaffold(
    tales: List<Tale>,
    removeTale: (Tale) -> Unit,
    onClickTale: (Tale) -> Unit,
    onClickSettings: () -> Unit,
    lensFacing: Int,
    setLensFacing: (Int) -> Unit,
    isRecording: Boolean,
    recordingTimeout: Long,
    setRecordingTimeout: (Long) -> Unit,
    activity: MainActivity?,
    canUseCamera: Boolean,
    canUseCameraAudio: Boolean,
    setCanUseCamera: (Boolean) -> Unit,
    setCanUseCameraAudio: (Boolean) -> Unit,
    currentRecordedVideoUri: Uri?,
    setCurrentRecordedVideoUri: (Uri?) -> Unit,
    toggleRecording: () -> Unit,
) {
    var start = AppDestinations.VIEW
    var pagerState = rememberPagerState(initialPage = start.pageNumber) {
        AppDestinations.entries.size
    }

    // TODO: pinned top-bar setting
    // TODO: auto top-bar pull down setting
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topBarState
    )

    var heightOffsetTarget by remember { mutableFloatStateOf(0f) }
    val heightOffsetAnimated by animateFloatAsState(
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        targetValue = heightOffsetTarget,
    )

    Scaffold(
        topBar = {
            YouDoUTopBar(
                onClickSettings = onClickSettings,
                recordingTimeout = recordingTimeout,
                scrollBehavior = scrollBehavior
            )
        }) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = !isRecording
        ) { page ->
            when (page) {
                AppDestinations.RECORD.pageNumber -> {
                    if (pagerState.targetPage == AppDestinations.RECORD.pageNumber) {
                        heightOffsetTarget = topBarState.heightOffsetLimit
                        topBarState.heightOffset = heightOffsetAnimated
                    }

                    TaleCamera(
                        contentPadding = innerPadding,
                        lensFacing = lensFacing,
                        setLensFacing = setLensFacing,
                        canUseCamera = canUseCamera,
                        canUseCameraAudio = canUseCameraAudio,
                        setCanUseCamera = setCanUseCamera,
                        setCanUseCameraAudio = setCanUseCameraAudio,
                        recordingTimeout = recordingTimeout,
                        isRecording = isRecording,
                        currentRecordedVideoUri = currentRecordedVideoUri,
                        setCurrentRecordedVideoUri = setCurrentRecordedVideoUri,
                        activity = activity,
                        setRecordingTimeout = setRecordingTimeout,
                        toggleRecording = toggleRecording
                    )
                }

                AppDestinations.VIEW.pageNumber -> {
                    val destination = AppDestinations.VIEW.pageNumber

                    with(pagerState) {
                        if (targetPage == destination) {
                            if (settledPage != destination) {
                                heightOffsetTarget = 0f
                                topBarState.heightOffset = heightOffsetAnimated
                            } else {
                                heightOffsetTarget = topBarState.heightOffset
                            }
                        }
                    }


                    TaleGrid(
                        modifier = Modifier,
                        tales = tales,
                        contentPadding = innerPadding,
                        onClickTale = onClickTale,
                        scrollBehavior = scrollBehavior,
                        removeTale = removeTale
                    )
                }
            }
        }

    }
}

// TODO: ui check top-bar, settings, glimpse videos, glimpse player
// TODO: transparent top bar in camera
// TODO: ensure top bar behavior resets when switching to camera
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouDoUTopBar(
    onClickSettings: () -> Unit = { },
    recordingTimeout: Long,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val color = MaterialTheme.colorScheme.primaryContainer
    val iconPadding = 10.dp

    Surface(
        shadowElevation = 3.dp
    ) {
        TopAppBar(
            title = {
                YouDoUTopText()
            },
            scrollBehavior = scrollBehavior,
            actions = {

                Text(
                    text = recordingTimeout.formatTimeSecondsFull(),
                    modifier = Modifier
                        .padding(iconPadding)
                        .alpha(
                            if (recordingTimeout > 0) 1f else 0f
                        ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )

                // TODO: add link to app in final version
                val context = LocalContext.current
                val text = stringResource(R.string.share_message).replace(
                    "%app_name%",
                    stringResource(
                        R.string.app_name
                    )
                )

                Icon(
                    Icons.Filled.Share,
                    contentDescription = "Share app",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = iconPadding)
                        .clickable {
                            share(
                                text = text,
                                context = context
                            )
                        })

                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = iconPadding)
                        .clickable {
                            onClickSettings()
                        })
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
            modifier = Modifier
                .background(color.gradient()),
        )
    }
}

@Composable
fun YouDoUTopText(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.app_name),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.headlineLarge,
        modifier = modifier
    )
}

private fun share(
    text: String,
    context: Context,
) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(
            Intent.EXTRA_TEXT,
            text
        )
        type = "text/plan"
    }
    val shareIntent = Intent.createChooser(
        sendIntent,
        null
    )

    context.startActivity(shareIntent)
}

@Suppress("VisualLintBounds")
@Composable
@Preview
fun PreviewScaffold() {
    YouDoUScaffold(
        tales = previewTales,
        removeTale = { },
        onClickTale = { },
        onClickSettings = { },
        lensFacing = LENS_FACING_FRONT,
        setLensFacing = { },
        isRecording = false,
        recordingTimeout = 0L,
        setRecordingTimeout = { },
        activity = null,
        canUseCamera = true,
        canUseCameraAudio = true,
        setCanUseCamera = { },
        setCanUseCameraAudio = { },
        currentRecordedVideoUri = null,
        setCurrentRecordedVideoUri = { },
        toggleRecording = { }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewYouDoUTopBar() {
    YouDoUTopBar(
        recordingTimeout = 123L,
        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    )
}
