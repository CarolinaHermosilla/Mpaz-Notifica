package com.example.mpaznotifica.ui.screens.destinatarios

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpaznotifica.data.model.GrupoConIntegrantes
import com.example.mpaznotifica.data.repositorio.ComunicacionRepositorio
import com.example.mpaznotifica.ui.components.EncabezadoApp
import com.example.mpaznotifica.ui.screens.grupos.FilaApoderado
import com.example.mpaznotifica.ui.theme.AzulClaroInfo
import com.example.mpaznotifica.ui.theme.AzulInstitucional
import com.example.mpaznotifica.ui.theme.AzulTextoInfo
import com.example.mpaznotifica.ui.theme.Blanco
import com.example.mpaznotifica.ui.theme.BordeCampo
import com.example.mpaznotifica.ui.theme.FondoApp
import com.example.mpaznotifica.ui.theme.TextoPrincipal
import com.example.mpaznotifica.ui.theme.TextoSecundario

@Composable
fun PantallaDestinatarios(
    usuarioId: Int,
    onVolver: () -> Unit,
    onComunicacionEnviada: () -> Unit,
    viewModel: DestinatariosViewModel = viewModel()
) {
    val estado = viewModel.uiState

    LaunchedEffect(usuarioId) {
        viewModel.cargar(usuarioId)
    }

    Scaffold(
        containerColor = FondoApp,
        topBar = {
            EncabezadoApp(
                titulo = "Destinatarios",
                onVolver = onVolver
            )
        },
        bottomBar = {
            BarraEnvio(
                estado = estado,
                onCanalSeleccionado = viewModel::onCanalSeleccionado,
                onEnviar = { viewModel.enviar(onComunicacionEnviada) }
            )
        }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            CajaAviso(texto = estado.textoAviso)

            Spacer(Modifier.height(20.dp))

            EtiquetaSeccion(texto = "ALCANCE DE LA COMUNICACIÓN")

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Capsula(
                    texto = estado.etiquetaAlcance,
                    seleccionada = estado.todosMarcados,
                    onClick = viewModel::onTodoElCurso
                )
                Capsula(
                    texto = "Elegir apoderados",
                    seleccionada = !estado.todosMarcados,
                    onClick = viewModel::onElegirApoderados
                )
            }

            Spacer(Modifier.height(22.dp))

            EtiquetaSeccion(texto = "O CARGA UN GRUPO GUARDADO")

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Al elegir un grupo se marcan sus integrantes. Después puedes " +
                    "agregar o quitar apoderados a mano.",
                color = TextoSecundario,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                estado.grupos.chunked(2).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        fila.forEach { grupo ->
                            CapsulaGrupo(
                                grupo = grupo,
                                seleccionado = grupoEstaMarcado(grupo, estado.idsSeleccionados),
                                onClick = { viewModel.onGrupoTocado(grupo) }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                text = "Marca a los apoderados que recibirán esta comunicación.",
                color = TextoSecundario,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(6.dp))

            if (estado.cargando) {
                CircularProgressIndicator(
                    color = AzulInstitucional,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                estado.apoderados.forEach { apoderado ->
                    FilaApoderado(
                        apoderado = apoderado,
                        marcado = estado.idsSeleccionados.contains(apoderado.id),
                        mostrarTaller = true,
                        onMarcado = { marcado -> viewModel.onApoderadoMarcado(apoderado.id, marcado) }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

private fun grupoEstaMarcado(grupo: GrupoConIntegrantes, idsSeleccionados: Set<Int>): Boolean {
    return grupo.integrantes.isNotEmpty() &&
        grupo.integrantes.all { idsSeleccionados.contains(it.id) }
}

@Composable
private fun CajaAviso(texto: String) {
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
private fun EtiquetaSeccion(texto: String) {
    Text(
        text = texto,
        color = TextoSecundario,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}

@Composable
private fun Capsula(
    texto: String,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    val forma = RoundedCornerShape(30.dp)
    Box(
        modifier = Modifier
            .height(42.dp)
            .clip(forma)
            .background(if (seleccionada) AzulInstitucional else Blanco)
            .border(
                width = 1.dp,
                color = if (seleccionada) AzulInstitucional else BordeCampo,
                shape = forma
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = if (seleccionada) Blanco else TextoPrincipal,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun CapsulaGrupo(
    grupo: GrupoConIntegrantes,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val forma = RoundedCornerShape(30.dp)
    Box(
        modifier = Modifier
            .clip(forma)
            .background(if (seleccionado) AzulInstitucional else Blanco)
            .border(
                width = 1.dp,
                color = if (seleccionado) AzulInstitucional else BordeCampo,
                shape = forma
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${grupo.grupo.nombre} (${grupo.cantidadDeIntegrantes})",
            color = if (seleccionado) Blanco else TextoPrincipal,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun BarraEnvio(
    estado: DestinatariosUiState,
    onCanalSeleccionado: (String) -> Unit,
    onEnviar: () -> Unit
) {
    // La aplicación ocupa toda la pantalla (enableEdgeToEdge), así que la barra
    // reserva el alto de los botones de navegación de Android; sin esto el botón
    // "Enviar comunicación" queda tapado por ellos.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Blanco)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(color = BordeCampo)

        Column(
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                top = 14.dp,
                // Un poco más de aire abajo, para que el botón no quede pegado
                // a los botones de navegación del teléfono.
                bottom = 22.dp
            )
        ) {
            Text(
                text = if (estado.cantidadSeleccionados == 0) {
                    "No hay apoderados marcados"
                } else {
                    "Se enviará a ${estado.cantidadSeleccionados} " +
                        if (estado.cantidadSeleccionados == 1) "apoderado" else "apoderados"
                },
                color = if (estado.cantidadSeleccionados == 0) TextoSecundario else TextoPrincipal,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            if (estado.tipoComunicacion.isNotBlank()) {
                Text(
                    text = "Tipo: ${estado.tipoComunicacion}",
                    color = TextoSecundario,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            EtiquetaSeccion(texto = "CANAL")

            Spacer(Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ComunicacionRepositorio.CANALES.forEach { canal ->
                    CapsulaCanal(
                        texto = canal,
                        seleccionado = canal == estado.canalSeleccionado,
                        onClick = { onCanalSeleccionado(canal) }
                    )
                }
            }

            AnimatedVisibility(
                visible = estado.errorDestinatarios != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = estado.errorDestinatarios.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onEnviar,
                enabled = !estado.enviando,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulInstitucional,
                    contentColor = Blanco
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (estado.enviando) {
                    CircularProgressIndicator(
                        color = Blanco,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(text = "Enviando...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(
                        text = "Enviar comunicación",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CapsulaCanal(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val forma = RoundedCornerShape(30.dp)
    Box(
        modifier = Modifier
            .clip(forma)
            .background(if (seleccionado) AzulInstitucional else Blanco)
            .border(
                width = 1.dp,
                color = if (seleccionado) AzulInstitucional else BordeCampo,
                shape = forma
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = if (seleccionado) Blanco else TextoPrincipal,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}
