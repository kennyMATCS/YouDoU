package cx.glean.ui.glimpse.record

import android.content.Context
import cx.glean.R
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// TODO: fix asking for permissions
// TODO: share button for top bar
// TODO: glimpse record timeout for top bar

@Composable
fun GlimpseCamera(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    canUseCamera: Boolean,
    canUseCameraAudio: Boolean,
    secondsUntilCanRecordAgain: MutableState<Long>,
    recording: MutableState<Boolean>,
    atEnd: MutableState<Boolean>,
) {
    var lensFacing = remember { mutableIntStateOf(LENS_FACING_FRONT) }


    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val preview = Preview.Builder().build()
    val previewView = remember {
        PreviewView(context)
    }.apply {
        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
    }

    var openConfirmDialog = remember { mutableStateOf(false) }

    when {
        openConfirmDialog.value -> {
            ConfirmDialog(onConfirm = {
                record(recording, atEnd)
                secondsUntilCanRecordAgain.value = 60 * 60 * 24
            }, openConfirmDialog)
        }
    }

    if (canUseCamera && canUseCameraAudio) {
        LaunchedEffect(lensFacing.intValue) {
            val cameraXSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing.intValue)
                .build()

            val cameraProvider = context.getCameraProvider()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraXSelector, preview)
            preview.surfaceProvider = previewView.surfaceProvider
        }

        Box(
            modifier = modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            AndroidView(factory = { previewView }, modifier = modifier.fillMaxSize())

            val recordImage =
                AnimatedImageVector.animatedVectorResource(R.drawable.anim_flip_camera)
            val rotated = remember { mutableStateOf(false) }

            IconButton(
                onClick = {
                    flipCamera(lensFacing, rotated)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(70.dp)
                    .padding(16.dp)
            ) {
                Image(
                    painter = rememberAnimatedVectorPainter(recordImage, rotated.value),
                    contentDescription = stringResource(R.string.flip_camera_content_description),
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            val interactionSource = remember { MutableInteractionSource() }
            IconButton(
                onClick = {
                    if (!recording.value) {
                        openConfirmDialog.value = true
                    } else {
                        record(recording, atEnd)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(120.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { },
            ) {
                val recordImage = AnimatedImageVector.animatedVectorResource(
                    R.drawable
                        .anim_camera_to_record
                )
                Image(
                    painter = rememberAnimatedVectorPainter(recordImage, atEnd.value),
                    contentDescription = stringResource(R.string.camera_button_content_description),
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }

    } else {
        // TODO: implement
    }
}

@Composable
fun ConfirmDialog(
    onConfirm: () -> Unit,
    openConfirmDialog: MutableState<Boolean>,
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Warning, contentDescription = "Recording alert")
        },
        title = {
            Text(stringResource(R.string.recording_timeout_title))
        },
        text = {
            Text(stringResource(R.string.recording_timeout_message))
        },
        onDismissRequest = {

        },
        confirmButton = {
            TextButton(
                onClick = {
                    openConfirmDialog.value = false
                    onConfirm()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openConfirmDialog.value = false
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewGlimpseCamera() {
    GlimpseCamera(
        modifier = Modifier,
        contentPadding = PaddingValues(),
        canUseCamera = true,
        canUseCameraAudio = true,
        secondsUntilCanRecordAgain = remember { mutableLongStateOf(0) },
        recording = remember { mutableStateOf(false) },
        atEnd = remember { mutableStateOf(false) }
    )
}

private fun record(recording: MutableState<Boolean>, atEnd: MutableState<Boolean>) {
    if (recording.value) {

    } else {

    }
    recording.value = !recording.value
    atEnd.value = !atEnd.value
}

private fun flipCamera(lensFacing: MutableIntState, rotated: MutableState<Boolean>) {
    if (lensFacing.intValue == LENS_FACING_FRONT) {
        lensFacing.intValue = LENS_FACING_BACK
    } else {
        lensFacing.intValue = LENS_FACING_FRONT
    }
    rotated.value = !rotated.value
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }