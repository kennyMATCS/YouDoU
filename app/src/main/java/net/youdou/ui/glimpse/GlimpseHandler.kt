package net.youdou.ui.glimpse

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.compose.material3.Icon
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import net.youdou.R
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import net.youdou.ui.theme.HeartRed
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@Serializable
data class Glimpse(
    val duration: Int,
    val thumbnail: Int,
    val contentDescription: Int,
    val time: Int,
    val video: Int,
    val secondsUntilExpiration: Int,
    val hearts: Int
)

data class DropDownItem(
    val text: String, val isError: Boolean, val onItemClick: () -> Unit
)

// TODO: live heart updates!!! this should have an animation too, keeps us all connected

// TODO: like animation
// TODO: like pop-up with heart count

// TODO: report feature done

// TODO: have a way to see how many times you hearted a glimpse. should you be able to heart more
//  than
//  once? i think so

// TODO: have a way for users to see how many times people liked and viewed their glimpse. this
//  is VERY IMPORTANT

// TODO: label all animations

// TODO: disable spaces in copy and paste with text field for login
// TODO: maintain video location after exiting glimpse
// TODO: don't reset app when orientation changed
// TODO: remove glimpse after watched

// TODO: Skipped 31 frames!  The application may be doing too much work on its main thread.

// TODO: In Jetpack Compose, you should never pass a MutableState<T> as parameter to other
//  Composables, as this violates the unidirectional data flow pattern.

// TODO: class rename, they don't feel right. They are too spigot-esque. Composables are not like
//  spigot

// TODO: is there a way to stop using mutablestate.value so much?

// TODO: good comments for everything

// TODO; report stack traces from crashes

// TODO: control where videos are stored for app. ensure they can be easily accessed for the user
//  . maybe have a setting to pick where they are stored. also, check android settings and make
//  sure videos don't fall under the "cache" category.
// Package cx.youdou does not have legacy storage error

// TODO: check all strings. See if they make sense

// TODO: Unable to configure camera Camera@7759467[id=1]
// java.util.concurrent.TimeoutException: Future[androidx.camera.core.impl.utils.futures
// .ListFuture@57472ca] is not done within 5000 ms.
//    at androidx.camera.core.impl.utils.futures.Futures.lambda$makeTimeoutFuture$1(Futures.java:427)
//    at androidx.camera.core.impl.utils.futures.Futures$$ExternalSyntheticLambda7.call(D8$$SyntheticClass:0)
//    at androidx.camera.core.impl.utils.executor.HandlerScheduledExecutorService$HandlerScheduledFuture.run(HandlerScheduledExecutorService.java:240)
//    at android.os.Handler.handleCallback(Handler.java:958)
//    at android.os.Handler.dispatchMessage(Handler.java:99)
//    at android.os.Looper.loopOnce(Looper.java:205)
//    at android.os.Looper.loop(Looper.java:294)
//    at android.os.HandlerThread.run(HandlerThread.java:67)

// TODO:
//Exception thrown during dispatchAppVisibility Window{517e2ac u0 cx.youdou/net.youdou.MainActivity
//        EXITING}
//android.os.DeadObjectException
//at android.os.BinderProxy.transactNative(Native Method)
//at android.os.BinderProxy.transact(BinderProxy.java:584)
//at android.view.IWindow$Stub$Proxy.dispatchAppVisibility(IWindow.java:546)
//at com.android.server.wm.WindowState.sendAppVisibilityToClients(WindowState.java:3271)
//at com.android.server.wm.WindowContainer.sendAppVisibilityToClients(WindowContainer.java:1221)
//at com.android.server.wm.WindowToken.setClientVisible(WindowToken.java:409)
//at com.android.server.wm.ActivityRecord.setClientVisible(ActivityRecord.java:6946)
//at com.android.server.wm.ActivityRecord.postApplyAnimation(ActivityRecord.java:5637)
//at com.android.server.wm.ActivityRecord.commitVisibility(ActivityRecord.java:5580)
//at com.android.server.wm.Transition.finishTransition(Transition.java:1151)
//at com.android.server.wm.TransitionController.finishTransition(TransitionController.java:875)
//at com.android.server.wm.WindowOrganizerController.finishTransition(WindowOrganizerController.java:396)
//at android.window.IWindowOrganizerController$Stub.onTransact(IWindowOrganizerController.java:286)
//at com.android.server.wm.WindowOrganizerController.onTransact(WindowOrganizerController.java:181)
//at android.os.Binder.execTransactInternal(Binder.java:1339)
//at android.os.Binder.execTransact(Binder.java:1275)

