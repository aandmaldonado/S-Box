# S-Box - Sistema de Grabación Multimodal

S-Box es un sistema avanzado de grabación multimodal que permite capturar simultáneamente video de cámara, audio de micrófono, grabación de pantalla y detección de expresiones faciales en tiempo real.

## Características Principales

### ✅ Funcionalidades Implementadas
- **Captura de Video**: Webcam en tiempo real (640x480, 30 FPS)
- **Grabación de Pantalla**: Captura completa de pantalla en MP4 (H.264)
- **Detección de Sonrisas**: Análisis facial usando OpenCV y clasificadores Haar
- **Captura de Audio**: Micrófono en WAV (44.1kHz, 16-bit, Mono)
- **Interfaz Unificada**: Control centralizado de todas las funcionalidades
- **Gestión de Recursos**: Liberación automática de recursos

### 🔄 Funcionalidades Pendientes
- Sincronización multimodal (cámara + pantalla + audio)
- Perspectiva externa (cliente-servidor)
- Reproducción multimodal
- Configuración avanzada de parámetros

## Estructura del Proyecto

```
S-Box/
├── sbox/                              # Módulo principal
│   ├── src/
│   │   ├── main/java/sbox/           # Código principal
│   │   │   ├── proyecto/
│   │   │   │   └── ProyectoMain.java # Aplicación principal
│   │   │   ├── facerecorder/
│   │   │   │   ├── AudioCapture.java # Captura de audio
│   │   │   │   └── WebcamAndMicrophoneCapture.java
│   │   │   ├── activityrender/
│   │   │   │   └── ScreenRecorder.java # Grabación de pantalla
│   │   │   └── detection/
│   │   │       └── VideoDetection.java # Detección de sonrisas
│   │   └── test/java/sbox/           # Tests de funcionalidad
│   │       ├── proyecto/
│   │       │   ├── CameraTest.java
│   │       │   ├── ScreenRecordingTest.java
│   │       │   └── AudioCaptureTest.java
│   │       └── detection/
│   │           └── VideoDetection.java
│   └── build.gradle
├── perspectivaExterna/                # Módulo de perspectiva externa
│   ├── src/main/java/sbox/perspectiva/
│   │   ├── PerspectivaServidor.java
│   │   ├── VentanaLog.java
│   │   └── Video.java
│   └── build.gradle
├── scripts/                          # Scripts de prueba
│   ├── test_camera.sh
│   ├── test_screen_recording.sh
│   ├── test_smile_detection.sh
│   ├── test_audio_capture.sh
│   ├── run_all_tests.sh
│   └── README.md
├── docs/                             # Documentación
│   ├── PRD.md
│   └── TECH_SOLUTION.md
├── CAMERA_TEST.md                    # Guía de pruebas
├── build.gradle                      # Configuración raíz
└── README.md                         # Este archivo
```

## Tecnologías Utilizadas

### Core Technologies
- **Java 8+**: Lenguaje principal
- **Gradle**: Sistema de build
- **Swing**: Interfaz gráfica

### Computer Vision & Multimedia
- **Bytedeco/JavaCV 1.5.8**: Bindings Java para OpenCV y FFmpeg
- **OpenCV 4.5.5**: Computer vision library
- **FFmpeg 4.4**: Procesamiento de video y audio
- **Haar Cascades**: Detección de objetos (caras, sonrisas, bocas)

### Audio Processing
- **Java Sound API**: Captura de audio nativa
- **PCM WAV**: Formato de audio sin compresión

### Development Tools
- **Lombok**: Reducción de boilerplate code
- **Log4j**: Sistema de logging

## Instalación y Configuración

### Prerrequisitos
- Java 8 o superior
- Gradle 7.0+
- Webcam funcional
- Micrófono funcional
- Permisos de acceso a cámara, pantalla y micrófono

### Instalación
```bash
# Clonar el repositorio
git clone <repository-url>
cd S-Box

# Compilar el proyecto
./gradlew build

# Ejecutar la aplicación principal
./gradlew :sbox:run
```

## Uso

### Aplicación Principal
```bash
# Ejecutar aplicación completa
./gradlew :sbox:run
```

