package com.softcom.collector.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softcom.collector.core.AppConstants
import com.softcom.collector.platform.openExternalUrl
import com.softcom.collector.resources.Res
import com.softcom.collector.resources.logo_by_softcom
import com.softcom.collector.resources.logo_collector
import org.jetbrains.compose.resources.painterResource

@Composable
fun CollectorLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.logo_collector),
        contentDescription = "Collector",
        contentScale = ContentScale.Fit,
        modifier = modifier,
    )
}

@Composable
fun SoftcomLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.logo_by_softcom),
        contentDescription = "Softcom",
        contentScale = ContentScale.Fit,
        modifier = modifier.height(28.dp).width(180.dp),
    )
}

@Composable
fun VersionFooter(showLogo: Boolean = true, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (showLogo) SoftcomLogo()
        Text("Versão ${AppConstants.APP_VERSION}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text("Softcom Tecnologia | 0800 003 3600", color = Color(0xFF5E5E5E), fontSize = 11.sp)
        Text(
            text = "www.softcomtecnologia.com.br",
            color = Color(0xFF555555),
            fontSize = 11.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { openExternalUrl(AppConstants.SOFTCOM_URL) },
        )
    }
}