// TODO: better login form error messages. model from Cloudflare login
// TODO: clean up login form, specifically the labels for text fields

// TODO: landscape mode remove overlapping camera ON glimpse view AND player view

// TODO: logo
// TODO; playful, animated, baldi's basics vibe

// TODO: admob

// TODO: ask nico about beta testing.
// TODO: button to reset app state for beta testers, e.g. reset recording timer or default glimpses

// TODO: subreddit

// TODO: improve record glimpse UI
// TODO: redo button shadows and length

// TODO: better accessibility content descriptions
// TODO: big plus button in empty glimpse at the bottom so people have a way to purchase premium

// TODO: one purchasable thing only: premium!
// TODO: make popup to show benefits of premium. this could be a nice pop-up composable in the
//  middle of the screen

// TODO: find a way to reduce minsdk. all people should be able to use the app :)

const val GLIMPSE_DURATION_SPECIFIER = "%glimpse_duration"

val dropDownItems = listOf(
    DropDownItem("Report", isError = true, onItemClick = { }),
    DropDownItem("Length: $GLIMPSE_DURATION_SPECIFIER", isError = false, onItemClick = { }),
)

var previewGlimpses = listOf(
    Glimpse(
        duration = R.integer.preview_1_duration,
        thumbnail = R.drawable.preview_1,
        contentDescription = R.string.preview_1_content_description,
        time = R.string.preview_1_date,
        video = R.raw.preview_1,
        secondsUntilExpiration = R.integer.preview_1_seconds_until_expiration,
        hearts = R.integer.preview_1_hearts,
    ), Glimpse(
        duration = R.integer.preview_2_duration,
        thumbnail = R.drawable.preview_2,
        contentDescription = R.string.preview_2_content_description,
        time = R.string.preview_2_date,
        video = R.raw.preview_2,
        secondsUntilExpiration = R.integer.preview_2_seconds_until_expiration,
        hearts = R.integer.preview_2_hearts,
    ), Glimpse(
        duration = R.integer.preview_3_duration,
        thumbnail = R.drawable.preview_3,
        contentDescription = R.string.preview_3_content_description,
        time = R.string.preview_3_date,
        video = R.raw.preview_3,
        secondsUntilExpiration = R.integer.preview_3_seconds_until_expiration,
        hearts = R.integer.preview_3_hearts,
    ), Glimpse(
        duration = R.integer.preview_4_duration,
        thumbnail = R.drawable.preview_4,
        contentDescription = R.string.preview_4_content_description,
        time = R.string.preview_4_date,
        video = R.raw.preview_4,
        secondsUntilExpiration = R.integer.preview_4_seconds_until_expiration,
        hearts = R.integer.preview_4_hearts,
    ), Glimpse(
        duration = R.integer.preview_5_duration,
        thumbnail = R.drawable.preview_5,
        contentDescription = R.string.preview_5_content_description,
        time = R.string.preview_5_date,
        video = R.raw.preview_5,
        secondsUntilExpiration = R.integer.preview_5_seconds_until_expiration,
        hearts = R.integer.preview_5_hearts,
    ), Glimpse(
        duration = R.integer.preview_6_duration,
        thumbnail = R.drawable.preview_6,
        contentDescription = R.string.preview_6_content_description,
        time = R.string.preview_6_date,
        video = R.raw.preview_6,
        secondsUntilExpiration = R.integer.preview_6_seconds_until_expiration,
        hearts = R.integer.preview_6_hearts,
    ), Glimpse(
        duration = R.integer.preview_7_duration,
        thumbnail = R.drawable.preview_1,
        contentDescription = R.string.preview_7_content_description,
        time = R.string.preview_7_date,
        video = R.raw.preview_7,
        secondsUntilExpiration = R.integer.preview_7_seconds_until_expiration,
        hearts = R.integer.preview_7_hearts,
    ), Glimpse(
        duration = R.integer.preview_8_duration,
        thumbnail = R.drawable.preview_8,
        contentDescription = R.string.preview_8_content_description,
        time = R.string.preview_8_date,
        video = R.raw.preview_8,
        secondsUntilExpiration = R.integer.preview_8_seconds_until_expiration,
        hearts = R.integer.preview_8_hearts,
    ), Glimpse(
        duration = R.integer.preview_9_duration,
        thumbnail = R.drawable.preview_9,
        contentDescription = R.string.preview_9_content_description,
        time = R.string.preview_9_date,
        video = R.raw.preview_9,
        secondsUntilExpiration = R.integer.preview_9_seconds_until_expiration,
        hearts = R.integer.preview_9_hearts,
    ), Glimpse(
        duration = R.integer.preview_10_duration,
        thumbnail = R.drawable.preview_10,
        contentDescription = R.string.preview_10_content_description,
        time = R.string.preview_10_date,
        video = R.raw.preview_10,
        secondsUntilExpiration = R.integer.preview_10_seconds_until_expiration,
        hearts = R.integer.preview_10_hearts,
    ), Glimpse(
        duration = R.integer.preview_11_duration,
        thumbnail = R.drawable.preview_11,
        contentDescription = R.string.preview_11_content_description,
        time = R.string.preview_11_date,
        video = R.raw.preview_11,
        secondsUntilExpiration = R.integer.preview_11_seconds_until_expiration,
        hearts = R.integer.preview_11_hearts,
    ), Glimpse(
        duration = R.integer.preview_12_duration,
        thumbnail = R.drawable.preview_12,
        contentDescription = R.string.preview_12_content_description,
        time = R.string.preview_12_date,
        video = R.raw.preview_12,
        secondsUntilExpiration = R.integer.preview_12_seconds_until_expiration,
        hearts = R.integer.preview_12_hearts,
    )
)

