package com.begoml.uistatedelegate.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.begoml.uistatedelegate.ui.theme.*

private val Color = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = Color,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
