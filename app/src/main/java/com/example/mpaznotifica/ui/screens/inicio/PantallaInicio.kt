package com.example.mpaznotifica.ui.screens.inicio

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpaznotifica.ui.theme.AzulClaroInfo
import com.example.mpaznotifica.ui.theme.AzulInstitucional
import com.example.mpaznotifica.ui.theme.AzulTextoInfo
import com.example.mpaznotifica.ui.theme.AzulTitulo
import com.example.mpaznotifica.ui.theme.Blanco
import com.example.mpaznotifica.ui.theme.BordeCampo
import com.example.mpaznotifica.ui.theme.FondoApp
import com.example.mpaznotifica.ui.theme.TextoPrincipal
import com.example.mpaznotifica.ui.theme.TextoSecundario
import com.example.mpaznotifica.ui.theme.VerdeFondoEstado
import com.example.mpaznotifica.ui.theme.VerdeTextoEstado

@Composable
fun PantallaInicio(
    usuarioId: Int,
    onNuevaComunicacion: () -> Unit,
    onVerGrupos: () -> Unit,
    onVerHistorial: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: InicioViewModel = viewModel()
) {
    val estado = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    // Se recarga cada vez que la pantalla vuelve a estar visible: así los
    // contadores quedan al día después de enviar una comunicación.
    LifecycleResumeEffect(usuarioId) {
        viewModel.cargar(usuarioId)
        onPauseOrDispose { }
    }

    // Aviso cuando el servidor no responde.
    LaunchedEffect(estado.aviso) {
        estado.aviso?.let { texto ->
            snackbarHostState.showSnackbar(texto)
            viewModel.onAvisoMostrado()
        }
    }

    val mensajeSesion = if (estado.rolYCurso.isBlank()) {
        ""
    } else {
        "Sesión iniciada como ${estado.rolYCurso}"
    }

    Scaffold(
        containerColor = FondoApp,
        topBar = {
            EncabezadoInicio(onCerrarSesion = { viewModel.cerrarSesion(onCerrarSesion) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .verticalScroll(rememberScrollState())
        ) {
            CabeceraProfesora(
                saludo = estado.saludo,
                nombreProfesor = estado.rolYCurso,
                curso = estado.curso,
                iniciales = estado.iniciales,
                mensajeSesion = mensajeSesion
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 28.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TarjetaContador(
                        numero = estado.borradores,
                        etiqueta = "Borradores",
                        icono = Icons.Filled.Edit,
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaContador(
                        numero = estado.enviadas,
                        etiqueta = "Enviadas",
                        icono = Icons.Filled.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaContador(
                        numero = estado.apoderados,
                        etiqueta = "Apoderados",
                        icono = Icons.Filled.Person,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = onNuevaComunicacion,
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulInstitucional,
                        contentColor = Blanco
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = "Nueva comunicación",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onVerGrupos,
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulInstitucional),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(1.dp, BordeCampo, RoundedCornerShape(30.dp))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = "Grupos de apoderados",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(18.dp))

                CajaInformativa(
                    texto = estado.textoAlcance
                )

                Spacer(Modifier.height(22.dp))

                FilaTituloHistorial(onVerHistorial = onVerHistorial)

                Spacer(Modifier.height(12.dp))

                when {
                    estado.cargando -> CargandoRecientes()

                    estado.comunicacionesRecientes.isEmpty() -> Text(
                        text = "Todavía no hay comunicaciones recientes.",
                        color = TextoSecundario,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    )

                    else -> estado.comunicacionesRecientes.forEach { comunicacion ->
                        TarjetaComunicacionReciente(comunicacion = comunicacion)
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CargandoRecientes() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = AzulInstitucional)
    }
}

@Composable
private fun EncabezadoInicio(onCerrarSesion: () -> Unit) {    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AzulInstitucional)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "MPAZ Notifica",
            color = Blanco,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Cerrar sesión",
            color = Blanco,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onCerrarSesion)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun CabeceraProfesora(
    saludo: String,
    nombreProfesor: String,
    curso: String,
    iniciales: String,
    mensajeSesion: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AzulInstitucional)
            .padding(horizontal = 20.dp)
            .padding(top = 6.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = saludo,
                color = Blanco,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = nombreProfesor,
                color = Blanco,
                fontSize = 15.sp
            )

            if (mensajeSesion.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = VerdeFondoEstado,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = mensajeSesion,
                        color = Blanco,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(AzulClaroInfo)
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = curso,
                    color = AzulTextoInfo,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(AzulClaroInfo),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iniciales,
                color = AzulTextoInfo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TarjetaContador(
    numero: Int,
    etiqueta: String,
    icono: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Blanco)
            .padding(vertical = 14.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = AzulInstitucional,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = numero.toString(),
            color = AzulTitulo,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = etiqueta,
            color = TextoSecundario,
            fontSize = 13.sp,
            maxLines = 1
        )
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
private fun FilaTituloHistorial(onVerHistorial: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "COMUNICACIONES RECIENTES",
            color = AzulTitulo,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onVerHistorial)
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Ver historial",
                color = AzulInstitucional,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = AzulInstitucional,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun TarjetaComunicacionReciente(comunicacion: ComunicacionResumen) {
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
                imageVector = if (comunicacion.enviada) Icons.AutoMirrored.Filled.Send else Icons.Outlined.Create,
                contentDescription = null,
                tint = AzulTextoInfo,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.size(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comunicacion.titulo,
                color = TextoPrincipal,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = comunicacion.tipo,
                color = TextoSecundario,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = comunicacion.fecha,
                color = TextoSecundario,
                fontSize = 13.sp
            )
            Text(
                text = comunicacion.destinatarios,
                color = TextoSecundario,
                fontSize = 13.sp
            )
        }

        if (comunicacion.enviada) {
            EtiquetaEnviada()
        }
    }
}

@Composable
private fun EtiquetaEnviada() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(VerdeFondoEstado)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .semantics { contentDescription = "Enviada" }
    ) {
        Text(
            text = "Enviada",
            color = VerdeTextoEstado,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
