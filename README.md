# MPAZ Notifica

Aplicación móvil Android para la **Escuela Escritora Marcela Paz** (San Bernardo). Su objetivo es apoyar
el envío y el registro de las comunicaciones que la escuela dirige a los apoderados —citaciones, avisos
institucionales, actividades, talleres y autorizaciones—, reemplazando parte del proceso en papel y
dejando un historial de lo enviado.

Proyecto académico de la asignatura **DSY1105 - Desarrollo de Aplicaciones Móviles** (Duoc UC).
Todos los datos que usa la aplicación son **ficticios**.

## Integrantes

- Carolina Hermosilla
- Pedro Toledo

## Qué se entrega en esta etapa

La aplicación funciona **conectada a un backend de microservicios en la nube**. Las cuentas ya no se
crean en el teléfono: se descargan del servidor. Los apoderados, los grupos, los borradores, los envíos
y el historial también viven en el servidor.

| Punto de la rúbrica | Estado |
|---|---|
| Dos roles funcionales con MVVM | Cumplido: Profesor/a jefe e Inspectoría General |
| Autenticación administrada con SQLite en ambos roles | Cumplido: las cuentas se descargan de `ms-usuarios` y el Login valida contra SQLite |
| Microservicios con Spring Boot | Cumplido: `ms-usuarios`, `ms-mensajeria` y `ms-historial` |
| Pruebas unitarias con JUnit en el backend | Cumplido en el repositorio del backend |
| Base de datos relacional | Cumplido: PostgreSQL en Supabase |
| Recursos nativos según el rol | Cumplido: cámara, galería y vibración |
| Conexión con Retrofit | Cumplido |
| Iniciar y cerrar sesión | Cumplido: la sesión se guarda y el cierre borra la sesión |
| Formularios validados | Cumplido en las seis pantallas |
| Navegación según el rol | Cumplido: la cuenta define el alcance de los envíos |
| Backend en la nube | Cumplido: los tres servicios desplegados en Render |
| APK firmada | Cumplido: `MPAZ-Notifica-release.apk` con la llave `mpaz-release.jks` |

## Backend

Los tres microservicios están desplegados en **Render** (plan gratuito) y comparten una base de datos
**PostgreSQL en Supabase**:

| Servicio | Dirección | Para qué sirve |
|---|---|---|
| `ms-usuarios` | https://mpaz-usuarios.onrender.com | Cuentas, apoderados y grupos |
| `ms-mensajeria` | https://mpaz-mensajeria.onrender.com | Borrador y envío de comunicaciones |
| `ms-historial` | https://mpaz-historial.onrender.com | Registro de los envíos |

Repositorio del backend: https://github.com/CarolinaHermosilla/Backend-MPaz-Notifica

Cada servicio responde en `/actuator/health`, que sirve para comprobar que está despierto.

> **Importante al probar:** el plan gratuito de Render duerme los servicios a los 15 minutos sin uso.
> El primer llamado puede tardar hasta 50 segundos en despertarlos. La aplicación espera 60 segundos y
> muestra un círculo de carga, así que no hay que asustarse si la primera pantalla tarda.

## Tecnologías

| Elemento | Uso en el proyecto |
|---|---|
| Kotlin 2.2.10 | Lenguaje |
| Jetpack Compose + Material 3 | Interfaz (BOM 2026.02.01) |
| MVVM | Cada pantalla tiene su `UiState` y su `ViewModel`; la lógica de validación está fuera de la interfaz |
| Retrofit 2.11 + OkHttp + Gson | Conexión con los tres microservicios |
| Room 2.8.4 (SQLite) + KSP | Base local: cuentas sincronizadas |
| SharedPreferences | Sesión guardada y cierre de sesión |
| Navigation Compose 2.8.0 | Navegación entre pantallas |
| Coil | Mostrar las imágenes adjuntas |
| Gradle 9.5 / AGP 9.3.3 | Compilación |

- `minSdk 23` (Android 6.0), `targetSdk 36`.
- Requiere **JDK 21**: Gradle lo descarga automáticamente la primera vez si no está instalado.

## Funcionalidades

Los seis componentes del requerimiento están implementados:

| # | Componente | Responsable | Qué hace |
|---|---|---|---|
| 1 | Login | Carolina | Correo y contraseña con validación por campo e íconos. El rol **no se elige**: es un atributo de la cuenta. Las cuentas se descargan del servidor y se validan en SQLite. Guarda la sesión al entrar. |
| 2 | Inicio | Pedro | Saludo, rol y curso de la cuenta que entró, aviso de sesión iniciada, contadores del servidor (borradores, enviadas y apoderados), accesos a nueva comunicación y grupos, comunicaciones recientes y cierre de sesión. |
| 3 | Comunicación | Pedro | Tipo de comunicación (circular, citación o material de apoyo), título y mensaje con validación, evidencias adjuntas con **cámara** o **galería**, vista previa firmada por quien inició sesión y creación del borrador en `ms-mensajeria`. |
| 4 | Destinatarios | Carolina | Alcance de la comunicación (todo el curso o elegir apoderados), carga de grupos guardados desde el servidor, lista de apoderados con su estudiante y taller, contador de destinatarios, selector de canal y envío con **animación de carga** y **vibración**. |
| 5 | Grupos | Carolina | Listado, detalle con opción de eliminar (con confirmación) y creación, todo contra el servidor. Valida nombre vacío, sin apoderados y nombre repetido; avisa al volver al listado. |
| 6 | Historial | Pedro | Comunicaciones enviadas de la más nueva a la más antigua, con su tipo, fecha, canal y cantidad de destinatarios. |

