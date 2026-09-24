package com.softcom.collector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.softcom.collector.platform.BarcodeCameraPreview
import com.softcom.collector.platform.rememberCameraPermissionState
import com.softcom.collector.ui.theme.CollectorOrange
import kotlinx.coroutines.delay

@Composable
fun BarcodeScannerDialog(
    onScanned: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val permission = rememberCameraPermissionState()
    var ready by remember { mutableStateOf(false) }
    var locked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!permission.granted) {
            permission.requestPermission()
        }
    }

    LaunchedEffect(permission.granted) {
        ready = false
        locked = false
        if (permission.granted) {
            delay(350)
            ready = true
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF111111)),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Outlined.Close, contentDescription = "Fechar", tint = Color.White)
                }
                Text(
                    "Ler código de barras",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.size(40.dp))
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1A1A)),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    !permission.granted -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp),
                        ) {
                            Text(
                                "Precisamos da câmera para ler o código de barras.",
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                            )
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .background(CollectorOrange, RoundedCornerShape(8.dp))
                                    .clickable { permission.requestPermission() }
                                    .padding(horizontal = 18.dp, vertical = 12.dp),
                            ) {
                                Text("Permitir câmera", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    !ready -> {
                        Text("Abrindo câmera...", color = Color.White, fontSize = 14.sp)
                    }
                    else -> {
                        BarcodeCameraPreview(
                            modifier = Modifier.fillMaxSize(),
                            onBarcodeDetected = { value ->
                                if (!locked && value.isNotBlank()) {
                                    locked = true
                                    onScanned(value.trim())
                                }
                            },
                        )
                    }
                }
            }

            Text(
                "Aponte a câmera para o código de barras do produto",
                color = Color(0xFFD0D0D0),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
            )
        }
    }
}
