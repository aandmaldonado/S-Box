# Tests de S-Box

Este directorio contiene todas las pruebas y tests de funcionalidad para S-Box.

## Estructura

```
sbox/src/test/
├── java/sbox/
│   ├── proyecto/
│   │   ├── CameraTest.java              # Prueba de cámara
│   │   ├── ScreenRecordingTest.java     # Prueba de grabación de pantalla
│   │   └── AudioCaptureTest.java        # Prueba de captura de audio
│   └── detection/
│       └── VideoDetection.java          # Prueba de detección de sonrisas
└── README.md                            # Este archivo
```

## Tests Disponibles

### 1. CameraTest.java
**Propósito**: Prueba la funcionalidad básica de captura de cámara
- Iniciar/detener cámara
- Visualización en tiempo real
- Gestión de recursos

**Ejecutar**:
```bash
./gradlew :sbox:runCameraTest
```

### 2. ScreenRecordingTest.java
**Propósito**: Prueba la grabación de pantalla
- Iniciar/detener grabación
- Guardar archivo MP4
- Estadísticas de grabación

**Ejecutar**:
```bash
./gradlew :sbox:runScreenRecordingTest
```

### 3. AudioCaptureTest.java
**Propósito**: Prueba la captura de audio
- Iniciar/detener captura de micrófono
- Medidor de volumen en tiempo real
- Guardar archivo WAV

**Ejecutar**:
```bash
./gradlew :sbox:runAudioCaptureTest
```

### 4. VideoDetection.java
**Propósito**: Prueba la detección de sonrisas
- Detección de caras, sonrisas y bocas
- Visualización con rectángulos de colores
- Estadísticas en tiempo real

**Ejecutar**:
```bash
./gradlew :sbox:runSmileDetection
```

## Scripts de Prueba

Los scripts de prueba se encuentran en el directorio `scripts/` del proyecto raíz:

- `scripts/test_camera.sh` - Prueba de cámara
- `scripts/test_screen_recording.sh` - Prueba de grabación de pantalla
- `scripts/test_smile_detection.sh` - Prueba de detección de sonrisas
- `scripts/test_audio_capture.sh` - Prueba de captura de audio
- `scripts/run_all_tests.sh` - Ejecuta todas las pruebas

## Ejecutar Todas las Pruebas

```bash
# Desde el directorio raíz del proyecto
./scripts/run_all_tests.sh

# O usando Gradle
./gradlew :sbox:runAllTests
```

## Configuración

### Requisitos
- Java 8 o superior
- Gradle 7.0+
- Webcam funcional
- Micrófono funcional
- Permisos de acceso a cámara, pantalla y micrófono

### Dependencias
Los tests utilizan las mismas dependencias que la aplicación principal:
- Bytedeco/JavaCV
- OpenCV
- FFmpeg
- Java Sound API

## Troubleshooting

### Error: "No se puede acceder a la cámara"
1. Verificar que la webcam esté conectada
2. Verificar permisos del sistema
3. Verificar que no haya otra aplicación usando la cámara

### Error: "Error al iniciar captura de audio"
1. Verificar que el micrófono esté conectado
2. Verificar permisos de acceso al micrófono
3. Verificar drivers de audio

### Error: "Error al cargar clasificadores Haar"
1. Verificar que los archivos XML estén en `resources/haarcascades/`
2. Verificar permisos de lectura
3. Verificar que OpenCV esté disponible

## Desarrollo

### Agregar Nuevos Tests

1. Crear la clase de test en el directorio apropiado
2. Agregar la tarea Gradle en `build.gradle`
3. Crear el script correspondiente en `scripts/`
4. Actualizar este README

### Ejemplo de Nueva Tarea Gradle

```gradle
task runNewTest(type: JavaExec) {
    group = 'application'
    description = 'Ejecuta la nueva prueba'
    mainClass = 'sbox.proyecto.NewTest'
    classpath = sourceSets.test.runtimeClasspath
}
```

## Notas

- Los tests están diseñados para ser ejecutados individualmente
- Cada test tiene su propia interfaz gráfica
- Los archivos de salida se guardan en la carpeta `recordings/`
- Los tests no son unitarios, son pruebas de integración funcional 