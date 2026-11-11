package com.example.mpaznotifica.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpaznotifica.data.repositorio.UsuarioRepositorio
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
fun PantallaLogin(
    onIngresar: (Int) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val estado = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    // Aviso de conexión (por ejemplo, si el servidor no responde).
    LaunchedEffect(estado.aviso) {
        estado.aviso?.let { texto ->
            snackbarHostState.showSnackbar(texto)
            viewModel.onAvisoMostrado()
        }
    }

    Scaffold(
        containerColor = FondoApp,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoApp)
                .padding(bottom = relleno.calculateBottomPadding())
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            BannerLogin()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 22.dp, bottom = 28.dp)
            ) {
                Text(
                    text = "Inicia sesión",
                    color = AzulTitulo,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Ingresa con tu correo institucional y tu contraseña.",
                    color = TextoSecundario,
                    fontSize = 15.sp
                )

                Spacer(Modifier.height(22.dp))

                CampoLogin(
                    valor = estado.correo,
                    onValorCambia = viewModel::onCorreoCambia,
                    etiqueta = "Correo institucional",
                    icono = Icons.Filled.Email,
                    error = estado.errorCorreo,
                    tipoTeclado = KeyboardType.Email,
                    esContrasena = false
                )

                Spacer(Modifier.height(14.dp))

                CampoLogin(
                    valor = estado.contrasena,
                    onValorCambia = viewModel::onContrasenaCambia,
                    etiqueta = "Contraseña",
                    icono = Icons.Filled.Lock,
                    error = estado.errorContrasena,
                    tipoTeclado = KeyboardType.Password,
                    esContrasena = true
                )

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = { viewModel.ingresar(onIngresar) },
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
                    } else {
                        Text(
                            text = "Ingresar",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                TarjetaCuentasDePrueba()

                Spacer(Modifier.height(14.dp))

                CajaAviso(
                    texto = "Las cuentas se descargan del servidor de la escuela y se " +
                        "validan en el teléfono. Los datos de la aplicación son ficticios."
                )
            }
        }
    }
}

@Composable
private fun BannerLogin() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AzulInstitucional)
            .statusBarsPadding()
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        Text(
            text = "MPAZ Notifica",
            color = Blanco,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Escuela Escritora Marcela Paz",
            color = Blanco,
            fontSize = 15.sp
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = "Comunicaciones entre la escuela y los apoderados",
            color = Blanco,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun CampoLogin(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    tipoTeclado: KeyboardType,
    esContrasena: Boolean
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        placeholder = { Text(text = etiqueta, color = TextoSecundario, fontSize = 15.sp) },
        leadingIcon = { Icon(imageVector = icono, contentDescription = null, tint = TextoSecundario) },
        singleLine = true,
        isError = error != null,
        visualTransformation = if (esContrasena) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
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

@Composable
private fun TarjetaCuentasDePrueba() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Blanco)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = AzulInstitucional,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.size(12.dp))
        Column {
            Text(
                text = "CUENTAS DE PRUEBA",
                color = AzulTitulo,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = UsuarioRepositorio.PERFIL_PROFESOR,
                color = AzulTitulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Correo: ${UsuarioRepositorio.CORREO_PROFESOR}",
                color = TextoPrincipal,
                fontSize = 15.sp
            )
            Text(
                text = "Contraseña: ${UsuarioRepositorio.CONTRASENA_PRUEBA}",
                color = TextoPrincipal,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(10.dp))
            Text(
                text = UsuarioRepositorio.PERFIL_INSPECTORIA,
                color = AzulTitulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Correo: ${UsuarioRepositorio.CORREO_INSPECTOR}",
                color = TextoPrincipal,
                fontSize = 15.sp
            )
            Text(
                text = "Contraseña: ${UsuarioRepositorio.CONTRASENA_PRUEBA}",
                color = TextoPrincipal,
                fontSize = 15.sp
            )
        }
    }
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
