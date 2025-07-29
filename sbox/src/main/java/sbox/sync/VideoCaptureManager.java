package sbox.sync;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.IplImage;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * Gestor de captura de video con sincronización
 * @author amaldonado
 */
@Slf4j
public class VideoCaptureManager {
    
    // Estados de captura
    private AtomicBoolean isCapturing = new AtomicBoolean(false);
    private AtomicLong startTimestamp = new AtomicLong(0);
    private AtomicLong currentTimestamp = new AtomicLong(0);
    private AtomicLong frameCount = new AtomicLong(0);
    
    // Componentes de captura
    private FrameGrabber grabber;
    private OpenCVFrameConverter.ToIplImage converter;
    private Java2DFrameConverter java2DConverter;
    private IplImage grabbedImage = null;
    private IplImage grayImage = null;
    private Thread captureThread;
    
    // Almacenamiento de frames en memoria
    private ArrayList<BufferedImage> frameBuffer;
    private static final int MAX_FRAMES = 300; // Máximo 10 segundos a 30 FPS
    
    // Configuración
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    private static final int FPS = 30;
    private static final long FRAME_INTERVAL_MS = 1000 / FPS; // ~33ms
    
    // Callbacks
    private VideoFrameCallback frameCallback;
    private VideoErrorCallback errorCallback;
    
    public interface VideoFrameCallback {
        void onFrameCaptured(BufferedImage frame, long timestamp);
    }
    
    public interface VideoErrorCallback {
        void onError(String error, long timestamp);
    }
    
    public VideoCaptureManager() {
        log.info("VideoCaptureManager inicializado");
        frameBuffer = new ArrayList<>();
    }
    
    /**
     * Inicia la captura de video
     */
    public void startCapture() {
        if (isCapturing.get()) {
            log.warn("La captura de video ya está activa");
            return;
        }
        
        try {
            log.info("Iniciando captura de video...");
            
            // Inicializar componentes
            grabber = new OpenCVFrameGrabber(0);
            grabber.setImageWidth(WIDTH);
            grabber.setImageHeight(HEIGHT);
            grabber.setFrameRate(FPS);
            grabber.start();
            
            converter = new OpenCVFrameConverter.ToIplImage();
            java2DConverter = new Java2DFrameConverter();
            
            // Crear imágenes OpenCV
            grabbedImage = cvCreateImage(cvSize(WIDTH, HEIGHT), IPL_DEPTH_8U, 3);
            grayImage = cvCreateImage(cvSize(WIDTH, HEIGHT), IPL_DEPTH_8U, 1);
            
            // Limpiar buffer anterior
            frameBuffer.clear();
            
            // Inicializar timestamps
            startTimestamp.set(System.currentTimeMillis());
            currentTimestamp.set(startTimestamp.get());
            frameCount.set(0);
            
            // Marcar como capturando
            isCapturing.set(true);
            
            // Iniciar thread de captura
            captureThread = new Thread(() -> {
                while (isCapturing.get()) {
                    try {
                        // Capturar frame
                        Frame frame = grabber.grab();
                        if (frame != null) {
                            // Convertir a IplImage
                            IplImage colorImage = converter.convert(frame);
                            
                            // Redimensionar si es necesario
                            if (colorImage.width() != WIDTH || colorImage.height() != HEIGHT) {
                                IplImage resizedImage = cvCreateImage(cvSize(WIDTH, HEIGHT), IPL_DEPTH_8U, 3);
                                cvResize(colorImage, resizedImage);
                                colorImage = resizedImage;
                            }
                            
                            // Convertir a escala de grises
                            cvCvtColor(colorImage, grayImage, CV_BGR2GRAY);
                            
                            // Convertir a BufferedImage
                            BufferedImage bufferedImage = java2DConverter.convert(frame);
                            
                            // Agregar al buffer (con límite de memoria)
                            synchronized (frameBuffer) {
                                if (frameBuffer.size() < MAX_FRAMES) {
                                    frameBuffer.add(bufferedImage);
                                } else {
                                    // Reemplazar el frame más antiguo
                                    frameBuffer.remove(0);
                                    frameBuffer.add(bufferedImage);
                                }
                            }
                            
                            // Actualizar estadísticas
                            currentTimestamp.set(System.currentTimeMillis());
                            frameCount.incrementAndGet();
                            
                            // Notificar callback
                            if (frameCallback != null) {
                                frameCallback.onFrameCaptured(bufferedImage, currentTimestamp.get());
                            }
                            
                            // Pequeña pausa para mantener FPS
                            Thread.sleep(FRAME_INTERVAL_MS);
                        }
                        
                    } catch (Exception e) {
                        log.error("Error en captura de video", e);
                        if (errorCallback != null) {
                            errorCallback.onError("Error de captura: " + e.getMessage(), 
                                System.currentTimeMillis());
                        }
                        break;
                    }
                }
            });
            
            captureThread.setName("VideoCaptureThread");
            captureThread.start();
            
            log.info("Captura de video iniciada - Resolución: {}x{}, FPS: {}", WIDTH, HEIGHT, FPS);
            
        } catch (Exception e) {
            log.error("Error al iniciar captura de video", e);
            throw new RuntimeException("No se pudo iniciar la captura de video", e);
        }
    }
    
