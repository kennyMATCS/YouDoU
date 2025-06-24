package net.youdou.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RawRes
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

// TODO: do we even need this
// TODO: maybe move it. it could go in TaleGridScreen
enum class HeartState {
    BEGIN, END
}

// TODO: maybe move it. it could go in TaleGridScreen
enum class DetailPaneBreakpoint {
    COMPACT, MEDIUM, EXPANDED
}

fun @receiver:RawRes Int.getUri(context: Context): Uri {
    val item = this

    return with(context.resources) {
        Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(getResourcePackageName(item)).appendPath(getResourceTypeName(item))
            .appendPath(getResourceEntryName(item)).build()
    }
}

fun Long.formatTimeSeconds(appendZero: Boolean = true): String {
    return seconds.toComponents { hours, minutes, seconds, nanoseconds ->
        StringBuilder().apply {
            when {
                hours > 1L -> append(
                    String.format(
                        Locale.US, "%d hours", hours
                    )
                )

                hours == 1L -> append(
                    String.format(
                        Locale.US, "%d hour", hours
                    )
                )

                else -> {
                    val specifier = if (appendZero) "%02d:%02d" else "%d:%02d"
                    append(
                        String.format(
                            Locale.US, specifier, minutes, seconds
                        )
                    )
                }
            }
        }.toString()
    }
}

data class Digit(val digitChar: Char, val fullNumber: Int, val place: Int) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Digit -> digitChar == other.digitChar
            else -> super.equals(other)
        }
    }

    override fun hashCode(): Int {
        var result = digitChar.hashCode()
        result = 31 * result + fullNumber
        result = 31 * result + place
        return result
    }
}

operator fun Digit.compareTo(other: Digit): Int {
    return fullNumber.compareTo(other.fullNumber)
}

fun vibrate(vibrator: Vibrator) {
    // must have equal amount in each array
    // TODO: unit test to ensure array have equal length?

    val intensity = 3
    val spread = 1

    // TODO: find way to ensure same size, maybe we could just make a pairing data class
    val timings: LongArray = longArrayOf(
        35, 35, 35, 60, 35, 35, 35, 35, 60, 35, 35, 35, 60, 35, 35, 35, 35, 35, 35, 35, 35, 35
    ).map {
        it * spread
    }.toLongArray()

    val amplitudes: IntArray = intArrayOf(
        10, 15, 10, 15, 10, 15, 10, 15, 10, 15, 10, 15, 26, 35, 42, 41, 38, 35, 30, 25, 19, 12,
    ).map {
        it * intensity
    }.toIntArray()

    val repeatIndex = -1 // Do not repeat.

    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))
}

fun Color.gradient(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            copy(alpha = 1f),
            copy(alpha = .8f),
            copy(alpha = .7f),
            copy(alpha = .5f),
            copy(alpha = .3f),
        ),
        startY = 0f,
    )
}