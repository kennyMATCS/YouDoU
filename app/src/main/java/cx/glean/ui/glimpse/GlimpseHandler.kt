package cx.glean.ui.glimpse

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import cx.glean.R
import cx.glean.ui.theme.DarkExpiringFar
import cx.glean.ui.theme.DarkExpiringMedium
import cx.glean.ui.theme.DarkExpiringSoon
import cx.glean.ui.theme.ExpiringFar
import cx.glean.ui.theme.ExpiringMedium
import cx.glean.ui.theme.ExpiringSoon
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

@Serializable
data class Glimpse(
    val author: Int,
    val duration: Int,
    val thumbnail: Int,
    val contentDescription: Int,
    val time: Int,
    val video: Int,
    val secondsUntilExpiration: Int,
    val hearts: Int
)

data class DropDownItem(
    val text: String,
    val color: Color,
    val onItemClick: () -> Unit
)

// TODO: add functionality
val dropDownItems = listOf(
    DropDownItem("Report", color = Color.Red, onItemClick = { }),
)
var previewGlimpses = listOf(
    Glimpse(
        author = R.string.preview_1_name,
        duration = R.integer.preview_1_duration,
        thumbnail = R.drawable.preview_1,
        contentDescription = R.string.preview_1_content_description,
        time = R.string.preview_1_date,
        video = R.raw.preview_1,
        secondsUntilExpiration = R.integer.preview_1_seconds_until_expiration,
        hearts = R.integer.preview_1_hearts,
    ),
    Glimpse(
        author = R.string.preview_2_name,
        duration = R.integer.preview_2_duration,
        thumbnail = R.drawable.preview_2,
        contentDescription = R.string.preview_2_content_description,
        time = R.string.preview_2_date,
        video = R.raw.preview_2,
        secondsUntilExpiration = R.integer.preview_2_seconds_until_expiration,
        hearts = R.integer.preview_2_hearts,
    ),
    Glimpse(
        author = R.string.preview_3_name,
        duration = R.integer.preview_3_duration,
        thumbnail = R.drawable.preview_3,
        contentDescription = R.string.preview_3_content_description,
        time = R.string.preview_3_date,
        video = R.raw.preview_3,
        secondsUntilExpiration = R.integer.preview_3_seconds_until_expiration,
        hearts = R.integer.preview_3_hearts,
    ),
    Glimpse(
        author = R.string.preview_4_name,
        duration = R.integer.preview_4_duration,
        thumbnail = R.drawable.preview_4,
        contentDescription = R.string.preview_4_content_description,
        time = R.string.preview_4_date,
        video = R.raw.preview_4,
        secondsUntilExpiration = R.integer.preview_4_seconds_until_expiration,
        hearts = R.integer.preview_4_hearts,
    ),
    Glimpse(
        author = R.string.preview_5_name,
        duration = R.integer.preview_5_duration,
        thumbnail = R.drawable.preview_5,
        contentDescription = R.string.preview_5_content_description,
        time = R.string.preview_5_date,
        video = R.raw.preview_5,
        secondsUntilExpiration = R.integer.preview_5_seconds_until_expiration,
        hearts = R.integer.preview_5_hearts,
    ),
    Glimpse(
        author = R.string.preview_6_name,
        duration = R.integer.preview_6_duration,
        thumbnail = R.drawable.preview_6,
        contentDescription = R.string.preview_6_content_description,
        time = R.string.preview_6_date,
        video = R.raw.preview_6,
        secondsUntilExpiration = R.integer.preview_6_seconds_until_expiration,
        hearts = R.integer.preview_6_hearts,
    ),
    Glimpse(
        author = R.string.preview_7_name,
        duration = R.integer.preview_7_duration,
        thumbnail = R.drawable.preview_1,
        contentDescription = R.string.preview_7_content_description,
        time = R.string.preview_7_date,
        video = R.raw.preview_7,
        secondsUntilExpiration = R.integer.preview_7_seconds_until_expiration,
        hearts = R.integer.preview_7_hearts,
    ),
    Glimpse(
        author = R.string.preview_8_name,
        duration = R.integer.preview_8_duration,
        thumbnail = R.drawable.preview_8,
        contentDescription = R.string.preview_8_content_description,
        time = R.string.preview_8_date,
        video = R.raw.preview_8,
        secondsUntilExpiration = R.integer.preview_8_seconds_until_expiration,
        hearts = R.integer.preview_8_hearts,
    ),
    Glimpse(
        author = R.string.preview_9_name,
        duration = R.integer.preview_9_duration,
        thumbnail = R.drawable.preview_9,
        contentDescription = R.string.preview_9_content_description,
        time = R.string.preview_9_date,
        video = R.raw.preview_9,
        secondsUntilExpiration = R.integer.preview_9_seconds_until_expiration,
        hearts = R.integer.preview_9_hearts,
    ),
    Glimpse(
        author = R.string.preview_10_name,
        duration = R.integer.preview_10_duration,
        thumbnail = R.drawable.preview_10,
        contentDescription = R.string.preview_10_content_description,
        time = R.string.preview_10_date,
        video = R.raw.preview_10,
        secondsUntilExpiration = R.integer.preview_10_seconds_until_expiration,
        hearts = R.integer.preview_10_hearts,
    ),
    Glimpse(
        author = R.string.preview_11_name,
        duration = R.integer.preview_11_duration,
        thumbnail = R.drawable.preview_11,
        contentDescription = R.string.preview_11_content_description,
        time = R.string.preview_11_date,
        video = R.raw.preview_11,
        secondsUntilExpiration = R.integer.preview_11_seconds_until_expiration,
        hearts = R.integer.preview_11_hearts,
    ),
    Glimpse(
        author = R.string.preview_12_name,
        duration = R.integer.preview_12_duration,
        thumbnail = R.drawable.preview_12,
        contentDescription = R.string.preview_12_content_description,
        time = R.string.preview_12_date,
        video = R.raw.preview_12,
        secondsUntilExpiration = R.integer.preview_12_seconds_until_expiration,
        hearts = R.integer.preview_12_hearts,
    )
)

