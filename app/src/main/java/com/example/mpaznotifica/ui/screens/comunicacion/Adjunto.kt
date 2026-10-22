package com.example.mpaznotifica.ui.screens.comunicacion

import android.net.Uri

data class Adjunto(
    val uri: Uri,
    val nombre: String,
    val origen: OrigenAdjunto,
    val fechaHora: String
)

enum class OrigenAdjunto(val etiqueta: String) {
    CAMARA("Cámara"),
    GALERIA("Galería")
}
