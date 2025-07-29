package sbox.activityrender;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import javax.sound.sampled.Mixer;
import javax.swing.SwingConstants;

import lombok.extern.slf4j.Slf4j;

// Importaciones de Bytedeco/JavaCV para grabación
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.IplImage;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * Clase para grabación de pantalla
 * @author amaldonado
 */
@Slf4j
public class ScreenRecorder {
    
    // Variables estáticas
    public static String nombreProyecto = "";
    public static int experimentos = 0;
    
    // Variables para grabación de pantalla
    private FFmpegFrameRecorder recorder;
    private Robot robot;
    private Rectangle screenRect;
    private boolean running;
    private long startTime;
    private Thread recordingThread;
    private BlockingQueue<BufferedImage> frameQueue;
    private Java2DFrameConverter converter;
    private OpenCVFrameConverter.ToIplImage opencvConverter;
    
    // Variables de sincronización
    private AtomicLong currentTimestamp = new AtomicLong(0);
    private AtomicLong frameCount = new AtomicLong(0);
    
    private ArrayList<File> recordedFiles;
    private GraphicsConfiguration cfg;
    private Rectangle areaRect;
    private File movieFolder;
    private Mixer mixer;
    
    public ScreenRecorder() {
        log.info("ScreenRecorder inicializado");
        recordedFiles = new ArrayList<>();
        running = false;
        frameQueue = new ArrayBlockingQueue<>(30); // Buffer de 30 frames
        converter = new Java2DFrameConverter();
        opencvConverter = new OpenCVFrameConverter.ToIplImage();
        
        try {
            robot = new Robot();
            screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        } catch (AWTException e) {
            log.error("Error al inicializar Robot", e);
        }
    }
    
    public void start() {
        log.info("Iniciando grabación de pantalla...");
        
        try {
            // Crear directorio para videos si no existe
            File outputDir = new File("recordings");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            // Configurar grabador FFmpeg
            String outputFile = "recordings/screen_" + nombreProyecto + "_" + experimentos + ".mp4";
            recorder = new FFmpegFrameRecorder(outputFile, screenRect.width, screenRect.height);
            
            // Configurar parámetros de grabación
            recorder.setVideoCodec(org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("mp4");
            recorder.setFrameRate(30);
            recorder.setPixelFormat(org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P);
            
            recorder.start();
            
            running = true;
            startTime = System.currentTimeMillis();
            currentTimestamp.set(startTime);
            frameCount.set(0);
            
            // Iniciar thread de grabación
            recordingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        try {
                            // Capturar pantalla
                            BufferedImage screenCapture = robot.createScreenCapture(screenRect);
                            
                            // Convertir a Frame para FFmpeg
                            Frame frame = converter.convert(screenCapture);
                            recorder.record(frame);
                            
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
            
            log.info("Grabación de pantalla iniciada - Resolución: {}x{}", screenRect.width, screenRect.height);
            
        } catch (Exception e) {
            log.error("Error al iniciar grabación de pantalla", e);
            throw new RuntimeException("No se pudo iniciar la grabación de pantalla", e);
        }
    }
    
    public String stop() {
        log.info("Deteniendo grabación de pantalla...");
        running = false;
        
        if (recordingThread != null) {
            try {
                recordingThread.join(2000); // Esperar máximo 2 segundos
            } catch (InterruptedException e) {
                log.warn("Interrupción al detener thread de grabación", e);
            }
        }
        
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
                
                // Obtener archivo grabado
                String outputFile = "recordings/screen_" + nombreProyecto + "_" + experimentos + ".mp4";
                File recordedFile = new File(outputFile);
                recordedFiles.add(recordedFile);
                
                log.info("Grabación de pantalla detenida - Archivo: {}", recordedFile.getAbsolutePath());
                return recordedFile.getAbsolutePath();
                
            } catch (Exception e) {
                log.error("Error al detener grabador", e);
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * Verifica si la grabación está activa
     */
    public boolean isRecording() {
        return running;
    }
    
    /**
     * Obtiene el timestamp actual de la grabación
     */
    public long getCurrentTimestamp() {
        if (!running) {
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
        if (!running) {
            return false;
        }
        
        long currentTime = currentTimestamp.get();
        long elapsedTime = currentTime - startTime;
        long drift = Math.abs(elapsedTime - expectedTimestamp);
        
        // Considerar sincronizado si el drift es menor a 50ms
        return drift <= 50;
    }
    
    public String getRecordingStats() {
        if (running) {
            long duration = System.currentTimeMillis() - startTime;
            long frames = frameCount.get();
            double fps = frames > 0 ? (frames * 1000.0) / duration : 0;
            
            return String.format("Duración: %ds | Frames: %d | FPS: %.1f", 
                duration / 1000, frames, fps);
        }
        return "No grabando";
    }
    
    public ArrayList<File> getRecordedFiles() {
        return new ArrayList<>(recordedFiles);
    }
    
    public void clearRecordedFiles() {
        recordedFiles.clear();
    }
    
    public static void main(String[] args) {
        ScreenRecorder recorder = new ScreenRecorder();
        ScreenRecorder.nombreProyecto = "test";
        ScreenRecorder.experimentos = 1;
        
        try {
            System.out.println("Iniciando grabación de pantalla...");
            recorder.start();
            
            // Grabar por 5 segundos
            Thread.sleep(5000);
            
            System.out.println("Deteniendo grabación...");
            String outputFile = recorder.stop();
            System.out.println("Archivo guardado: " + outputFile);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
