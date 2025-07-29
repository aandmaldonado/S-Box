/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.proyecto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import lombok.extern.slf4j.Slf4j;

// Comentamos las importaciones problemáticas de Bytedeco
// import org.bytedeco.javacpp.avcodec;
// import org.bytedeco.javacpp.opencv_core;
// import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
// import static org.bytedeco.javacpp.opencv_core.cvInitFont;
// import static org.bytedeco.javacpp.opencv_core.cvPoint;
// import static org.bytedeco.javacpp.opencv_core.cvPutText;
// import org.bytedeco.javacpp.opencv_highgui;

/**
 * Clase para pruebas de OpenCV
 * @author amaldonado
 */
@Slf4j
public class PruebasOpenCV extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Comentamos las variables problemáticas de Bytedeco
    // private opencv_core.Mat mat = new opencv_core.Mat();
    // private opencv_core.IplImage iplImage = null;
    // private opencv_highgui.VideoCapture cap = null;
    // private opencv_core.CvMemStorage storage;
    // private opencv_core.CvFont mCvFont = new opencv_core.CvFont();
    
    private JPanel mainPanel;
    private JButton btnProbarOpenCV;
    private JButton btnDetenerPrueba;
    private JLabel lblEstado;
    
    public PruebasOpenCV() {
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        setTitle("Pruebas de OpenCV");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de controles
        JPanel controlPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        btnProbarOpenCV = new JButton("Probar OpenCV");
        btnDetenerPrueba = new JButton("Detener Prueba");
        
        // Configurar botones
        btnProbarOpenCV.setFont(new Font("Arial", Font.BOLD, 14));
        btnDetenerPrueba.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Agregar listeners
        btnProbarOpenCV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                probarOpenCV();
            }
        });
        
        btnDetenerPrueba.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detenerPrueba();
            }
        });
        
        controlPanel.add(btnProbarOpenCV);
        controlPanel.add(btnDetenerPrueba);
        
        // Panel de estado
        JPanel statusPanel = new JPanel();
        lblEstado = new JLabel("Estado: Listo");
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 16));
        statusPanel.add(lblEstado);
        
        // Área de prueba (simulada)
        JPanel testPanel = new JPanel();
        testPanel.setBackground(Color.BLACK);
        testPanel.setPreferredSize(new Dimension(400, 300));
        JLabel testLabel = new JLabel("Área de Prueba OpenCV");
        testLabel.setForeground(Color.WHITE);
        testLabel.setFont(new Font("Arial", Font.BOLD, 18));
        testPanel.add(testLabel);
        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(testPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void setupLayout() {
        // Configuración adicional del layout si es necesaria
    }
    
    private void probarOpenCV() {
        log.info("Probando OpenCV...");
        lblEstado.setText("Estado: Probando OpenCV...");
        // Comentamos el código original de Bytedeco
        /*
        try {
            // Código original de pruebas OpenCV aquí
            // cvInitFont(mCvFont, opencv_core.CV_FONT_HERSHEY_TRIPLEX, 0.5f, 1.0f, 0, 1, 8);
            // cap = new opencv_highgui.VideoCapture(source.getAbsolutePath());
            // recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            // storage = opencv_core.CvMemStorage.create();
            // cvPutText(iplImage, "PRUEBA", cvPoint(x, y), mCvFont, opencv_core.CvScalar.BLACK);
            
            lblEstado.setText("Estado: Probando OpenCV...");
        } catch (Exception e) {
            log.error("Error al probar OpenCV", e);
            lblEstado.setText("Estado: Error al probar OpenCV");
        }
        */
    }
    
    private void detenerPrueba() {
        log.info("Deteniendo prueba de OpenCV...");
        lblEstado.setText("Estado: Prueba detenida");
        // Comentamos el código original de Bytedeco
        /*
        // Código original para detener prueba aquí
        if (cap != null) {
            cap.release();
            cap = null;
        }
        lblEstado.setText("Estado: Prueba detenida");
        */
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PruebasOpenCV().setVisible(true);
            }
        });
    }
}
