package com.transporteursanitaire.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Import des couleurs depuis Color.kt
import com.transporteursanitaire.ui.theme.Purple80
import com.transporteursanitaire.ui.theme.PurpleGrey80
import com.transporteursanitaire.ui.theme.Pink80
import com.transporteursanitaire.ui.theme.Purple40
import com.transporteursanitaire.ui.theme.PurpleGrey40
import com.transporteursanitaire.ui.theme.Pink40

// Import de la typographie corrigée
import com.transporteursanitaire.ui.theme.TransporteurSanitaireTypography

// Définition de la palette sombre
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

// Définition de la palette claire
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

/**
 * Thème global de l'application.
 *
 * @param darkTheme  Choix entre le mode clair et sombre (par défaut, se base sur l'état système via [isSystemInDarkTheme]).
 * @param dynamicColor Active les couleurs dynamiques (Material You) sur Android 12+ (si [Build.VERSION.SDK_INT] >= S).
 * @param content    Contenu Composable enveloppé par ce thème.
 */
@Composable
fun TransporteurSanitaireTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TransporteurSanitaireTypography, // Utilisation de la typographie corrigée
        content = content
    )
}