@Composable
fun GlimpseGrid(
    modifier: Modifier, glimpses: MutableList<Glimpse>, contentPadding: PaddingValues,
    onClickGlimpse:
        (Glimpse) -> Unit
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val detailPaneBreakpoint: DetailPaneBreakpoint = if (windowSizeClass.isAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND,
            WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND
        )
    ) {
        DetailPaneBreakpoint.EXPANDED
    } else if (windowSizeClass.isAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
            WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND
        )
    ) {
        DetailPaneBreakpoint.MEDIUM
    } else {
        DetailPaneBreakpoint.COMPACT
    }

    val pad = 8.dp
    LazyVerticalGrid(
        columns = when (detailPaneBreakpoint) {
            DetailPaneBreakpoint.COMPACT -> GridCells.Adaptive(150.dp)
            DetailPaneBreakpoint.MEDIUM -> GridCells.Fixed(4)
            DetailPaneBreakpoint.EXPANDED -> GridCells.Fixed(6)
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

        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(
            items = glimpses,
        ) {
            GlimpseCard(
                modifier = Modifier,
                glimpse = it,
                onClickGlimpse = onClickGlimpse,
                onRemoveGlimpse = { glimpse ->
                    // glimpses.remove(glimpse)
                }
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

    Surface(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(5.dp)
            .combinedClickable(
                true,
                onClick = {
                    onClickGlimpse(glimpse)
                },
                onLongClick = {
                    isContextMenuVisible.value = true
                }
            )
            .pointerInteropFilter {
                contextMenuOffset.value = Offset(it.x, it.y)
                false
            },
        shadowElevation = 5.dp,
        tonalElevation = 5.dp,
        shape = MaterialTheme.shapes.large,
    ) {
        Box {
            GleanDropDown(dropDownItems, isContextMenuVisible, contextMenuOffset)
        }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp)
        ) {
            Box {
                val secs = integerResource(glimpse.secondsUntilExpiration).toLong()
                var expirationSeconds by remember { mutableLongStateOf(secs.toLong()) }

                val expirationColor =
                    if (!isSystemInDarkTheme()) {
                        when {
                            expirationSeconds > (60 * 60 * 8) -> ExpiringFar
                            expirationSeconds > (60 * 60 * 1) -> ExpiringMedium
                            else -> ExpiringSoon
                        }
                    } else {
                        when {
                            expirationSeconds > (60 * 60 * 8) -> DarkExpiringFar
                            expirationSeconds > (60 * 60 * 1) -> DarkExpiringMedium
                            else -> DarkExpiringSoon
                        }
                    }

                val cornerPadding = 5.dp

                val textStyle = MaterialTheme.typography.labelSmall
                val behindShape = MaterialTheme.shapes.extraLarge
                val behindPadding = 5.dp
                val behindColor = MaterialTheme.colorScheme.surface

                val heightFactor: Float = (expirationSeconds / (60f * 60f))

                Image(
                    painter = painterResource(glimpse.thumbnail),
                    contentDescription = stringResource(glimpse.contentDescription),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .drawWithContent {
                            drawContent()
                            if (expirationSeconds < 60 * 60) {
                                drawRect(
                                    color = Color(0, 0, 0, 125),
                                    size = Size(
                                        size.width - (size.width * heightFactor), size
                                            .height
                                    )
                                )
                            }
                        }
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier
                        .padding(cornerPadding)
                ) {
                    Text(
                        fontWeight = FontWeight.Bold,
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

                Box(
                    modifier = Modifier
                        .clickable {
                            hearts++
                        }
                        .align(Alignment.BottomEnd)
                ) {
                    val color: Brush =
                        if (hearts == 0) ShimmerAnimation(behindColor) else
                            Brush.linearGradient(
                                colors = listOf(behindColor, behindColor)
                            )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(cornerPadding)
                            .clip(behindShape)
                            .background(color)
                            .padding(behindPadding),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val icon: ImageVector
                        val tint: Color

                        when (hearts) {
                            0 -> {
                                icon = ImageVector.vectorResource(
                                    R.drawable
                                        .outline_favorite
                                )
                                tint = MaterialTheme.colorScheme.onSurface
                            }

                            else -> {
                                icon = Icons.Filled.Favorite
                                tint = Color(0xFFEA3323)
                            }
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = stringResource(
                                R.string
                                    .heart_content_description
                            ),
                            tint = tint
                        )

                        if (hearts > 0) {
                            Text(
                                text = hearts.toString(),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
            }

            Text(
                text = stringResource(glimpse.time),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp)
            )
        }
    }
}

@Composable
fun ShimmerAnimation(color: Color): Brush {
    val transition = rememberInfiniteTransition()

    val shimmer =
        listOf(
            color.copy(alpha = 1f),
            color.copy(alpha = 0.55f),
            color.copy(alpha = 1f)
        )


    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1300,
                easing = FastOutSlowInEasing,
                delayMillis = 2000
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmer,
        end = Offset(translateAnimation, translateAnimation)
    )

    return brush
}

@Composable
fun GleanDropDown(
    dropDownItems: List<DropDownItem>,
    isContextMenuVisible: MutableState<Boolean>,
    contextMenuOffset: MutableState<Offset>
) {
    val density = LocalDensity.current
    val dpOffset = with(density) {
        DpOffset(
            contextMenuOffset.value.x.toDp(), contextMenuOffset
                .value.y.toDp()
        )
    }

    DropdownMenu(
        expanded = isContextMenuVisible.value,
        onDismissRequest = {
            isContextMenuVisible.value = false
        },
        offset = dpOffset
    ) {
        dropDownItems.forEach {
            DropdownMenuItem(
                onClick = {
                    it.onItemClick
                    isContextMenuVisible.value = false
                },
                text = {
                    Text(
                        text = it.text,
                        color = it.color,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    }
}

enum class DetailPaneBreakpoint {
    COMPACT,
    MEDIUM,
    EXPANDED
}

private fun Long.formatTimeSeconds(): String {
    return seconds.toComponents { hours, minutes, seconds, nanoseconds ->
        StringBuilder().apply {
            if (hours > 1) {
                append(
                    String.format(
                        Locale.US,
                        "%2d hours", hours
                    )
                )
            } else if (hours == 1L) {
                append(
                    String.format(
                        Locale.US,
                        "%2d hour", hours
                    )
                )
            } else {
                append(
                    String.format(
                        Locale.US,
                        "%02d:%02d", minutes, seconds
                    )
                )
            }
        }.toString()
    }
}

fun Int.getUri(context: Context): Uri {
    val item = this
    return with(context.resources) {
        Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(getResourcePackageName(item))
            .appendPath(getResourceTypeName(item))
            .appendPath(getResourceEntryName(item))
            .build()
    }
}

@Preview
@Composable
fun PreviewGlimpseCard() {
    var glimpse = previewGlimpses[0]

    GlimpseCard(
        modifier = Modifier,
        glimpse = glimpse,
        onClickGlimpse = { },
        onRemoveGlimpse = { },
    )
}

@Preview
@Composable
fun PreviewGlimpseGrid() {
    GlimpseGrid(
        modifier = Modifier,
        glimpses = previewGlimpses.toMutableList(),
        contentPadding = PaddingValues(4.dp),
        onClickGlimpse = { }
    )
}