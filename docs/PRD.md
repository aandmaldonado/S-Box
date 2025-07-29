# Product Requirements Document (PRD)

## Visión General
S-Box es una plataforma de análisis multimodal de videos centrada en el estudio de la sonrisa y las respuestas emocionales humanas, orientada a investigadores, psicólogos y profesionales de la computación afectiva. El sistema permite capturar, sincronizar, analizar y visualizar videos de expresiones faciales, actividad en pantalla y perspectivas externas, utilizando inteligencia artificial y visión por computadora.

## Objetivos de Negocio
- Proveer una herramienta robusta y moderna para el análisis de sonrisas y emociones en entornos experimentales controlados.
- Facilitar la captura y sincronización de múltiples fuentes de video y audio.
- Permitir la detección automática de episodios de sonrisa y la generación de secuencias relevantes para el análisis.
- Ofrecer visualización avanzada y reproductores multimodales para el estudio detallado de los datos.
- Modernizar la solución para su despliegue en entornos productivos y facilitar su mantenimiento y escalabilidad.

## Actores Clave
- **Investigador**: Diseña experimentos, configura la captura y analiza los resultados.
- **Participante**: Interactúa con el sistema durante los experimentos.
- **Administrador**: Gestiona la infraestructura, usuarios y mantenimiento del sistema.

## Casos de Uso Principales
- **Captura Multimodal**: Registrar simultáneamente video facial, audio, actividad en pantalla y video externo.
- **Sincronización y Procesamiento**: Sincronizar automáticamente las fuentes y detectar episodios de sonrisa mediante IA.
- **Filtrado y Generación de Secuencias**: Identificar y extraer segmentos relevantes para el análisis.
- **Visualización y Reproducción**: Permitir la revisión sincronizada de las secuencias desde diferentes perspectivas.
- **Exportación y Reportes**: Generar reportes y exportar datos para análisis externo.

## Contexto de Modernización
El sistema original fue desarrollado como proyecto universitario sobre tecnologías legacy (Java 7, OpenCV 2.4.11, VLCJ antiguo, NetBeans 8.1). Se requiere migrar a tecnologías actuales (Java 17+, OpenCV moderno, contenedores, CI/CD, UI moderna) para asegurar mantenibilidad, escalabilidad y despliegue productivo.

## Diagrama de Alto Nivel

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

## Modelo de Dominio

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

## Requerimientos No Funcionales
- Compatibilidad multiplataforma (Windows, Linux, MacOS)
- Despliegue en contenedores (Docker)
- Seguridad y privacidad de datos
- Escalabilidad y mantenibilidad
- Automatización de pruebas y CI/CD
