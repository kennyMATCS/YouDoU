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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import cx.glean.R
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.seconds

@Serializable
data class Glimpse(
    var author: Int,
    var duration: Int,
    var thumbnail: Int,
    var contentDescription: Int,
    val time: Int,
    val video: Int,
    val secondsUntilExpiration: Int
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
    ),
    Glimpse(
        author = R.string.preview_2_name,
        duration = R.integer.preview_2_duration,
        thumbnail = R.drawable.preview_2,
        contentDescription = R.string.preview_2_content_description,
        time = R.string.preview_2_date,
        video = R.raw.preview_2,
        secondsUntilExpiration = R.integer.preview_2_seconds_until_expiration,
    ),
    Glimpse(
        author = R.string.preview_3_name,
        duration = R.integer.preview_3_duration,
        thumbnail = R.drawable.preview_3,
        contentDescription = R.string.preview_3_content_description,
        time = R.string.preview_3_date,
        video = R.raw.preview_3,
        secondsUntilExpiration = R.integer.preview_3_seconds_until_expiration,
    ),
    Glimpse(
        author = R.string.preview_4_name,
        duration = R.integer.preview_4_duration,
        thumbnail = R.drawable.preview_4,
        contentDescription = R.string.preview_4_content_description,
        time = R.string.preview_4_date,
        video = R.raw.preview_4,
        secondsUntilExpiration = R.integer.preview_4_seconds_until_expiration,
    ),
    Glimpse(
        author = R.string.preview_5_name,
        duration = R.integer.preview_5_duration,
        thumbnail = R.drawable.preview_5,
        contentDescription = R.string.preview_5_content_description,
        time = R.string.preview_5_date,
        video = R.raw.preview_5,
        secondsUntilExpiration = R.integer.preview_5_seconds_until_expiration,
    ),
    Glimpse(
        author = R.string.preview_6_name,
        duration = R.integer.preview_6_duration,
        thumbnail = R.drawable.preview_6,
        contentDescription = R.string.preview_6_content_description,
        time = R.string.preview_6_date,
        video = R.raw.preview_6,
        secondsUntilExpiration = R.integer.preview_6_seconds_until_expiration,
    ),
    Glimpse(
        author = R.string.preview_7_name,
        duration = R.integer.preview_7_duration,
        thumbnail = R.drawable.preview_7,
        contentDescription = R.string.preview_7_content_description,
        time = R.string.preview_7_date,
        video = R.raw.preview_7,
        secondsUntilExpiration = R.integer.preview_7_seconds_until_expiration,
    ),
    Glimpse(
        author = R.string.preview_8_name,
        duration = R.integer.preview_8_duration,
        thumbnail = R.drawable.preview_8,
        contentDescription = R.string.preview_8_content_description,
        time = R.string.preview_8_date,
        video = R.raw.preview_8,
        secondsUntilExpiration = R.integer.preview_8_seconds_until_expiration,
    ),
    Glimpse(
        author = R.string.preview_9_name,
        duration = R.integer.preview_9_duration,
        thumbnail = R.drawable.preview_9,
        contentDescription = R.string.preview_9_content_description,
        time = R.string.preview_9_date,
        video = R.raw.preview_9,
        secondsUntilExpiration = R.integer.preview_9_seconds_until_expiration,
    ),
    Glimpse(
        author = R.string.preview_10_name,
        duration = R.integer.preview_10_duration,
        thumbnail = R.drawable.preview_10,
        contentDescription = R.string.preview_10_content_description,
        time = R.string.preview_10_date,
        video = R.raw.preview_10,
        secondsUntilExpiration = R.integer.preview_10_seconds_until_expiration,
    ),
    Glimpse(
        author = R.string.preview_11_name,
        duration = R.integer.preview_11_duration,
        thumbnail = R.drawable.preview_11,
        contentDescription = R.string.preview_11_content_description,
        time = R.string.preview_11_date,
        video = R.raw.preview_11,
        secondsUntilExpiration = R.integer.preview_11_seconds_until_expiration,
    ),
    Glimpse(
        author = R.string.preview_12_name,
        duration = R.integer.preview_12_duration,
        thumbnail = R.drawable.preview_12,
        contentDescription = R.string.preview_12_content_description,
        time = R.string.preview_12_date,
        video = R.raw.preview_12,
        secondsUntilExpiration = R.integer.preview_12_seconds_until_expiration,
    )
)

@Composable
fun GlimpseGrid(
    modifier: Modifier, glimpses: List<Glimpse>, contentPadding: PaddingValues, onClickGlimpse:
        (Glimpse) -> Unit
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
            items = glimpses,
        ) {
            GlimpseCard(
                modifier = Modifier,
                glimpse = it,
                onClickGlimpse = onClickGlimpse
            )
        }
    }
}

@Composable
fun GlimpseCard(modifier: Modifier, glimpse: Glimpse, onClickGlimpse: (Glimpse) -> Unit) {
    Surface(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(true) {
                onClickGlimpse(glimpse)
            },
        tonalElevation = 5.dp
    ) {
        Column (
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize()
        ) {
            Box {
                val heightFactor: Float = (integerResource(glimpse
                    .secondsUntilExpiration) / (60f * 24f))

                Image(
                    painter = painterResource(glimpse.thumbnail),
                    contentDescription = stringResource(glimpse.contentDescription),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                color = Color(0, 0, 0, 100),
                                size = Size(size.width * heightFactor, size.height)
                            )
                        }
                )

                Text(
                    text = integerResource(glimpse.duration).seconds.toComponents { hours, minutes, seconds ->
                        "$hours:$minutes"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                )
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
    return with (context.resources) {
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
        onClickGlimpse = { }
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