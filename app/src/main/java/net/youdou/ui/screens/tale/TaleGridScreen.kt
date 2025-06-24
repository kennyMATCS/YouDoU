package net.youdou.ui.screens.tale

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import net.youdou.R
import net.youdou.ui.theme.HeartRed
import net.youdou.util.Digit
import net.youdou.util.compareTo
import net.youdou.util.formatTimeSeconds
import net.youdou.util.previewTales
import net.youdou.util.vibrate
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

@Serializable
data class Tale(
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

// TODO: add this to resource manager?
val DROP_DOWN_ITEMS = listOf(
    DropDownItem("Report", isError = true, onItemClick = { }),
    DropDownItem("Length: $TALE_DURATION_SPECIFIER", isError = false, onItemClick = { }),
)

const val TALE_DURATION_SPECIFIER = "%tale_duration"

enum class DetailPaneBreakpoint {
    COMPACT, MEDIUM, EXPANDED
}

// TODO: live heart updates!!! this should have an animation too, keeps us all connected
// TODO: the heart count could update in sync with the shake so we aren't spamming updates on screen
// TODO: we could also just not shake unless the user is doing it themselves
// TODO: we could only show hearts once the tale reaches certain milestones: 1, 5, 10, 20, 40,
//  100, 200, 300, 400, 500, etc.

// TODO: like animation
// TODO: like pop-up with heart count

// TODO: report feature done. nsfw scanning done

// TODO: have a way to see how many times you hearted a tale. should you be able to heart more
//  than
//  once? i think so

// TODO: more organized previews

// TODO: torrent like video sharing so i don't have to buy a server
// TODO: is this allowed in every country?
// TODO: maybe i can use a library for it
// TODO: i could do it in python

// TODO: animation when you get a new heart at the start of the day

// TODO: test record and watch player to see if they r working after consolidated classes

// TODO: have a way for users to see how many times people liked and viewed their tale. this
//  is VERY IMPORTANT

// TODO: prevent recorded video from stretching once opening to edit

// TODO: keep app data saved when tabbing out. rememberSaveable

// TODO: label all animations

// TODO: rememberSaveable to preserve state between screen rotations
//  https://developer.android.com/develop/ui/compose/state#restore-ui-state

// TODO: disable spaces in copy and paste with text field for login
// TODO: maintain video location after exiting tale
// TODO: don't reset app when orientation changed
// TODO: remove tale after watched

// TODO: Skipped 31 frames!  The application may be doing too much work on its main thread.

// TODO: make sure status bar does not show when exiting tale

// TODO: In Jetpack Compose, you should never pass a MutableState<T> as parameter to other
//  Composables, as this violates the unidirectional data flow pattern.
// TODO: what is unidirectional data flow pattern?
// TODO: "Utility" class to mange all the callbacks. That way, we don't need to pass as many
//  arguments down to child composables
// TODO: is there a way to stop using mutablestate.value so much? ANSWERED

// TODO: fix lag in with heart animation

// TODO: new NavHost transition

// TODO: title for Tales above tale grid

// TODO: class rename, they don't feel right. They are too spigot-esque. Composables are not like
//  spigot

// TODO: watermark for videos made. probably can be done through bunny

// TODO: good comments for everything

// TODO: pay attention to heart spam. if that animation triggers a lot it might be annoying

// TODO; report stack traces from crashes

// TODO: go through logcat and just look for issues we should watch out for

// TODO: better dark mode colors

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

// TODO: landscape mode remove overlapping camera ON tale view AND player view

// TODO: logo
// TODO; playful, animated, baldi's basics vibe

// TODO: ask nico about beta testing.
// TODO: button to reset app state for beta testers, e.g. reset recording timer or default tales

// TODO: subreddit
// TODO: idea of app is to encourage conversation

// TODO: improve record tale UI
// TODO: redo button shadows and length

// TODO: better accessibility content descriptions

// TODO: find a way to reduce minsdk. all people should be able to use the app :)

// TODO: more powerful api server for constant requests. specifically, sending heart count update
//  and video removed updates

// TODO: shake animation for ui. login and heart updates
// https://www.sinasamaki.com/shake-animations-compose/

// TODO: test out all new callables

// TODO: tale expiring animation. make it like fly away. also make times of tale 2 hours
//  apart with first tale having time of like 10 seconds
// TODO: disable haptics in settings
// TODO: introduction tutorial
// TODO: force ui check before pushing code
// TODO: fix dark mode in ui check. also look through dark mode needs to be better
// TODO: better landscape mode OR force portrait on phone
// TODO: create settings
// TODO: adjust in-out animation. learn how to use animation manager
// TODO: fix ui check FOR EVERYTHING
// TODO: unit testing -- espresso

// TODO: add bottom navbar again

// TODO: make code look better, clean it up! more functions!
// TODO: we should not get lost in the code!
// TODO: we have lots of mutable states and if statements. see if we can clean them up

// TODO: lots of launch effects. anyway to trim that up?

// TODO: if we have more functions, we can have better names for stuff which means more readable
//  code
// TODO: visit weatherspoon
// TODO: improve dropdown popup

// TODO: dark mode in settings

// TODO: maintain states for everything
// TODO: how do we ensure local states are synced with API and people don't cheat the system
// TODO: maybe only accept api calls for local app events if they are valid, i.e. times match up
//  from server-side by what client is asking

// TODO: clean up all variables

// TODO: ability to tap to focus with camera

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaleGrid(
    modifier: Modifier,
    tales: List<Tale>,
    removeTale: (Tale) -> Unit,
    contentPadding: PaddingValues,
    onClickTale: (Tale) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
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
            .windowInsetsPadding(WindowInsets.navigationBars)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
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
            items = tales
        ) {
            TaleCard(
                modifier = Modifier,
                tale = it,
                onClickTale = onClickTale,
                onRemoveTale = { tale ->
                    removeTale(tale)
                })
        }
    }
}

