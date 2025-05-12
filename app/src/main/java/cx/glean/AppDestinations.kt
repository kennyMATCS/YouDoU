package cx.glean

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

// TODO: better content description
enum class AppDestinations(
    @StringRes val label: Int,
    val icon: ImageVector,
    @StringRes val contentDescription: Int
) {
    RECORD(R.string.destination_record, Icons.Filled.AddCircle, contentDescription = R.string.destination_record),
    VIEW(R.string.destination_view, Icons.Filled.Star, contentDescription = R.string.destination_view),
    INFO(R.string.destination_info, Icons.Filled.Info, contentDescription = R.string.destination_info),
    SETTINGS(R.string.destination_settings, Icons.Filled.Settings, contentDescription = R.string.destination_settings),
}