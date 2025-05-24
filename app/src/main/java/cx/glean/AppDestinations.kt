package cx.glean

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

// TODO: better content description
enum class AppDestinations(
    val pageNumber: Int
) {
    RECORD(0),
    VIEW(1),
}