// TODO: glimpse expiring animation. make it like fly away. also make times of glimpse 2 hours
//  apart with first glimpse having time of like 10 seconds
// TODO: disable haptics in settings
// TODO: clicking hearts and plus at bottom should prompt to buy new things, we don't need a
//  store tab
// TODO: introduction tutorial
// TODO: force ui check before pushing code
// TODO: fix dark mode in ui check. also look through dark mode needs to be better
// TODO: better landscape mode OR force portrait on phone
// TODO: create settings
// TODO: adjust in-out animation. learn how to use animation manager
// TODO: fix ui check FOR EVERYTHING
// TODO: unit testing -- espresso

// TODO: make code look better, clean it up! more functions!
// TODO: we should not get lost in the code!
// TODO: we have lots of mutable states and if statements. see if we can clean them up

// TODO: lots of launch effects. anyway to trim that up?

// TODO: if we have more functions, we can have better names for stuff which means more readable
//  code
// TODO: visit weatherspoon
// TODO: improve dropdown popup

// TODO: maintain states for everything
// TODO: how do we ensure local states are synced with API and people don't cheat the system
// TODO: maybe only accept api calls for local app events if they are valid, i.e. times match up
//  from server-side by what client is asking

// TODO: clean up all variables

// TODO: ability to tap to focus with camera

@Composable
fun GlimpseGrid(
    modifier: Modifier,
    glimpses: MutableList<Glimpse>,
    contentPadding: PaddingValues,
    onClickGlimpse: (Glimpse) -> Unit,
    isPremium: Boolean
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val detailPaneBreakpoint: DetailPaneBreakpoint = if (windowSizeClass.isAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND,
            WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND
        )
    ) {
        DetailPaneBreakpoint.EXPANDED
    } else if (windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
        )
    ) {
        DetailPaneBreakpoint.MEDIUM
    } else {
        DetailPaneBreakpoint.COMPACT
    }

    val pad = 8.dp
    val arrangementPadding = 2.dp
    LazyVerticalGrid(
        columns = when (detailPaneBreakpoint) {
            DetailPaneBreakpoint.COMPACT -> GridCells.Fixed(1)
            DetailPaneBreakpoint.MEDIUM -> GridCells.Fixed(2)
            DetailPaneBreakpoint.EXPANDED -> GridCells.Fixed(3)
        },
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.navigationBars),
        contentPadding = with(contentPadding) {
            PaddingValues(
                top = calculateTopPadding() + pad,
                bottom = pad,
                start = calculateLeftPadding(LayoutDirection.Ltr) + pad,
                end = calculateRightPadding(LayoutDirection.Rtl) + pad
            )
        },

        horizontalArrangement = Arrangement.spacedBy(arrangementPadding),
        verticalArrangement = Arrangement.spacedBy(arrangementPadding)
    ) {
        items(
            items = if (!isPremium) glimpses.take(6) else glimpses,
        ) {
            GlimpseCard(
                modifier = Modifier,
                glimpse = it,
                onClickGlimpse = onClickGlimpse,
                onRemoveGlimpse = { glimpse ->
                    // TODO: glimpses expiring
                    // glimpses.remove(glimpse)
                })
        }
        if (!isPremium) {
            item {
                GlimpsePurchaseCard(modifier = Modifier)
            }
        }
    }
}

