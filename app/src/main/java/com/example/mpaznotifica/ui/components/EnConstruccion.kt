package com.example.mpaznotifica.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mpaznotifica.ui.theme.FondoApp
import com.example.mpaznotifica.ui.theme.TextoSecundario

/**
 * Pantalla temporal que se muestra mientras una vista todavía no está desarrollada.
 * Sirve para que el proyecto compile y se pueda navegar entre todas las pantallas.
 */
@Composable
fun EnConstruccion(titulo: String, detalle: String) {
    Scaffold(
        containerColor = FondoApp,
        topBar = { EncabezadoApp(titulo = titulo) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = titulo,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = detalle,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
