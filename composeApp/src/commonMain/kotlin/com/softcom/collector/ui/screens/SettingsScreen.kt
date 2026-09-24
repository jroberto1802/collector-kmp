package com.softcom.collector.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softcom.collector.core.PriceType
import com.softcom.collector.core.SearchType
import com.softcom.collector.presentation.SettingsViewModel
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorText

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = state.settings
    val isQuickExclusive = settings.quickCollection
    val isDetailedExclusive = settings.lotSerial || settings.grade
    val isLotDisabled = !state.isSoftcomshop || isQuickExclusive

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(horizontal = 16.dp)
            .padding(top = 13.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(42.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(42.dp)
                    .background(Color(0xFFF4F4F4), CircleShape)
                    .clickable(onClick = onBack),
            ) {
                Icon(
                    Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Voltar",
                    tint = Color(0xFF555555),
                    modifier = Modifier.size(18.dp),
                )
            }
            Text(
                "Configurações",
                color = CollectorText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CollectorOrange)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 18.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SettingCard(
                    title = "Coleta rápida ⚡",
                    description = "Lançamento de itens ao capturar código",
                    disabled = isDetailedExclusive,
                    control = {
                        SettingSwitch(
                            checked = settings.quickCollection,
                            enabled = !isDetailedExclusive,
                            onCheckedChange = viewModel::setQuickCollection,
                        )
                    },
                )

                if (state.isSoftcomshop) {
                    SettingCard(
                        title = "Lote/Serial",
                        description = "Solicitar o lote ou número de série e as datas de fabricação ou validade dos produtos.",
                        disabled = isLotDisabled,
                        control = {
                            SettingSwitch(
                                checked = settings.lotSerial,
                                enabled = !isLotDisabled,
                                onCheckedChange = viewModel::setLotSerial,
                            )
                        },
                    )
                }

                SettingCard(
                    title = "Grade",
                    description = "Solicitar atributos de cor e tamanho",
                    disabled = isQuickExclusive,
                    control = {
                        SettingSwitch(
                            checked = settings.grade,
                            enabled = !isQuickExclusive,
                            onCheckedChange = viewModel::setGrade,
                        )
                    },
                )

                SettingCard(
                    title = "Tipo de Busca",
                    description = "Selecione o tipo de busca",
                    control = {
                        SettingSelect(
                            valueLabel = settings.searchType.label,
                            options = SearchType.entries.map { it.label to it },
                            onSelect = viewModel::setSearchType,
                        )
                    },
                )

                SettingCard(
                    title = "Tipo de Preço",
                    description = "Selecione entre preço de venda e preço de compra",
                    control = {
                        SettingSelect(
                            valueLabel = settings.priceType.label,
                            options = PriceType.entries.map { it.label to it },
                            onSelect = viewModel::setPriceType,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun SettingCard(
    title: String,
    description: String,
    disabled: Boolean = false,
    control: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (disabled) 0.dp else 2.dp, RoundedCornerShape(12.dp))
            .background(
                if (disabled) Color(0xFFE8ECF0) else Color.White,
                RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                title,
                color = if (disabled) Color(0xFF7A8490) else CollectorText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                description,
                color = if (disabled) Color(0xFF9AA3AD) else Color(0xFF9A9A9A),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        control()
    }
}

@Composable
private fun SettingSwitch(
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        checked = checked,
        enabled = enabled,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = CollectorOrange,
            checkedTrackColor = Color(0xFFFFB07A),
            uncheckedThumbColor = Color(0xFFF4F4F4),
            uncheckedTrackColor = Color(0xFFD8D8D8),
        ),
    )
}

@Composable
private fun <T> SettingSelect(
    valueLabel: String,
    options: List<Pair<String, T>>,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(148.dp)
                .height(36.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(start = 10.dp, end = 8.dp),
        ) {
            Text(
                valueLabel,
                color = CollectorText,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )
            Icon(
                Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFF8A8A8A),
                modifier = Modifier.size(16.dp),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (label, value) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            label,
                            color = if (label == valueLabel) CollectorOrange else CollectorText,
                            fontWeight = if (label == valueLabel) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    },
                )
            }
        }
    }
}