@Composable
private fun GlimpseCardBase(modifier: Modifier, content: @Composable (() -> Unit)) {
    Surface(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        shadowElevation = 5.dp,
        tonalElevation = 5.dp,
        shape = MaterialTheme.shapes.large,
    ) {
        content()
    }
}

// TODO: clickable purchase card that prompts to store
@Composable
fun GlimpsePurchaseCard(
    modifier: Modifier,
) {
    // hard-coded
    val cornerRadiusDp = 15.dp

    GlimpseCardBase(modifier) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp)
                .clip(MaterialTheme.shapes.large)
                .dashedBorder(
                    strokeWidth = 7.dp,
                    color = MaterialTheme.colorScheme.outline,
                    cornerRadiusDp = cornerRadiusDp
                )
                .clickable {

                },
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_add),
                contentDescription = stringResource(R.string.glimpse_purchase_content_description),
                modifier = Modifier
                    .graphicsLayer(1f)
                    .size(200.dp)
                    .align(Alignment.Center),
                tint = MaterialTheme.colorScheme.secondary
            )

            Image(
                painter = painterResource(R.drawable.transparent_background),
                contentDescription = stringResource(R.string.glimpse_purchase_content_description),
                alpha = 0f
            )
        }
    }
}

@Composable
fun GlimpseCard(
    modifier: Modifier,
    glimpse: Glimpse,
    onClickGlimpse: (Glimpse) -> Unit,
    onRemoveGlimpse: (Glimpse) -> Unit,
) {
    var isContextMenuVisible = remember { mutableStateOf(false) }
    var contextMenuOffset = remember { mutableStateOf(Offset.Zero) }

    GlimpseCardBase(
        modifier = modifier
    ) {
        Box {
            YouDoUDropDown(dropDownItems, isContextMenuVisible, contextMenuOffset, glimpse)
        }

        val farSeconds = integerResource(R.integer.far_seconds)
        val mediumSeconds = integerResource(R.integer.medium_seconds)

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp)
        ) {
            val cornerPadding = 8.dp
            val textStyle = MaterialTheme.typography.bodyLarge
            val behindShape = MaterialTheme.shapes.large
            val behindPadding = 8.dp
            val behindColor = MaterialTheme.colorScheme.surface

            Box {
                val secs = integerResource(glimpse.secondsUntilExpiration).toLong()
                var expirationSeconds by remember { mutableLongStateOf(secs.toLong()) }

                val expirationColor = if (!isSystemInDarkTheme()) {
                    when {
                        expirationSeconds > farSeconds -> MaterialTheme.colorScheme.onSurface
                        expirationSeconds > mediumSeconds -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.error
                    }
                } else {
                    when {
                        expirationSeconds > farSeconds -> MaterialTheme.colorScheme.onSurface
                        expirationSeconds > mediumSeconds -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.error
                    }
                }

                // TODO: make this constant ?
                val heightFactor: Float = (expirationSeconds / (60f * 60f))

                // TODO: better variable names

                var width by remember { mutableIntStateOf(1) }
                var height by remember { mutableIntStateOf(1) }

                var startX by remember { mutableFloatStateOf(0f) }
                var startY by remember { mutableFloatStateOf(0f) }

                // gray expiration bar and thumbnail
                Image(
                    painter = painterResource(glimpse.thumbnail),
                    contentDescription = stringResource(glimpse.contentDescription),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .drawWithContent {
                            drawContent()
                            if (expirationSeconds < mediumSeconds) {
                                drawRect(
                                    color = Color(0, 0, 0, 125), size = Size(
                                        size.width - (size.width * heightFactor), size.height
                                    )
                                )
                            }
                        }
                        .onGloballyPositioned {
                            width = max(abs(it.size.width), 1)
                            height = max(abs(it.size.height), 1)
                        }
                        .combinedClickable(true, onClick = {
                            onClickGlimpse(glimpse)
                        }, onLongClick = {
                            isContextMenuVisible.value = true
                        })
                        .pointerInteropFilter {
                            contextMenuOffset.value = Offset(it.x, it.y)
                            false
                        },
                )

                // TODO: change when expiration data isn't hardcoded
                LaunchedEffect(Unit) {
                    while (expirationSeconds > 0) {
                        delay(1000L)
                        expirationSeconds -= 1
                    }

                    onRemoveGlimpse(glimpse)
                    cancel()
                }

                // time
                Box(
                    modifier = Modifier
                        .padding(cornerPadding)
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        fontWeight = if (expirationSeconds < mediumSeconds) FontWeight.Bold else FontWeight.Normal,
                        text = expirationSeconds.formatTimeSeconds(),
                        style = textStyle,
                        color = expirationColor,
                        modifier = modifier
                            .clip(behindShape)
                            .background(behindColor)
                            .padding(behindPadding)
                    )
                }

                val heartsVal = integerResource(glimpse.hearts)
                var hearts by remember { mutableIntStateOf(heartsVal) }

                val context = LocalContext.current
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(
                        Context.VIBRATOR_MANAGER_SERVICE
                    ) as VibratorManager
                    vibratorManager.defaultVibrator
                } else {
                    @Suppress("DEPRECATION") context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }

                // TODO: test bubbling on different display sizes

                // hearts
                var bubbling by remember { mutableStateOf(HeartState.HIDE) }
                val heartShownDuration = integerResource(R.integer.heart_shown_duration)
                val heartRepeatAmount = integerResource(R.integer.heart_repeat_amount)

                var heartTargets by remember {
                    mutableStateOf(
                        List(heartRepeatAmount) {
                            Offset(startX, startY)
                        }
                    )
                }

                LaunchedEffect(bubbling) {
                    if (bubbling == HeartState.SHOW) {
                        heartTargets = heartTargets.makeHeartTargets(width, height)

                        // DELAY
                        delay(heartShownDuration.toLong())
                        // DELAY

                        bubbling = HeartState.HIDE
                    }
                }

                val painter = rememberVectorPainter(Icons.Filled.Favorite)

                val transition = updateTransition(bubbling)

                val offsets = heartTargets.map {
                    transition.animateOffset { state ->
                        when (state) {
                            HeartState.SHOW -> Offset(it.x, it.y)
                            HeartState.HIDE -> Offset(startX, startY)
                        }
                    }
                }

                Canvas(
                    modifier = Modifier
                ) {
                    with(painter) {
                        repeat(heartRepeatAmount) { i ->
                            with(offsets[i]) {
                                translate(value.x, value.y) {
                                    draw(
                                        painter.intrinsicSize,
                                        colorFilter = ColorFilter.tint(HeartRed)
                                    )
                                }
                            }
                        }
                    }
                }

                // hearts
                Box(
                    modifier = Modifier
                        .clip(behindShape)
                        .clickable {
                            hearts++

                            bubbling = HeartState.SHOW

                            vibrate(vibrator)
                        }
                        .align(Alignment.BottomEnd)) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(cornerPadding)
                            .clip(behindShape)
                            .background(behindColor)
                            .padding(behindPadding),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val icon: ImageVector
                        val tint: Color

                        when (hearts) {
                            0 -> {
                                icon = ImageVector.vectorResource(
                                    R.drawable.outline_favorite
                                )
                                tint = MaterialTheme.colorScheme.onSurface
                            }

                            else -> {
                                icon = Icons.Filled.Favorite
                                // TODO: specify colors in Colors.kt
                                tint = HeartRed
                            }
                        }

                        Icon(
                            imageVector = icon, contentDescription = if (hearts == 0) {
                                stringResource(
                                    R.string.no_hearts_content_description
                                )
                            } else {
                                String.format(
                                    stringResource(
                                        R.string.yes_hearts_content_description
                                    ), hearts
                                )
                            }, tint = tint, modifier = Modifier.onGloballyPositioned {
                                with(it.positionInRoot()) {
                                    startX = x
                                    startY = y
                                }
                            })

                        if (hearts > 0) {
                            Text(
                                text = hearts.toString(),
                                style = textStyle,
                            )
                        }
                    }
                }
            }

            Text(
                text = stringResource(glimpse.time),
                style = textStyle,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }
    }
}

