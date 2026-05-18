package mx.edu.itson.pompompurincafe.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Configuración del esquema de colores para el modo oscuro de la aplicación.
 */
private val DarkColorScheme = darkColorScheme(
    background = lightyellow,
    primary = yellow,
    secondary = brown,
    tertiary = white
)

/**
 * Configuración del esquema de colores para el modo claro de la aplicación.
 */
private val LightColorScheme = lightColorScheme(
    background = lightyellow,
    primary = yellow,
    secondary = brown,
    tertiary = white

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

/**
 * Función principal del tema que aplica el estilo visual de Pompompurin Café.
 * Se encarga de gestionar la paleta de colores y la tipografía global de la interfaz.
 */
@Composable
fun PompompurinCafeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    /** Determina el esquema de colores a utilizar según la versión de Android y la preferencia del sistema. */
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    /** Inyecta los colores seleccionados y la tipografía base en el contenedor de MaterialTheme. */
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}