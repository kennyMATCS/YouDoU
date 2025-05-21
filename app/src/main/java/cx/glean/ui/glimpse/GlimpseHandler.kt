package cx.glean.ui.glimpse

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import cx.glean.R
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
        thumbnail = R.drawable.preview_7,
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
    modifier: Modifier, glimpses: List<Glimpse>, contentPadding: PaddingValues, onClickGlimpse:
        (Glimpse) -> Unit
) {
    var mutableGlimpses = glimpses

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

    LazyVerticalGrid(
        columns = when (detailPaneBreakpoint) {
            DetailPaneBreakpoint.COMPACT -> GridCells.Adaptive(125.dp)
            DetailPaneBreakpoint.MEDIUM -> GridCells.Fixed(4)
            DetailPaneBreakpoint.EXPANDED -> GridCells.Fixed(6)
        },
        modifier = modifier
            .padding(8.dp),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(
            items = mutableGlimpses,
        ) {
            GlimpseCard(
                modifier = Modifier,
                glimpse = it,
                onClickGlimpse = onClickGlimpse,
                onRemoveGlimpse = { glimpse ->
                    // TODO: actual working logic, when things are hardcoded fix
                }
            )
        }
    }
}

@Composable
fun GlimpseCard(modifier: Modifier, glimpse: Glimpse, onClickGlimpse: (Glimpse) -> Unit, onRemoveGlimpse: (Glimpse) -> Unit) {
    Surface(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(true) {
                onClickGlimpse(glimpse)
            },
        tonalElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize()
        ) {
            Box {
                val secs = integerResource(glimpse.secondsUntilExpiration).toLong()
                var expirationSeconds by remember { mutableLongStateOf(secs.toLong()) }

                val expirationColor = when {
                    expirationSeconds > (60 * 60 * 8) -> Color(51, 105, 30, 255)
                    expirationSeconds > (60 * 60 * 1) -> Color(255, 143, 0, 255)
                    else -> Color(229, 57, 53, 255)
                }

                val cornerPadding = 6.dp

                val textStyle = MaterialTheme.typography.labelSmall
                val behindShape = MaterialTheme.shapes.small
                val behindPadding = 3.dp
                val behindColor = MaterialTheme.colorScheme.surfaceContainer

                Image(
                    painter = painterResource(glimpse.thumbnail),
                    contentDescription = stringResource(glimpse.contentDescription),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
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
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .padding(cornerPadding)
                ) {
                    Text(
                        text = integerResource(glimpse.duration).seconds.toComponents { hours, minutes, seconds ->
                            String.format(Locale.US, "%d:%02d", hours, minutes)
                        },
                        style = textStyle,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = modifier
                            .clip(behindShape)
                            .background(behindColor)
                            .padding(behindPadding)
                    )

                    Text(
                        text = expirationSeconds.seconds.toComponents { hours, minutes, seconds, nanoseconds ->
                            StringBuilder().apply {
                                if (hours > 0) {
                                    append(
                                        String.format(
                                            Locale.US,
                                            "%2d:", hours
                                        )
                                    )
                                }

                                append(
                                    String.format(
                                        Locale.US,
                                        "%02d:", minutes
                                    )
                                )

                                append(
                                    String.format(
                                        Locale.US,
                                        "%02d", seconds
                                    )
                                )
                            }.toString()
                        },
                        style = textStyle,
                        color = expirationColor,
                        modifier = modifier
                            .clip(behindShape)
                            .background(behindColor)
                            .padding(behindPadding)
                    )
                }

                val hearts = integerResource(glimpse.hearts)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(cornerPadding)
                        .alpha(if (hearts == 0) 0f else 1f)
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = stringResource(R.string.heart_content_description),
                        tint = Color(0xFFEA3323),
                        modifier = Modifier
                            .clip(behindShape)
                            .background(behindColor)
                            .padding(behindPadding)

                    )

                    // TODO: heart text. make ui better

//                    Text(
//                        text = hearts.toString(),
//                        modifier = Modifier
//                            .align(Alignment.BottomEnd)
//                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
            ) {

                Text(
                    text = stringResource(glimpse.time),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

enum class DetailPaneBreakpoint {
    COMPACT,
    MEDIUM,
    EXPANDED
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
    var glimpse = previewGlimpses[6]

    GlimpseCard(
        modifier = Modifier,
        glimpse = glimpse,
        onClickGlimpse = { },
        onRemoveGlimpse = { }
    )
}

@Preview
@Composable
fun PreviewGlimpseGrid() {
    GlimpseGrid(
        modifier = Modifier,
        glimpses = previewGlimpses,
        contentPadding = PaddingValues(0.dp),
        onClickGlimpse = { }
    )
}