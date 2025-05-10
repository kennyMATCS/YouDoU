package cx.glean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cx.glean.ui.theme.GleanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GleanTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GlimpseGrid(
                        glimpses = listOf(),
                        modifier = Modifier
                            .consumeWindowInsets(innerPadding),
                        contentPadding = innerPadding
                    )
                }
            }
        }
    }
}

data class Glimpse(var author: String, var duration: Int, var thumbnail: Int, var contentDescription: Int)

// TODO: how do we pass a modifier to each Glimpse item?
@Composable
fun GlimpseGrid(glimpses: List<Glimpse>, modifier: Modifier, contentPadding: PaddingValues) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(glimpses) {
            GlimpseCard(
                glimpse = it,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun GlimpseCard(glimpse: Glimpse, modifier: Modifier) {
    Surface(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .border(0.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
    ) {
        Image(
            painter = painterResource(glimpse.thumbnail),
            contentDescription = stringResource(glimpse.contentDescription)
        )
    }
}

@Preview
@Composable
fun PreviewGlimpseCard() {
    var glimpse = Glimpse("Marisol", 387, R.drawable.preview_2, R.string.preview_2_content_description)
    GlimpseCard(
        glimpse = glimpse,
        modifier = Modifier
    )
}

@Preview
@Composable
fun PreviewGlimpseGrid() {
    var glimpses = listOf(
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
        Glimpse("Hoyt", 419, R.drawable.preview_11, R.string.preview_11_content_description)
    )

    GlimpseGrid(
        glimpses = glimpses,
        modifier = Modifier,
        contentPadding = PaddingValues(0.dp)
    )
}