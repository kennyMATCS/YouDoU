package cx.glean.ui.glimpse.record

import android.content.Context
import cx.glean.R
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun GlimpseCamera(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    canUseCamera: Boolean = true,
    canUseCameraAudio: Boolean = true
) {
    var lensFacing = remember { mutableIntStateOf(LENS_FACING_FRONT) }
    var recording = remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val preview = Preview.Builder().build()
    val previewView = remember {
        PreviewView(context)
    }.apply {
        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
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

            IconButton(
                onClick = {
                    flipCamera(lensFacing)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(70.dp)
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.flip_camera),
                    contentDescription = stringResource(R.string.flip_camera_content_description),
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            val interactionSource = remember { MutableInteractionSource() }
            IconButton(
                onClick = {
                    record(recording)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(120.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { },
            ) {
                Image(
                    painter = if (!recording.value) painterResource(R.drawable.camera_button) else painterResource(
                        R.drawable.camera_recording
                    ),
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

private fun record(recording: MutableState<Boolean>) {
    if (recording.value) {

    } else {

    }
    recording.value = !recording.value
}

private fun flipCamera(lensFacing: MutableIntState) {
    if (lensFacing.intValue == LENS_FACING_FRONT) {
        lensFacing.intValue = LENS_FACING_BACK
    } else {
        lensFacing.intValue = LENS_FACING_FRONT
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }