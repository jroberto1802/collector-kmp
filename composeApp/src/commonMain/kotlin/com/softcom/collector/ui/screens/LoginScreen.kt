package com.softcom.collector.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softcom.collector.presentation.LoginViewModel
import com.softcom.collector.ui.components.CollectorLogo
import com.softcom.collector.ui.components.CollectorTextField
import com.softcom.collector.ui.components.VersionFooter
import com.softcom.collector.ui.theme.CollectorBorder
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorText

@Composable
fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(color = Color(0xFF20272B), modifier = Modifier.fillMaxSize()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(5.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 520.dp)
                    .background(Color.White, RoundedCornerShape(7.dp))
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 18.dp, vertical = 28.dp),
            ) {
                Spacer(Modifier.height(78.dp))
                CollectorLogo(Modifier.fillMaxWidth(0.53f).height(42.dp))
                Text(
                    text = "Seja bem vindo!",
                    color = CollectorText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 30.dp),
                )
                Text(
                    text = "Pronto para simplificar o seu controle do estoque?",
                    color = Color(0xFF888888),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp),
                )

                Spacer(Modifier.height(45.dp))
                CollectorTextField(
                    value = state.email,
                    onValueChange = viewModel::setEmail,
                    label = "E-mail",
                    placeholder = "Digite seu e-mail",
                )
                Spacer(Modifier.height(11.dp))
                CollectorTextField(
                    value = state.password,
                    onValueChange = viewModel::setPassword,
                    label = "Senha",
                    placeholder = "Digite sua senha",
                    visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = viewModel::togglePasswordVisibility) {
                            Icon(
                                imageVector = if (state.passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = if (state.passwordVisible) "Ocultar senha" else "Mostrar senha",
                                tint = Color(0xFF858585),
                            )
                        }
                    },
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = viewModel::toggleRememberPassword)
                        .padding(vertical = 13.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(17.dp)
                            .border(1.dp, if (state.rememberPassword) CollectorOrange else CollectorBorder, RoundedCornerShape(3.dp))
                            .background(
                                color = if (state.rememberPassword) CollectorOrange else Color.White,
                                shape = RoundedCornerShape(3.dp),
                            )
                            ,
                    ) {
                        if (state.rememberPassword) {
                            Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(13.dp))
                        }
                    }
                    Text("Salvar Senha", fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
                }

                Button(
                    onClick = { viewModel.submit(onLoginSuccess) },
                    enabled = !state.isSubmitting,
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CollectorOrange),
                    modifier = Modifier.fillMaxWidth().height(47.dp),
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                    } else {
                        Text("Continuar", fontSize = 14.sp)
                    }
                }

                if (state.feedback.isNotBlank()) {
                    Text(
                        text = state.feedback,
                        color = if (state.feedback.contains("sucesso", ignoreCase = true)) Color(0xFF2E7D32) else Color(0xFFB3261E),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    )
                }

                Spacer(Modifier.weight(1f).height(70.dp))
                VersionFooter(modifier = Modifier.padding(top = 20.dp, bottom = 10.dp))
            }
        }
    }
}
