package co.edu.cecar.smartbooks.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import co.edu.cecar.smartbooks.R

// 1. Declaración de la familia de fuentes Inter
val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_bold, FontWeight.Bold)
)

// Estilo base seguro para que Inter no recorte las letras por abajo
private val baseInterStyle = TextStyle(
    fontFamily = InterFontFamily,
    platformStyle = PlatformTextStyle(
        includeFontPadding = false // 🚀 Arregla el corte vertical de letras en botones
    )
)

// 2. Configuración global para el sistema de temas Material Design 3
val Typography = Typography(
    titleLarge = baseInterStyle.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    bodyLarge = baseInterStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = baseInterStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = baseInterStyle.copy( // Usado automáticamente por los botones de Material 3
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    labelSmall = baseInterStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)