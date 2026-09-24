package com.softcom.collector.platform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureSessionPresetHigh
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVMetadataObjectTypeCode128Code
import platform.AVFoundation.AVMetadataObjectTypeCode39Code
import platform.AVFoundation.AVMetadataObjectTypeCode93Code
import platform.AVFoundation.AVMetadataObjectTypeEAN13Code
import platform.AVFoundation.AVMetadataObjectTypeEAN8Code
import platform.AVFoundation.AVMetadataObjectTypeITF14Code
import platform.AVFoundation.AVMetadataObjectTypeUPCECode
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.AVFoundation.videoGravity
import platform.CoreGraphics.CGRectMake
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIColor
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_create

@Composable
actual fun rememberCameraPermissionState(): CameraPermissionState {
    var granted by remember {
        mutableStateOf(
            AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) ==
                AVAuthorizationStatusAuthorized,
        )
    }

    return remember(granted) {
        object : CameraPermissionState {
            override val granted: Boolean get() = granted
            override fun requestPermission() {
                val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
                when (status) {
                    AVAuthorizationStatusAuthorized -> granted = true
                    AVAuthorizationStatusNotDetermined -> {
                        AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { allowed ->
                            dispatch_async(dispatch_get_main_queue()) {
                                granted = allowed
                            }
                        }
                    }
                    else -> granted = false
                }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun BarcodeCameraPreview(
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier,
) {
    val session = remember { AVCaptureSession() }
    val delegate = remember {
        object : NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {
            override fun captureOutput(
                output: platform.AVFoundation.AVCaptureOutput,
                didOutputMetadataObjects: List<*>,
                fromConnection: platform.AVFoundation.AVCaptureConnection,
            ) {
                val first = didOutputMetadataObjects.firstOrNull()
                    as? platform.AVFoundation.AVMetadataMachineReadableCodeObject
                val value = first?.stringValue?.trim().orEmpty()
                if (value.isNotEmpty()) {
                    dispatch_async(dispatch_get_main_queue()) {
                        onBarcodeDetected(value)
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (session.running) {
                session.stopRunning()
            }
        }
    }

    UIKitView(
        modifier = modifier,
        factory = {
            val container = UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0))
            container.backgroundColor = UIColor.blackColor

            val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
            if (device == null) {
                return@UIKitView container
            }

            val input = AVCaptureDeviceInput.deviceInputWithDevice(device, null)
            val output = AVCaptureMetadataOutput()
            session.beginConfiguration()
            session.sessionPreset = AVCaptureSessionPresetHigh
            if (input != null && session.canAddInput(input)) {
                session.addInput(input)
            }
            if (session.canAddOutput(output)) {
                session.addOutput(output)
                val queue = dispatch_queue_create("barcodeQueue", null)
                output.setMetadataObjectsDelegate(delegate, queue)
                output.metadataObjectTypes = listOf(
                    AVMetadataObjectTypeEAN13Code,
                    AVMetadataObjectTypeEAN8Code,
                    AVMetadataObjectTypeUPCECode,
                    AVMetadataObjectTypeCode128Code,
                    AVMetadataObjectTypeCode39Code,
                    AVMetadataObjectTypeCode93Code,
                    AVMetadataObjectTypeITF14Code,
                )
            }
            session.commitConfiguration()

            val previewLayer = AVCaptureVideoPreviewLayer(session = session)
            previewLayer.videoGravity = platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
            container.layer.addSublayer(previewLayer)

            container
        },
        update = { view ->
            val previewLayer = view.layer.sublayers
                ?.firstOrNull { it is AVCaptureVideoPreviewLayer } as? AVCaptureVideoPreviewLayer
            if (previewLayer != null) {
                CATransaction.begin()
                CATransaction.setValue(true, kCATransactionDisableActions)
                previewLayer.frame = view.bounds
                CATransaction.commit()
            }
            if (!session.running) {
                dispatch_async(dispatch_queue_create("cameraStart", null)) {
                    session.startRunning()
                }
            }
        },
    )
}
