# Detalle Técnico de la Solución (Tech Solution)

## 1. Arquitectura General
S-Box es una aplicación de escritorio modular para la captura, análisis y visualización de videos multimodales, compuesta por los siguientes módulos principales:

- **Captura Multimodal**: Video facial (webcam), audio (micrófono), pantalla (screen recorder), perspectiva externa (red).
- **Procesamiento y Sincronización**: Detección de sonrisas (OpenCV), sincronización de fuentes, filtrado de episodios relevantes.
- **Visualización y Reproducción**: Reproductor multimodal (VLCJ), interfaz de usuario para análisis.
- **Gestión de Experimentos**: Organización de usuarios, experimentos y secuencias.

## 2. Componentes y Flujos

### Diagrama de Flujo de Componentes
```mermaid
flowchart TD
  subgraph Captura
    A1["Captura de Video Facial (Webcam)"]
    A2["Captura de Audio (Micrófono)"]
    A3["Captura de Pantalla (Screen Recorder)"]
    A4["Captura de Perspectiva Externa (Red)"]
  end
  subgraph Procesamiento
    B1["Detección de Sonrisas (OpenCV + Haarcascades)"]
    B2["Sincronización de Secuencias"]
    B3["Filtrado de Episodios de Sonrisa"]
    B4["Generación de Secuencias Inteligentes"]
  end
  subgraph Visualización
    C1["Reproductor Multimodal (VLCJ)"]
    C2["Visualización de Resultados y Secuencias"]
  end
  A1 --> B1
  A2 --> B2
  A3 --> B2
  A4 --> B2
  B1 --> B3
  B2 --> B3
  B3 --> B4
  B4 --> C1
  C1 --> C2
```

### Diagrama de Red y Sincronización
```mermaid
flowchart TD
  subgraph Red
    S1["PC1: S-Box (Cliente)"]
    S2["PC2: Perspectiva Externa (Servidor)"]
  end
  S1 -- "Solicita dispositivos, frames, videos" --> S2
  S2 -- "Envía video externo, lista de dispositivos" --> S1
  S1 -- "Sincroniza y procesa" --> S1
```

## 3. Dependencias y Tecnologías
- **Lenguaje:** Java 7 (legacy, migrar a Java 17+)
- **IDE:** NetBeans 8.1 (legacy)
- **Visión por Computadora:** OpenCV 2.4.11 (migrar a versión actual)
- **Procesamiento de Video/Audio:** JavaCV, FFmpeg, VLCJ
- **UI:** Swing (migrar a JavaFX o Web)
- **Logging:** log4j
- **Contenedores:** No implementado (recomendado Docker)
- **Automatización:** No implementado (recomendado CI/CD)

## 4. Modelo de Dominio
```mermaid
erDiagram
  USUARIO ||--o{ EXPERIMENTO : realiza
  EXPERIMENTO ||--|{ SECUENCIA : contiene
  SECUENCIA ||--|{ VIDEO : incluye
  USUARIO {
    string id
    string nombre
    string correo
  }
  EXPERIMENTO {
    string id
    string descripcion
    date fecha
  }
  SECUENCIA {
    string id
    string tipo
    string path
    int duracion
  }
  VIDEO {
    string id
    string formato
    string path
    int fps
  }
```

## 5. Roadmap de Modernización

### Diagrama de Roadmap
```mermaid
flowchart TD
  subgraph Modernizacion
    M1["Migrar a Java 17+"]
    M2["Reemplazar OpenCV 2.4.11 por versión actual"]
    M3["Actualizar VLCJ y dependencias multimedia"]
    M4["Contenerización (Docker)"]
    M5["Automatización CI/CD"]
    M6["Documentación y pruebas automatizadas"]
    M7["UI/UX moderna (JavaFX o Web)"]
  end
  M1 --> M2
  M2 --> M3
  M3 --> M4
  M4 --> M5
  M5 --> M6
  M6 --> M7
```

### Acciones Clave
- Migrar el código fuente a Java 17+ y modularizar.
- Actualizar dependencias a versiones actuales (OpenCV, VLCJ, JavaCV, FFmpeg).
- Implementar contenedores Docker para despliegue y pruebas.
- Automatizar build, pruebas y despliegue con CI/CD (GitHub Actions, Jenkins, etc).
- Rediseñar la UI con JavaFX o migrar a una SPA web (React, Angular, etc).
- Mejorar la documentación y cobertura de pruebas.
- Asegurar cumplimiento de estándares de seguridad y privacidad.

## 6. Consideraciones de Seguridad y Privacidad
- Encriptación de datos sensibles.
- Control de acceso y autenticación.
- Cumplimiento de normativas de protección de datos.

## 7. Escalabilidad y Mantenibilidad
- Modularización del código.
- Separación de responsabilidades (captura, procesamiento, visualización).
- Uso de contenedores y automatización para facilitar despliegue y mantenimiento.
