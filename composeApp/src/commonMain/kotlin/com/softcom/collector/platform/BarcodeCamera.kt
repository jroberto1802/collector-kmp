package com.softcom.collector.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface CameraPermissionState {
    val granted: Boolean
    fun requestPermission()
}

@Composable
expect fun rememberCameraPermissionState(): CameraPermissionState

@Composable
expect fun BarcodeCameraPreview(
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier = Modifier,
)
