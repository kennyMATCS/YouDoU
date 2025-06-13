package net.youdou.ui.glimpse.record

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import net.youdou.MainActivity
import net.youdou.R
import net.youdou.ui.glimpse.formatTimeSeconds
import net.youdou.ui.glimpse.getUri
import net.youdou.ui.glimpse.player.GlimpseRecordPlayer
import java.io.File
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val delayBeforeShowingEditor = 1000L

@OptIn(ExperimentalCamera2Interop::class)
@Composable
fun GlimpseCamera(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    canUseCamera: MutableState<Boolean>,
    canUseCameraAudio: MutableState<Boolean>,
    secondsUntilCanRecordAgain: MutableState<Long>,
    isRecording: MutableState<Boolean>,
    atEnd: MutableState<Boolean>,
    uri: MutableState<Uri?>,
    activity: MainActivity?,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var recording: MutableState<Recording?> = remember { mutableStateOf(null) }
    val previewView = PreviewView(context)
    var lensFacing = remember { mutableIntStateOf(LENS_FACING_FRONT) }
    val cameraSelector = remember {
        mutableStateOf(
            CameraSelector.Builder()
                .requireLensFacing(lensFacing.intValue)
                .build()
        )
    }

    val videoCapture: MutableState<VideoCapture<Recorder>?> = remember { mutableStateOf(null) }
    val cameraProvider: MutableState<ProcessCameraProvider?> = remember { mutableStateOf(null) }
    val preview: MutableState<Preview?> = remember { mutableStateOf(null) }

    // TODO: separate function for me below?
    activity?.let {
        var requestList = mutableListOf<String>()

        checkPermission(
            context = context,
            state = canUseCamera,
            activity = activity,
            requestList = requestList,
            permission = Manifest.permission.CAMERA,
        )

        checkPermission(
            context = context,
            state = canUseCameraAudio,
            activity = activity,
            requestList = requestList,
            permission = Manifest.permission.RECORD_AUDIO,
        )

        activity.requestPermissionLauncher.launch(requestList.toTypedArray())
    }

    var openConfirmDialog = remember { mutableStateOf(false) }
    var openTimeoutDialog = remember { mutableStateOf(false) }

    when {
        openTimeoutDialog.value -> {
            AlertDialog(
                icon = {
                    Icon(
                        Icons.Default.Warning, contentDescription = stringResource(
                            R.string
                                .recording_timeout_content_description
                        )
                    )
                },
                title = {
                    Text(stringResource(R.string.recording_timeout_notify_title))
                },
                text = {
                    Text(stringResource(R.string.recording_timeout_notify_message))
                },
                onDismissRequest = { },
                confirmButton = { },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openTimeoutDialog.value = false
                        }
                    ) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }

    when {
        openConfirmDialog.value -> {
            ConfirmDialog(onConfirm = {
                startRecording(
                    isRecording, atEnd,
                    videoCapture = videoCapture,
                    recording = recording,
                    context = context,
                    uri = uri
                )
            }, openConfirmDialog)
        }
    }


    val isDelayBeforeShowingEditor = remember { mutableStateOf(false) }

    // TODO: make this if statement cleaner and more readable
    if (uri.value != null && !isDelayBeforeShowingEditor.value) {
        Box(
            modifier = modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            GlimpseRecordPlayer(
                uri = uri.value!!,
            )
            Button(
                onClick = {
                    // TODO: upload logic
                    uri.value = null
                },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                modifier = Modifier
                    .padding(6.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Text("Upload")
            }
        }
    } else {
        if (canUseCamera.value && canUseCameraAudio.value) {
            LaunchedEffect(Unit) {
                videoCapture.value = context.createVideoCaptureUseCase(
                    lifecycleOwner = lifecycleOwner,
                    cameraSelector = cameraSelector.value,
                    previewView = previewView,
                    cameraProvider = cameraProvider,
                    preview = preview
                )
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
                var recordingLength = remember { mutableLongStateOf(0) }

                LaunchedEffect(isRecording.value) {
                    while (isRecording.value) {
                        delay(1000L)

                        recordingLength.longValue += 1L
                    }
                }

                // TODO: add constants for shadows throughout app

                val cornerPadding = 12.dp
                AnimatedVisibility(visible = isRecording.value) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .alpha(if (isRecording.value) 1f else 0f)
                            .padding(cornerPadding),
                        shadowElevation = 5.dp,
                        tonalElevation = 5.dp
                    ) {
                        Text(
                            text = recordingLength.longValue.formatTimeSeconds(appendZero = false),
                            modifier = Modifier
                                .padding(8.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(cornerPadding)
                ) {
                    IconButton(
                        onClick = {
                            flipCamera(
                                lensFacing = lensFacing,
                                rotated = rotated,
                                cameraSelector = cameraSelector,
                                recording = recording,
                                context = context,
                                lifecycleOwner = lifecycleOwner,
                                videoCapture = videoCapture,
                                preview = preview,
                                cameraProvider = cameraProvider
                            )
                        },
                        modifier = Modifier
                            .size(55.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Image(
                            painter = rememberAnimatedVectorPainter(recordImage, rotated.value),
                            contentDescription = stringResource(R.string.flip_camera_content_description),
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }

                LaunchedEffect(isDelayBeforeShowingEditor.value) {
                    delay(1000L) // hardcoded for a second
                    isDelayBeforeShowingEditor.value = false
                }

                val interactionSource = remember { MutableInteractionSource() }
                val glimpseRecordDelay = integerResource(R.integer.glimpse_record_delay).toLong()

                IconButton(
                    onClick = {
                        if (!isRecording.value) {
                            if (secondsUntilCanRecordAgain.value > 0) {
                                openTimeoutDialog.value = true
                            } else {
                                openConfirmDialog.value = true
                            }
                        } else {
                            stopRecording(isRecording, recording, atEnd)
                            isDelayBeforeShowingEditor.value = true
                            secondsUntilCanRecordAgain.value = glimpseRecordDelay
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

            // TODO: make this better???
        } else {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 5.dp,
                    tonalElevation = 5.dp
                ) {
                    // TODO: make the permissions text more specific. see what other apps do
                    Text(
                        text = stringResource(R.string.camera_and_audio_required),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    onConfirm: () -> Unit,
    openConfirmDialog: MutableState<Boolean>,
) {
    AlertDialog(
        icon = {
            Icon(
                Icons.Default.Warning, contentDescription = stringResource(
                    R.string.recording_timeout_content_description
                )
            )
        },
        title = {
            Text(stringResource(R.string.recording_timeout_confirm_title))
        },
        text = {
            Text(stringResource(R.string.recording_timeout_confirm_message))
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
        canUseCamera = remember { mutableStateOf(true) },
        canUseCameraAudio = remember { mutableStateOf(true) },
        secondsUntilCanRecordAgain = remember { mutableLongStateOf(0) },
        isRecording = remember { mutableStateOf(true) },
        atEnd = remember { mutableStateOf(false) },
        uri = remember { mutableStateOf(null) },
        activity = null,
    )
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewGlimpseCameraNoPermissions() {
    GlimpseCamera(
        canUseCamera = remember { mutableStateOf(false) },
        canUseCameraAudio = remember { mutableStateOf(false) },
        secondsUntilCanRecordAgain = remember { mutableLongStateOf(0) },
        isRecording = remember { mutableStateOf(false) },
        atEnd = remember { mutableStateOf(false) },
        uri = remember { mutableStateOf(null) },
        activity = null,
    )
}

// TODO: fix this little guy!
//  we could add a parent composable that doesn't have the exoplayer in it, but the content of the
//  composable in the primary code is an actual exoplayer
@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewGlimpseRecording() {
    var context = LocalContext.current

    GlimpseCamera(
        secondsUntilCanRecordAgain = remember { mutableLongStateOf(60 * 60 * 24) },
        isRecording = remember { mutableStateOf(false) },
        atEnd = remember { mutableStateOf(false) },
        activity = null,
        canUseCamera = remember { mutableStateOf(true) },
        canUseCameraAudio = remember { mutableStateOf(true) },
        uri = remember { mutableStateOf(R.raw.preview_5.getUri(context)) }
    )
}

private fun startRecording(
    isRecording: MutableState<Boolean>,
    atEnd: MutableState<Boolean>,
    videoCapture: MutableState<VideoCapture<Recorder>?>,
    recording: MutableState<Recording?>,
    uri: MutableState<Uri?>,
    context: Context
) {
    toggleRecording(isRecording, atEnd)

    videoCapture.value.let {
        val mediaDir = context.externalCacheDirs.firstOrNull()?.let {
            File(it, context.getString(R.string.app_name)).apply { mkdirs() }
        }

        recording.value = startRecordingVideo(
            context = context,
            filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
            videoCapture = videoCapture,
            outputDirectory = if (mediaDir != null && mediaDir.exists()) mediaDir else context
                .filesDir,
            executor = context.mainExecutor,
            audioEnabled = true,
        ) { event ->
            if (event is VideoRecordEvent.Finalize) {
                uri.value = event.outputResults.outputUri
            }
        }
    }
}

private fun stopRecording(
    isRecording: MutableState<Boolean>,
    recording: MutableState<Recording?>,
    atEnd: MutableState<Boolean>,
) {
    toggleRecording(isRecording, atEnd)
    recording.value?.stop()
}

private fun toggleRecording(
    recording: MutableState<Boolean>,
    atEnd: MutableState<Boolean>
) {
    recording.value = !recording.value
    atEnd.value = !atEnd.value
}

private fun flipCamera(
    lensFacing: MutableIntState,
    rotated: MutableState<Boolean>,
    cameraSelector: MutableState<CameraSelector>,
    recording: MutableState<Recording?>,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    videoCapture: MutableState<VideoCapture<Recorder>?>,
    preview: MutableState<Preview?>,
    cameraProvider: MutableState<ProcessCameraProvider?>
) {
    if (lensFacing.intValue == LENS_FACING_FRONT) {
        lensFacing.intValue = LENS_FACING_BACK
    } else {
        lensFacing.intValue = LENS_FACING_FRONT
    }
    rotated.value = !rotated.value

    cameraSelector.value = CameraSelector.Builder()
        .requireLensFacing(lensFacing.intValue)
        .build()

    recording.let {
        context.bindCamera(
            lifecycleOwner, cameraSelector.value, preview, videoCapture.value,
            cameraProvider
        )
    }
}

private fun checkPermission(
    context: Context, state: MutableState<Boolean>,
    activity:
    MainActivity,
    requestList: MutableList<String>, permission: String,
) {
    if (ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        state.value = true
    } else {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                activity, permission
            )
        ) {
            requestList.add(permission)
        }
    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener(
            {
                continuation.resume(future.get())
            },
            mainExecutor
        )
    }
}

suspend fun Context.createVideoCaptureUseCase(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView,
    preview: MutableState<Preview?>,
    cameraProvider: MutableState<ProcessCameraProvider?>
): VideoCapture<Recorder> {
    preview.value = Preview.Builder().build().apply {
        surfaceProvider = previewView.surfaceProvider
    }

    val qualitySelector = QualitySelector.from(
        Quality.FHD,
        FallbackStrategy.lowerQualityOrHigherThan(Quality.FHD)
    )

    val recorder = Recorder.Builder()
        .setExecutor(mainExecutor)
        .setQualitySelector(qualitySelector)
        .build()

    val videoCapture = VideoCapture.withOutput(recorder)

    cameraProvider.value = getCameraProvider()
    bindCamera(lifecycleOwner, cameraSelector, preview, videoCapture, cameraProvider)

    return videoCapture
}

fun Context.bindCamera(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    preview: MutableState<Preview?>,
    videoCapture: VideoCapture<Recorder>?,
    cameraProvider: MutableState<ProcessCameraProvider?>
) {
    cameraProvider.value?.unbindAll()
    cameraProvider.value?.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview.value,
        videoCapture
    )
}

@SuppressLint("MissingPermission")
fun startRecordingVideo(
    context: Context,
    filenameFormat: String,
    videoCapture: MutableState<VideoCapture<Recorder>?>,
    outputDirectory: File,
    executor: Executor,
    audioEnabled: Boolean,
    consumer: Consumer<VideoRecordEvent>
): Recording? {
    val videoFile = File(
        outputDirectory,
        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".mp4"
    )

    val outputOptions = FileOutputOptions.Builder(videoFile).build()

    val output = videoCapture.value?.output
        ?.prepareRecording(context, outputOptions)
        ?.asPersistentRecording()
        ?.apply { if (audioEnabled) withAudioEnabled() }
        ?.start(executor, consumer)

    return output
}