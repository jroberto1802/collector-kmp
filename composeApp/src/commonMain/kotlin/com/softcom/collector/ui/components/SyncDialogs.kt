package com.softcom.collector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softcom.collector.presentation.ProductSyncViewModel
import com.softcom.collector.presentation.SyncStatus
import com.softcom.collector.resources.Res
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorText
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay

@Composable
fun SyncConfirmationDialog(onCancel: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onCancel) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 18.dp),
        ) {
            Box(Modifier.fillMaxWidth()) {
                IconButton(onClick = onCancel, modifier = Modifier.align(Alignment.TopEnd)) {
                    Icon(Icons.Outlined.Close, "Fechar", tint = Color(0xFF7E7E7E))
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .background(Color(0xFFFFF3EB), CircleShape),
            ) {
                Text("!", color = Color(0xFFFFDCC7), fontSize = 58.sp, fontWeight = FontWeight.Light)
            }
            Text(
                "Deseja sincronizar os dados?",
                color = CollectorText,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 18.dp),
            )
            Text(
                "Atenção, essa ação pode levar alguns minutos.",
                color = Color(0xFFA0A0A0),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 14.dp),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 21.dp),
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    shape = RoundedCornerShape(7.dp),
                    modifier = Modifier.weight(1f).height(43.dp),
                ) { Text("Não", color = CollectorOrange) }
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = CollectorOrange),
                    shape = RoundedCornerShape(7.dp),
                    modifier = Modifier.weight(1f).height(43.dp),
                ) { Text("Sim") }
            }
        }
    }
}

@Composable
fun ProductSyncDialog(
    viewModel: ProductSyncViewModel,
    onCompleted: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val syncJson by produceState<String?>(null) {
        value = Res.readBytes("files/sync_animation.json").decodeToString()
    }
    val loadingJson by produceState<String?>(null) {
        value = Res.readBytes("files/loading_animation.json").decodeToString()
    }
    val syncComposition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(syncJson.orEmpty())
    }
    val loadingComposition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(loadingJson.orEmpty())
    }
    val syncProgress by animateLottieCompositionAsState(
        syncComposition,
        iterations = if (state.status == SyncStatus.SUCCESS) 1 else Compottie.IterateForever,
    )
    val loadingProgress by animateLottieCompositionAsState(
        loadingComposition,
        iterations = Compottie.IterateForever,
    )

    LaunchedEffect(state.status) {
        if (state.status == SyncStatus.SUCCESS) {
            delay(850)
            onCompleted()
        }
    }

    Dialog(onDismissRequest = {}) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 14.dp, vertical = 22.dp),
        ) {
            if (syncComposition != null) {
                Image(
                    painter = rememberLottiePainter(syncComposition, progress = { syncProgress }),
                    contentDescription = null,
                    modifier = Modifier.size(126.dp),
                )
            } else {
                Spacer(Modifier.height(126.dp))
            }

            Text(
                text = when (state.status) {
                    SyncStatus.ERROR -> "Não foi possível sincronizar"
                    SyncStatus.SUCCESS -> "Sincronização concluída!"
                    else -> "Sincronizando seus itens..."
                },
                color = CollectorText,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            if (state.status == SyncStatus.ERROR) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(108.dp)
                        .padding(top = 14.dp)
                        .background(Color(0xFFF7F7F7), RoundedCornerShape(7.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp),
                ) {
                    Text(state.errorMessage, color = Color(0xFF707070), fontSize = 12.sp, textAlign = TextAlign.Center)
                }
                Button(
                    onClick = { viewModel.synchronize() },
                    colors = ButtonDefaults.buttonColors(containerColor = CollectorOrange),
                    shape = RoundedCornerShape(7.dp),
                    modifier = Modifier.padding(top = 18.dp),
                ) { Text("Tentar novamente") }
            } else {
                Text(
                    text = if (state.status == SyncStatus.SUCCESS) {
                        "${state.productCount} produtos disponíveis para uso offline."
                    } else {
                        "Esta ação pode levar alguns minutos. Por favor, aguarde!"
                    },
                    color = Color(0xFFA0A0A0),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp),
                )

                if (state.status == SyncStatus.SUCCESS) {
                    Button(
                        onClick = onCompleted,
                        colors = ButtonDefaults.buttonColors(containerColor = CollectorOrange),
                        shape = RoundedCornerShape(7.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp)
                            .height(43.dp),
                    ) { Text("Continuar") }
                } else {
                    if (loadingComposition != null) {
                        Image(
                            painter = rememberLottiePainter(loadingComposition, progress = { loadingProgress }),
                            contentDescription = null,
                            modifier = Modifier.size(54.dp).padding(top = 5.dp),
                        )
                    }
                    val progress = state.progress
                    Text(
                        text = progress?.let {
                            "Página ${it.currentPage} de ${it.lastPage} • ${it.downloadedProducts} produtos"
                        } ?: "Preparando a sincronização...",
                        color = Color(0xFFA0A0A0),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
