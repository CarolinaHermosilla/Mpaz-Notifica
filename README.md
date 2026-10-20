# MPAZ Notifica

Aplicación móvil Android para la **Escuela Escritora Marcela Paz** (San Bernardo). Su objetivo es apoyar
el envío y el registro de las comunicaciones que la escuela dirige a los apoderados —citaciones, avisos
institucionales, actividades, talleres y autorizaciones—, reemplazando parte del proceso en papel y
dejando un historial de lo enviado.

Proyecto académico de la asignatura **DSY1105 - Desarrollo de Aplicaciones Móviles** (Duoc UC).
Es un MVP: todos los datos que usa la aplicación son **ficticios** y no hay servidor ni usuarios reales.

## Integrantes

- Carolina Hermosilla
- Pedro Toledo

## Tecnologías

| Elemento | Uso en el proyecto |
|---|---|
| Kotlin 2.2.10 | Lenguaje |
| Jetpack Compose + Material 3 | Interfaz (BOM 2026.02.01) |
| MVVM | Cada pantalla tiene su `UiState` y su `ViewModel`; la lógica de validación está fuera de la interfaz |
| Room 2.8.4 (SQLite) + KSP | Persistencia local |
| Navigation Compose 2.8.0 | Navegación entre pantallas |
| Gradle 9.5 / AGP 9.3.3 | Compilación |
| Android Studio | Entorno de desarrollo |

- `minSdk 23` (Android 6.0), `targetSdk 36`.
- Requiere **JDK 21**: Gradle lo descarga automáticamente la primera vez si no está instalado.

## Funcionalidades

Los seis componentes del requerimiento están implementados:

| # | Componente | Responsable | Qué hace |
|---|---|---|---|
| 1 | Login | Carolina | Correo y contraseña con validación por campo e íconos. El rol **no se elige**: es un atributo de la cuenta y al ingresar viaja el id del usuario en la ruta. Muestra las cuentas de prueba y avisa que no hay autenticación real. |
| 2 | Inicio | Pedro | Saludo y rol de la cuenta, aviso de sesión iniciada, contadores reales (borradores, enviadas y apoderados), accesos a nueva comunicación y grupos, comunicaciones recientes y cierre de sesión. |
| 3 | Comunicación | Pedro | Tipo de comunicación (circular, citación o material de apoyo), título y mensaje con validación, evidencias adjuntas con **cámara** o **galería**, vista previa firmada por quien inició sesión y guardado del borrador al continuar. |
| 4 | Destinatarios | Carolina | Alcance de la comunicación (todo el curso o elegir apoderados), carga de grupos guardados, lista de apoderados con su estudiante y taller, contador de destinatarios, selector de canal y envío con **animación de carga** y **vibración**. |
| 5 | Grupos | Carolina | Listado de grupos con su cantidad de integrantes, detalle con opción de eliminar (con confirmación) y creación con validaciones (nombre vacío, sin apoderados y nombre repetido), avisando al volver al listado. |
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

La aplicación no tiene autenticación real: no existe servidor ni usuarios verdaderos.
Las dos cuentas se cargan solas la primera vez que se abre la aplicación:

| Cuenta | Correo | Contraseña | Rol |
|---|---|---|---|
| Andrea Muñoz | `profesor.jefe@ejemplo.cl` | `123456` | Profesor o profesora jefe de 5.º Básico B |
| Mario Gutierrez | `inspector.general@ejemplo.cl` | `123456` | Inspectoría General |

La cuenta de profesora jefe solo puede enviar a los apoderados de su curso; la de Inspectoría General
puede enviar a todo el colegio.

## Cómo ejecutar el proyecto

1. Clonar o descargar el repositorio.
2. Abrir la carpeta del proyecto con **Android Studio**.
3. Esperar a que Gradle sincronice las dependencias. La primera vez necesita conexión a internet
   (descarga Room, las bibliotecas de Compose y, si falta, el JDK 21).
4. Crear el archivo `local.properties` con la ruta del SDK de Android de tu computador
   (Android Studio lo genera solo; el archivo no se sube al repositorio).
5. Elegir un emulador o un teléfono con Android 6.0 (API 23) o superior y presionar **Run**.

