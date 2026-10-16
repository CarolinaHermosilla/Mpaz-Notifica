package com.example.mpaznotifica.data.nativo

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

/**
 * Recurso nativo Vibración.
 *
 * Está armado siguiendo el ejemplo de la profesora (proyecto MyAppRecursosNativos):
 *  - En Android 12 o superior el vibrador se obtiene del VibratorManager.
 *  - En versiones anteriores se usa el servicio VIBRATOR_SERVICE (que está obsoleto).
 *  - En Android 8 o superior se usa VibrationEffect.createOneShot; en versiones
 *    anteriores, el método antiguo vibrate(milisegundos).
 *
 * El permiso android.permission.VIBRATE está declarado en el manifiesto.
 */
object Vibracion {

    fun vibrar(context: Context, milisegundos: Long = 500L) {
        val vibrador: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrador.vibrate(
                VibrationEffect.createOneShot(milisegundos, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrador.vibrate(milisegundos)
        }

        Log.d("Vibracion", "Vibrando $milisegundos ms")
    }
}
