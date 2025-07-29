package sbox.sync;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

/**
 * Grabador de pantalla simplificado sin FFmpeg
 * @author amaldonado
 */
@Slf4j
public class SimpleScreenRecorder {
    
    // Variables para grabación de pantalla
    private Robot robot;
    private Rectangle screenRect;
    private AtomicBoolean running;
    private long startTime;
    private Thread recordingThread;
    
    // Variables de sincronización
    private AtomicLong currentTimestamp = new AtomicLong(0);
    private AtomicLong frameCount = new AtomicLong(0);
    
    // Almacenamiento de frames en memoria
    private ArrayList<BufferedImage> frameBuffer;
    private static final int MAX_FRAMES = 300; // Máximo 10 segundos a 30 FPS
    
    public SimpleScreenRecorder() {
        log.info("SimpleScreenRecorder inicializado");
        frameBuffer = new ArrayList<>();
        running = new AtomicBoolean(false);
        
        try {
            robot = new Robot();
            screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        } catch (AWTException e) {
            log.error("Error al inicializar Robot", e);
        }
    }
    
    public void start() {
        log.info("Iniciando grabación de pantalla simplificada...");
        
        try {
            // Limpiar buffer anterior
            frameBuffer.clear();
            
            running.set(true);
            startTime = System.currentTimeMillis();
            currentTimestamp.set(startTime);
            frameCount.set(0);
            
            // Iniciar thread de grabación
            recordingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running.get()) {
                        try {
                            // Capturar pantalla
                            BufferedImage screenCapture = robot.createScreenCapture(screenRect);
                            
                            // Agregar al buffer (con límite de memoria)
                            synchronized (frameBuffer) {
                                if (frameBuffer.size() < MAX_FRAMES) {
                                    frameBuffer.add(screenCapture);
                                } else {
                                    // Reemplazar el frame más antiguo
                                    frameBuffer.remove(0);
                                    frameBuffer.add(screenCapture);
                                }
                            }
                            
                            // Actualizar estadísticas
                            currentTimestamp.set(System.currentTimeMillis());
                            frameCount.incrementAndGet();
                            
                            // Pequeña pausa para mantener 30 FPS
                            Thread.sleep(33); // ~30 FPS
                            
                        } catch (Exception e) {
                            log.error("Error en grabación de pantalla", e);
                            break;
                        }
                    }
                }
            });
            recordingThread.start();
            
            log.info("Grabación de pantalla simplificada iniciada - Resolución: {}x{}", screenRect.width, screenRect.height);
            
        } catch (Exception e) {
            log.error("Error al iniciar grabación de pantalla", e);
            throw new RuntimeException("No se pudo iniciar la grabación de pantalla", e);
        }
    }
    
    public void stop() {
        log.info("Deteniendo grabación de pantalla simplificada...");
        running.set(false);
        
        if (recordingThread != null) {
            try {
                recordingThread.join(2000); // Esperar máximo 2 segundos
            } catch (InterruptedException e) {
                log.warn("Interrupción al detener thread de grabación", e);
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("Grabación de pantalla simplificada detenida - Frames: {}, Duración: {}ms", 
            frameCount.get(), duration);
    }
    
    /**
     * Verifica si la grabación está activa
     */
    public boolean isRecording() {
        return running.get();
    }
    
    /**
     * Obtiene el timestamp actual de la grabación
     */
    public long getCurrentTimestamp() {
        if (!running.get()) {
            return 0;
        }
        return currentTimestamp.get() - startTime;
    }
    
    /**
     * Obtiene el número de frames grabados
     */
    public long getFrameCount() {
        return frameCount.get();
    }
    
    /**
     * Obtiene el tiempo de inicio de la grabación
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * Verifica si la grabación está sincronizada
     */
    public boolean isInSync(long expectedTimestamp) {
        if (!running.get()) {
            return false;
        }
        
        long currentTime = currentTimestamp.get();
        long elapsedTime = currentTime - startTime;
        long drift = Math.abs(elapsedTime - expectedTimestamp);
        
        // Considerar sincronizado si el drift es menor a 50ms
        return drift <= 50;
    }
    
    /**
     * Obtiene estadísticas de grabación
     */
    public String getRecordingStats() {
        if (running.get()) {
            long duration = System.currentTimeMillis() - startTime;
            long frames = frameCount.get();
            double fps = frames > 0 ? (frames * 1000.0) / duration : 0;
            
            return String.format("Duración: %ds | Frames: %d | FPS: %.1f | Buffer: %d", 
                duration / 1000, frames, fps, frameBuffer.size());
        }
        return "No grabando";
    }
    
    /**
     * Obtiene el buffer de frames
     */
    public ArrayList<BufferedImage> getFrameBuffer() {
        synchronized (frameBuffer) {
            return new ArrayList<>(frameBuffer);
        }
    }
    
    /**
     * Limpia el buffer de frames
     */
    public void clearFrameBuffer() {
        synchronized (frameBuffer) {
            frameBuffer.clear();
        }
    }
    
    /**
     * Guarda un frame específico como imagen
     */
    public File saveFrameAsImage(int frameIndex, String filename) {
        try {
            BufferedImage frame = null;
            synchronized (frameBuffer) {
                if (frameIndex >= 0 && frameIndex < frameBuffer.size()) {
                    frame = frameBuffer.get(frameIndex);
                }
            }
            
            if (frame != null) {
                // Crear directorio si no existe
                File outputDir = new File("recordings");
                if (!outputDir.exists()) {
                    outputDir.mkdirs();
                }
                
                File outputFile = new File(outputDir, filename);
                javax.imageio.ImageIO.write(frame, "PNG", outputFile);
                
                log.info("Frame guardado: {}", outputFile.getAbsolutePath());
                return outputFile;
            }
            
        } catch (Exception e) {
            log.error("Error al guardar frame", e);
        }
        
        return null;
    }
} 