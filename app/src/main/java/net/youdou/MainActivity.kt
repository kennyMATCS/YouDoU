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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.credentials.CredentialManager
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsRadioButton
import com.alorma.compose.settings.ui.SettingsSwitch
import net.youdou.ui.glimpse.Glimpse
import net.youdou.ui.glimpse.GlimpseGrid
import net.youdou.ui.glimpse.player.GlimpseWatchPlayer
import net.youdou.ui.theme.YouDoUTheme
import net.youdou.ui.glimpse.previewGlimpses
import net.youdou.ui.glimpse.record.GlimpseCamera
import net.youdou.ui.theme.DarkExpiringSoon
import net.youdou.ui.theme.ExpiringSoon
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import net.youdou.ui.account.AccountSignInPage
import net.youdou.ui.account.AccountSignUpPage
import net.youdou.ui.account.AccountStartPage
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

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

class MainActivity : ComponentActivity() {
    lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    var canUseCameraCallback = mutableStateOf(false)
    var canUseCameraAudioCallback = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher. You can use either a val, as shown in this snippet,
        // or a lateinit var in your onAttach() or onCreate() method.
        requestPermissionLauncher =
            registerForActivityResult(
                RequestMultiplePermissions()
            ) { granted ->
                granted.forEach { a ->
                    when (a.key) {
                        Manifest.permission.CAMERA -> {
                            if (a.value) canUseCameraCallback.value = true
                        }

                        Manifest.permission.RECORD_AUDIO -> {
                            if (a.value) canUseCameraAudioCallback.value = true
                        }
                    }
                }
            }

        val uri: MutableState<Uri?> = mutableStateOf(null)
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

                        enterTransition = {
                            slideInVertically(
                                initialOffsetY = { -40 }) + expandVertically(expandFrom = Alignment.CenterVertically) + scaleIn(
                                transformOrigin = TransformOrigin(0.5f, 0f)
                            ) + fadeIn(initialAlpha = 0.3f)
                        },

