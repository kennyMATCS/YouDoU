package cx.glean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import cx.glean.ui.theme.GleanTheme
import cx.glean.ui.glimpse.GlimpseScaffold

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