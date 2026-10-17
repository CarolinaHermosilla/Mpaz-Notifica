package com.example.mpaznotifica.ui.screens.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpaznotifica.ui.components.EncabezadoApp
import com.example.mpaznotifica.ui.theme.AzulClaroInfo
import com.example.mpaznotifica.ui.theme.AzulInstitucional
import com.example.mpaznotifica.ui.theme.AzulTextoInfo
import com.example.mpaznotifica.ui.theme.AzulTitulo
import com.example.mpaznotifica.ui.theme.Blanco
import com.example.mpaznotifica.ui.theme.FondoApp
import com.example.mpaznotifica.ui.theme.TextoPrincipal
import com.example.mpaznotifica.ui.theme.TextoSecundario
import com.example.mpaznotifica.ui.theme.VerdeFondoEstado
import com.example.mpaznotifica.ui.theme.VerdeTextoEstado

/**
 * PANTALLA 6 - HISTORIAL DE COMUNICACIONES  (Pedro)
 *
 * Lista las comunicaciones que ya se enviaron, con su tipo, su fecha, el canal
 * por el que salieron y cuántos apoderados las recibieron.
 *
 * El informe trae esta pantalla sin captura (solo mencionada), así que se usó la
 * misma estructura visual que "Comunicaciones recientes" de la pantalla Inicio,
 * con los colores y componentes del mockup.
 */
@Composable
fun PantallaHistorial(
    onVolver: () -> Unit,
    viewModel: HistorialViewModel = viewModel()
) {
    val estado = viewModel.uiState

    // Se recarga cada vez que se entra a la pantalla, para que aparezca la
    // comunicación que se acaba de enviar.
    LaunchedEffect(Unit) {
        viewModel.cargar()
    }

    Scaffold(
        containerColor = FondoApp,
        topBar = { EncabezadoApp(titulo = "Historial", onVolver = onVolver) }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 18.dp, bottom = 28.dp)
            ) {
                Text(
                    text = "Comunicaciones enviadas",
                    color = AzulTitulo,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Todas las comunicaciones que has enviado a los apoderados, " +
                        "de la más reciente a la más antigua.",
                    color = TextoSecundario,
                    fontSize = 15.sp
                )

                Spacer(Modifier.height(16.dp))

                CajaInformativa(
                    texto = if (estado.cargando) {
                        "Revisando las comunicaciones guardadas en el teléfono."
                    } else {
                        "Se han enviado ${estado.total} " +
                            (if (estado.total == 1) "comunicación" else "comunicaciones") +
                            ". Cada envío queda guardado en el teléfono, incluso al cerrar " +
                            "la aplicación."
                    }
                )

                Spacer(Modifier.height(20.dp))

                when {
                    estado.cargando -> CargandoHistorial()

                    estado.comunicaciones.isEmpty() -> SinComunicaciones()

                    else -> estado.comunicaciones.forEach { comunicacion ->
                        TarjetaHistorial(comunicacion = comunicacion)
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

/** Caja celeste con la información general del listado. */
@Composable
private fun CajaInformativa(texto: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AzulClaroInfo)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = AzulTextoInfo,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.size(12.dp))
        Text(
            text = texto,
            color = AzulTextoInfo,
            fontSize = 15.sp
        )
    }
}

/** Rueda de carga mientras se leen las comunicaciones guardadas. */
@Composable
private fun CargandoHistorial() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = AzulInstitucional)
    }
}

/** Mensaje que se muestra cuando todavía no se ha enviado ninguna comunicación. */
@Composable
private fun SinComunicaciones() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Blanco)
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(AzulClaroInfo),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = null,
                tint = AzulTextoInfo,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = "Todavía no hay comunicaciones enviadas",
            color = TextoPrincipal,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Cuando envíes una comunicación, aparecerá en esta lista con su fecha, " +
                "su canal y sus destinatarios.",
            color = TextoSecundario,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Tarjeta de una comunicación enviada: ícono, título, tipo, fecha, canal,
 * destinatarios y la etiqueta verde "Enviada".
 */
@Composable
private fun TarjetaHistorial(comunicacion: ComunicacionHistorial) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Blanco)
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(AzulClaroInfo),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                tint = AzulTextoInfo,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comunicacion.titulo,
                color = TextoPrincipal,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            EtiquetaTipo(texto = comunicacion.tipo)

            Spacer(Modifier.height(8.dp))

            Text(
                text = comunicacion.fecha,
                color = TextoSecundario,
                fontSize = 13.sp
            )
            Text(
                text = canalLegible(comunicacion.canal),
                color = TextoSecundario,
                fontSize = 13.sp
            )
            Text(
                text = textoDestinatarios(comunicacion.destinatarios),
                color = TextoSecundario,
                fontSize = 13.sp
            )
        }

        Box(modifier = Modifier.padding(start = 6.dp)) {
            EtiquetaEnviada()
        }
    }
}

/** Etiqueta celeste con el tipo de comunicación. */
@Composable
private fun EtiquetaTipo(texto: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AzulClaroInfo)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = texto.ifBlank { "Sin tipo" },
            color = AzulTextoInfo,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Etiqueta verde que marca una comunicación ya enviada. */
@Composable
private fun EtiquetaEnviada() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(VerdeFondoEstado)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = "Enviada",
            color = VerdeTextoEstado,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Cómo se muestra el canal del envío (si quedó vacío, se avisa). */
private fun canalLegible(canal: String): String {
    return if (canal.isBlank()) "Canal no registrado" else "Canal: $canal"
}

/** Cómo se muestra la cantidad de apoderados que recibieron la comunicación. */
private fun textoDestinatarios(cantidad: Int): String {
    return when (cantidad) {
        0 -> "Sin destinatarios registrados"
        1 -> "1 apoderado"
        else -> "$cantidad apoderados"
    }
}
