# Perspectiva Externa

## Descripción
Servidor de captura de perspectiva externa para el sistema S-Box. Este módulo se encarga de capturar y transmitir video desde fuentes externas (cámaras IP, streams de red, etc.) para proporcionar una perspectiva adicional en el análisis multimodal de sonrisas.

## Estado Actual (AS-IS)
- **Versión**: 1.0-SNAPSHOT
- **Estado**: En desarrollo/refactorización
- **Java**: Compatible con Java 8+
- **Dependencias**: Bytedeco/JavaCV stack
- **Protocolo**: TCP/IP (puerto 3000)

## Arquitectura
```
PerspectivaServidor
├── PerspectivaServidor.java (Clase principal)
├── VentanaLog.java (Interfaz de logging)
├── Video.java (Manejo de video)
└── VentanaLog.form (Formulario NetBeans)
```

## Funcionalidades Implementadas
- **Servidor de Captura**: Captura de video desde fuentes externas
- **Interfaz de Logging**: Ventana para monitorear el estado del servidor
- **Manejo de Video**: Procesamiento básico de streams de video
- **Comunicación en Red**: Capacidad de transmitir video a clientes
- **Gestión de Dispositivos**: Listado y control de dispositivos de captura

## Dependencias
```gradle
implementation 'org.bytedeco:javacv:1.5.8'
implementation 'org.bytedeco:javacpp:1.5.8'
implementation 'org.bytedeco:opencv-platform:4.5.5-1.5.7'
implementation 'org.bytedeco:ffmpeg-platform:4.4-1.5.6'
```

## Compilación y Ejecución

### Requisitos
- Java 8 o superior
- Gradle 7.0+
- Acceso a fuentes de video externas

### Compilar
```bash
./gradlew :perspectivaExterna:build
```

### Ejecutar
```bash
./gradlew :perspectivaExterna:run
```

O directamente:
```bash
java -cp build/libs/perspectivaExterna-1.0-SNAPSHOT.jar sbox.perspectiva.PerspectivaServidor
```

## Estructura del Código

### PerspectivaServidor.java
- **Clase Principal**: Punto de entrada del servidor
- **Funcionalidad**: Inicialización y control del servidor de captura
- **Estado**: Código comentado en proceso de refactorización
- **Características**:
  - Control de inicio/parada del servidor
  - Gestión de conexiones de clientes
  - Control de captura de video
  - Integración con sistema de logging

### VentanaLog.java
- **Propósito**: Interfaz gráfica para monitoreo
- **Características**: Logging en tiempo real del estado del servidor
- **Formulario**: VentanaLog.form (NetBeans)
- **Funcionalidades**:
  - Visualización de logs en tiempo real
  - Control de nivel de logging
  - Interfaz para monitoreo de estado
  - Gestión de eventos del servidor

### Video.java
- **Propósito**: Manejo de streams de video
- **Funcionalidad**: Captura y procesamiento de video externo
- **Características**:
  - Captura de video desde múltiples fuentes
  - Procesamiento de frames
  - Gestión de formatos de video
  - Optimización de rendimiento

## Protocolo de Comunicación

### Conexión TCP/IP
- **Puerto**: 3000
- **Protocolo**: TCP/IP
- **Formato**: Comunicación bidireccional con objetos serializados

### Comandos del Servidor
- **Lista de Dispositivos**: Envío de dispositivos disponibles
- **Captura de Frames**: Transmisión de frames de video
- **Control de Video**: Inicio/parada de captura
- **Gestión de Archivos**: Envío de archivos de video

### Interacción con Cliente
El servidor se comunica con `PerspectivaCliente` en el módulo principal:
- Recibe solicitudes de dispositivos
- Transmite frames de video en tiempo real
- Envía archivos de video completos
- Proporciona información de estado

## Configuración de Red

### Requisitos de Red
- **Puerto**: 3000 debe estar disponible
- **Firewall**: Configurar para permitir conexiones TCP
- **IP**: Configurar IP del servidor en el cliente

### Configuración del Servidor
```java
// Puerto por defecto
private final int port = 3000;

// Configuración de conexión
cliente = new Socket(InetAddress.getByName(servidor), port);
```

## Integración con S-Box Principal

### Comunicación Bidireccional
- **Cliente → Servidor**: Solicitudes de dispositivos y video
- **Servidor → Cliente**: Envío de frames y archivos
- **Sincronización**: Coordinación de captura y transmisión

### Flujo de Datos
1. Cliente solicita lista de dispositivos
2. Servidor envía dispositivos disponibles
3. Cliente solicita captura de video
4. Servidor transmite frames en tiempo real
5. Cliente procesa y sincroniza con otras fuentes

## Notas de Desarrollo
- El código actual tiene importaciones comentadas de Bytedeco/JavaCV
- En proceso de migración desde OpenCV legacy
- Requiere configuración adicional para fuentes de video externas
- Sistema de comunicación en red implementado pero requiere pruebas
- Interfaz de logging funcional para monitoreo

## Configuración Adicional

### Fuentes de Video Externas
- **Cámaras IP**: Configurar URLs de streams
- **Archivos de Video**: Rutas a archivos locales
- **Streams de Red**: URLs de transmisiones en vivo
- **Dispositivos USB**: Cámaras conectadas localmente

### Optimización de Rendimiento
- **Frame Rate**: Configurar según capacidad de red
- **Resolución**: Ajustar según ancho de banda
- **Compresión**: Optimizar para transmisión en red
- **Buffer**: Configurar buffer de transmisión

## Próximos Pasos
1. Descomentar y actualizar importaciones de Bytedeco
2. Implementar captura real de fuentes externas
3. Agregar configuración de red avanzada
4. Mejorar manejo de errores y logging
5. Implementar autenticación y seguridad
6. Optimizar transmisión de video en red
7. Agregar soporte para múltiples clientes 