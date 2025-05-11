package cx.glean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import cx.glean.ui.theme.GleanTheme

// TODO: adaptive layouts by screen size
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