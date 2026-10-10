package com.example.mpaznotifica.ui.screens.comunicacion

import androidx.compose.runtime.Composable
import com.example.mpaznotifica.ui.components.EnConstruccion

/**
 * PANTALLA 3 - COMUNICACIÓN  (Pedro)
 * Incluye los recursos nativos de cámara y galería para adjuntar evidencias.
 *
 * ESTA PANTALLA NO LA DESARROLLA CAROLINA.
 * La ruta ya está creada en AppNavigation para que la navegación funcione.
 */
@Composable
fun PantallaComunicacion(
    onVolver: () -> Unit,
    onContinuar: () -> Unit
) {
    EnConstruccion(
        titulo = "Comunicación",
        detalle = "Pantalla pendiente: la desarrolla el otro integrante del equipo."
    )
}
