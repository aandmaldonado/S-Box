# S-Box - Aplicación Principal

## Descripción
Aplicación principal del sistema S-Box para análisis multimodal de sonrisas. Este módulo integra todas las funcionalidades de captura, procesamiento y visualización para el análisis científico de emociones humanas.

## Estado Actual (AS-IS)
- **Versión**: 1.0-SNAPSHOT
- **Estado**: En desarrollo/refactorización
- **Java**: Compatible con Java 8+
- **UI**: Swing (NetBeans forms)
- **Dependencias**: Bytedeco/JavaCV stack + VLCJ

## Arquitectura del Proyecto
```
sbox/
├── activityrender/          # Grabación de actividad en pantalla
│   ├── initScreenRecorder.java
│   ├── ScreenRecorder.java
│   └── images/              # Recursos gráficos
├── detection/               # Algoritmos de detección
│   ├── TimeDetection.java
│   └── VideoDetection.java
├── facerecorder/           # Captura de video facial
│   └── WebcamAndMicrophoneCapture.java
├── perspectiva/            # Cliente de perspectiva externa
│   └── PerspectivaCliente.java
├── proyecto/               # Interfaz principal y reproductor
│   ├── ProyectoMain.java   # Ventana principal
│   ├── ProyectoMain.form   # Formulario NetBeans
│   ├── Reproductor.java    # Reproductor multimodal
│   ├── Reproductor.form    # Formulario del reproductor
│   ├── ReproductorSec.java # Reproductor secundario
│   ├── ReproductorSec.form # Formulario secundario
│   ├── Cronometro.java     # Control de tiempo
│   └── PruebasOpenCV.java  # Pruebas de OpenCV
└── resources/              # Recursos del proyecto
    ├── haarcascades/       # Modelos de detección facial
    │   ├── FaceDetection.xml
    │   ├── MouthDetection.xml
    │   └── SmileDetection.xml
    └── images/             # Iconos y recursos gráficos
```

## Funcionalidades Implementadas

### 1. Captura Multimodal
- **Video Facial**: Captura desde webcam con `WebcamAndMicrophoneCapture`
- **Audio**: Grabación de micrófono sincronizada
- **Pantalla**: Grabación de actividad en pantalla con `ScreenRecorder`
- **Perspectiva Externa**: Cliente para recibir video externo

### 2. Detección de Sonrisas
- **Algoritmos OpenCV**: Detección facial y de sonrisas
- **Modelos Haarcascade**: 
  - `FaceDetection.xml` - Detección de rostros
  - `MouthDetection.xml` - Detección de boca
  - `SmileDetection.xml` - Detección de sonrisas
- **Procesamiento en Tiempo Real**: Análisis continuo de frames

### 3. Interfaz de Usuario
- **Ventana Principal**: `ProyectoMain` con controles de grabación
- **Reproductor Multimodal**: `Reproductor` para revisar secuencias
- **Controles de Tiempo**: `Cronometro` para sincronización
- **Interfaz Swing**: Formularios NetBeans para UI

### 4. Procesamiento de Video
- **Sincronización**: Algoritmos para alinear múltiples fuentes
- **Filtrado**: Identificación de episodios relevantes
- **Exportación**: Generación de secuencias para análisis

## Dependencias
```gradle
// Bytedeco/JavaCV stack
implementation 'org.bytedeco:javacv:1.5.8'
implementation 'org.bytedeco:javacpp:1.5.8'
implementation 'org.bytedeco:opencv-platform:4.5.5-1.5.7'
implementation 'org.bytedeco:ffmpeg-platform:4.4-1.5.6'

// VLCJ para reproducción
implementation 'uk.co.caprica:vlcj:3.10.1'

// Utilidades
implementation 'log4j:log4j:1.2.17'
implementation 'net.java.dev.jna:jna:4.5.2'
implementation 'org.swinglabs:swing-worker:1.1'
```

## Compilación y Ejecución

### Requisitos
- Java 8 o superior
- Gradle 7.0+
- VLC Media Player (para VLCJ)
- Webcam funcional

