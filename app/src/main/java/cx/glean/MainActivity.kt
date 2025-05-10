package cx.glean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cx.glean.ui.theme.GleanTheme

// TODO: adaptive layouts by screen size
// TODO: reorganizing into different files
// TODO: learn how much I should nest composables in main project
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GleanTheme {
                GlimpseScaffold(Modifier, listOf())
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
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
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
            .clip(shape = MaterialTheme.shapes.small)
            // TODO: fix clickable delay
            .clickable(true) {

            }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(glimpse.thumbnail),
                contentDescription = stringResource(glimpse.contentDescription)
            )

            Text(
                text = glimpse.author,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
            )
        }
    }
}

@Composable
fun GlimpseScaffold(modifier: Modifier, glimpses: List<Glimpse>) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        bottomBar = {
            GleanBottomBar(modifier)
        }
    ) { innerPadding ->
        GlimpseGrid(
            glimpses = glimpses,
            modifier = Modifier
                .consumeWindowInsets(innerPadding)
                .padding(7.dp),
            contentPadding = innerPadding
        )
    }
}

@Composable
fun GleanBottomBar(modifier: Modifier) {
    var selectedItem by remember { mutableIntStateOf(1) }

    var items = listOf("Create", "View", "Info", "Settings")
    
    NavigationBar(
        modifier = modifier,
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    when (index) {
                        0 -> Icon(Icons.Filled.AddCircle, contentDescription = null)
                        1 -> Icon(Icons.Filled.Star, contentDescription = null)
                        2 -> Icon(Icons.Filled.Info, contentDescription = null)
                        3 -> Icon(Icons.Filled.Settings, contentDescription = null)
                    }
                },
                // TODO: ???
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
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
    Glimpse("Hoyt", 419, R.drawable.preview_11, R.string.preview_11_content_description)
)