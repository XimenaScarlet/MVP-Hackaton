# UnivApp

Proyecto de aplicación universitaria desarrollado en Android con Jetpack Compose.

## Prerrequisitos

- Android Studio (versión 2023.2.1 o superior)
- JDK 17
- Una cuenta de Firebase para configurar los servicios de backend.

## Configuración del Entorno

1.  **Clonar el repositorio:**

    ```bash
    git clone https://github.com/tu-usuario/tu-repositorio.git
    ```

2.  **Configurar Firebase:**

    - Ve a la [consola de Firebase](https://console.firebase.google.com/) y crea un nuevo proyecto.
    - Añade una aplicación de Android a tu proyecto de Firebase con el nombre de paquete `com.example.univapp`.
    - Descarga el archivo `google-services.json` y colócalo en el directorio `app/`.

3.  **Abrir en Android Studio:**

    - Abre Android Studio y selecciona "Open an Existing Project".
    - Navega hasta el directorio donde clonaste el proyecto y ábrelo.
    - Espera a que Gradle sincronice las dependencias del proyecto.

## Pasos para Compilar y Ejecutar

1.  **Sincronizar Gradle:**

    - Si Android Studio no lo hace automáticamente, sincroniza el proyecto con los archivos de Gradle haciendo clic en el icono de elefante con una flecha en la barra de herramientas.

2.  **Construir el Proyecto:**

    - En el menú, ve a `Build` > `Make Project` (o usa el atajo `Ctrl+F9`).

3.  **Ejecutar la Aplicación:**

    - Selecciona una configuración de ejecución (normalmente `app`) en la barra de herramientas.
    - Elige un emulador o un dispositivo físico conectado.
    - Haz clic en `Run` (o usa el atajo `Shift+F10`).

¡Listo! La aplicación se compilará e instalará en el dispositivo seleccionado.
