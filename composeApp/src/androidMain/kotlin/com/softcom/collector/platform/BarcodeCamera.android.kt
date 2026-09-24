package com.softcom.collector.platform

import android.Manifest
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import androidx.camera.core.Preview as CameraPreview

@Composable
actual fun rememberCameraPermissionState(): CameraPermissionState {
    val context = LocalContext.current
    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        granted = isGranted
    }

    return remember {
        object : CameraPermissionState {
            override val granted: Boolean
                get() = granted

            override fun requestPermission() {
                launcher.launch(Manifest.permission.CAMERA)
            }
        }
    }.also {
        @Suppress("UNUSED_EXPRESSION")
        granted
    }
}

@Composable
actual fun BarcodeCameraPreview(
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val localLifecycleOwner = LocalLifecycleOwner.current
    val lifecycleOwner: LifecycleOwner = remember(context, localLifecycleOwner) {
        context.findActivity() ?: localLifecycleOwner
    }
    val onDetectedUpdated by rememberUpdatedState(onBarcodeDetected)
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val locked = remember { AtomicBoolean(false) }

    val barcodeScanner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build(),
        )
    }

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    DisposableEffect(lifecycleOwner, previewView) {
        val mainExecutor = ContextCompat.getMainExecutor(context)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        var cameraProvider: ProcessCameraProvider? = null

        fun processImageProxy(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage == null || locked.get()) {
                imageProxy.close()
                return
            }

            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees,
            )

            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    if (locked.get()) return@addOnSuccessListener
                    val value = barcodes
                        .asSequence()
                        .mapNotNull { it.rawValue?.trim() }
                        .firstOrNull { it.isNotEmpty() }
                        .orEmpty()
                    if (value.isNotEmpty() && locked.compareAndSet(false, true)) {
                        mainExecutor.execute {
                            onDetectedUpdated(value)
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }

        val bindCamera = Runnable {
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider

                val preview = CameraPreview.Builder()
                    .build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .build()
                    .also { useCase ->
                        useCase.setAnalyzer(analysisExecutor) { imageProxy ->
                            processImageProxy(imageProxy)
                        }
                    }

                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis,
                )
            } catch (_: Exception) {
                // Emuladores sem câmera ou dispositivos sem câmera traseira.
            }
        }

        // Garante que o PreviewView já está no layout antes do bind.
        previewView.post {
            cameraProviderFuture.addListener(bindCamera, mainExecutor)
        }

        onDispose {
            cameraProvider?.unbindAll()
            barcodeScanner.close()
            analysisExecutor.shutdown()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier,
    )
}

private tailrec fun android.content.Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}