### Recursos nativos del dispositivo

La rúbrica pide al menos dos; el proyecto usa tres:

| Recurso | Dónde se usa | Cómo está implementado |
|---|---|---|
| **Cámara** | Comunicación (evidencias) | `ActivityResultContracts.TakePicture`, permiso `CAMERA` solicitado en uso y `FileProvider` para guardar la foto en la carpeta de la aplicación |
| **Galería** | Comunicación (evidencias) | `ActivityResultContracts.PickVisualMedia`; permisos `READ_MEDIA_IMAGES` y `READ_EXTERNAL_STORAGE` (hasta Android 12) declarados en el manifiesto |
| **Vibración** | Destinatarios (al enviar) | `VibratorManager` en Android 12 o superior y `VibrationEffect` desde Android 8; permiso `VIBRATE` |

## Flujo de la aplicación

```
Login ─► Inicio ─┬─► Nueva comunicación ─► Comunicación ─► Destinatarios ─► envío
                 │                                                          │
                 ├─► Grupos ─► Crear grupo                                  │
                 ├─► Historial ◄─────────────────────────────────────────────┘
                 └─► Cerrar sesión ─► Login
```

## Credenciales de prueba

Las cuentas vienen del servidor (`ms-usuarios`). Si el servidor no responde, se usan las que ya
quedaron guardadas en el teléfono:

| Cuenta | Correo | Contraseña | Rol |
|---|---|---|---|
| Andrea Muñoz | `profesor.jefe@ejemplo.cl` | `123456` | Profesor o profesora jefe de 5.º Básico B |
| Mario Gutierrez | `inspector.general@ejemplo.cl` | `123456` | Inspectoría General |
| Camila Rojas | `camila.rojas@ejemplo.cl` | `123456` | Profesor o profesora jefe de 5.º Básico B |

La cuenta de profesora jefe solo puede enviar a los apoderados de su curso; la de Inspectoría General
puede enviar a todo el colegio.

## Cómo ejecutar el proyecto

1. Clonar o descargar el repositorio.
2. Abrir la carpeta del proyecto con **Android Studio**.
3. Esperar a que Gradle sincronice las dependencias. La primera vez necesita conexión a internet
   (descarga Room, Retrofit, las bibliotecas de Compose y, si falta, el JDK 21).
4. Crear el archivo `local.properties` con la ruta del SDK de Android de tu computador
   (Android Studio lo genera solo; el archivo no se sube al repositorio).
5. Elegir un emulador o un teléfono con Android 6.0 (API 23) o superior y presionar **Run**.

### Recorrido para probar

1. Abrir el navegador y cargar `https://mpaz-usuarios.onrender.com/actuator/health` para despertar los
   servicios antes de empezar.
2. Entrar con la cuenta de la profesora jefe.
3. En Inicio, ver los contadores y las comunicaciones recientes del servidor.
4. Presionar **Nueva comunicación**: elegir el tipo, escribir título y mensaje, y opcionalmente adjuntar
   una foto con la cámara o una imagen de la galería.
5. Presionar **Continuar**: el borrador se crea en `ms-mensajeria` y se abre **Destinatarios**.
6. Cargar un grupo guardado (por ejemplo, Taller de Teatro) y ver cómo se marcan sus integrantes.
7. Presionar **Enviar comunicación**: aparece el círculo de carga, el teléfono vibra y se llega al **Historial**.
8. Probar **Grupos**: crear un grupo, verlo en el listado y eliminarlo.
9. Presionar **Cerrar sesión**: debe volver al Login y el botón "atrás" no debe volver a Inicio.
10. Entrar con la cuenta de Inspectoría General: el aviso de alcance debe decir "Todo el colegio".

> La vibración solo se puede comprobar en un teléfono real. En el emulador se puede revisar con
> `adb logcat | findstr Vibracion`, que muestra `Vibrando 500 ms`.
>
> Las peticiones al servidor se ven en Logcat con el filtro `okhttp.OkHttpClient` (`--> 200 OK`, `<-- 400`).

## Cómo compilar el APK de entrega

El APK de entrega va firmado con la llave `mpaz-release.jks` (no se sube al repositorio). Los datos de la
llave están en `keystore.properties`, que tampoco se sube.

```bash
./gradlew assembleRelease
# El archivo queda en app/build/outputs/apk/release/app-release.apk
```

Para comprobar que quedó firmado:

```bash
"$env:LOCALAPPDATA\Android\Sdk\build-tools\36.0.0\apksigner.bat" verify --print-certs app-release.apk
```

## Estructura del proyecto

