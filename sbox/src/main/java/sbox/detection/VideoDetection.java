package sbox.detection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import lombok.extern.slf4j.Slf4j;

// Importaciones de Bytedeco/JavaCV para detección
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_objdetect.*;

/**
 * Clase para detección de video y sonrisas
 * @author amaldonado
 */
@Slf4j
public class VideoDetection extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Variables para detección
    private FrameGrabber grabber;
    private OpenCVFrameConverter.ToMat converter;
    private Java2DFrameConverter java2DConverter;
    private Mat grabbedImage = null;
    private Mat grayImage = null;
    private boolean isDetecting = false;
    private Thread detectionThread;
    private BufferedImage currentFrame = null;
    
    // Clasificadores Haar
    private CascadeClassifier faceClassifier;
    private CascadeClassifier smileClassifier;
    private CascadeClassifier mouthClassifier;
    
    // Estadísticas de detección
    private int faceCount = 0;
    private int smileCount = 0;
    private int mouthCount = 0;
    
    private JPanel mainPanel;
    private JButton btnIniciarDeteccion;
    private JButton btnDetenerDeteccion;
    private JLabel lblEstado;
    private JLabel lblStats;
    private DetectionPanel detectionPanel;
    
    public VideoDetection() {
        initComponents();
        setupLayout();
        setupWindowListener();
        loadClassifiers();
    }
    
    private void initComponents() {
        setTitle("S-Box - Detección de Sonrisas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de controles
        JPanel controlPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        btnIniciarDeteccion = new JButton("Iniciar Detección");
        btnDetenerDeteccion = new JButton("Detener Detección");
        lblEstado = new JLabel("Estado: Listo");
        lblStats = new JLabel("Estadísticas: -");
        
        // Configurar botones
        btnIniciarDeteccion.setFont(new Font("Arial", Font.BOLD, 14));
        btnDetenerDeteccion.setFont(new Font("Arial", Font.BOLD, 14));
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 14));
        lblStats.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Agregar listeners
        btnIniciarDeteccion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarDeteccion();
            }
        });
        
        btnDetenerDeteccion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detenerDeteccion();
            }
        });
        
        controlPanel.add(btnIniciarDeteccion);
        controlPanel.add(btnDetenerDeteccion);
        controlPanel.add(lblEstado);
        controlPanel.add(lblStats);
        
        // Panel de detección
        detectionPanel = new DetectionPanel();
        detectionPanel.setPreferredSize(new Dimension(640, 480));
        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(detectionPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void setupLayout() {
        // Configuración adicional del layout si es necesaria
    }
    
    private void setupWindowListener() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                detenerDeteccion();
            }
        });
    }
    
    private void loadClassifiers() {
        try {
            // Cargar clasificadores Haar desde recursos
            String facePath = getClass().getResource("/haarcascades/FaceDetection.xml").getPath();
            String smilePath = getClass().getResource("/haarcascades/SmileDetection.xml").getPath();
            String mouthPath = getClass().getResource("/haarcascades/MouthDetection.xml").getPath();
            
            faceClassifier = new CascadeClassifier(facePath);
            smileClassifier = new CascadeClassifier(smilePath);
            mouthClassifier = new CascadeClassifier(mouthPath);
            
            log.info("Clasificadores Haar cargados exitosamente");
            
        } catch (Exception e) {
            log.error("Error al cargar clasificadores Haar", e);
        }
    }
    
    private void iniciarDeteccion() {
        log.info("Iniciando detección de sonrisas...");
        lblEstado.setText("Estado: Iniciando detección...");
        
        try {
            // Inicializar grabber
            grabber = new OpenCVFrameGrabber(0);
            grabber.start();
            
            // Inicializar convertidores
            converter = new OpenCVFrameConverter.ToMat();
            java2DConverter = new Java2DFrameConverter();
            
            // Crear imágenes
            grabbedImage = new Mat();
            grayImage = new Mat();
            
            isDetecting = true;
            lblEstado.setText("Estado: Detectando...");
            
            // Reiniciar contadores
            faceCount = 0;
            smileCount = 0;
            mouthCount = 0;
            
            // Iniciar thread de detección
            detectionThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isDetecting) {
                        try {
                            Frame frame = grabber.grab();
                            if (frame != null) {
                                // Convertir frame a Mat
                                Mat image = converter.convert(frame);
                                if (image != null) {
                                    // Redimensionar si es necesario
                                    if (image.cols() != 640 || image.rows() != 480) {
                                        Mat resized = new Mat();
                                        org.bytedeco.opencv.global.opencv_imgproc.resize(image, resized, new Size(640, 480));
                                        image = resized;
                                    }
                                    
                                    // Copiar a grabbedImage
                                    image.copyTo(grabbedImage);
                                    
                                    // Convertir a escala de grises
                                    cvtColor(grabbedImage, grayImage, COLOR_BGR2GRAY);
                                    
                                    // Realizar detecciones
                                    performDetections(grabbedImage, grayImage);
                                    
                                    // Convertir a BufferedImage para mostrar
                                    Frame displayFrame = converter.convert(grabbedImage);
                                    BufferedImage bufferedImage = java2DConverter.convert(displayFrame);
                                    
                                    // Actualizar UI en EDT
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            currentFrame = bufferedImage;
                                            detectionPanel.setFrame(currentFrame);
                                            detectionPanel.repaint();
                                            
                                            // Actualizar estadísticas
                                            lblStats.setText(String.format("Caras: %d | Sonrisas: %d | Bocas: %d", 
                                                faceCount, smileCount, mouthCount));
                                        }
                                    });
                                }
                            }
                            Thread.sleep(33); // ~30 FPS
                        } catch (Exception e) {
                            log.error("Error en detección", e);
                            break;
                        }
                    }
                }
            });
            detectionThread.start();
            
        } catch (Exception e) {
            log.error("Error al iniciar detección", e);
            lblEstado.setText("Estado: Error al iniciar detección - " + e.getMessage());
        }
    }
    
    private void performDetections(Mat colorImage, Mat grayImage) {
        try {
            // Detectar caras
            RectVector faces = new RectVector();
            faceClassifier.detectMultiScale(grayImage, faces, 1.1, 3, 0, 
                new Size(30, 30), new Size());
            
            faceCount = (int) faces.size();
            
            // Para cada cara detectada, buscar sonrisas y bocas
            for (long i = 0; i < faces.size(); i++) {
                Rect faceRect = faces.get(i);
                
                // Extraer región de la cara
                Mat faceRegion = new Mat(grayImage, faceRect);
                
                // Detectar sonrisas en la región de la cara
                RectVector smiles = new RectVector();
                smileClassifier.detectMultiScale(faceRegion, smiles, 1.1, 3, 0,
                    new Size(20, 20), new Size());
                
                smileCount = (int) smiles.size();
                
                // Detectar bocas en la región de la cara
                RectVector mouths = new RectVector();
                mouthClassifier.detectMultiScale(faceRegion, mouths, 1.1, 3, 0,
                    new Size(20, 20), new Size());
                
                mouthCount = (int) mouths.size();
                
                // Dibujar rectángulos de detección
                rectangle(colorImage, 
                    new Point(faceRect.x(), faceRect.y()),
                    new Point(faceRect.x() + faceRect.width(), faceRect.y() + faceRect.height()),
                    new Scalar(0, 255, 0, 255), 2, 8, 0);
                
                // Dibujar rectángulos de sonrisas
                for (long j = 0; j < smiles.size(); j++) {
                    Rect smileRect = smiles.get(j);
                    rectangle(colorImage,
                        new Point(faceRect.x() + smileRect.x(), faceRect.y() + smileRect.y()),
                        new Point(faceRect.x() + smileRect.x() + smileRect.width(), 
                               faceRect.y() + smileRect.y() + smileRect.height()),
                        new Scalar(255, 0, 0, 255), 2, 8, 0);
                }
                
                // Dibujar rectángulos de bocas
                for (long j = 0; j < mouths.size(); j++) {
                    Rect mouthRect = mouths.get(j);
                    rectangle(colorImage,
                        new Point(faceRect.x() + mouthRect.x(), faceRect.y() + mouthRect.y()),
                        new Point(faceRect.x() + mouthRect.x() + mouthRect.width(), 
                               faceRect.y() + mouthRect.y() + mouthRect.height()),
                        new Scalar(0, 0, 255, 255), 2, 8, 0);
                }
            }
            
        } catch (Exception e) {
            log.error("Error en detecciones", e);
        }
    }
    
    private void detenerDeteccion() {
        log.info("Deteniendo detección...");
        isDetecting = false;
        
        if (detectionThread != null) {
            try {
                detectionThread.join(1000);
            } catch (InterruptedException e) {
                log.warn("Interrupción al detener thread de detección", e);
            }
        }
        
        if (grabber != null) {
            try {
                grabber.stop();
                grabber.release();
            } catch (Exception e) {
                log.error("Error al detener grabber", e);
            }
        }
        
        if (grabbedImage != null) {
            grabbedImage.release();
            grabbedImage = null;
        }
        if (grayImage != null) {
            grayImage.release();
            grayImage = null;
        }
        
        currentFrame = null;
        detectionPanel.setFrame(null);
        detectionPanel.repaint();
        
        lblEstado.setText("Estado: Detección detenida");
        lblStats.setText("Estadísticas: -");
    }
    
    // Panel personalizado para mostrar detecciones
    private class DetectionPanel extends JPanel {
        private BufferedImage frame = null;
        
        public void setFrame(BufferedImage frame) {
            this.frame = frame;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            if (frame != null) {
                // Dibujar el frame centrado
                int x = (getWidth() - frame.getWidth()) / 2;
                int y = (getHeight() - frame.getHeight()) / 2;
                g2d.drawImage(frame, x, y, null);
            } else {
                // Dibujar fondo negro con texto
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 18));
                String text = "Área de Detección";
                int x = (getWidth() - g2d.getFontMetrics().stringWidth(text)) / 2;
                int y = getHeight() / 2;
                g2d.drawString(text, x, y);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VideoDetection().setVisible(true);
            }
        });
    }
}
