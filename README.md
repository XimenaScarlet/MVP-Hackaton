# MI UTC - Plataforma Universitaria Integral

## 📌 Descripción del Proyecto
**MI UTC** es una solución móvil diseñada para optimizar la gestión académica y la seguridad de la comunidad de la Universidad Tecnológica de Coahuila (UTC). La aplicación permite a los estudiantes gestionar trámites, salud y seguridad, mientras que ofrece a los administradores herramientas de monitoreo en tiempo real.

---

## 🔗 Repositorio
**URL:** [https://github.com/scarl-dev/MVP-Hackaton](https://github.com/scarl-dev/MVP-Hackaton)

---

## 🛠️ Stack Tecnológico
- **UI:** Jetpack Compose (Material 3).
- **Arquitectura:** MVVM + Clean Architecture + Hilt (DI).
- **Backend:** Firebase (Auth, Firestore, Cloud Messaging).
- **Networking:** Retrofit 2 + SSL Pinning.
- **Mapas:** Google Maps SDK.

---

## 🚀 Despliegue y Configuración de Firebase
Para que la aplicación funcione con su propia instancia de Firebase, siga estos pasos:

1. **Crear Proyecto:** En la [Consola de Firebase](https://console.firebase.google.com/), cree un nuevo proyecto llamado `MI-UTC`.
2. **Registrar App:** Añada una app Android con el paquete `com.example.univapp`.
3. **Huellas Digitales (Crítico):**
   - Ejecute `./gradlew signingReport` en la terminal de Android Studio.
   - Copie el **SHA-1** y **SHA-256** y regístrelos en la configuración de la app en Firebase. Esto es necesario para que Google Maps y Auth funcionen.
4. **Archivo de Configuración:** Descargue `google-services.json` y colóquelo en la carpeta `app/`.
5. **Firestore:** Habilite Cloud Firestore en modo producción y cree las colecciones base.

### 📂 Colecciones Esperadas en Firestore
El sistema espera la siguiente estructura de datos:
- `alumnos`: Documentos con ID de matrícula (campos: `nombre`, `correo`, `matricula`, `carreraId`, `grupoId`).
- `carreras`: Catálogo de programas educativos (campos: `nombre`, `tipo`).
- `grupos`: Gestión de secciones (campos: `nombre`, `carreraId`, `tutorId`).
- `sos_alerts`: Alertas activas (campos: `alumnoNombre`, `location` (GeoPoint), `active` (boolean), `timestamp`).
- `profesores`: Directorio de docentes.

---

## 📱 Configuración del Emulador
Para validar todas las funciones (especialmente SOS y Maps):
- **Imagen:** Android 11+ (API 30+) con **Google Play APIs**.
- **Localización:** Debe tener los permisos de ubicación habilitados en la configuración del sistema del emulador.
- **Red:** Acceso a internet sin restricciones de firewall corporativo.

---

## 📸 Evidencia Visual (Funciones Clave)
| Login Seguro | Panel Administrador | SOS Activo |
| :---: | :---: | :---: |
| ![Login](https://raw.githubusercontent.com/scarl-dev/MVP-Hackaton/main/docs/login.png) | ![Admin](https://raw.githubusercontent.com/scarl-dev/MVP-Hackaton/main/docs/admin.png) | ![SOS](https://raw.githubusercontent.com/scarl-dev/MVP-Hackaton/main/docs/sos.png) |

---

## ⚠️ Limitaciones Conocidas
1. **Bypass de Admin:** Se incluye un acceso de emergencia (`admin@utc.edu.mx` / `admin123`) para facilitar la evaluación si existen problemas de red con Google Play Services en el emulador.
2. **Certificate Pinning:** El pinning está desactivado para dominios de Firebase para permitir el acceso desde redes locales, pero activo para Google Maps.
3. **Importación Masiva:** La creación de cuentas en Firebase Auth desde Excel requiere que el administrador tenga permisos de Admin SDK; en esta versión cliente, la importación solo actualiza los registros en Firestore.

---

## 👨‍💻 Instrucciones de Compilación
1. Sincronizar Gradle.
2. Añadir `MAPS_API_KEY` en `local.properties`.
3. Ejecutar `Build > Rebuild Project`.
4. Ejecutar en el dispositivo seleccionado.