@Composable
private fun TaleCardBase(modifier: Modifier, content: @Composable (() -> Unit)) {
    Surface(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        shadowElevation = 3.dp,
        tonalElevation = 3.dp,
        shape = MaterialTheme.shapes.large,
    ) {
        content()
    }
}

@Composable
fun TaleCard(
    modifier: Modifier,
    tale: Tale,
    onClickTale: (Tale) -> Unit,
    onRemoveTale: (Tale) -> Unit,
) {
    var isContextMenuVisible by remember { mutableStateOf(false) }
    var contextMenuOffset by remember { mutableStateOf(Offset.Zero) }

    // TODO: better var names
    var cardWidth by remember { mutableIntStateOf(1) }
    var cardHeight by remember { mutableIntStateOf(1) }

    // TODO: better variable names
    // TODO: mutable state callables
    var explodingHeartsStartOffset by remember { mutableStateOf(Offset.Zero) }
    var isBubbling by remember { mutableStateOf(false) }
    var isExploding by remember { mutableStateOf(false) }
    val shakeTransition = updateTransition(isBubbling)

    // TODO: setting to disable vibrate shake
    // TODO: setting to disable visual shake
    // TODO: setting to disable live heart explosion updates

    var shakingLeft = true
    val shake = 1.5f
    
    val explodeTransition = updateTransition(isExploding)
    val cardShake by shakeTransition.animateOffset(
        transitionSpec = {
            repeatable(
                iterations = 15,
                animation = tween(durationMillis = 20),
                repeatMode = RepeatMode.Reverse
            )
        }
    ) { state ->
        when (state) {
            true -> {
                shakingLeft = !shakingLeft
                if (shakingLeft) Offset(-shake, -shake) else Offset(shake, shake)
            }

            false -> Offset(0f, 0f)
        }
    }

    TaleCardBase(
        modifier = modifier
            .onGloballyPositioned {
                cardWidth = max(abs(it.size.width), 1)
                cardHeight = max(abs(it.size.height), 1)
            }
            .offset(x = cardShake.x.dp, y = cardShake.y.dp)
    ) {
        Box {
            YouDoUDropDown(
                dropDownItems = DROP_DOWN_ITEMS,
                isContextMenuVisible = isContextMenuVisible,
                setContextMenuVisible = { isContextMenuVisible = it },
                contextMenuOffset = contextMenuOffset,
                tale = tale,
            )
        }

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
                val secs = integerResource(tale.secondsUntilExpiration).toLong()
                var expirationSeconds by remember { mutableLongStateOf(secs.toLong()) }
                val expirationSecondsWarningThreshold = integerResource(
                    R.integer
                        .expiration_seconds_warning_threshold
                )

                // TODO: change when expiration data isn't hardcoded. api connection
                LaunchedEffect(Unit) {
                    while (expirationSeconds > 0) {
                        delay(1000L)
                        expirationSeconds -= 1
                    }

                    onRemoveTale(tale)
                    cancel()
                }

                TaleImage(
                    tale = tale,
                    expirationSeconds = expirationSeconds,
                    expirationSecondsWarningThreshold = expirationSecondsWarningThreshold,
                    onClickTale = onClickTale,
                    makeContextMenuVisible = { isContextMenuVisible = true },
                    setContextMenuOffset = { contextMenuOffset = it },
                ) 

                TaleTime(
                    modifier = Modifier
                        .align(Alignment.BottomStart),
                    behindShape = behindShape,
                    behindColor = behindColor,
                    behindPadding = behindPadding,
                    cornerPadding = cornerPadding,
                    textStyle = textStyle,
                    expirationSeconds = expirationSeconds,
                    expirationSecondsWarningThreshold = expirationSecondsWarningThreshold,
                )

                TaleHeartCount(
                    modifier = Modifier
                        .align(Alignment.BottomEnd),
                    tale = tale,
                    behindShape = behindShape,
                    behindColor = behindColor,
                    behindPadding = behindPadding,
                    cornerPadding = cornerPadding,
                    textStyle = textStyle,
                    explodeTransition = explodeTransition,
                    startBubbling = { isBubbling = true }
                )

                ExplodingHearts(
                    explodingHeartsStartOffset = explodingHeartsStartOffset,
                    isBubbling = isBubbling,
                    isExploding = isExploding,
                    stopBubbling = { isBubbling = false },
                    startExploding = { isExploding = true },
                    stopExploding = { isExploding = false },
                    cardWidth,
                    cardHeight,
                    explodeTransition = explodeTransition
                )
            }

            Text(
                text = stringResource(tale.time),
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

// TODO: test bubbling on different display sizes
@Composable
fun ExplodingHearts(
    explodingHeartsStartOffset: Offset,
    isBubbling: Boolean,
    isExploding: Boolean,
    stopBubbling: () -> Unit,
    startExploding: () -> Unit,
    stopExploding: () -> Unit,
    cardWidth: Int,
    cardHeight: Int,
    explodeTransition: Transition<Boolean>,
) {
    val heartShownDuration = integerResource(R.integer.heart_shown_duration)
    val heartBeforeExplosionDuration =
        integerResource(R.integer.heart_duration_before_explosion)
    val heartRepeatAmount = integerResource(R.integer.heart_repeat_amount)
    var heartTargets by remember {
        mutableStateOf(
            List(heartRepeatAmount) {
                explodingHeartsStartOffset
            }
        )
    }

    LaunchedEffect(isBubbling) {
        if (isBubbling) {
            delay(heartBeforeExplosionDuration.toLong())

            heartTargets = heartTargets.makeHeartTargets(cardWidth, cardHeight)
            startExploding()
            stopBubbling()
        }
    }

    LaunchedEffect(isExploding) {
        if (isExploding) {
            delay(heartShownDuration.toLong())

            stopExploding()
        }
    }

    // TODO: make sure we can spam the heart button
    val painter = rememberVectorPainter(Icons.Filled.Favorite)
    val stiffness = 10f
    val offsets = heartTargets.map {
        explodeTransition.animateOffset(
            transitionSpec = {
                spring(
                    stiffness = stiffness,
                )
            }
        ) { state ->
            when (state) {
                true -> Offset(it.x, it.y)
                false -> explodingHeartsStartOffset
            }
        }
    }

    val opacity by explodeTransition.animateFloat(
        transitionSpec = { spring(stiffness = stiffness) }
    ) { state ->
        when (state) {
            true -> 0f
            false -> 1f
        }
    }

    val scale by explodeTransition.animateFloat(
        transitionSpec = { spring(stiffness = stiffness) }
    ) { state ->
        when (state) {
            true -> 2.5f
            false -> 1.0f
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
                            painter.intrinsicSize * scale,
                            colorFilter = ColorFilter.tint(
                                HeartRed.copy(
                                    alpha = if (isExploding) opacity else 0f
                                )
                            ),
                        )
                    }
                }
            }
        }
    }
}

