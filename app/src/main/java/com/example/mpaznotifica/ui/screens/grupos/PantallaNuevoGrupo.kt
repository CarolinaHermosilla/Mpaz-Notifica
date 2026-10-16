package com.example.mpaznotifica.ui.screens.grupos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpaznotifica.data.local.entity.ApoderadoEntity
import com.example.mpaznotifica.ui.components.EncabezadoApp
import com.example.mpaznotifica.ui.theme.AzulClaroInfo
import com.example.mpaznotifica.ui.theme.AzulInstitucional
import com.example.mpaznotifica.ui.theme.AzulTextoInfo
import com.example.mpaznotifica.ui.theme.Blanco
import com.example.mpaznotifica.ui.theme.BordeCampo
import com.example.mpaznotifica.ui.theme.FondoApp
import com.example.mpaznotifica.ui.theme.TextoPrincipal
import com.example.mpaznotifica.ui.theme.TextoSecundario

/**
 * PANTALLA 5b - CREACIÓN DE GRUPOS  (Carolina)
 *
 * Replica la captura "5. Creación de grupos" del informe (página 3): la lista de
 * apoderados del curso con su casilla para marcarlos, el botón "Guardar grupo" y,
 * abajo de todo, los grupos que ya existen.
 *
 * El nombre y la descripción van arriba de la lista: en la captura esa parte queda
 * fuera de la pantalla (la lista aparece desplazada), así que se armó con los
 * mismos colores y campos que el resto de la aplicación.
 */
@Composable
fun PantallaNuevoGrupo(
    onVolver: () -> Unit,
    onGrupoGuardado: () -> Unit,
    viewModel: NuevoGrupoViewModel = viewModel()
) {
    val estado = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.cargar()
    }

    Scaffold(
        containerColor = FondoApp,
        topBar = {
            EncabezadoApp(
                titulo = "Grupos de apoderados",
                onVolver = onVolver
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
            Spacer(Modifier.height(18.dp))

            CampoTexto(
                valor = estado.nombre,
                onValorCambia = viewModel::onNombreCambia,
                etiqueta = "Nombre del grupo",
                error = estado.errorNombre,
                lineasMaximas = 1
            )

            Spacer(Modifier.height(14.dp))

            CampoTexto(
                valor = estado.descripcion,
                onValorCambia = viewModel::onDescripcionCambia,
                etiqueta = "Descripción (opcional)",
                error = null,
                lineasMaximas = 3
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Marca a los apoderados que formarán el grupo",
                color = TextoSecundario,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(6.dp))

            if (estado.cargando) {
                Spacer(Modifier.height(16.dp))
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
                        onMarcado = { marcado ->
                            viewModel.onApoderadoMarcado(apoderado.id, marcado)
                        }
                    )
                }
            }

            AnimatedVisibility(
                visible = estado.errorIntegrantes != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = estado.errorIntegrantes.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = if (estado.cantidadSeleccionados == 0) {
                    "Sin apoderados marcados"
                } else {
                    "Se marcarán ${estado.cantidadSeleccionados} " +
                        if (estado.cantidadSeleccionados == 1) "apoderado" else "apoderados"
                },
                color = AzulTextoInfo,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { viewModel.guardar(onGrupoGuardado) },
                enabled = !estado.guardando,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulInstitucional,
                    contentColor = Blanco
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (estado.guardando) {
                    CircularProgressIndicator(
                        color = Blanco,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = "Guardar grupo",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            HorizontalDivider(color = BordeCampo)

            Spacer(Modifier.height(18.dp))

            Text(
                text = "GRUPOS DEL CURSO (${estado.gruposExistentes.size})",
                color = TextoSecundario,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(12.dp))

            estado.gruposExistentes.forEach { grupo ->
                TarjetaGrupo(grupo = grupo)
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Fila de un apoderado: su casilla, su círculo con la inicial y sus datos. */
@Composable
private fun FilaApoderado(
    apoderado: ApoderadoEntity,
    marcado: Boolean,
    onMarcado: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            // Toda la fila se puede tocar, no solo la casilla: es más cómodo en el teléfono.
            .clickable { onMarcado(!marcado) }
            .padding(vertical = 6.dp)
    ) {
        Checkbox(
            checked = marcado,
            onCheckedChange = onMarcado,
            colors = CheckboxDefaults.colors(
                checkedColor = AzulInstitucional,
                uncheckedColor = TextoSecundario,
                checkmarkColor = Blanco
            )
        )

        Spacer(Modifier.size(8.dp))

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(AzulClaroInfo),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = apoderado.nombre.take(1).uppercase(),
                color = AzulInstitucional,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.size(12.dp))

        Column {
            Text(
                text = apoderado.nombre,
                color = TextoPrincipal,
                fontSize = 16.sp
            )
            Text(
                text = apoderado.estudiante,
                color = TextoSecundario,
                fontSize = 13.sp
            )
        }
    }

    HorizontalDivider(color = BordeCampo.copy(alpha = 0.5f))
}

/** Campo de texto con su mensaje de error debajo. */
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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
        modifier = Modifier.fillMaxWidth()
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