### Compilar
```bash
./gradlew :sbox:build
```

### Ejecutar
```bash
./gradlew :sbox:run
```

O directamente:
```bash
java -cp build/libs/sbox-1.0-SNAPSHOT.jar sbox.proyecto.ProyectoMain
```

## Estructura del Código

### Clases Principales

#### ProyectoMain.java
- **Propósito**: Ventana principal de la aplicación
- **Funcionalidad**: Control de grabación, cámara y reproducción
- **UI**: Interfaz Swing con botones de control
- **Características**: 
  - Control de grabación de video facial
  - Control de grabación de pantalla
  - Integración con reproductor multimodal
  - Sincronización de múltiples fuentes

#### WebcamAndMicrophoneCapture.java
- **Propósito**: Captura de video facial y audio
- **Características**: Sincronización de webcam y micrófono
- **Estado**: En proceso de migración a Bytedeco
- **Funcionalidades**:
  - Captura de video desde webcam
  - Grabación de audio desde micrófono
  - Sincronización de video y audio
  - Interfaz de control para captura

#### ScreenRecorder.java
- **Propósito**: Grabación de actividad en pantalla
- **Funcionalidad**: Captura de pantalla en tiempo real
- **Integración**: Con sistema de sincronización
- **Características**:
  - Grabación de pantalla completa o área específica
  - Control de frame rate y calidad
  - Integración con sistema de archivos
  - Gestión de archivos de grabación

#### Reproductor.java
- **Propósito**: Reproductor multimodal
- **Características**: Reproducción de múltiples fuentes sincronizadas
- **UI**: Interfaz avanzada para revisión de secuencias
- **Funcionalidades**:
  - Reproducción de video con VLCJ
  - Control de progreso con slider
  - Botones de play/pause/stop
  - Sincronización de múltiples fuentes
  - Corte de video (función cutVideo)

#### PerspectivaCliente.java
- **Propósito**: Cliente para comunicación con servidor externo
- **Funcionalidad**: Recepción de video desde fuentes externas
- **Características**:
  - Conexión TCP/IP con servidor (puerto 3000)
  - Recepción de listas de dispositivos
  - Descarga de archivos de video
  - Comunicación bidireccional con servidor

#### VideoDetection.java
- **Propósito**: Detección de sonrisas en tiempo real
- **Funcionalidad**: Análisis de video usando OpenCV
- **Características**:
  - Detección facial con Haarcascades
  - Detección de sonrisas
  - Procesamiento en tiempo real
  - Interfaz de control para detección

### Recursos Importantes

#### Modelos de Detección
- `haarcascades/FaceDetection.xml`: Detección de rostros
- `haarcascades/MouthDetection.xml`: Detección de boca
- `haarcascades/SmileDetection.xml`: Detección de sonrisas

#### Recursos Gráficos
- Iconos para controles de grabación y reproducción
- Cursor personalizado para diferentes estados
- Imágenes de interfaz de usuario

## Configuración Adicional

### VLC Media Player
Para que VLCJ funcione correctamente:
1. Instalar VLC Media Player
2. Configurar variable de entorno `VLC_PLUGIN_PATH`
3. Verificar que VLC esté en el PATH del sistema

### Webcam
- Verificar permisos de acceso a cámara
- Probar con diferentes resoluciones
- Configurar frame rate según necesidades

### Red (Perspectiva Externa)
- Configurar IP del servidor externo
- Verificar puerto 3000 disponible
- Configurar firewall si es necesario

## Notas de Desarrollo
- Código en proceso de migración desde OpenCV legacy
- Importaciones de Bytedeco comentadas temporalmente
- Interfaz Swing funcional pero en proceso de modernización
- Requiere configuración de VLC para reproducción
- Sistema de comunicación en red implementado pero requiere configuración

## Próximos Pasos
1. Completar migración a Bytedeco/JavaCV
2. Mejorar interfaz de usuario
3. Optimizar algoritmos de detección
4. Agregar más opciones de exportación
5. Implementar análisis estadístico avanzado
6. Mejorar sistema de comunicación en red 