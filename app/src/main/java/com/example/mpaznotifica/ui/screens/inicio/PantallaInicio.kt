package com.example.mpaznotifica.ui.screens.inicio

import androidx.compose.runtime.Composable
import com.example.mpaznotifica.ui.components.EnConstruccion

/**
 * PANTALLA 2 - INICIO  (Pedro)
 * Referencia: captura "2. Inicio" del informe (página 3).
 *
 * ESTA PANTALLA NO LA DESARROLLA CAROLINA.
 * La ruta ya está creada en AppNavigation para que la navegación funcione.
 */
@Composable
fun PantallaInicio(
    onNuevaComunicacion: () -> Unit,
    onVerGrupos: () -> Unit,
    onVerHistorial: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    EnConstruccion(
        titulo = "Inicio",
        detalle = "Pantalla pendiente: la desarrolla el otro integrante del equipo."
    )
}
