package com.example.mpaznotifica.ui.screens.grupos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpaznotifica.data.model.GrupoConIntegrantes
import com.example.mpaznotifica.ui.components.EncabezadoApp
import com.example.mpaznotifica.ui.theme.AzulClaroInfo
import com.example.mpaznotifica.ui.theme.AzulInstitucional
import com.example.mpaznotifica.ui.theme.AzulTextoInfo
import com.example.mpaznotifica.ui.theme.AzulTitulo
import com.example.mpaznotifica.ui.theme.Blanco
import com.example.mpaznotifica.ui.theme.FondoApp
import com.example.mpaznotifica.ui.theme.TextoPrincipal
import com.example.mpaznotifica.ui.theme.TextoSecundario

/**
 * PANTALLA 5a - GRUPOS DE APODERADOS (listado)  (Carolina)
 *
 * Replica la captura "5. Grupos guardados" del informe (página 4): la caja
 * informativa, el contador "GRUPOS DEL CURSO" y una tarjeta por grupo, con su
 * avatar, su cantidad de integrantes, su descripción y los nombres de sus
 * integrantes. El botón "+" de la barra superior lleva a crear un grupo.
 *
 * Al tocar una tarjeta se abre una hoja inferior con el detalle del grupo y la
 * opción de eliminarlo (esta parte no está en el mockup: se acordó con el equipo).
 */
@Composable
fun PantallaGrupos(
    onVolver: () -> Unit,
    onNuevoGrupo: () -> Unit,
    viewModel: GruposViewModel = viewModel()
) {
    val estado = viewModel.uiState

    // Grupo que se está mirando en el detalle y grupo que se va a eliminar.
    var grupoEnDetalle by remember { mutableStateOf<GrupoConIntegrantes?>(null) }
    var grupoAEliminar by remember { mutableStateOf<GrupoConIntegrantes?>(null) }

    // Se vuelve a cargar al entrar a la pantalla, para ver los grupos recién creados.
    LaunchedEffect(Unit) {
        viewModel.cargarGrupos()
    }

    Scaffold(
        containerColor = FondoApp,
        topBar = {
            EncabezadoApp(
                titulo = "Grupos de apoderados",
                onVolver = onVolver,
                accion = {
                    IconButton(onClick = onNuevoGrupo) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Crear grupo",
                            tint = Blanco
                        )
                    }
                }
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

            CajaInformativa()

            Spacer(Modifier.height(20.dp))

            Text(
                text = "GRUPOS DEL CURSO (${estado.grupos.size})",
                color = TextoSecundario,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(12.dp))

            if (estado.cargando) {
                Spacer(Modifier.height(20.dp))
            } else if (estado.grupos.isEmpty()) {
                Text(
                    text = "Todavía no hay grupos del curso. Crea el primero con el botón +.",
                    color = TextoSecundario,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                )
            } else {
                estado.grupos.forEach { grupo ->
                    TarjetaGrupo(
                        grupo = grupo,
                        onClick = { grupoEnDetalle = grupo }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // ---------- Detalle del grupo ----------
    grupoEnDetalle?.let { grupo ->
        HojaDetalleGrupo(
            grupo = grupo,
            onCerrar = { grupoEnDetalle = null },
            onEliminar = { grupoAEliminar = grupo }
        )
    }

    // ---------- Confirmación antes de eliminar ----------
    grupoAEliminar?.let { grupo ->
        AlertDialog(
            onDismissRequest = { grupoAEliminar = null },
            title = { Text(text = "¿Eliminar el grupo?") },
            text = {
                Text(
                    text = "El grupo \"${grupo.grupo.nombre}\" se va a eliminar y no se " +
                        "puede deshacer. Los apoderados del curso se mantienen."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarGrupo(grupo.grupo.id)
                        grupoAEliminar = null
                        grupoEnDetalle = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = "Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { grupoAEliminar = null }) {
                    Text(text = "Cancelar")
                }
            }
        )
    }
}

/** Caja celeste que explica para qué sirve un grupo. */
@Composable
private fun CajaInformativa() {
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
            text = "Un grupo sirve para enviar la misma comunicación a varios " +
                "apoderados sin seleccionarlos uno por uno. Por ejemplo, los " +
                "apoderados de los estudiantes de un taller.",
            color = AzulTextoInfo,
            fontSize = 15.sp
        )
    }
}

/** Tarjeta blanca con los datos de un grupo. Al tocarla se abre su detalle. */
@Composable
private fun TarjetaGrupo(
    grupo: GrupoConIntegrantes,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Blanco)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(AzulClaroInfo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = AzulInstitucional,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.size(12.dp))

            Column {
                Text(
                    text = grupo.grupo.nombre,
                    color = TextoPrincipal,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    Text(
                        text = "${grupo.cantidadDeIntegrantes} " +
                            if (grupo.cantidadDeIntegrantes == 1) "integrante" else "integrantes",
                        color = AzulTitulo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (grupo.grupo.creadoPorTaller) {
                        Text(
                            text = " · creado por el taller",
                            color = TextoSecundario,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        if (grupo.grupo.descripcion.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = grupo.grupo.descripcion,
                color = TextoSecundario,
                fontSize = 15.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        grupo.integrantes.forEach { integrante ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = AzulInstitucional,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = integrante.nombre,
                    color = TextoPrincipal,
                    fontSize = 15.sp
                )
            }
        }
    }
}

/** Hoja inferior con el detalle del grupo y la opción de eliminarlo. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HojaDetalleGrupo(
    grupo: GrupoConIntegrantes,
    onCerrar: () -> Unit,
    onEliminar: () -> Unit
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
                text = "DETALLE DEL GRUPO",
                color = AzulTitulo,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = grupo.grupo.nombre,
                color = TextoPrincipal,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Row {
                Text(
                    text = "${grupo.cantidadDeIntegrantes} " +
                        if (grupo.cantidadDeIntegrantes == 1) "integrante" else "integrantes",
                    color = AzulTitulo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (grupo.grupo.creadoPorTaller) {
                    Text(
                        text = " · creado por el taller",
                        color = TextoSecundario,
                        fontSize = 14.sp
                    )
                }
            }

            if (grupo.grupo.descripcion.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = grupo.grupo.descripcion,
                    color = TextoSecundario,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = "INTEGRANTES",
                color = TextoSecundario,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(10.dp))

            grupo.integrantes.forEach { integrante ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = AzulInstitucional,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    Column {
                        Text(
                            text = integrante.nombre,
                            color = TextoPrincipal,
                            fontSize = 15.sp
                        )
                        Text(
                            text = integrante.estudiante,
                            color = TextoSecundario,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

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
                Text(text = "Cerrar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onEliminar,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Eliminar grupo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
