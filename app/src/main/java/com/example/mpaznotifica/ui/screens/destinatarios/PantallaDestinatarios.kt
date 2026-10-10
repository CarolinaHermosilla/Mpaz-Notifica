package com.example.mpaznotifica.ui.screens.destinatarios

import androidx.compose.runtime.Composable
import com.example.mpaznotifica.ui.components.EnConstruccion

/**
 * PANTALLA 4 - SELECCIÓN DE APODERADOS (DESTINATARIOS)  (Carolina)
 * Referencia: captura "4. Selección de apoderados" del informe (página 4).
 *
 * Incluye las dos funciones nativas acordadas:
 *  - Vibración al enviar la comunicación.
 *  - Animación de carga mientras se envía.
 *
 * PENDIENTE: se desarrolla en un paso posterior.
 */
@Composable
fun PantallaDestinatarios(
    onVolver: () -> Unit,
    onComunicacionEnviada: () -> Unit
) {
    EnConstruccion(
        titulo = "Destinatarios",
        detalle = "Pantalla en desarrollo (selección de apoderados)."
    )
}
