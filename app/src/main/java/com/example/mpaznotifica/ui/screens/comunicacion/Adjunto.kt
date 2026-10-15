package com.example.mpaznotifica.ui.screens.comunicacion

import android.net.Uri

/**
 * Una imagen adjunta a la comunicación (evidencia).
 *
 * @param uri        Dirección del archivo de imagen dentro del teléfono.
 * @param nombre     Nombre del archivo.
 * @param origen     De dónde salió la imagen: cámara o galería.
 * @param fechaHora  Cuándo se adjuntó, en texto. Se guarda como texto para
 *                   mostrarlo tal cual en la vista previa y en el historial.
 */
data class Adjunto(
    val uri: Uri,
    val nombre: String,
    val origen: OrigenAdjunto,
    val fechaHora: String
)

/** Origen de una imagen adjunta. */
enum class OrigenAdjunto(val etiqueta: String) {
    CAMARA("Cámara"),
    GALERIA("Galería")
}
