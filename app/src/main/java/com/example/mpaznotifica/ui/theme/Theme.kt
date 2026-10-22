package com.example.mpaznotifica.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = AzulInstitucional,
    onPrimary = Blanco,
    secondary = AzulTitulo,
    onSecondary = Blanco,
    background = FondoApp,
    onBackground = TextoPrincipal,
    surface = Blanco,
    onSurface = TextoPrincipal,
    surfaceVariant = AzulClaroInfo,
    onSurfaceVariant = AzulTextoInfo,
    outline = BordeCampo
)

@Composable
fun MpazNotificaTheme(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = TipografiaApp,
        content = contenido
    )
}
