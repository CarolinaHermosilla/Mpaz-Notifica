package com.example.mpaznotifica.ui.screens.comunicacion

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mpaznotifica.ui.components.EncabezadoApp
import com.example.mpaznotifica.ui.theme.AzulClaroInfo
import com.example.mpaznotifica.ui.theme.AzulInstitucional
import com.example.mpaznotifica.ui.theme.AzulTextoInfo
import com.example.mpaznotifica.ui.theme.AzulTitulo
import com.example.mpaznotifica.ui.theme.Blanco
import com.example.mpaznotifica.ui.theme.BordeCampo
import com.example.mpaznotifica.ui.theme.FondoApp
import com.example.mpaznotifica.ui.theme.TextoPrincipal
import com.example.mpaznotifica.ui.theme.TextoSecundario

@Composable
fun PantallaComunicacion(
    usuarioId: Int,
    onVolver: () -> Unit,
    onContinuar: () -> Unit,
    viewModel: ComunicacionViewModel = viewModel()
) {
    val estado = viewModel.uiState
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(usuarioId) {
        viewModel.cargarUsuario(usuarioId)
    }

    val lanzadorCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exitoso ->
        viewModel.onFotoTomada(exitoso)
    }

    val lanzadorPermisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        viewModel.onPermisoCamaraSolicitado()
        if (concedido) {
            viewModel.onPermisoCamaraConcedido()
            viewModel.uriFotoPendiente?.let { uri ->
                lanzadorCamara.launch(uri)
            }
        } else {
            viewModel.onPermisoCamaraRechazado()
        }
    }

    val lanzadorGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        viewModel.onImagenDeGaleriaElegida(uri)
    }

    LaunchedEffect(estado.pedirPermisoCamara) {
        if (estado.pedirPermisoCamara) {
            lanzadorPermisoCamara.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(estado.aviso) {
        estado.aviso?.let { texto ->
            snackbarHostState.showSnackbar(texto)
            viewModel.onAvisoMostrado()
        }
    }

    fun abrirCamara() {
        val concedido = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (concedido) {
            viewModel.onPermisoCamaraConcedido()
            viewModel.uriFotoPendiente?.let { uri ->
                lanzadorCamara.launch(uri)
            }
        } else {
            viewModel.pedirPermisoDeCamara()
        }
    }

    Scaffold(
        containerColor = FondoApp,
        topBar = { EncabezadoApp(titulo = "Nueva comunicación", onVolver = onVolver) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 18.dp, bottom = 28.dp)
            ) {
                EtiquetaSeccion("TIPO DE COMUNICACIÓN")
                Spacer(Modifier.height(10.dp))
                SelectorTipo(
                    tipoSeleccionado = estado.tipoSeleccionado,
                    onTipoSeleccionado = viewModel::onTipoSeleccionado
                )

                Spacer(Modifier.height(18.dp))

                CajaInformativaDestino(texto = estado.textoAlcance)

                Spacer(Modifier.height(20.dp))

                CampoTexto(
                    valor = estado.titulo,
                    onValorCambia = viewModel::onTituloCambia,
                    etiqueta = "Título de la comunicación",
                    error = estado.errorTitulo,
                    lineasMaximas = 1
                )

                Spacer(Modifier.height(16.dp))

                CampoTexto(
                    valor = estado.mensaje,
                    onValorCambia = viewModel::onMensajeCambia,
                    etiqueta = "Escribe el mensaje que recibirán los apoderados",
                    error = estado.errorMensaje,
                    lineasMaximas = 6
                )

                Spacer(Modifier.height(20.dp))

                EtiquetaSeccion("EVIDENCIAS (OPCIONAL)")
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Puedes adjuntar una o varias imágenes: una foto tomada con la " +
                        "cámara o una imagen que ya esté en el teléfono.",
                    color = TextoSecundario,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { abrirCamara() },
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AzulInstitucional
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .border(1.dp, BordeCampo, RoundedCornerShape(30.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(text = "Cámara", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = {
                            lanzadorGaleria.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AzulInstitucional
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .border(1.dp, BordeCampo, RoundedCornerShape(30.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(text = "Galería", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                if (estado.adjuntos.isNotEmpty()) {
                    Spacer(Modifier.height(14.dp))
                    estado.adjuntos.forEach { adjunto ->
                        FilaAdjunto(
                            adjunto = adjunto,
                            onQuitar = { viewModel.quitarAdjunto(adjunto) }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                Spacer(Modifier.height(22.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.abrirVistaPrevia() },
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AzulInstitucional
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .border(1.dp, BordeCampo, RoundedCornerShape(30.dp))
                    ) {
                        Text(
                            text = "Vista previa",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { viewModel.continuar(usuarioId, onContinuar) },
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AzulInstitucional,
                            contentColor = Blanco
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Text(
                            text = "Continuar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (estado.mostrarVistaPrevia) {
        HojaVistaPrevia(
            estado = estado,
            onCerrar = { viewModel.cerrarVistaPrevia() },
            onContinuar = { viewModel.continuar(usuarioId, onContinuar) }
        )
    }
}

@Composable
private fun EtiquetaSeccion(texto: String) {
    Text(
        text = texto,
        color = AzulTitulo,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun SelectorTipo(
    tipoSeleccionado: String,
    onTipoSeleccionado: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ComunicacionUiState.TIPOS.forEach { tipo ->
            val seleccionado = tipo == tipoSeleccionado
            val forma = RoundedCornerShape(12.dp)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(forma)
                    .background(if (seleccionado) AzulClaroInfo else Blanco)
                    .border(
                        width = if (seleccionado) 2.dp else 1.dp,
                        color = if (seleccionado) AzulInstitucional else BordeCampo,
                        shape = forma
                    )
                    .clickable { onTipoSeleccionado(tipo) }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = iconoDeTipo(tipo),
                    contentDescription = null,
                    tint = AzulTextoInfo,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.size(12.dp))
                Text(
                    text = tipo,
                    color = if (seleccionado) AzulTitulo else TextoPrincipal,
                    fontSize = 16.sp,
                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
                if (seleccionado) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Seleccionado",
                        tint = AzulInstitucional,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun iconoDeTipo(tipo: String): ImageVector {
    return when (tipo) {
        "Circular" -> Icons.Filled.Home
        "Citación" -> Icons.Filled.Call
        else -> Icons.Filled.Share
    }
}

@Composable
private fun CajaInformativaDestino(texto: String) {
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
private fun CampoTexto(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    error: String?,
    lineasMaximas: Int
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        placeholder = { Text(text = etiqueta, color = TextoSecundario, fontSize = 15.sp) },
        singleLine = lineasMaximas == 1,
        maxLines = lineasMaximas,
        isError = error != null,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Blanco,
            unfocusedContainerColor = Blanco,
            errorContainerColor = Blanco,
            focusedBorderColor = AzulInstitucional,
            unfocusedBorderColor = BordeCampo,
            focusedTextColor = TextoPrincipal,
            unfocusedTextColor = TextoPrincipal
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(if (lineasMaximas == 1) 56.dp else 150.dp)
    )

    AnimatedVisibility(
        visible = error != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.size(6.dp))
            Text(
                text = error.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun FilaAdjunto(
    adjunto: Adjunto,
    onQuitar: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Blanco)
            .padding(10.dp)
    ) {
        AsyncImage(
            model = adjunto.uri,
            contentDescription = "Imagen adjunta: ${adjunto.nombre}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AzulClaroInfo)
        )

        Spacer(Modifier.size(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = adjunto.nombre,
                color = TextoPrincipal,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${adjunto.origen.etiqueta} · ${adjunto.fechaHora}",
                color = TextoSecundario,
                fontSize = 13.sp
            )
        }

        IconButton(onClick = onQuitar) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Quitar imagen",
                tint = TextoSecundario
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HojaVistaPrevia(
    estado: ComunicacionUiState,
    onCerrar: () -> Unit,
    onContinuar: () -> Unit
) {
    val estadoHoja = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onCerrar,
        sheetState = estadoHoja,
        containerColor = Blanco
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "VISTA PREVIA",
                color = AzulTitulo,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(FondoApp)
                    .padding(16.dp)
            ) {
                DatoVistaPrevia(etiqueta = "Tipo", valor = estado.tipoSeleccionado)
                Spacer(Modifier.height(10.dp))
                DatoVistaPrevia(
                    etiqueta = "Título",
                    valor = estado.titulo.ifBlank { "(sin título)" }
                )
                Spacer(Modifier.height(10.dp))
                DatoVistaPrevia(
                    etiqueta = "Mensaje",
                    valor = estado.mensaje.ifBlank { "(sin mensaje)" }
                )
                Spacer(Modifier.height(10.dp))
                DatoVistaPrevia(
                    etiqueta = "Destinatarios",
                    valor = estado.textoDestinatarios
                )
                Spacer(Modifier.height(10.dp))
                DatoVistaPrevia(
                    etiqueta = "Adjuntos",
                    valor = if (estado.adjuntos.isEmpty()) {
                        "Sin adjuntos"
                    } else {
                        estado.adjuntos.joinToString(", ") { it.nombre }
                    }
                )
                Spacer(Modifier.height(10.dp))
                DatoVistaPrevia(etiqueta = "Firma", valor = estado.firma)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = AzulTextoInfo,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = "Esta es una vista previa: todavía no se ha enviado nada.",
                    color = TextoSecundario,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onCerrar,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulInstitucional,
                    contentColor = Blanco
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(text = "Volver a editar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onContinuar,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulInstitucional),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(1.dp, BordeCampo, RoundedCornerShape(30.dp))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = "Elegir destinatarios",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DatoVistaPrevia(etiqueta: String, valor: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = etiqueta,
            color = TextoSecundario,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = valor,
            color = TextoPrincipal,
            fontSize = 15.sp
        )
    }
}
