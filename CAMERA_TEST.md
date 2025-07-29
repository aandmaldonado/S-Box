# Pruebas de Cámara, Grabación, Detección y Audio - S-Box

## Descripción
Este documento describe cómo probar la funcionalidad de captura de cámara, grabación de pantalla, detección de sonrisas y captura de audio en S-Box.

## Funcionalidades Implementadas

### ✅ Captura de Video
- **Webcam**: Captura desde cámara USB (índice 0)
- **Resolución**: 640x480 píxeles
- **Frame Rate**: ~30 FPS
- **Formato**: RGB (3 canales)

### ✅ Grabación de Pantalla
- **Pantalla Completa**: Captura de toda la pantalla
- **Formato**: MP4 (H.264)
- **Frame Rate**: 30 FPS
- **Resolución**: Resolución nativa de la pantalla
- **Almacenamiento**: Carpeta 'recordings'

### ✅ Detección de Sonrisas
- **Detección de Caras**: Usando clasificador Haar `FaceDetection.xml`
- **Detección de Sonrisas**: Usando clasificador Haar `SmileDetection.xml`
- **Detección de Bocas**: Usando clasificador Haar `MouthDetection.xml`
- **Visualización**: Rectángulos de colores para cada detección
- **Estadísticas**: Contador en tiempo real de caras, sonrisas y bocas

### ✅ Captura de Audio
- **Micrófono**: Captura desde micrófono del sistema
- **Formato**: WAV (44.1kHz, 16-bit, Mono)
- **Medidor de Volumen**: Visualización en tiempo real del nivel de audio
- **Cálculo RMS**: Medición precisa del volumen en dB
- **Almacenamiento**: Archivos WAV en carpeta 'recordings'

### ✅ Visualización en Tiempo Real
- **Panel de Video**: Muestra el video de la cámara en tiempo real
- **Medidor de Volumen**: Barra visual del nivel de audio
- **Interfaz Swing**: Controles para iniciar/detener cámara, grabación, detección y audio
- **Estado Visual**: Indicador de estado de todas las funcionalidades

### ✅ Gestión de Recursos
- **Inicialización**: Configuración automática de grabber, convertidores y líneas de audio
- **Limpieza**: Liberación correcta de recursos al detener
- **Threading**: Captura en threads separados para no bloquear UI

## Estructura del Proyecto

```
S-Box/
├── sbox/
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
├── scripts/                          # Scripts de prueba
│   ├── test_camera.sh
│   ├── test_screen_recording.sh
│   ├── test_smile_detection.sh
│   ├── test_audio_capture.sh
│   └── run_all_tests.sh
└── CAMERA_TEST.md                    # Esta documentación
```

## Cómo Probar

### 1. Prueba Simple de Cámara
```bash
# Compilar el proyecto
./gradlew :sbox:build

# Ejecutar prueba de cámara
./gradlew :sbox:runCameraTest

# O usar el script
./scripts/test_camera.sh
```

### 2. Prueba de Grabación de Pantalla
```bash
# Ejecutar prueba de grabación de pantalla
./gradlew :sbox:runScreenRecordingTest

# O usar el script
./scripts/test_screen_recording.sh
```

### 3. Prueba de Detección de Sonrisas
```bash
# Ejecutar detección de sonrisas
./gradlew :sbox:runSmileDetection

# O usar el script
./scripts/test_smile_detection.sh
```

### 4. Prueba de Captura de Audio
```bash
# Ejecutar captura de audio
./gradlew :sbox:runAudioCaptureTest

# O usar el script
./scripts/test_audio_capture.sh
```

### 5. Aplicación Principal
```bash
# Ejecutar aplicación principal
./gradlew :sbox:run
```

### 6. Ejecutar Todas las Pruebas
```bash
# Ejecutar todas las pruebas
./scripts/run_all_tests.sh

# O usando Gradle
./gradlew :sbox:runAllTests
```

## Interfaz de Usuario

