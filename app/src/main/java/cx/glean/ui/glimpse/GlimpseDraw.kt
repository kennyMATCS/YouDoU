package cx.glean.ui.glimpse

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import cx.glean.R
import cx.glean.ui.theme.GleanTheme
import kotlin.time.Duration.Companion.seconds

data class Glimpse(var author: String, var duration: Int, var thumbnail: Int, var contentDescription: Int)

@Composable
fun GlimpseGrid(modifier: Modifier, glimpses: List<Glimpse>, contentPadding: PaddingValues) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        items(
            items = glimpses,
        ) {
            GlimpseCard(
                glimpse = it,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun GlimpseCard(modifier: Modifier, glimpse: Glimpse) {
    Surface(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.small)
            .clickable(true) { }
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            Image(
                painter = painterResource(glimpse.thumbnail),
                contentDescription = stringResource(glimpse.contentDescription)
            )

            Text(
                text = glimpse.duration.seconds.toComponents { hours, minutes, seconds ->
                    "$hours:$minutes"
                },
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                modifier = modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
            )
        }
    }
}

@Composable
fun GlimpseScaffold(modifier: Modifier, glimpses: List<Glimpse>) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val expandedListDetailPane = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
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
                        if (expandedListDetailPane) Text(stringResource(it.label)) else ""
                    },
                    selected = (it == currentDestination),
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        when (currentDestination) {
            AppDestinations.RECORD -> { }
            // TODO: where to consume inner padding?
            AppDestinations.VIEW -> { GlimpseGrid(modifier = modifier.padding(7.dp), glimpses = glimpses, contentPadding = PaddingValues(0.dp)) }
            AppDestinations.INFO -> { }
            AppDestinations.SETTINGS -> { }
        }
    }
}

// TODO: fix glimpse card preview. scale card size to screen size
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
        contentPadding = PaddingValues(0.dp)
    )
}

@Preview
@Composable
fun PreviewScaffold() {
    GleanTheme {
        GlimpseScaffold(Modifier, previewGlimpses)
    }
}

var previewGlimpses = listOf(
    Glimpse("Elena", 557, R.drawable.preview_1, R.string.preview_1_content_description),
    Glimpse("Marisol", 387, R.drawable.preview_2, R.string.preview_2_content_description),
    Glimpse("Kristy", 407, R.drawable.preview_3, R.string.preview_3_content_description),
    Glimpse("Virgie", 553, R.drawable.preview_4, R.string.preview_4_content_description),
    Glimpse("Buford", 416, R.drawable.preview_5, R.string.preview_5_content_description),
    Glimpse("Liz", 330, R.drawable.preview_6, R.string.preview_6_content_description),
    Glimpse("Ariel", 327, R.drawable.preview_7, R.string.preview_7_content_description),
    Glimpse("Noelle", 441, R.drawable.preview_8, R.string.preview_8_content_description),
    Glimpse("Marion", 440, R.drawable.preview_9, R.string.preview_9_content_description),
    Glimpse("Rosie", 328, R.drawable.preview_10, R.string.preview_10_content_description),
    Glimpse("Hoyt", 419, R.drawable.preview_11, R.string.preview_11_content_description),
    Glimpse("Jordan", 562, R.drawable.preview_12, R.string.preview_12_content_description)
)