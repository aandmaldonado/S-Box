# Scripts de Prueba - S-Box

Esta carpeta contiene scripts para automatizar las pruebas de funcionalidad de S-Box.

## Scripts Disponibles

### Scripts Individuales

#### `test_camera.sh`
Prueba la funcionalidad básica de captura de cámara.
```bash
./scripts/test_camera.sh
```

#### `test_screen_recording.sh`
Prueba la grabación de pantalla.
```bash
./scripts/test_screen_recording.sh
```

#### `test_smile_detection.sh`
Prueba la detección de sonrisas usando OpenCV.
```bash
./scripts/test_smile_detection.sh
```

#### `test_audio_capture.sh`
Prueba la captura de audio desde micrófono.
```bash
./scripts/test_audio_capture.sh
```

### Script Maestro

#### `run_all_tests.sh`
Ejecuta todas las pruebas de funcionalidad en secuencia.
```bash
./scripts/run_all_tests.sh
```

## Uso

### Ejecutar desde el directorio raíz del proyecto

```bash
# Ir al directorio raíz del proyecto
cd /path/to/S-Box

# Ejecutar script individual
./scripts/test_camera.sh

# Ejecutar todas las pruebas
./scripts/run_all_tests.sh
```

### Ejecutar desde cualquier ubicación

Los scripts están diseñados para funcionar desde cualquier ubicación, ya que automáticamente navegan al directorio raíz del proyecto.

```bash
# Desde cualquier ubicación
/path/to/S-Box/scripts/test_camera.sh
```

## Funcionalidad

### Compilación Automática
Todos los scripts compilan automáticamente el proyecto antes de ejecutar las pruebas.

### Manejo de Errores
- Verificación de compilación exitosa
- Mensajes informativos de progreso
- Salida con códigos de error apropiados

### Interfaz de Usuario
- Instrucciones claras para el usuario
- Indicadores de progreso
- Información sobre qué hacer en cada paso

## Requisitos

### Sistema
- macOS, Linux o Windows (con WSL)
- Bash shell
- Permisos de ejecución en los scripts

### Proyecto
- Gradle instalado y configurado
- Java 8 o superior
- Todas las dependencias del proyecto instaladas

## Troubleshooting

### Error: "Permission denied"
```bash
chmod +x scripts/*.sh
```

### Error: "No such file or directory"
Verificar que estás ejecutando desde el directorio raíz del proyecto o que la ruta al proyecto es correcta.

### Error: "Gradle not found"
Asegurar que Gradle esté instalado y en el PATH del sistema.

### Error: "Java not found"
Asegurar que Java esté instalado y configurado correctamente.

## Personalización

### Modificar Scripts
Los scripts pueden ser modificados para:
- Cambiar parámetros de compilación
- Agregar nuevas pruebas
- Modificar el comportamiento de las pruebas
- Cambiar mensajes de salida

### Agregar Nuevos Scripts
1. Crear el nuevo script con extensión `.sh`
2. Hacerlo ejecutable: `chmod +x scripts/nuevo_script.sh`
3. Agregar la tarea Gradle correspondiente en `build.gradle`
4. Actualizar este README

## Integración con CI/CD

Los scripts pueden ser integrados en pipelines de CI/CD:

```yaml
# Ejemplo para GitHub Actions
- name: Run S-Box Tests
  run: |
    chmod +x scripts/*.sh
    ./scripts/run_all_tests.sh
```

## Notas

- Los scripts están diseñados para ser ejecutados en entornos de desarrollo
- No están optimizados para entornos de producción
- Requieren interfaz gráfica para las pruebas de UI
- Los archivos de salida se guardan en la carpeta `recordings/` 