### Recorrido para probar

1. Entrar con la cuenta de la profesora jefe.
2. En Inicio, ver los contadores y las comunicaciones recientes.
3. Presionar **Nueva comunicación**: elegir el tipo, escribir título y mensaje, y opcionalmente adjuntar
   una foto con la cámara o una imagen de la galería.
4. Presionar **Continuar**: se guarda el borrador y se abre **Destinatarios**.
5. Cargar un grupo guardado (por ejemplo, Taller de Teatro) y ver cómo se marcan sus integrantes.
6. Presionar **Enviar comunicación**: aparece el círculo de carga, el teléfono vibra y se llega al **Historial**,
   donde la comunicación aparece como enviada.
7. Volver a Inicio: los contadores se actualizan. Probar también **Grupos** (crear y eliminar un grupo).

> La vibración solo se puede comprobar en un teléfono real. En el emulador se puede revisar con
> `adb logcat | findstr Vibracion`, que muestra `Vibrando 500 ms`.

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
│   └── repositorio/           UsuarioRepositorio, GrupoRepositorio, ComunicacionRepositorio
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

Room guarda todo en `mpaz_notifica.db`, en la versión **3**. Tablas:

| Tabla | Contenido |
|---|---|
| `usuarios` | Cuentas que inician sesión, con su rol y su curso |
| `apoderados` | Apoderados del curso, con su estudiante anonimizado y su taller |
| `grupos` | Grupos de apoderados (con descripción y si vienen de un taller) |
| `grupo_apoderado` | Qué apoderados forman cada grupo |
| `comunicaciones` | Comunicaciones redactadas: título, mensaje, tipo, canal, adjuntos, fecha y estado (`Borrador` o `Enviada`) |
| `comunicacion_destinatario` | Qué apoderados recibieron cada comunicación |

**Datos de ejemplo:** dos cuentas (profesora jefe e Inspectoría General), ocho apoderados del
5.º Básico B con sus estudiantes anonimizados (Estudiante A a H) y tres grupos: Taller de Ajedrez,
Taller de Teatro y Directiva del curso.

## Decisiones de diseño

- **El rol no se elige en el Login.** Es un atributo de la cuenta, porque en la escuela el rol lo asigna
  la institución. La etapa siguiente es que el rol se defina al crear la cuenta con un código enviado por SMS.
- **La sesión viaja en la ruta.** El Login entrega el `usuarioId` y las pantallas de Inicio, Comunicación y
  Destinatarios lo usan para cargar el nombre, el rol y el curso desde la base.
- **Ciclo de la comunicación.** Al presionar "Continuar" en Comunicación se guarda como `Borrador`; en
  Destinatarios se marca como `Enviada` con el canal, la fecha y sus destinatarios. Así el Inicio puede
  mostrar borradores y enviadas, y el Historial puede listar lo enviado.
- **Componentes "cápsula".** Las selecciones (alcance, grupos y canales) usan el mismo componente para
  mantener la interfaz coherente.
- **Datos ficticios y anonimizados.** No se usan nombres, RUT ni contactos reales de estudiantes ni
  apoderados, conforme a la regla de protección de datos del requerimiento.

## Alcance del MVP y limitaciones conocidas

- **No hay servidor.** La autenticación es local y el "envío" simula el tiempo de envío: no se envían
  correos, SMS ni mensajes de WhatsApp reales.
- **La creación de cuentas** (con el código por SMS para asignar el rol) queda como etapa siguiente.
- **La base usa migración destructiva:** si se cambia la estructura de las tablas, la base local se vuelve
  a crear y se recargan los datos de ejemplo. Los datos sí sobreviven al cerrar y reabrir la aplicación.
- **Adjuntos:** se guardan los nombres de los archivos adjuntos; el envío real del archivo queda para
  una versión con servidor.

## Documentación de trabajo del equipo

- `registro-ramas-borradas.md`: registro de las ramas ya fusionadas y eliminadas, con sus datos, para
  poder recuperarlas si algún día hiciera falta.

---

Proyecto académico · DSY1105 Desarrollo de Aplicaciones Móviles · Duoc UC · 2026
