package net.youdou.ui.screens.tale

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
import androidx.camera.core.CameraSelector.LENS_FACING_UNKNOWN
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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import net.youdou.MainActivity
import net.youdou.R
import net.youdou.ui.screens.tale.player.TalePlayer
import net.youdou.util.formatTimeSeconds
import java.io.File
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// TODO: use this in resource manager
const val delayBeforeShowingEditor = 1000L

// TODO: review parameters to see if we really need them. specifically, modifier and contentPadding
// TODO: remove suppressions. split this function up
@ExperimentalMaterial3Api
@OptIn(ExperimentalCamera2Interop::class, ExperimentalMaterial3Api::class)
@Composable
fun TaleCamera(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    lensFacing: Int, // TODO: annotation for lensFacing
    setLensFacing: (Int) -> Unit,
    canUseCamera: Boolean,
    canUseCameraAudio: Boolean,
    setCanUseCamera: (Boolean) -> Unit,
    setCanUseCameraAudio: (Boolean) -> Unit,
    recordingTimeout: Long,
    isRecording: Boolean,
    currentRecordedVideoUri: Uri?,
    setCurrentRecordedVideoUri: (Uri?) -> Unit,
    toggleRecording: () -> Unit,
    activity: MainActivity?,
    setRecordingTimeout: (Long) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var recording by remember { mutableStateOf<Recording?>(null) }
    var cameraSelector by remember {
        mutableStateOf(
            CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()
        )
    }

    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var preview by remember { mutableStateOf<Preview?>(null) }
    val previewView = PreviewView(context)

    var isConfirmDialogOpen by remember { mutableStateOf(false) }
    var isTimeoutDialogOpen by remember { mutableStateOf(false) }
    var isDelayBeforeShowingEditor by remember { mutableStateOf(false) }

    // TODO: separate function for me below?
    activity?.let {
        var requestList = mutableListOf<String>()

        // TODO: check permission somewhere else
        checkPermission(
            context = context,
            setState = setCanUseCamera,
            activity = activity,
            requestList = requestList,
            permission = Manifest.permission.CAMERA,
        )

        checkPermission(
            context = context,
            setState = setCanUseCameraAudio,
            activity = activity,
            requestList = requestList,
            permission = Manifest.permission.RECORD_AUDIO,
        )

        activity.requestPermissionLauncher.launch(requestList.toTypedArray())
    }

    // TODO: make the bottom two when's neater
    when {
        isTimeoutDialogOpen -> {
            // TODO: make this its own composable function
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
                            isTimeoutDialogOpen = false
                        }
                    ) {
                        Text("Dismiss")
                    }
                }
            )
        }

        isConfirmDialogOpen -> {
            // TODO: make this its own composable function
            ConfirmDialog(onConfirm = {
                startRecording(
                    setRecording = { recording = it },
                    videoCapture = videoCapture,
                    context = context,
                    setCurrentRecordedVideoUri = setCurrentRecordedVideoUri,
                    toggleRecording = toggleRecording,
                )
            }, { isConfirmDialogOpen = false })
        }

    }

    // TODO: make this if statement cleaner and more readable
    if (currentRecordedVideoUri != null && !isDelayBeforeShowingEditor) {
        Box(
            modifier = modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            // TODO: can we move this somewhere else
            val exoPlayer = ExoPlayer.Builder(LocalContext.current)
                .setHandleAudioBecomingNoisy(true)
                .build()

            TalePlayer(
                uri = currentRecordedVideoUri, // TODO: what are you doing bro
                exoPlayer,
                R.layout.tale_record_player_view,
                onVideoEndOrClose = { }
            )

            // TODO: add back. make look actually good
//            Button(
//                onClick = {
//                    // TODO: upload logic. use callback
//                    // TODO: set uri to be null
//                },
//                shape = MaterialTheme.shapes.medium,
//                colors = ButtonDefaults.buttonColors(
//                    contentColor = MaterialTheme.colorScheme.primary,
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                ),
//                modifier = Modifier
//                    .padding(6.dp)
//                    .align(Alignment.BottomEnd)
//            ) {
//                Text(stringResource(R.string.tale_upload_text))
//            }
        }
    } else {
        if (canUseCamera && canUseCameraAudio) {
            LaunchedEffect(Unit) {
                videoCapture = context.createVideoCaptureUseCase(
                    lifecycleOwner = lifecycleOwner,
                    cameraSelector = cameraSelector,
                    previewView = previewView,
                    cameraProvider = cameraProvider,
                    setCameraProvider = { cameraProvider = it },
                    preview = preview,
                    setPreview = { preview = it }
                )
            }

            Box(
                modifier = modifier
                    .padding(contentPadding)
                    .fillMaxSize()
            ) {
                AndroidView(
                    factory = { previewView },
                    modifier = modifier.fillMaxSize()
                )

                val recordImage =
                    AnimatedImageVector.animatedVectorResource(R.drawable.anim_flip_camera)
                var recordingLength by remember { mutableLongStateOf(0) }

                LaunchedEffect(isRecording) {
                    while (isRecording) {
                        delay(1000L) // TODO: constant for seconds?

                        recordingLength += 1L
                    }
                }

                // TODO: add constants for shadows throughout app
                val cornerPadding = 12.dp
                AnimatedVisibility(
                    visible = isRecording,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .padding(cornerPadding),
                        shadowElevation = 5.dp,
                        tonalElevation = 5.dp
                    ) {
                        Text(
                            text = recordingLength.formatTimeSeconds(appendZero = false),
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
                                setLensFacing = setLensFacing,
                                setCameraSelector = { cameraSelector = it },
                                recording = recording,
                                context = context,
                                lifecycleOwner = lifecycleOwner,
                                videoCapture = videoCapture,
                                preview = preview,
                                cameraProvider = cameraProvider
                            )
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        // TODO: add shadow to this and record button.
                        //  well, i actually don't know. maybe it is better without?
                        Image(
                            painter = rememberAnimatedVectorPainter(
                                animatedImageVector = recordImage,
                                atEnd = lensFacing == LENS_FACING_BACK
                            ),
                            contentDescription = stringResource(R.string.flip_camera_content_description),
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }

                // TODO: consolidate launch effect. or organize them into code somewhere
                LaunchedEffect(isDelayBeforeShowingEditor) {
                    delay(delayBeforeShowingEditor) // hardcoded for a second
                    isDelayBeforeShowingEditor = false
                }

                val interactionSource = remember { MutableInteractionSource() }
                val taleRecordDelay = integerResource(R.integer.tale_record_delay).toLong()

                // TODO: break this stuff up into separate composables
                // TODO: show dynamic timeout in actual dialog
                IconButton(
                    onClick = {
                        when (isRecording) {
                            false -> if (recordingTimeout > 0) {
                                isTimeoutDialogOpen = true
                            } else {
                                isConfirmDialogOpen = true
                            }

                            true -> {
                                stopRecording(
                                    toggleRecording = toggleRecording,
                                    recording = recording
                                )

                                isDelayBeforeShowingEditor = true
                                setRecordingTimeout(taleRecordDelay)
                            }
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
                    // TODO: atEnd turn into camera orientation enum
                    Image(
                        painter = rememberAnimatedVectorPainter(
                            animatedImageVector = recordImage,
                            atEnd = isRecording
                        ),
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
                    .fillMaxSize()
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
    closeDialog: () -> Unit
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
                    closeDialog()
                    onConfirm()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    closeDialog()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

// TODO: no more suppressions for previews!
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewTaleCamera() {
    TaleCamera(
        canUseCamera = true,
        canUseCameraAudio = true,
        setCanUseCamera = { },
        setCanUseCameraAudio = { },
        setRecordingTimeout = { },
        recordingTimeout = 0L,
        isRecording = false,
        toggleRecording = { },
        currentRecordedVideoUri = null,
        setCurrentRecordedVideoUri = { },
        lensFacing = LENS_FACING_FRONT,
        setLensFacing = { },
        activity = null,
    )
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewTaleCameraNoPermissions() {
    TaleCamera(
        canUseCamera = false,
        canUseCameraAudio = false,
        setCanUseCamera = { },
        setCanUseCameraAudio = { },
        setRecordingTimeout = { },
        recordingTimeout = 0L,
        isRecording = false,
        toggleRecording = { },
        currentRecordedVideoUri = null,
        setCurrentRecordedVideoUri = { },
        lensFacing = LENS_FACING_FRONT,
        setLensFacing = { },
        activity = null,
    )
}

// TODO: fix this little guy!
//  we could add a parent composable that doesn't have the exoplayer in it, but the content of the
//  composable in the primary code is an actual exoplayer

// TODO: no more suppressions for previews!
@kotlin.OptIn(ExperimentalMaterial3Api::class)
// TODO: don't use full class name
@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewTaleRecording() {
    TaleCamera(
        canUseCamera = true,
        canUseCameraAudio = true,
        setCanUseCamera = { },
        setCanUseCameraAudio = { },
        setRecordingTimeout = { },
        recordingTimeout = 0L,
        isRecording = false,
        toggleRecording = { },
        currentRecordedVideoUri = null,
        setCurrentRecordedVideoUri = { },
        lensFacing = LENS_FACING_FRONT,
        setLensFacing = { },
        activity = null,
    )
}

// TODO: remove all these nulls
private fun startRecording(
    toggleRecording: () -> Unit,
    videoCapture: VideoCapture<Recorder>?,
    setRecording: (Recording?) -> Unit,
    setCurrentRecordedVideoUri: (Uri?) -> Unit,
    context: Context
) {
    toggleRecording()

    videoCapture.let {
        // TODO: constant mediaDir in helper class maybe
        // TODO: resource manager for mediaDir. maybe in a setting in app?
        val mediaDir = context.externalCacheDirs.firstOrNull()?.let {
            File(it, context.getString(R.string.app_name)).apply { mkdirs() }
        }

        setRecording(
            startRecordingVideo(
                context = context,
                // TODO: resource manager for filenameFormat
                filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                videoCapture = videoCapture,
                outputDirectory = if (mediaDir != null && mediaDir.exists()) mediaDir else context
                    .filesDir,
                executor = context.mainExecutor,
                audioEnabled = true,
            ) { event ->
                if (event is VideoRecordEvent.Finalize) {
                    setCurrentRecordedVideoUri(event.outputResults.outputUri)
                }
            })
    }
}

private fun stopRecording(
    toggleRecording: () -> Unit,
    recording: Recording?,
) {
    toggleRecording()
    recording?.stop()
}

// TODO: no more nulls
private fun flipCamera(
    lensFacing: Int,
    setLensFacing: (Int) -> Unit,
    setCameraSelector: (CameraSelector) -> Unit,
    recording: Recording?,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    videoCapture: VideoCapture<Recorder>?,
    preview: Preview?,
    cameraProvider: ProcessCameraProvider?
) {
    val tempLensFacing = when (lensFacing) {
        LENS_FACING_FRONT -> LENS_FACING_BACK
        LENS_FACING_BACK -> LENS_FACING_FRONT
        else -> LENS_FACING_UNKNOWN
    }

    val tempCameraSelector = CameraSelector.Builder()
        .requireLensFacing(tempLensFacing)
        .build()

    setLensFacing(tempLensFacing)

    recording.let {
        context.bindCamera(
            lifecycleOwner, tempCameraSelector, preview, videoCapture,
            cameraProvider
        )
    }

    setCameraSelector(tempCameraSelector)
}

// TODO: move into better file
private fun checkPermission(
    context: Context, setState: (Boolean) -> Unit,
    activity:
    MainActivity,
    requestList: MutableList<String>, permission: String,
) {
    if (ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        setState(true)
    } else {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                activity, permission
            )
        ) {
            requestList.add(permission)
        }
    }
}

// TODO: move into better file
// TODO: finish up making callbacks for everything. no mutable states through parameters!
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

// TODO: can we forget all the nulls
suspend fun Context.createVideoCaptureUseCase(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView,
    preview: Preview?,
    setPreview: (Preview) -> Unit,
    cameraProvider: ProcessCameraProvider?,
    setCameraProvider: (ProcessCameraProvider) -> Unit
): VideoCapture<Recorder> {
    val tempPreview = Preview.Builder().build().apply {
        surfaceProvider = previewView.surfaceProvider
    }
    setPreview(tempPreview)

    val qualitySelector = QualitySelector.from(
        Quality.FHD, // TODO: hardcoded qualities? maybe make variable?
        FallbackStrategy.lowerQualityOrHigherThan(Quality.FHD)
    )

    val recorder = Recorder.Builder()
        .setExecutor(mainExecutor)
        .setQualitySelector(qualitySelector)
        .build()

    val videoCapture = VideoCapture.withOutput(recorder)

    val tempCameraProvider = getCameraProvider()
    setCameraProvider(tempCameraProvider)

    bindCamera(lifecycleOwner, cameraSelector, tempPreview, videoCapture, tempCameraProvider)

    return videoCapture
}

// TODO: can we forget all the nulls
fun Context.bindCamera(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    preview: Preview?,
    videoCapture: VideoCapture<Recorder>?,
    cameraProvider: ProcessCameraProvider?,
) {
    cameraProvider?.unbindAll()
    cameraProvider?.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        videoCapture
    )
}

// TODO: don't suppress this permission
@SuppressLint("MissingPermission")
fun startRecordingVideo(
    context: Context,
    filenameFormat: String,
    videoCapture: VideoCapture<Recorder>?,
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

    val output = videoCapture?.output
        ?.prepareRecording(context, outputOptions)
        ?.asPersistentRecording()
        ?.apply { if (audioEnabled) withAudioEnabled() }
        ?.start(executor, consumer)

    return output
}