### Controles Disponibles
- **Iniciar Cámara**: Activa la captura de video
- **Detener Cámara**: Desactiva la captura y libera recursos
- **Iniciar Grabación**: Comienza la grabación de pantalla
- **Detener Grabación**: Detiene la grabación y guarda el archivo
- **Activar/Desactivar Detección**: Toggle para detección de sonrisas
- **Activar/Desactivar Audio**: Toggle para captura de audio
- **Estado**: Muestra el estado actual de todas las funcionalidades

### Panel de Video
- **Fondo Negro**: Cuando la cámara está desactivada
- **Video en Tiempo Real**: Cuando la cámara está activa
- **Rectángulos de Detección**: Cuando la detección está activa
  - 🟢 **Verde**: Cara detectada
  - 🔴 **Rojo**: Sonrisa detectada
  - 🔵 **Azul**: Boca detectada

### Medidor de Volumen
- **Barra Visual**: Muestra el nivel de audio en tiempo real
- **Colores**: Verde (bajo), Amarillo (medio), Rojo (alto)
- **Escala**: -60 dB a 0 dB
- **Actualización**: 10 veces por segundo

### Estadísticas
- **Video**: Caras, sonrisas y bocas detectadas
- **Audio**: Duración, volumen en dB, bytes capturados

## Requisitos del Sistema

### Hardware
- **Webcam**: Cámara USB funcional
- **Micrófono**: Micrófono del sistema o externo
- **Permisos**: Acceso a cámara, pantalla y micrófono habilitado

### Software
- **Java**: 8 o superior
- **Gradle**: 7.0+
- **Dependencias**: Bytedeco/JavaCV stack
- **FFmpeg**: Incluido en las dependencias de Bytedeco
- **OpenCV**: Incluido en las dependencias de Bytedeco
- **Java Sound API**: Incluido en el JDK

## Solución de Problemas

### Error: "No se puede acceder a la cámara"
1. Verificar que la webcam esté conectada
2. Verificar permisos del sistema
3. Probar con diferentes índices de cámara (0, 1, 2...)

### Error: "Error al iniciar grabber"
1. Verificar que no haya otra aplicación usando la cámara
2. Reiniciar la aplicación
3. Verificar drivers de la cámara

### Error: "Error al iniciar grabación de pantalla"
1. Verificar permisos de acceso a pantalla
2. Verificar espacio en disco
3. Verificar que FFmpeg esté disponible

### Error: "Error al cargar clasificadores Haar"
1. Verificar que los archivos XML estén en `resources/haarcascades/`
2. Verificar permisos de lectura
3. Verificar que OpenCV esté disponible

### Error: "Error al iniciar captura de audio"
1. Verificar que el micrófono esté conectado y funcionando
2. Verificar permisos de acceso al micrófono
3. Verificar que no haya otra aplicación usando el micrófono
4. Verificar drivers de audio

### Rendimiento Lento
1. Reducir frame rate (aumentar Thread.sleep)
2. Reducir resolución
3. Verificar recursos del sistema

## Código de Ejemplo

### Inicialización de Cámara
```java
// Inicializar grabber
grabber = new OpenCVFrameGrabber(0); // Cámara 0
grabber.start();

// Inicializar convertidores
converter = new OpenCVFrameConverter.ToMat();
java2DConverter = new Java2DFrameConverter();
```

### Inicialización de Grabación de Pantalla
```java
// Inicializar grabador
screenRecorder = new ScreenRecorder();
screenRecorder.start();

// Detener grabación
String outputFile = screenRecorder.stop();
```

### Inicialización de Detección de Sonrisas
```java
// Cargar clasificadores Haar
faceClassifier = new CascadeClassifier(facePath);
smileClassifier = new CascadeClassifier(smilePath);
mouthClassifier = new CascadeClassifier(mouthPath);

// Realizar detección
RectVector faces = new RectVector();
faceClassifier.detectMultiScale(grayImage, faces, 1.1, 3, 0, 
    new Size(30, 30), new Size());
```

