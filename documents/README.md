# Diagramas de arquitectura

Diagramas del proyecto **MPAZ Notifica** para la Experiencia 3 de DSY1105 (Duoc UC).
Acompañan el código que está en la rama `main`.

## 01 · Arquitectura de microservicios

`01-arquitectura-microservicios.png`

Muestra el recorrido completo de una comunicación:

- **Aplicación Android** (Kotlin, Jetpack Compose, MVVM) que guarda la sesión y la caché en Room.
- Conexión por **HTTPS con Retrofit** y JSON contra los tres servicios.
- **Render (nube)**: `ms-usuarios` en el puerto 8081 (cuentas, apoderados y grupos),
  `ms-mensajeria` en el 8082 (borrador y envío) y `ms-historial` en el 8083 (registro de envíos).
- La llamada interna de mensajería a historial cuando una comunicación se envía.
- **Supabase (nube)**: PostgreSQL 17 con un esquema por servicio (`usuarios`, `mensajeria`,
  `historial`), conectado por JPA e Hibernate.

## 02 · Arquitectura del proyecto Android

`02-arquitectura-proyecto-android.png`

Muestra el patrón **MVVM por capas**:

- **Vista**: las seis pantallas en Compose, que solo dibujan y avisan lo que hace el usuario.
- **ViewModel**: estado de la pantalla (`mutableStateOf` y `UiState`), validaciones, corrutinas y
  decisiones según el rol.
- **Modelo**: repositorios, con la parte remota (Retrofit hacia los microservicios) y la local
  (Room sobre SQLite, donde se valida el login como pide la rúbrica).
- El árbol de paquetes del proyecto y la regla de que una pantalla nunca llama directamente a la
  red ni a la base de datos.

## Notas

- Los dos diagramas son imágenes de 3200 px de ancho, pensadas para el informe y para la defensa.
- Se generaron con maquetas HTML y se exportaron a PNG; los archivos fuente no forman parte del
  repositorio.