// TODO: constants for these randoms?
// TODO: make explode look better
private fun List<Offset>.makeHeartTargets(
    width: Int, height: Int
) = map {
    val xRandom = Random.nextInt(0, width).toFloat()
    val yRandom = Random.nextInt(0, height).toFloat()
    Offset(
        xRandom, yRandom
    )
}

@Composable
fun YouDoUDropDown(
    dropDownItems: List<DropDownItem>,
    isContextMenuVisible: Boolean,
    setContextMenuVisible: (Boolean) -> Unit,
    contextMenuOffset: Offset,
    tale: Tale
) {
    val density = LocalDensity.current
    val dpOffset = with(density) {
        DpOffset(
            contextMenuOffset.x.toDp(), contextMenuOffset.y.toDp()
        )
    }

    DropdownMenu(
        expanded = isContextMenuVisible,
        onDismissRequest = {
            setContextMenuVisible(false)
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
                setContextMenuVisible(false)
            }, text = {
                Text(
                    text = it.text.replace(
                        TALE_DURATION_SPECIFIER,
                        integerResource(tale.duration).toLong()
                            .formatTimeSeconds(appendZero = false)
                    ),
                    color = if (it.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                )
            })
        }
    }
}

@Composable
fun TaleImage(
    tale: Tale,
    expirationSeconds: Long,
    expirationSecondsWarningThreshold: Int,
    onClickTale: (Tale) -> Unit,
    makeContextMenuVisible: () -> Unit,
    setContextMenuOffset: (Offset) -> Unit,
) {
    val heightFactor: Float = (expirationSeconds / (60f * 60f))

    // TODO: split into it's own composable
    // gray expiration bar and thumbnail
    Image(
        painter = painterResource(tale.thumbnail),
        contentDescription = stringResource(tale.contentDescription),
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .drawWithContent {
                drawContent()
                // TODO: replace me with a simple boolean to pass to the composable
                if (expirationSeconds < expirationSecondsWarningThreshold) {
                    drawRect(
                        color = Color(0, 0, 0, 125), size = Size(
                            size.width - (size.width * heightFactor), size.height
                        )
                    )
                }
            }
            .combinedClickable(true, onClick = {
                onClickTale(tale)
            }, onLongClick = {
                makeContextMenuVisible()
            })
            .pointerInteropFilter {
                setContextMenuOffset(Offset(it.x, it.y))
                false
            }
    )
}

