# S-Box

S-Box: Plataforma para el análisis multimodal de videos asociados al fenómeno de la sonrisa

## Descripción General
S-Box es una solución de Computación Afectiva que utiliza Inteligencia Artificial y Visión por Computadora para analizar videos de sonrisas y respuestas emocionales. Permite la captura sincronizada de video facial, audio, actividad en pantalla y perspectivas externas, facilitando el análisis científico y profesional de emociones humanas.

## Arquitectura del Proyecto
Este es un proyecto monorepo que contiene dos módulos principales:

### 📁 sbox/ - Aplicación Principal
Aplicación completa para análisis multimodal de sonrisas con:
- **Captura Multimodal**: Video facial, audio, pantalla y perspectiva externa
- **Detección de Sonrisas**: Algoritmos OpenCV con modelos Haarcascade
- **Interfaz de Usuario**: Swing con formularios NetBeans
- **Reproductor Multimodal**: Visualización sincronizada de múltiples fuentes

### 📁 perspectivaExterna/ - Servidor de Captura Externa
Servidor para capturar y transmitir video desde fuentes externas:
- **Captura de Video**: Streams de red, cámaras IP, fuentes externas
- **Transmisión**: Comunicación en red para perspectivas adicionales
- **Interfaz de Monitoreo**: Logging y control del servidor

## Características Implementadas

### 🎥 Captura Multimodal
- **Video Facial**: Captura desde webcam con sincronización de audio
- **Grabación de Pantalla**: Actividad en pantalla en tiempo real
- **Perspectiva Externa**: Captura de fuentes de video externas
- **Sincronización**: Algoritmos para alinear múltiples fuentes

### 🤖 Detección de Sonrisas
- **Algoritmos OpenCV**: Detección facial y de expresiones
- **Modelos Haarcascade**: 
  - Detección de rostros
  - Detección de boca
  - Detección de sonrisas
- **Procesamiento en Tiempo Real**: Análisis continuo de frames

### 🎮 Interfaz de Usuario
- **Ventana Principal**: Controles de grabación y reproducción
- **Reproductor Multimodal**: Revisión de secuencias desde múltiples perspectivas
- **Controles de Tiempo**: Sincronización y cronometro
- **Interfaz Swing**: Formularios NetBeans para UI

### 📊 Procesamiento y Análisis
- **Filtrado Inteligente**: Identificación de episodios relevantes
- **Generación de Secuencias**: Creación de clips para análisis
- **Exportación**: Datos y reportes para análisis externo

## Estado Actual (AS-IS)

### Tecnologías Utilizadas
- **Java**: 8+ (compatible)
- **Gradle**: Sistema de build
- **OpenCV**: 4.5.5 (Bytedeco/JavaCV stack)
- **VLCJ**: 3.10.1 para reproducción multimedia
- **Swing**: Interfaz de usuario
- **NetBeans**: Formularios de diseño

### Dependencias Principales
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

## Instalación y Configuración

### Requisitos del Sistema
- **Java**: 8 o superior
- **Gradle**: 7.0+
- **VLC Media Player**: Para reproducción multimedia
- **Webcam**: Para captura de video facial
- **Permisos**: Acceso a cámara y micrófono

### Configuración de VLC
Para que VLCJ funcione correctamente:
1. Instalar VLC Media Player
2. Configurar variable de entorno `VLC_PLUGIN_PATH`
3. Verificar que VLC esté en el PATH del sistema

### Compilación del Proyecto
```bash
# Compilar todo el proyecto
./gradlew build

# Compilar módulo específico
./gradlew :sbox:build
./gradlew :perspectivaExterna:build
```

### Ejecución
```bash
# Ejecutar aplicación principal
./gradlew :sbox:run

# Ejecutar servidor de perspectiva externa
./gradlew :perspectivaExterna:run
```

## Estructura del Proyecto
```
S-Box/
├── build.gradle              # Configuración Gradle raíz
├── settings.gradle           # Configuración de módulos
├── gradlew                   # Wrapper Gradle (Unix/macOS)
├── sbox/                     # Aplicación principal
│   ├── build.gradle
│   ├── README.md             # Documentación específica
│   └── src/main/java/sbox/
│       ├── activityrender/   # Grabación de pantalla
│       ├── detection/        # Algoritmos de detección
│       ├── facerecorder/     # Captura facial
│       ├── perspectiva/      # Cliente externo
│       ├── proyecto/         # UI principal
│       └── resources/        # Recursos (haarcascades, imágenes)
├── perspectivaExterna/       # Servidor externo
│   ├── build.gradle
│   ├── README.md             # Documentación específica
│   └── src/main/java/sbox/perspectiva/
└── docs/                     # Documentación general
    ├── PRD.md
    └── TECH_SOLUTION.md
```

## Uso del Sistema

### 1. Iniciar Aplicación Principal
```bash
./gradlew :sbox:run
```

### 2. Configurar Captura
- Conectar webcam
- Verificar micrófono
- Configurar resolución de pantalla

### 3. Iniciar Grabación
- Usar controles en la interfaz principal
- Monitorear estado de captura
- Verificar sincronización

### 4. Análisis y Reproducción
- Usar reproductor multimodal
- Revisar secuencias detectadas
- Exportar datos para análisis

## Documentación Específica
- **[S-Box Principal](sbox/README.md)**: Documentación completa de la aplicación principal
- **[Perspectiva Externa](perspectivaExterna/README.md)**: Documentación del servidor externo
- **[PRD](docs/PRD.md)**: Requerimientos de negocio
- **[TECH_SOLUTION](docs/TECH_SOLUTION.md)**: Solución técnica y roadmap

## Notas de Desarrollo
- Código en proceso de refactorización
- Migración desde OpenCV legacy a Bytedeco/JavaCV
- Interfaz Swing funcional con formularios NetBeans
- Requiere configuración adicional para fuentes externas

## Licencia
Proyecto académico, licencia abierta a definir.