### Inicialización de Captura de Audio
```java
// Inicializar captura de audio
audioCapture = new AudioCapture();
audioCapture.startCapture();

// Detener captura
audioCapture.stopCapture();

// Guardar archivo
File audioFile = audioCapture.saveToFile("audio.wav");
```

### Captura de Frames
```java
Frame frame = grabber.grab();
if (frame != null) {
    Mat image = converter.convert(frame);
    // Procesar imagen...
}
```

### Limpieza de Recursos
```java
if (grabber != null) {
    grabber.stop();
    grabber.release();
}
```

## Archivos de Salida

### Grabación de Pantalla
- **Ubicación**: `recordings/`
- **Formato**: `screen_[nombreProyecto]_[experimento].mp4`
- **Codec**: H.264
- **Calidad**: Alta calidad con compresión eficiente

### Captura de Audio
- **Ubicación**: `recordings/`
- **Formato**: `audio_[timestamp].wav`
- **Sample Rate**: 44.1kHz
- **Bits**: 16-bit
- **Canales**: Mono

### Ejemplo de Archivos
```
recordings/
├── screen_sbox_main_1.mp4
├── screen_test_1.mp4
├── audio_1703123456789.wav
└── audio_1703123456790.wav
```

## Clasificadores Haar

### Archivos Disponibles
- **FaceDetection.xml**: Detección de caras frontales
- **SmileDetection.xml**: Detección de sonrisas
- **MouthDetection.xml**: Detección de bocas

### Ubicación
```
sbox/src/main/resources/haarcascades/
├── FaceDetection.xml
├── SmileDetection.xml
└── MouthDetection.xml
```

## Configuración de Audio

### Parámetros de Captura
- **Sample Rate**: 44.1kHz (CD quality)
- **Sample Size**: 16 bits
- **Channels**: 1 (Mono)
- **Encoding**: PCM Signed
- **Endianness**: Little Endian

### Medición de Volumen
- **Método**: RMS (Root Mean Square)
- **Unidad**: dB (decibeles)
- **Rango**: -60 dB a 0 dB
- **Actualización**: 100ms

## Próximos Pasos

### Funcionalidades Pendientes
1. **Sincronización**: Alinear cámara, pantalla y audio
2. **Múltiples Cámaras**: Soporte para varias fuentes
3. **Grabación de Área Específica**: Seleccionar región de pantalla
4. **Perspectiva Externa**: Activar comunicación cliente-servidor
5. **Reproductor Multimodal**: Visualización de múltiples fuentes

### Mejoras Técnicas
1. **Optimización**: Mejorar rendimiento de captura y detección
2. **Configuración**: Permitir ajustar parámetros de todas las funcionalidades
3. **Error Handling**: Mejorar manejo de errores
4. **Logging**: Agregar más información de debug
5. **Compresión**: Optimizar tamaño de archivos

## Archivos Relacionados

### Código Principal
- `sbox/src/main/java/sbox/proyecto/ProyectoMain.java` - Aplicación principal
- `sbox/src/main/java/sbox/facerecorder/AudioCapture.java` - Captura de audio
- `sbox/src/main/java/sbox/activityrender/ScreenRecorder.java` - Grabación de pantalla

### Tests de Funcionalidad
- `sbox/src/test/java/sbox/proyecto/CameraTest.java` - Prueba de cámara
- `sbox/src/test/java/sbox/proyecto/ScreenRecordingTest.java` - Prueba de grabación de pantalla
- `sbox/src/test/java/sbox/detection/VideoDetection.java` - Detección de sonrisas
- `sbox/src/test/java/sbox/proyecto/AudioCaptureTest.java` - Prueba de captura de audio

### Scripts de Prueba
- `scripts/test_camera.sh` - Script de prueba de cámara
- `scripts/test_screen_recording.sh` - Script de prueba de grabación
- `scripts/test_smile_detection.sh` - Script de prueba de detección
- `scripts/test_audio_capture.sh` - Script de prueba de audio
- `scripts/run_all_tests.sh` - Script maestro para todas las pruebas

### Documentación
- `sbox/src/test/README.md` - Documentación específica de tests 