// TODO: constants for these randoms?
private fun List<Offset>.makeHeartTargets(
    width: Int, height: Int
) = map {
    val xRandom = (width - Random.nextInt(width / 12, width / 2)).toFloat()
    val yRandom = (height - Random.nextInt(height / 7, height / 2)).toFloat()
    Offset(
        xRandom, yRandom
    )
}


private fun List<Offset>.resetHeartTargets(
    startX: Float, startY: Float
) = map {
    Offset(startX, startY)
}

private fun vibrate(vibrator: Vibrator) {
    // must have equal amount in each array
    // TODO: unit test to ensure array have equal length?
    val timings: LongArray = longArrayOf(
        35, 35, 60, 35, 35, 35, 35, 35, 35, 35, 35, 35
    )

    val amplitudes: IntArray = intArrayOf(
        12, 25, 26, 28, 30, 33, 34, 35, 30, 25, 19, 12,
    )

    val repeatIndex = -1 // Do not repeat.

    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))
}

@Composable
fun YouDoUDropDown(
    dropDownItems: List<DropDownItem>,
    isContextMenuVisible: MutableState<Boolean>,
    contextMenuOffset: MutableState<Offset>,
    glimpse: Glimpse
) {
    val density = LocalDensity.current
    val dpOffset = with(density) {
        DpOffset(
            contextMenuOffset.value.x.toDp(), contextMenuOffset.value.y.toDp()
        )
    }

    DropdownMenu(
        expanded = isContextMenuVisible.value,
        onDismissRequest = {
            isContextMenuVisible.value = false
        },
        offset = dpOffset,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 3.dp,
        shape = MaterialTheme.shapes.small
    ) {
        dropDownItems.forEach {
            DropdownMenuItem(onClick = {
                it.onItemClick()
                isContextMenuVisible.value = false
            }, text = {
                Text(
                    text = it.text.replace(
                        GLIMPSE_DURATION_SPECIFIER,
                        integerResource(glimpse.duration).toLong()
                            .formatTimeSeconds(appendZero = false)
                    ),
                    color = if (it.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                )
            })
        }
    }
}

enum class HeartState {
    SHOW, HIDE
}

enum class DetailPaneBreakpoint {
    COMPACT, MEDIUM, EXPANDED
}

// TODO: fix this guy right here
private fun Modifier.dashedBorder(strokeWidth: Dp, color: Color, cornerRadiusDp: Dp) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }
        val cornerRadiusPx = density.run { cornerRadiusDp.toPx() }

        this.then(
            Modifier.drawWithCache {
                onDrawBehind {
                    val stroke = Stroke(
                        width = strokeWidthPx,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(25f, 15f), 0f)
                    )

                    drawRoundRect(
                        color = color, style = stroke, cornerRadius = CornerRadius(cornerRadiusPx)
                    )
                }
            })
    })