### Pruebas Individuales
```bash
# Prueba de cámara
./gradlew :sbox:runCameraTest
# o
./scripts/test_camera.sh

# Prueba de grabación de pantalla
./gradlew :sbox:runScreenRecordingTest
# o
./scripts/test_screen_recording.sh

# Prueba de detección de sonrisas
./gradlew :sbox:runSmileDetection
# o
./scripts/test_smile_detection.sh

# Prueba de captura de audio
./gradlew :sbox:runAudioCaptureTest
# o
./scripts/test_audio_capture.sh
```

### Ejecutar Todas las Pruebas
```bash
# Usando Gradle
./gradlew :sbox:runAllTests

# Usando script
./scripts/run_all_tests.sh
```

## Funcionalidades Detalladas

### Captura de Video
- **Resolución**: 640x480 píxeles
- **Frame Rate**: ~30 FPS
- **Formato**: RGB (3 canales)
- **Compatibilidad**: macOS ARM64 (Apple Silicon)

### Grabación de Pantalla
- **Formato**: MP4 (H.264)
- **Frame Rate**: 30 FPS
- **Resolución**: Nativa de pantalla
- **Almacenamiento**: Carpeta `recordings/`

### Detección de Sonrisas
- **Clasificadores**: Haar Cascades
- **Detecciones**: Caras, sonrisas, bocas
- **Visualización**: Rectángulos de colores
- **Estadísticas**: Contadores en tiempo real

### Captura de Audio
- **Formato**: WAV (44.1kHz, 16-bit, Mono)
- **Medidor**: Volumen en tiempo real (dB)
- **Almacenamiento**: Archivos WAV en `recordings/`

## Archivos de Salida

### Ubicación
Todos los archivos se guardan en la carpeta `recordings/`:
```
recordings/
├── screen_[proyecto]_[experimento].mp4  # Grabaciones de pantalla
├── audio_[timestamp].wav                # Archivos de audio
└── ...
```

### Formatos
- **Video**: MP4 con codec H.264
- **Audio**: WAV sin compresión
- **Metadatos**: Timestamp y parámetros de captura

## Solución de Problemas

### Errores Comunes

#### Error de Cámara
```bash
# Verificar permisos
# Verificar que no haya otra aplicación usando la cámara
# Reiniciar la aplicación
```

#### Error de Audio
```bash
# Verificar micrófono conectado
# Verificar permisos de acceso
# Verificar drivers de audio
```

#### Error de Permisos
```bash
# macOS: System Preferences > Security & Privacy > Privacy
# Linux: Verificar permisos de /dev/video* y /dev/audio*
```

### Logs
Los logs se generan automáticamente y pueden ser consultados para debugging.

## Desarrollo

### Estructura de Tests
Los tests están organizados en `sbox/src/test/` y pueden ejecutarse individualmente o en conjunto.

### Agregar Nuevas Funcionalidades
1. Crear clase en `src/main/java/sbox/`
2. Crear test en `src/test/java/sbox/`
3. Agregar tarea Gradle en `build.gradle`
4. Crear script en `scripts/`
5. Actualizar documentación

### Build y Deploy
```bash
# Compilar
./gradlew build

# Ejecutar tests
./gradlew :sbox:runAllTests

# Crear JAR
./gradlew :sbox:jar
```

## Contribución

1. Fork el proyecto
2. Crear rama para feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## Contacto

- **Autor**: amaldonado
- **Proyecto**: S-Box
- **Documentación**: Ver `docs/` y `CAMERA_TEST.md`

## Changelog

### v1.0.0 (Actual)
- ✅ Captura de cámara funcional
- ✅ Grabación de pantalla funcional
- ✅ Detección de sonrisas funcional
- ✅ Captura de audio funcional
- ✅ Interfaz unificada
- ✅ Tests organizados
- ✅ Scripts de automatización
- ✅ Documentación completa

### Próximas Versiones
- 🔄 Sincronización multimodal
- 🔄 Perspectiva externa
- 🔄 Reproducción avanzada
- 🔄 Configuración avanzada