@Composable
fun TaleTime(
    modifier: Modifier,
    behindShape: Shape,
    behindColor: Color,
    behindPadding: Dp,
    cornerPadding: Dp,
    textStyle: TextStyle,
    expirationSeconds: Long,
    expirationSecondsWarningThreshold: Int,
) {
    val expirationFontWeight =
        if (expirationSeconds < expirationSecondsWarningThreshold) FontWeight.Bold else FontWeight.Normal
    val expirationColor =
        if (expirationSeconds < expirationSecondsWarningThreshold) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .padding(cornerPadding)
    ) {
        Text(
            fontWeight = expirationFontWeight,
            text = expirationSeconds.formatTimeSeconds(),
            style = textStyle,
            color = expirationColor,
            modifier = modifier
                .clip(behindShape)
                .background(behindColor)
                .padding(behindPadding)
        )
    }
}

@Composable
fun TaleHeartCount(
    modifier: Modifier,
    tale: Tale,
    behindShape: Shape,
    behindColor: Color,
    behindPadding: Dp,
    cornerPadding: Dp,
    textStyle: TextStyle,
    explodeTransition: Transition<Boolean>,
    startBubbling: () -> Unit,
) {
    val heartsVal = integerResource(tale.hearts)
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

    Box(
        modifier = modifier
            .clip(behindShape)
            .clickable {
                if (!explodeTransition.isRunning) {
                    // TODO: mutable state callable maybe?
                    hearts++

                    startBubbling()

                    // TODO: open heart composable right here, the current behavior
                    //  in this if statement should have through that composable instead

                    // TODO: ensure that when that heart composable is open. no heart
                    //  animations can be send to prioritize that user get a heart
                    //  animation sent

                    // TODO: show other live heart updates somehow. we can use a heart
                    //  animation callback

                    vibrate(vibrator)
                }
            }) {

        // TODO: animate width increase
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(cornerPadding)
                .clip(behindShape)
                .background(behindColor)
                .padding(behindPadding)
                .animateContentSize(),
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
                    tint = HeartRed
                }
            }

            Icon(
                imageVector = icon,
                contentDescription = if (hearts == 0) {
                    stringResource(
                        R.string.no_hearts_content_description
                    )
                } else {
                    String.format(
                        stringResource(
                            R.string.yes_hearts_content_description
                        ), hearts
                    )
                },
                tint = tint, modifier = Modifier,
            )

            // TODO: we gotta test this
            if (hearts > 0) {
                hearts.toString()
                    .mapIndexed { index, c -> Digit(c, hearts, index) }
                    .forEachIndexed { i, digit ->
                        AnimatedContent(
                            targetState = digit,
                            transitionSpec = {
                                // TODO: how to make this be just greater than sign
                                if (targetState > initialState) {
                                    slideInVertically { -it } togetherWith slideOutVertically { it }
                                } else {
                                    slideInVertically { it } togetherWith slideOutVertically { -it }
                                }
                            }
                        ) { digit ->
                            val last = hearts.toString().length - 1
                            Text(
                                text = "${digit.digitChar}",
                                style = textStyle,
                                // TODO: ensure this width is consistent on different
                                //  displays
                                // TODO: ui check

                                modifier = Modifier
                                    .width(
                                        if (i == last) 11.dp
                                        else 9.dp
                                    )
                            )
                        }
                    }
            }
        }
    }
}

@Preview
@Composable
fun PreviewTaleCardSoon() {
    var tale = previewTales[1]

    TaleCard(
        modifier = Modifier,
        tale = tale,
        onClickTale = { },
        onRemoveTale = { },
    )
}

@Preview
@Composable
fun PreviewTaleCardMedium() {
    var tale = previewTales[2]

    TaleCard(
        modifier = Modifier,
        tale = tale,
        onClickTale = { },
        onRemoveTale = { },
    )
}

@Preview
@Composable
fun PreviewTaleCardFar() {
    var tale = previewTales[4]

    TaleCard(
        modifier = Modifier,
        tale = tale,
        onClickTale = { },
        onRemoveTale = { },
    )
}

// TODO: no suppressions!
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("VisualLintBounds", "VisualLintAccessibilityTestFramework")
@Preview
@Composable
fun PreviewTaleGrid() {
    TaleGrid(
        modifier = Modifier,
        tales = previewTales.toMutableList(),
        contentPadding = PaddingValues(4.dp),
        onClickTale = { },
        removeTale = { },
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    )
}