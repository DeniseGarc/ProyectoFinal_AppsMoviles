package mx.edu.itson.pompompurincafe.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import mx.edu.itson.pompompurincafe.R

/**
 * Registro y agrupación de la tipografía personalizada para la aplicación.
 * Define la familia de fuentes oficial del proyecto según sus diferentes grosores.
 */
val CustomFontFamily = FontFamily(
    Font(resId = R.font.fredoka_bold, weight = FontWeight.Bold),
    Font(resId = R.font.fredoka_medium, weight = FontWeight.Medium),
    Font(resId = R.font.fredoka_regular, weight = FontWeight.Normal),
    Font(resId = R.font.fredoka_semibold, weight = FontWeight.SemiBold)
)

/**
 * Configuración de los estilos tipográficos.
 * Asigna la familia tipográfica personalizada a los diferentes roles de texto en la interfaz.
 */
val Typography = Typography(
    /** Estilo de texto general utilizado para cuerpos de texto, descripciones y párrafos extensos. */
    bodyLarge = TextStyle(
        fontFamily = CustomFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)