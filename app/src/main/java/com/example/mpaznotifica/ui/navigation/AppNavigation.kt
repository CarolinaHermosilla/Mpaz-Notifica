package com.example.mpaznotifica.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mpaznotifica.ui.screens.comunicacion.PantallaComunicacion
import com.example.mpaznotifica.ui.screens.destinatarios.PantallaDestinatarios
import com.example.mpaznotifica.ui.screens.grupos.PantallaGrupos
import com.example.mpaznotifica.ui.screens.grupos.PantallaNuevoGrupo
import com.example.mpaznotifica.ui.screens.historial.PantallaHistorial
import com.example.mpaznotifica.ui.screens.inicio.PantallaInicio
import com.example.mpaznotifica.ui.screens.login.PantallaLogin

/**
 * Nombres de las rutas de navegación de la aplicación.
 *
 * La ruta de Inicio lleva el id del usuario que inició sesión: así la pantalla
 * sabe qué cuenta entró (nombre, rol y curso) sin que el usuario elija nada.
 */
object Rutas {
    const val LOGIN = "login"

    /** Ruta con argumento: se arma con [inicio]. */
    const val INICIO = "inicio/{usuarioId}"
    const val ARG_USUARIO_ID = "usuarioId"

    const val COMUNICACION = "comunicacion/{usuarioId}"

    /** Ruta de Comunicación para un usuario concreto (se usa para la firma de la vista previa). */
    fun comunicacion(usuarioId: Int) = "comunicacion/$usuarioId"

    const val DESTINATARIOS = "destinatarios/{usuarioId}"

    /** Ruta de Destinatarios para un usuario concreto (necesita su rol y su curso). */
    fun destinatarios(usuarioId: Int) = "destinatarios/$usuarioId"

    const val GRUPOS = "grupos"
    const val NUEVO_GRUPO = "nuevo_grupo"
    const val HISTORIAL = "historial"

    /** Clave con la que la pantalla de creación deja el aviso "Grupo guardado". */
    const val AVISO_GRUPO_GUARDADO = "aviso_grupo_guardado"

    /** Ruta de Inicio para un usuario concreto. */
    fun inicio(usuarioId: Int) = "inicio/$usuarioId"
}

/**
 * Navegación general de la aplicación.
 *
 * Las pantallas están repartidas entre los dos integrantes del equipo:
 *  - Login, Destinatarios, Grupos y Nuevo grupo: Carolina.
 *  - Inicio, Comunicación e Historial: Pedro.
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
                onIngresar = { usuarioId ->
                    navController.navigate(Rutas.inicio(usuarioId)) {
                        // Se saca el Login de la pila para que "atrás" no vuelva a él
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.GRUPOS) { entrada ->
            // Aviso que deja la pantalla de creación al guardar un grupo.
            val aviso = entrada.savedStateHandle.get<String>(Rutas.AVISO_GRUPO_GUARDADO)

            PantallaGrupos(
                onVolver = { navController.popBackStack() },
                onNuevoGrupo = { navController.navigate(Rutas.NUEVO_GRUPO) },
                aviso = aviso,
                onAvisoMostrado = {
                    entrada.savedStateHandle.remove<String>(Rutas.AVISO_GRUPO_GUARDADO)
                }
            )
        }

        composable(Rutas.NUEVO_GRUPO) {
            PantallaNuevoGrupo(
                onVolver = { navController.popBackStack() },
                onGrupoGuardado = {
                    // Se deja el aviso en la pantalla anterior y se vuelve al listado.
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(Rutas.AVISO_GRUPO_GUARDADO, "Grupo guardado")
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Rutas.DESTINATARIOS,
            arguments = listOf(navArgument(Rutas.ARG_USUARIO_ID) { type = NavType.IntType })
        ) { entrada ->
            val usuarioId = entrada.arguments?.getInt(Rutas.ARG_USUARIO_ID) ?: 0

            PantallaDestinatarios(
                usuarioId = usuarioId,
                onVolver = { navController.popBackStack() },
                onComunicacionEnviada = {
                    navController.navigate(Rutas.HISTORIAL) {
                        popUpTo(Rutas.INICIO)
                    }
                }
            )
        }

        // ---------- Pantallas de Pedro ----------
        composable(
            route = Rutas.INICIO,
            arguments = listOf(navArgument(Rutas.ARG_USUARIO_ID) { type = NavType.IntType })
        ) { entrada ->
            val usuarioId = entrada.arguments?.getInt(Rutas.ARG_USUARIO_ID) ?: 0

            PantallaInicio(
                usuarioId = usuarioId,
                onNuevaComunicacion = { navController.navigate(Rutas.comunicacion(usuarioId)) },
                onVerGrupos = { navController.navigate(Rutas.GRUPOS) },
                onVerHistorial = { navController.navigate(Rutas.HISTORIAL) },
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.INICIO) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Rutas.COMUNICACION,
            arguments = listOf(navArgument(Rutas.ARG_USUARIO_ID) { type = NavType.IntType })
        ) { entrada ->
            val usuarioId = entrada.arguments?.getInt(Rutas.ARG_USUARIO_ID) ?: 0

            PantallaComunicacion(
                usuarioId = usuarioId,
                onVolver = { navController.popBackStack() },
                onContinuar = { navController.navigate(Rutas.destinatarios(usuarioId)) }
            )
        }

        composable(Rutas.HISTORIAL) {
            PantallaHistorial(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
