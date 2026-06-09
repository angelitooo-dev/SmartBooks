package co.edu.cecar.smartbooks.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = RojoInstitucional,
    onPrimary = BlancoFondo,
    surface = BlancoFondo,
    onSurface = GrisOscuroTexto,
    outline = GrisMedioBordes,
    background = BlancoFondo
)

@Composable
fun SmartBooksTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}