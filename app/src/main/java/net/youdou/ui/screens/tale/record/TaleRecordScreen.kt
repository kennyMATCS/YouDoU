package net.youdou.ui.screens.tale.record

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
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
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import net.youdou.MainActivity
import net.youdou.R
import net.youdou.ui.screens.tale.player.TalePlayer
import net.youdou.util.formatTimeSeconds

const val delayBeforeShowingEditor = 1000L

// TODO: save video progress when leaving record screen
// TODO: improve video resolution

// TODO: remove suppressions
// TODO: make choices about if this file should be organized more
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

    // TODO: is this safe
    activity?.let {
        var requestList = mutableListOf<String>()

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

    when {
        isTimeoutDialogOpen -> {
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

    when {
        currentRecordedVideoUri != null && !isDelayBeforeShowingEditor -> {
            Box(
                modifier = modifier
                    .padding(contentPadding)
                    .fillMaxSize()
            ) {
                val exoPlayer = ExoPlayer.Builder(LocalContext.current)
                    .setHandleAudioBecomingNoisy(true)
                    .build()

                TalePlayer(
                    uri = currentRecordedVideoUri,
                    exoPlayer = exoPlayer,
                    layout = R.layout.tale_record_player_view,
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
        }

        else -> {
            when {
                canUseCamera && canUseCameraAudio -> {
                    var recordingLength by remember { mutableLongStateOf(0) }

                    LaunchedEffect(Unit) {
                        videoCapture = context.createVideoCaptureUseCase(
                            lifecycleOwner = lifecycleOwner,
                            cameraSelector = cameraSelector,
                            previewView = previewView,
                            setCameraProvider = { cameraProvider = it },
                            setPreview = { preview = it }
                        )
                    }

                    LaunchedEffect(isDelayBeforeShowingEditor) {
                        delay(delayBeforeShowingEditor) // hardcoded for a second
                        isDelayBeforeShowingEditor = false
                    }

                    LaunchedEffect(isRecording) {
                        while (isRecording) {
                            delay(1000L)

                            recordingLength += 1L
                        }
                    }

                    Box(
                        modifier = modifier
                            .padding(contentPadding)
                            .consumeWindowInsets(contentPadding)
                            .fillMaxSize()
                    ) {
                        AndroidView(
                            factory = { previewView },
                            modifier = modifier.fillMaxSize()
                        )

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
                                val recordImage =
                                    AnimatedImageVector.animatedVectorResource(R.drawable.anim_flip_camera)

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
                }

                else -> {
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

// TODO: no more Mutable passdown!
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

// TODO: no more suppressions for previews!
@kotlin.OptIn(ExperimentalMaterial3Api::class)
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
        isRecording = true,
        toggleRecording = { },
        currentRecordedVideoUri = null,
        setCurrentRecordedVideoUri = { },
        lensFacing = LENS_FACING_FRONT,
        setLensFacing = { },
        activity = null,
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