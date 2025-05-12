package cx.glean.ui.glimpse

import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import cx.glean.AppDestinations
import cx.glean.R
import kotlin.time.Duration.Companion.seconds

data class Glimpse(
    @StringRes var author: Int,
    @IntegerRes var duration: Int,
    @DrawableRes var thumbnail: Int,
    @StringRes var contentDescription: Int,
    val time: Int
)

var previewGlimpses = listOf(
    Glimpse(
        author = R.string.preview_1_name,
        duration = R.integer.preview_1_duration,
        thumbnail = R.drawable.preview_1,
        contentDescription = R.string.preview_1_content_description,
        time = R.string.preview_1_date
    ),
    Glimpse(
        author = R.string.preview_2_name,
        duration = R.integer.preview_2_duration,
        thumbnail = R.drawable.preview_2,
        contentDescription = R.string.preview_2_content_description,
        time = R.string.preview_2_date
    ),
    Glimpse(
        author = R.string.preview_3_name,
        duration = R.integer.preview_3_duration,
        thumbnail = R.drawable.preview_3,
        contentDescription = R.string.preview_3_content_description,
        time = R.string.preview_3_date
    ),
    Glimpse(
        author = R.string.preview_4_name,
        duration = R.integer.preview_4_duration,
        thumbnail = R.drawable.preview_4,
        contentDescription = R.string.preview_4_content_description,
        time = R.string.preview_4_date
    ),
    Glimpse(
        author = R.string.preview_5_name,
        duration = R.integer.preview_5_duration,
        thumbnail = R.drawable.preview_5,
        contentDescription = R.string.preview_5_content_description,
        time = R.string.preview_5_date
    ),
    Glimpse(
        author = R.string.preview_6_name,
        duration = R.integer.preview_6_duration,
        thumbnail = R.drawable.preview_6,
        contentDescription = R.string.preview_6_content_description,
        time = R.string.preview_6_date
    ),
    Glimpse(
        author = R.string.preview_7_name,
        duration = R.integer.preview_7_duration,
        thumbnail = R.drawable.preview_7,
        contentDescription = R.string.preview_7_content_description,
        time = R.string.preview_7_date
    ),
    Glimpse(
        author = R.string.preview_8_name,
        duration = R.integer.preview_8_duration,
        thumbnail = R.drawable.preview_8,
        contentDescription = R.string.preview_8_content_description,
        time = R.string.preview_8_date
    ),
    Glimpse(
        author = R.string.preview_9_name,
        duration = R.integer.preview_9_duration,
        thumbnail = R.drawable.preview_9,
        contentDescription = R.string.preview_9_content_description,
        time = R.string.preview_9_date
    ),
    Glimpse(
        author = R.string.preview_10_name,
        duration = R.integer.preview_10_duration,
        thumbnail = R.drawable.preview_10,
        contentDescription = R.string.preview_10_content_description,
        time = R.string.preview_10_date
    ),
    Glimpse(
        author = R.string.preview_11_name,
        duration = R.integer.preview_11_duration,
        thumbnail = R.drawable.preview_11,
        contentDescription = R.string.preview_11_content_description,
        time = R.string.preview_11_date
    ),
    Glimpse(
        author = R.string.preview_12_name,
        duration = R.integer.preview_12_duration,
        thumbnail = R.drawable.preview_12,
        contentDescription = R.string.preview_12_content_description,
        time = R.string.preview_12_date
    )
)

@Composable
fun GlimpseGrid(modifier: Modifier, glimpses: List<Glimpse>, contentPadding: PaddingValues, detailPaneBreakpoint: DetailPaneBreakpoint) {
    LazyVerticalGrid(
        columns = when (detailPaneBreakpoint) {
            DetailPaneBreakpoint.COMPACT -> GridCells.Adaptive(120.dp)
            DetailPaneBreakpoint.MEDIUM -> GridCells.Fixed(4)
            DetailPaneBreakpoint.EXPANDED -> GridCells.Fixed(6)
        },
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(
            items = glimpses,
        ) {
            GlimpseCard(
                glimpse = it,
                modifier = Modifier,
            )
        }
    }
}

@Composable
fun GlimpseCard(modifier: Modifier, glimpse: Glimpse) {
    Surface(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(true) { },
        tonalElevation = 5.dp
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Column (
                modifier = Modifier
                    .padding(4.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(glimpse.thumbnail),
                        contentDescription = stringResource(glimpse.contentDescription),
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
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
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

enum class DetailPaneBreakpoint {
    COMPACT,
    MEDIUM,
    EXPANDED
}

@Composable
fun GlimpseScaffold(modifier: Modifier, glimpses: List<Glimpse>) {
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

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.VIEW) }

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
fun PreviewGlimpseCard() {
    var glimpse = previewGlimpses[6]
    GlimpseCard(
        glimpse = glimpse,
        modifier = Modifier
    )
}

@Preview
@Composable
fun PreviewGlimpseGrid() {
    GlimpseGrid(
        glimpses = previewGlimpses,
        modifier = Modifier,
        contentPadding = PaddingValues(0.dp),
        detailPaneBreakpoint = DetailPaneBreakpoint.COMPACT
    )
}

@Preview
@Composable
fun PreviewScaffold() {
    GlimpseScaffold(Modifier, previewGlimpses)
}