    /**
     * Detiene la captura de video
     */
    public void stopCapture() {
        if (!isCapturing.get()) {
            log.warn("La captura de video no está activa");
            return;
        }
        
        log.info("Deteniendo captura de video...");
        
        // Marcar como no capturando
        isCapturing.set(false);
        
        // Detener thread
        if (captureThread != null) {
            try {
                captureThread.join(2000); // Esperar máximo 2 segundos
            } catch (InterruptedException e) {
                log.warn("Interrupción al detener thread de captura", e);
            }
        }
        
        // Liberar recursos
        try {
            if (grabber != null) {
                grabber.stop();
                grabber.release();
            }
            
            if (grabbedImage != null) {
                cvReleaseImage(grabbedImage);
                grabbedImage = null;
            }
            
            if (grayImage != null) {
                cvReleaseImage(grayImage);
                grayImage = null;
            }
            
        } catch (Exception e) {
            log.error("Error al liberar recursos de video", e);
        }
        
        long duration = System.currentTimeMillis() - startTimestamp.get();
        log.info("Captura de video detenida - Frames: {}, Duración: {}ms", 
            frameCount.get(), duration);
    }
    
    /**
     * Verifica si la captura está activa
     */
    public boolean isCapturing() {
        return isCapturing.get();
    }
    
    /**
     * Obtiene el timestamp actual de la captura
     */
    public long getCurrentTimestamp() {
        if (!isCapturing.get()) {
            return 0;
        }
        return currentTimestamp.get() - startTimestamp.get();
    }
    
    /**
     * Obtiene el número de frames capturados
     */
    public long getFrameCount() {
        return frameCount.get();
    }
    
    /**
     * Obtiene el tiempo de inicio de la captura
     */
    public long getStartTimestamp() {
        return startTimestamp.get();
    }
    
    /**
     * Verifica si la captura está sincronizada
     */
    public boolean isInSync(long expectedTimestamp) {
        if (!isCapturing.get()) {
            return false;
        }
        
        long currentTime = currentTimestamp.get();
        long elapsedTime = currentTime - startTimestamp.get();
        long drift = Math.abs(elapsedTime - expectedTimestamp);
        
        // Considerar sincronizado si el drift es menor a 50ms
        return drift <= 50;
    }
    
    /**
     * Obtiene estadísticas de captura
     */
    public String getCaptureStats() {
        if (isCapturing.get()) {
            long duration = System.currentTimeMillis() - startTimestamp.get();
            long frames = frameCount.get();
            double fps = frames > 0 ? (frames * 1000.0) / duration : 0;
            
            return String.format("Duración: %ds | Frames: %d | FPS: %.1f | Buffer: %d", 
                duration / 1000, frames, fps, frameBuffer.size());
        }
        return "No capturando";
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
                
                log.info("Frame de video guardado: {}", outputFile.getAbsolutePath());
                return outputFile;
            }
            
        } catch (Exception e) {
            log.error("Error al guardar frame de video", e);
        }
        
        return null;
    }
    
    /**
     * Guarda video como archivo (simulado - guarda frames como imágenes)
     */
    public File saveToFile(String filename) throws IOException {
        try {
            // Crear directorio si no existe
            File outputDir = new File("recordings");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            // Guardar algunos frames representativos
            ArrayList<BufferedImage> frames = getFrameBuffer();
            int totalFrames = frames.size();
            
            // Guardar frame inicial, medio y final
            if (totalFrames > 0) {
                saveFrameAsImage(0, filename.replace(".mp4", "_frame_001.png"));
                if (totalFrames > 1) {
                    saveFrameAsImage(totalFrames / 2, filename.replace(".mp4", "_frame_mid.png"));
                    saveFrameAsImage(totalFrames - 1, filename.replace(".mp4", "_frame_end.png"));
                }
            }
            
            // Crear archivo de información de video
            File infoFile = new File(outputDir, filename.replace(".mp4", "_info.txt"));
            String info = String.format("Video Info:\nTotal Frames: %d\nDuration: %dms\nFPS: %.1f\nResolution: %dx%d\nBuffer Size: %d", 
                totalFrames, getCurrentTimestamp(), 
                totalFrames > 0 ? (totalFrames * 1000.0) / getCurrentTimestamp() : 0,
                WIDTH, HEIGHT, frames.size());
            
            java.nio.file.Files.write(infoFile.toPath(), info.getBytes());
            
            log.info("Video guardado como frames - Frames: {}, Archivo: {}", totalFrames, infoFile.getName());
            return infoFile;
            
        } catch (Exception e) {
            log.error("Error al guardar video", e);
            throw new IOException("No se pudo guardar el video", e);
        }
    }
    
    // Setters para callbacks
    
    public void setFrameCallback(VideoFrameCallback callback) {
        this.frameCallback = callback;
    }
    
    public void setErrorCallback(VideoErrorCallback callback) {
        this.errorCallback = callback;
    }
} 