package eu.gryta.compose.elements.theme

import androidx.compose.material3.Typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import eu.gryta.compose.elements.resources.Res
import eu.gryta.compose.elements.resources.roboto_medium
import org.jetbrains.compose.resources.Font

@Composable
fun roboto() = FontFamily(
    Font(Res.font.roboto_medium)
)

val AppTypography = Typography()
