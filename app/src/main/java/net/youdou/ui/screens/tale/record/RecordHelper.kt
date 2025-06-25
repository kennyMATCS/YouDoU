package net.youdou.ui.screens.tale.record

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
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
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleOwner
import net.youdou.R
import java.io.File
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// TODO: ensure recording works okay!
internal suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
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
internal suspend fun Context.createVideoCaptureUseCase(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView,
    setPreview: (Preview) -> Unit,
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
internal fun Context.bindCamera(
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

// TODO: no more nulls
internal fun flipCamera(
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

// TODO: don't suppress this permission
@SuppressLint("MissingPermission")
internal fun startRecordingVideo(
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


// TODO: remove all these nulls
internal fun startRecording(
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

internal fun stopRecording(
    toggleRecording: () -> Unit,
    recording: Recording?,
) {
    toggleRecording()
    recording?.stop()
}