                        exitTransition = {
                            slideOutVertically(
                                targetOffsetY = { -40 }) + shrinkVertically(shrinkTowards = Alignment.CenterVertically) + scaleOut(
                                transformOrigin = TransformOrigin(0.5f, 0f)
                            ) + fadeOut(targetAlpha = 0.3f)
                        },

                        ) {
                        var windowInsetsController =
                            WindowCompat.getInsetsController(window, window.decorView)

                        composable<AccountStartPage> {
                            AccountStartPage(
                                navigateSignIn = { navController.navigate(route = AccountSignIn) },
                                navigateSignUp = { navController.navigate(route = AccountSignUp) }
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

                            YouDoUScaffold(
                                activity = activity,
                                glimpses = previewGlimpses,
                                onClickGlimpse = { glimpse ->
                                    navController.navigate(
                                        route = glimpse
                                    )
                                },
                                onClickSettings = {
                                    navController.navigate(Settings)
                                },
                                canUseCameraCallback = canUseCameraCallback,
                                canUseCameraAudioCallback = canUseCameraAudioCallback,
                                uri = uri
                            )
                        }

                        composable<Glimpse> { backStackEntry ->
                            with(windowInsetsController) {
                                hide(WindowInsetsCompat.Type.systemBars())
                                systemBarsBehavior =
                                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                            }

                            val glimpse: Glimpse = backStackEntry.toRoute()

                            GlimpseWatchPlayer(
                                glimpse = glimpse,
                            ) {
                                navController.navigate(route = MainApp)
                            }
                        }
                        composable<Settings> {
                            YouDoUSettings()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun YouDoUScaffold(
    glimpses: List<Glimpse> = listOf(),
    onClickGlimpse: (Glimpse) -> Unit,
    onClickSettings: () -> Unit,
    activity: MainActivity?,
    canUseCameraCallback: MutableState<Boolean>,
    canUseCameraAudioCallback: MutableState<Boolean>,
    uri: MutableState<Uri?>
) {
    var start = AppDestinations.VIEW
    var pagerState = rememberPagerState(initialPage = start.pageNumber) {
        AppDestinations.entries.size
    }

    var recording = remember { mutableStateOf(false) }
    var secondsUntilCanRecordAgain = remember { mutableLongStateOf(0) }
    val atEnd = remember { mutableStateOf(false) }

    LaunchedEffect(secondsUntilCanRecordAgain.longValue) {
        if (secondsUntilCanRecordAgain.longValue > 0) {
            delay(1000L)
            secondsUntilCanRecordAgain.longValue -= 1
        }
    }

    Scaffold(
        topBar = {
            YouDoUTopBar(onClickSettings, secondsUntilCanRecordAgain)
        }) { innerPadding ->
        HorizontalPager(
            state = pagerState, modifier = Modifier.fillMaxSize(),
            userScrollEnabled = !recording.value
        ) { page ->
            when (page) {
                AppDestinations.RECORD.pageNumber -> {
                    GlimpseCamera(
                        contentPadding = innerPadding,
                        canUseCamera = canUseCameraCallback,
                        canUseCameraAudio = canUseCameraAudioCallback,
                        secondsUntilCanRecordAgain = secondsUntilCanRecordAgain,
                        isRecording = recording,
                        atEnd = atEnd,
                        uri = uri,
                        activity = activity,
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
@Composable
fun YouDoUTopBar(
    onClickSettings: () -> Unit = { },
    secondsUntilCanRecordAgain: MutableState<Long>,
) {
    val color = MaterialTheme.colorScheme.primaryContainer

    Surface(
        shadowElevation = 10.dp,
    ) {
        TopAppBar(
            title = {
                YouDoUTopText()
            },
            actions = {
                Text(
                    text = secondsUntilCanRecordAgain.value.formatTimeSeconds(),
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .alpha(
                            if (secondsUntilCanRecordAgain.value > 0) 1f else 0f
                        ),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (!isSystemInDarkTheme()) ExpiringSoon else DarkExpiringSoon,
                )

                // TODO: add link to app in final version
                val context = LocalContext.current
                val text = stringResource(R.string.share_message).replace(
                    "%app_name%", stringResource(
                        R.string.app_name
                    )
                )

                Icon(
                    Icons.Filled.Share,
                    contentDescription = "Share app",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .clickable {
                            share(
                                text = text, context = context
                            )
                        })

                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .clickable {
                            onClickSettings()
                        })
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.background(color.gradient()),
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

@Preview
@Composable
fun YouDoUSettings() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            var switchState by remember { mutableStateOf(false) }
            var checkBoxState by remember { mutableStateOf(false) }
            var radioState by remember { mutableIntStateOf(0) }

            Column {
                SettingsGroup {
                    SettingsSwitch(
                        state = switchState, title = { Text("Test Switch") }) {
                        switchState = !switchState
                    }

                    SettingsCheckbox(
                        state = checkBoxState, title = { Text("Test Checkbox") }) {
                        checkBoxState = !checkBoxState
                    }
                }

                SettingsGroup {
                    SettingsRadioButton(
                        state = radioState == 0, title = { Text("Option 1") }) {
                        radioState = 0
                    }

                    SettingsRadioButton(
                        state = radioState == 1, title = { Text("Option 2") }) {
                        radioState = 1
                    }

                    SettingsRadioButton(
                        state = radioState == 2, title = { Text("Option 3") }) {
                        radioState = 2
                    }
                }
            }
        }
    }
}


@Composable
@Preview
fun PreviewScaffold() {
    YouDoUScaffold(
        glimpses = previewGlimpses,
        onClickGlimpse = { },
        onClickSettings = { },
        activity = null,
        canUseCameraCallback = remember { mutableStateOf(true) },
        canUseCameraAudioCallback = remember { mutableStateOf(true) },
        uri = remember { mutableStateOf(null) }
    )
}

@Preview
@Composable
fun PreviewYouDoUTopBar() {
    YouDoUTopBar(
        onClickSettings = { },
        secondsUntilCanRecordAgain = remember { mutableLongStateOf(100L) })
}

private fun Long.formatTimeSeconds(): String {
    return seconds.toComponents { hours, minutes, seconds, nanoseconds ->
        StringBuilder().apply {
            if (hours > 0) {
                append(
                    String.format(
                        Locale.US, "%2d:", hours
                    )
                )
            }

            append(
                String.format(
                    Locale.US, "%02d:", minutes
                )
            )

            append(
                String.format(
                    Locale.US, "%02d", seconds
                )
            )
        }.toString()
    }
}

private fun share(text: String, context: Context) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plan"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)

    context.startActivity(shareIntent)
}