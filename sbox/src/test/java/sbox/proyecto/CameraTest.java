package sbox.proyecto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import lombok.extern.slf4j.Slf4j;

// Importaciones de Bytedeco/JavaCV
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.IplImage;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * Prueba simple de cámara para S-Box
 * @author amaldonado
 */
@Slf4j
public class CameraTest extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Variables para captura de video
    private FrameGrabber grabber;
    private OpenCVFrameConverter.ToIplImage converter;
    private Java2DFrameConverter java2DConverter;
    private boolean isCapturing = false;
    private Thread captureThread;
    private BufferedImage currentFrame = null;
    
    private JPanel mainPanel;
    private JButton btnStart;
    private JButton btnStop;
    private JLabel lblStatus;
    private VideoPanel videoPanel;
    
    public CameraTest() {
        initComponents();
        setupLayout();
        setupWindowListener();
    }
    
    private void initComponents() {
        setTitle("S-Box - Prueba de Cámara");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 550);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de controles
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnStart = new JButton("Iniciar Cámara");
        btnStop = new JButton("Detener Cámara");
        lblStatus = new JLabel("Estado: Listo");
        
        btnStart.setFont(new Font("Arial", Font.BOLD, 14));
        btnStop.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Agregar listeners
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startCamera();
            }
        });
        
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopCamera();
            }
        });
        
        controlPanel.add(btnStart);
        controlPanel.add(btnStop);
        controlPanel.add(lblStatus);
        
        // Panel de video
        videoPanel = new VideoPanel();
        videoPanel.setPreferredSize(new Dimension(640, 480));
        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(videoPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void setupLayout() {
        // Configuración adicional del layout si es necesaria
    }
    
    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopCamera();
            }
        });
    }
    
    private void startCamera() {
        log.info("Iniciando cámara...");
        lblStatus.setText("Estado: Iniciando cámara...");
        
        try {
            // Inicializar grabber - usar OpenCVFrameGrabber para compatibilidad con macOS
            grabber = new OpenCVFrameGrabber(0); // Cámara 0
            grabber.start();
            
            // Inicializar convertidores
            converter = new OpenCVFrameConverter.ToIplImage();
            java2DConverter = new Java2DFrameConverter();
            
            isCapturing = true;
            lblStatus.setText("Estado: Cámara iniciada");
            
            // Iniciar thread de captura
            captureThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isCapturing) {
                        try {
                            Frame frame = grabber.grab();
                            if (frame != null) {
                                // Convertir frame a IplImage
                                IplImage image = converter.convert(frame);
                                if (image != null) {
                                    // Redimensionar si es necesario
                                    if (image.width() != 640 || image.height() != 480) {
                                        IplImage resized = IplImage.create(640, 480, IPL_DEPTH_8U, 3);
                                        cvResize(image, resized);
                                        image = resized;
                                    }
                                    
                                    // Convertir a BufferedImage para mostrar
                                    Frame displayFrame = converter.convert(image);
                                    BufferedImage bufferedImage = java2DConverter.convert(displayFrame);
                                    
                                    // Actualizar UI en EDT
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            currentFrame = bufferedImage;
                                            videoPanel.setFrame(currentFrame);
                                            videoPanel.repaint();
                                        }
                                    });
                                }
                            }
                            Thread.sleep(33); // ~30 FPS
                        } catch (Exception e) {
                            log.error("Error en captura de cámara", e);
                            break;
                        }
                    }
                }
            });
            captureThread.start();
            
        } catch (Exception e) {
            log.error("Error al iniciar cámara", e);
            lblStatus.setText("Estado: Error al iniciar cámara - " + e.getMessage());
        }
    }
    
    private void stopCamera() {
        log.info("Deteniendo cámara...");
        isCapturing = false;
        
        if (captureThread != null) {
            try {
                captureThread.join(1000); // Esperar máximo 1 segundo
            } catch (InterruptedException e) {
                log.warn("Interrupción al detener thread de cámara", e);
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
        
        currentFrame = null;
        videoPanel.setFrame(null);
        videoPanel.repaint();
        
        lblStatus.setText("Estado: Cámara detenida");
    }
    
    // Panel personalizado para mostrar video
    private class VideoPanel extends JPanel {
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
                String text = "Área de Video";
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
                new CameraTest().setVisible(true);
            }
        });
    }
} 