```
app/src/main/java/com/example/mpaznotifica/
├── MainActivity.kt
├── data/
│   ├── local/                 Base de datos Room
│   │   ├── AppDatabase.kt     Base de datos (versión 3)
│   │   ├── dao/               UsuarioDao, ApoderadoDao, GrupoDao, ComunicacionDao
│   │   └── entity/            Usuario, Apoderado, Grupo, GrupoApoderado,
│   │                          Comunicacion y ComunicacionDestinatario
│   ├── model/                 GrupoConIntegrantes
│   ├── nativo/                Vibracion (recurso nativo del dispositivo)
│   ├── remoto/                Conexión con el backend
│   │   ├── ClienteRetrofit.kt Las tres conexiones (60 s de espera)
│   │   ├── Apis.kt            ApiUsuarios, ApiMensajeria y ApiHistorial
│   │   ├── Dtos.kt            Clases que reciben el JSON del servidor
│   │   ├── ErroresRed.kt      Traduce los errores 400 / 404 y da formato a las fechas
│   │   └── EnvioUnificado.kt  Envío juntado de historial y mensajería
│   ├── repositorio/           UsuarioRepositorio, GrupoRepositorio, ComunicacionRepositorio
│   └── sesion/                SesionPreferencias (sesión guardada)
└── ui/
    ├── components/            EncabezadoApp (barra azul superior reutilizable)
    ├── navigation/            Rutas y navegación (AppNavigation)
    ├── screens/
    │   ├── login/             Login
    │   ├── inicio/            Inicio
    │   ├── comunicacion/      Comunicación (cámara y galería)
    │   ├── destinatarios/     Destinatarios (animación y vibración)
    │   ├── grupos/            Listado, creación y componentes compartidos
    │   └── historial/         Historial
    └── theme/                 Colores y tipografía (tomados del mockup)
```

Cada pantalla sigue el mismo orden: la carpeta tiene su `Pantalla*.kt` (interfaz), su `*UiState.kt`
(estado) y su `*ViewModel.kt` (lógica y validaciones).

## Base de datos local

Room guarda todo en `mpaz_notifica.db`, en la versión **3**. La tabla que se usa en esta etapa es
`usuarios`, porque guarda las cuentas que se descargan del servidor. Las demás tablas quedan del avance
anterior:

| Tabla | Contenido |
|---|---|
| `usuarios` | Cuentas descargadas de `ms-usuarios`, con su rol y su curso |
| `apoderados` | Apoderados del curso (el avance anterior los cargaba desde acá) |
| `grupos` | Grupos de apoderados |
| `grupo_apoderado` | Qué apoderados forman cada grupo |
| `comunicaciones` | Comunicaciones redactadas |
| `comunicacion_destinatario` | Qué apoderados recibieron cada comunicación |

## Diagramas de arquitectura

En la rama `documents` del repositorio están los dos diagramas que pide la rúbrica:

- **Arquitectura de microservicios:** aplicación Android → Retrofit/HTTPS → los tres servicios → PostgreSQL.
- **Arquitectura del proyecto:** capas `ui` → ViewModel → repositorio → API y Room.

## Decisiones de diseño

- **El rol no se elige en el Login.** Es un atributo de la cuenta, porque en la escuela el rol lo asigna
  la institución.
- **La sesión viaja en la ruta y se guarda en el teléfono.** El Login entrega el `usuarioId`, la sesión
  queda guardada y al volver a abrir la aplicación se entra directo a Inicio. Al cerrar sesión se borra.
- **Las cuentas solo nacen en el servidor.** La aplicación no puede inventar usuarios ni roles.
- **El borrador y el envío viven en el servidor.** Al presionar "Continuar" se crea el borrador en
  `ms-mensajeria`; en Destinatarios se envía con el canal y los destinatarios.
- **El Historial une dos fuentes.** `ms-historial` y `ms-mensajeria` saben qué se envió; la aplicación
  junta los dos para no perder ninguna comunicación.
- **Datos ficticios y anonimizados.** No se usan nombres, RUT ni contactos reales de estudiantes ni
  apoderados, conforme a la regla de protección de datos del requerimiento.

## Limitaciones conocidas

- **El envío sigue siendo simulado:** no se envían correos, SMS ni mensajes de WhatsApp reales. El
  servidor registra el envío con su canal y sus destinatarios.
- **Adjuntos:** se guarda el nombre de las imágenes adjuntas; el archivo no se sube al servidor.
- **La creación de cuentas** (con el código por SMS para asignar el rol) queda como etapa siguiente.
- **La base local usa migración destructiva:** si se cambia la estructura de las tablas, se vuelve a
  crear y se recargan las cuentas desde el servidor al iniciar sesión.

## Documentación de trabajo del equipo

- `documents/`: los dos diagramas de arquitectura.
- `registro-ramas-borradas.md`: registro de las ramas ya fusionadas y eliminadas, con sus datos, para
  poder recuperarlas si algún día hiciera falta.

---

Proyecto académico · DSY1105 Desarrollo de Aplicaciones Móviles · Duoc UC · 2026
