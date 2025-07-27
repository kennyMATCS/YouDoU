package net.youdou.util

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import net.youdou.R

enum class AppDestinations(
    @StringRes val label: Int,
    val icon: ImageVector,
    @StringRes val contentDescription: Int,
    val pageNumber: Int,
) {
    RECORD(
        label = R.string.destination_record,
        icon = Icons.Filled.AddCircle,
        contentDescription = R.string.destination_record,
        pageNumber = 0
    ),
    VIEW(
        label = R.string.destination_view,
        icon = Icons.Filled.Star,
        contentDescription = R.string.destination_view,
        pageNumber = 1
    ),
}