// TODO: where should this go? should it be in a Util class? or do we just leave it here? other
//  classes are referencing this which is why im asking
fun Long.formatTimeSeconds(appendZero: Boolean = true): String {
    return seconds.toComponents { hours, minutes, seconds, nanoseconds ->
        StringBuilder().apply {
            if (hours > 1) {
                append(
                    String.format(
                        Locale.US, "%d hours", hours
                    )
                )
            } else if (hours == 1L) {
                append(
                    String.format(
                        Locale.US, "%d hour", hours
                    )
                )
            } else {
                val specifier = if (appendZero) "%02d:%02d" else "%d:%02d"
                append(
                    String.format(
                        Locale.US, specifier, minutes, seconds
                    )
                )
            }
        }.toString()
    }
}

fun Int.getUri(context: Context): Uri {
    val item = this
    return with(context.resources) {
        Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(getResourcePackageName(item)).appendPath(getResourceTypeName(item))
            .appendPath(getResourceEntryName(item)).build()
    }
}

@Preview
@Composable
fun PreviewGlimpseCardSoon() {
    var glimpse = previewGlimpses[1]

    GlimpseCard(
        modifier = Modifier,
        glimpse = glimpse,
        onClickGlimpse = { },
        onRemoveGlimpse = { },
    )
}

@Preview
@Composable
fun PreviewGlimpseCardMedium() {
    var glimpse = previewGlimpses[2]

    GlimpseCard(
        modifier = Modifier,
        glimpse = glimpse,
        onClickGlimpse = { },
        onRemoveGlimpse = { },
    )
}

@Preview
@Composable
fun PreviewGlimpseCardFar() {
    var glimpse = previewGlimpses[3]

    GlimpseCard(
        modifier = Modifier,
        glimpse = glimpse,
        onClickGlimpse = { },
        onRemoveGlimpse = { },
    )
}

@Preview
@Composable
fun PreviewGlimpsePurchaseCard() {
    GlimpsePurchaseCard(modifier = Modifier)
}

@Suppress("VisualLintBounds", "VisualLintAccessibilityTestFramework")
@Preview
@Composable
fun PreviewGlimpseGrid() {
    GlimpseGrid(
        modifier = Modifier,
        glimpses = previewGlimpses.toMutableList(),
        contentPadding = PaddingValues(4.dp),
        onClickGlimpse = { },
        isPremium = false
    )
}