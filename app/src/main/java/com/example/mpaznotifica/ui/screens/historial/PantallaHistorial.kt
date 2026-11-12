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

@Composable
fun PantallaHistorial(
    onVolver: () -> Unit,
    viewModel: HistorialViewModel = viewModel()
) {
    val estado = viewModel.uiState

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
                    texto = when {
                        estado.cargando -> "Revisando las comunicaciones enviadas."
                        estado.error != null -> estado.error.orEmpty()
                        else -> "Se han enviado ${estado.total} " +
                            (if (estado.total == 1) "comunicación" else "comunicaciones") +
                            ". Cada envío queda registrado en el servidor de la escuela."
                    }
                )

                Spacer(Modifier.height(20.dp))

                when {
                    estado.cargando -> CargandoHistorial()

                    estado.error != null -> SinComunicaciones(
                        titulo = "No se pudo cargar el historial",
                        detalle = "Revisa tu conexión y vuelve a entrar a la pantalla."
                    )

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

@Composable
private fun SinComunicaciones(
    titulo: String = "Todavía no hay comunicaciones enviadas",
    detalle: String = "Cuando envíes una comunicación, aparecerá en esta lista con su " +
        "fecha, su canal y sus destinatarios."
) {
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
            text = titulo,
            color = TextoPrincipal,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = detalle,
            color = TextoSecundario,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

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

private fun canalLegible(canal: String): String {
    return if (canal.isBlank()) "Canal no registrado" else "Canal: $canal"
}

private fun textoDestinatarios(cantidad: Int): String {
    return when (cantidad) {
        0 -> "Sin destinatarios registrados"
        1 -> "1 apoderado"
        else -> "$cantidad apoderados"
    }
}
