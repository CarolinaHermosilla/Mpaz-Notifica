package com.example.mpaznotifica.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mpaznotifica.ui.screens.comunicacion.PantallaComunicacion
import com.example.mpaznotifica.ui.screens.destinatarios.PantallaDestinatarios
import com.example.mpaznotifica.ui.screens.grupos.PantallaGrupos
import com.example.mpaznotifica.ui.screens.grupos.PantallaNuevoGrupo
import com.example.mpaznotifica.ui.screens.historial.PantallaHistorial
import com.example.mpaznotifica.ui.screens.inicio.PantallaInicio
import com.example.mpaznotifica.ui.screens.login.PantallaLogin

/**
 * Nombres de las rutas de navegación de la aplicación.
 */
object Rutas {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val COMUNICACION = "comunicacion"
    const val DESTINATARIOS = "destinatarios"
    const val GRUPOS = "grupos"
    const val NUEVO_GRUPO = "nuevo_grupo"
    const val HISTORIAL = "historial"
}

/**
 * Navegación general de la aplicación.
 *
 * Las pantallas están repartidas entre los dos integrantes del equipo:
 *  - Login, Destinatarios, Grupos y Nuevo grupo: Carolina.
 *  - Inicio, Comunicación e Historial: Pedro.
 *
 * Las rutas de las tres pantallas de Pedro ya están creadas, pero su contenido
 * está pendiente: las desarrolla él en sus propios archivos.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        // ---------- Pantallas de Carolina ----------
        composable(Rutas.LOGIN) {
            PantallaLogin(
                onIngresar = {
                    navController.navigate(Rutas.INICIO) {
                        // Se saca el Login de la pila para que "atrás" no vuelva a él
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.GRUPOS) {
            PantallaGrupos(
                onVolver = { navController.popBackStack() },
                onNuevoGrupo = { navController.navigate(Rutas.NUEVO_GRUPO) }
            )
        }

        composable(Rutas.NUEVO_GRUPO) {
            PantallaNuevoGrupo(
                onVolver = { navController.popBackStack() },
                onGrupoGuardado = { navController.popBackStack() }
            )
        }

        composable(Rutas.DESTINATARIOS) {
            PantallaDestinatarios(
                onVolver = { navController.popBackStack() },
                onComunicacionEnviada = {
                    navController.navigate(Rutas.HISTORIAL) {
                        popUpTo(Rutas.INICIO)
                    }
                }
            )
        }

        // ---------- Pantallas de Pedro (pendientes) ----------
        composable(Rutas.INICIO) {
            PantallaInicio(
                onNuevaComunicacion = { navController.navigate(Rutas.COMUNICACION) },
                onVerGrupos = { navController.navigate(Rutas.GRUPOS) },
                onVerHistorial = { navController.navigate(Rutas.HISTORIAL) },
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.INICIO) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.COMUNICACION) {
            PantallaComunicacion(
                onVolver = { navController.popBackStack() },
                onContinuar = { navController.navigate(Rutas.DESTINATARIOS) }
            )
        }

        composable(Rutas.HISTORIAL) {
            PantallaHistorial(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
