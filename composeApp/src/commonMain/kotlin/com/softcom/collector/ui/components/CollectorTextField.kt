package com.softcom.collector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softcom.collector.ui.theme.CollectorBorder
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorSurface
import com.softcom.collector.ui.theme.CollectorText

/**
 * TextField compacto sem o padding vertical excessivo do Material3,
 * que cortava o texto em alturas de 46–56dp.
 */
@Composable
fun CollectorCompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    singleLine: Boolean = true,
    textAlign: TextAlign = TextAlign.Start,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    height: Dp = 46.dp,
    horizontalPadding: Dp = 12.dp,
    background: Color = CollectorSurface,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = singleLine,
        textStyle = TextStyle(
            color = CollectorText,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign,
        ),
        cursorBrush = SolidColor(CollectorOrange),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .then(
                        if (borderWidth > 0.dp) {
                            Modifier.border(borderWidth, borderColor, shape)
                        } else {
                            Modifier
                        },
                    )
                    .background(background, shape)
                    .padding(horizontal = horizontalPadding),
            ) {
                if (leadingIcon != null) {
                    Box(modifier = Modifier.padding(end = 8.dp), contentAlignment = Alignment.Center) {
                        leadingIcon()
                    }
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = when (textAlign) {
                        TextAlign.Center -> Alignment.Center
                        TextAlign.End -> Alignment.CenterEnd
                        else -> Alignment.CenterStart
                    },
                ) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color(0xFFA8A8A8),
                            fontSize = fontSize,
                            textAlign = textAlign,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    innerTextField()
                }
                if (trailingIcon != null) {
                    Box(modifier = Modifier.padding(start = 8.dp), contentAlignment = Alignment.Center) {
                        trailingIcon()
                    }
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
fun CollectorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = CollectorText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        CollectorCompactTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            singleLine = singleLine,
            fontSize = 13.sp,
            height = 48.dp,
            background = Color.White,
            borderColor = CollectorBorder,
            borderWidth = 1.dp,
            shape = RoundedCornerShape(7.dp),
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
        )
    }
}
