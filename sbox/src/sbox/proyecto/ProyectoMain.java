/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.proyecto;

import java.awt.AWTException;
import sbox.perspectiva.PerspectivaCliente;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.bytedeco.javacpp.avcodec;
import static org.bytedeco.javacpp.opencv_core.CV_FONT_HERSHEY_COMPLEX_SMALL;
import static org.bytedeco.javacpp.opencv_core.CV_FONT_HERSHEY_TRIPLEX;
import org.bytedeco.javacpp.opencv_core.CvFont;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvInitFont;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvPutText;
import org.bytedeco.javacpp.opencv_highgui.VideoCapture;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.jdesktop.swingworker.SwingWorker;
import sbox.activityrender.initScreenRecorder;
import sbox.detection.TimeDetection;
import sbox.detection.VideoDetection;
import sbox.facerecorder.*;

/**
 *
 * @author Álvaro Andrés Maldonado Pinto
 */
public class ProyectoMain extends javax.swing.JFrame implements Runnable {

    public void iniciarCronometro() {
        cronometroActivo = true;
        hilo = new Thread(this);
        hilo.start();
    }

    public void pararCronometro() {
        cronometroActivo = false;
    }

    public void run() {
        Integer minutos = 0, segundos = 0, milesimas = 0;
        //min es minutos, seg es segundos y mil es milesimas de segundo
        String min = "", seg = "", mil = "";
        try {
            //Mientras cronometroActivo sea verdadero entonces seguira
            //aumentando el tiempo
            while (cronometroActivo) {
                Thread.sleep(20);
                //Incrementamos 4 milesimas de segundo
                milesimas += 20;

                //Cuando llega a 1000 osea 1 segundo aumenta 1 segundo
                //y las milesimas dmilesimase segundo de nuevo a 0
                if (milesimas == 1000) {
                    milesimas = 0;
                    segundos += 1;
                    //Si los segundos llegan a 60 entonces aumenta 1 los minutos
                    //y los segundos vuelven a 0
                    if (segundos == 60) {
                        segundos = 0;
                        minutos++;
                    }
                }

                //Esto solamente es estetica para que siempre este en formato
                //00:00:000
                if (minutos < 10) {
                    min = "0" + minutos;
                } else {
                    min = minutos.toString();
                }
                if (segundos < 10) {
                    seg = "0" + segundos;
                } else {
                    seg = segundos.toString();
                }

                if (milesimas < 10) {
                    mil = "00" + milesimas;
                } else if (milesimas < 100) {
                    mil = "0" + milesimas;
                } else {
                    mil = milesimas.toString();
                }

                //Colocamos en la etiqueta la informacion
                setTimeText(min + ":" + seg + ":" + mil);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    class CameraSwingWorker extends SwingWorker<String, Object> {

        private String path = "";
        private String nombreProyecto = "";

        public CameraSwingWorker(String path, String nombreProyecto) {
            this.path = path;
            this.nombreProyecto = nombreProyecto;
        }

        @Override
        public String doInBackground() throws Exception {
            try {
                capture = new WebcamAndMicrophoneCapture();
                capture.startCamera(path, nombreProyecto, experimentos);
            } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
                log.error(e);
            }
            return null;
        }

    }

    public ProyectoMain() {
        initComponents();
        tabPanelPrincipal.setVisible(false);
        txtDirIP.setEnabled(false);
        buscarButton.setEnabled(false);
        comboDispositivos.setEnabled(false);
        txtDescPerspectiva.setEnabled(false);
        tabPanelPrincipal.setBounds(10, 10, 500, 500);
        proyectoCargado = false;
        creacionProyecto = false;
        obtencionFuentes = false;
        syncFuentes = false;
        visualizacion = false;
        buscarProgressBar.setVisible(false);
        rutaHaarcascades = System.getProperty("user.dir") + "\\src\\resources\\haarcascades";
        File haarcascades = new File(rutaHaarcascades);
        File[] archivos = haarcascades.listFiles();
        String[] listHaar = new String[archivos.length + 1];
        listHaar[0] = "Seleccione reconocedor";
        for (int i = 0; i < archivos.length; i++) {
            listHaar[i + 1] = archivos[i].getName();
        }
        comboReconocedor.setModel(new DefaultComboBoxModel<>(listHaar));
        setLocationRelativeTo(null);
        setBounds(0, 0, 842, 641);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabPanelPrincipal = new javax.swing.JTabbedPane();
        proyectoPanel = new javax.swing.JPanel();
        informacionProyectoPanel = new javax.swing.JPanel();
        labelNombreProyecto = new javax.swing.JLabel();
        txtNombreProyecto = new javax.swing.JTextField();
        labelRutaProyecto = new javax.swing.JLabel();
        txtRutaProyecto = new javax.swing.JTextField();
        rutaProyectoButton = new javax.swing.JButton();
        crearProyectoButton = new javax.swing.JButton();
        informacionPerspectivaPanel = new javax.swing.JPanel();
        labelDescCheck = new javax.swing.JLabel();
        faceRecorderCheck = new javax.swing.JCheckBox();
        activityRenderCheck = new javax.swing.JCheckBox();
        agregarPerspectivaButton = new javax.swing.JButton();
        perspectivaPanel = new javax.swing.JPanel();
        labelDirIP = new javax.swing.JLabel();
        txtDirIP = new javax.swing.JTextField();
        buscarButton = new javax.swing.JButton();
        labelDispositivos = new javax.swing.JLabel();
        comboDispositivos = new javax.swing.JComboBox<>();
        labelDescPerspectiva = new javax.swing.JLabel();
        descPerspectivaScrollPanel = new javax.swing.JScrollPane();
        txtDescPerspectiva = new javax.swing.JTextArea();
        buscarProgressBar = new javax.swing.JProgressBar();
        faceRecorderIcon = new javax.swing.JLabel();
        activityRenderIcon = new javax.swing.JLabel();
        reconocedorPanel = new javax.swing.JPanel();
        comboReconocedor = new javax.swing.JComboBox<>();
        fuentesPanel = new javax.swing.JPanel();
        confPerspectivaPanel = new javax.swing.JPanel();
        labelFaceRecorder = new javax.swing.JLabel();
        labelActivityRender = new javax.swing.JLabel();
        labelPerspExt = new javax.swing.JLabel();
        labelFaceRecorderIcon = new javax.swing.JLabel();
        labelActivityRenderIcon = new javax.swing.JLabel();
        labelPerspExtIcon = new javax.swing.JLabel();
        simbolosPanel = new javax.swing.JPanel();
        labelOk = new javax.swing.JLabel();
        labelNok = new javax.swing.JLabel();
        faceRecorderGrabando = new javax.swing.JProgressBar();
        activityRenderGrabando = new javax.swing.JProgressBar();
        perspExtGrabando = new javax.swing.JProgressBar();
        faceRecorderVerButton = new javax.swing.JButton();
        actRenderVerButton = new javax.swing.JButton();
        perspExtVerButton = new javax.swing.JButton();
        controlPanel = new javax.swing.JPanel();
        iniciarButton = new javax.swing.JButton();
        detenerButton = new javax.swing.JButton();
        guardarFuentesButton = new javax.swing.JButton();
        descartarFuentesButton = new javax.swing.JButton();
        procesamientoPanel = new javax.swing.JPanel();
        preliminarPanel = new javax.swing.JPanel();
        tableScrollPane1 = new javax.swing.JScrollPane();
        deteccionTable = new javax.swing.JTable();
        marcadoPanel = new javax.swing.JPanel();
        labelVideoMaster = new javax.swing.JLabel();
        txtVideoMaster = new javax.swing.JTextField();
        marcadorButton = new javax.swing.JButton();
        videoMasterButton = new javax.swing.JButton();
        labelVideoSec = new javax.swing.JLabel();
        txtVideoSec = new javax.swing.JTextField();
        labelVideoExt = new javax.swing.JLabel();
        txtVideoExt = new javax.swing.JTextField();
        procesandoProgressBar = new javax.swing.JProgressBar();
        labelFPS = new javax.swing.JLabel();
        txtFPS1 = new javax.swing.JTextField();
        txtFPS2 = new javax.swing.JTextField();
        txtFPS3 = new javax.swing.JTextField();
        labelHolgura = new javax.swing.JLabel();
        txtHolgura = new javax.swing.JTextField();
        labelSeg = new javax.swing.JLabel();
        filtroPanel = new javax.swing.JPanel();
        labelReconocedor = new javax.swing.JLabel();
        txtReconecedor = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        visualizacionPanel = new javax.swing.JPanel();
        infoPanel = new javax.swing.JPanel();
        labelMaster = new javax.swing.JLabel();
        txtMaster = new javax.swing.JTextField();
        cortarButton = new javax.swing.JButton();
        labelSec = new javax.swing.JLabel();
        txtSec = new javax.swing.JTextField();
        labelExt = new javax.swing.JLabel();
        txtExt = new javax.swing.JTextField();
        cortandoProgressBar = new javax.swing.JProgressBar();
        labelFPS2 = new javax.swing.JLabel();
        txtFPS4 = new javax.swing.JTextField();
        txtFPS5 = new javax.swing.JTextField();
        txtFPS6 = new javax.swing.JTextField();
        secuenciasPanel = new javax.swing.JPanel();
        secScrollPane = new javax.swing.JScrollPane();
        secList = new javax.swing.JList<>();
        visualizarButton = new javax.swing.JButton();
        labelSecMaster = new javax.swing.JLabel();
        filtroPanel2 = new javax.swing.JPanel();
        txtReco = new javax.swing.JTextField();
        labelReco = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        statusBarPanel = new javax.swing.JPanel();
        labelDondeEstoy = new javax.swing.JLabel();
        menuBarPrincipal = new javax.swing.JMenuBar();
        menuProyecto = new javax.swing.JMenu();
        itemMenuNuevo = new javax.swing.JMenuItem();
        itemMenuAbrir = new javax.swing.JMenuItem();
        separadorMenuArchivo = new javax.swing.JPopupMenu.Separator();
        itemMenuSalir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tabPanelPrincipal.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tabPanelPrincipalComponentShown(evt);
            }
        });

        proyectoPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                proyectoPanelComponentShown(evt);
            }
        });

        informacionProyectoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Información del proyecto"));

        labelNombreProyecto.setText("Nombre:");

        labelRutaProyecto.setText("Destino:");

        txtRutaProyecto.setEditable(false);

        rutaProyectoButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        rutaProyectoButton.setText("...");
        rutaProyectoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rutaProyectoButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout informacionProyectoPanelLayout = new javax.swing.GroupLayout(informacionProyectoPanel);
        informacionProyectoPanel.setLayout(informacionProyectoPanelLayout);
        informacionProyectoPanelLayout.setHorizontalGroup(
            informacionProyectoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(informacionProyectoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(informacionProyectoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(informacionProyectoPanelLayout.createSequentialGroup()
                        .addComponent(labelRutaProyecto)
                        .addGap(19, 19, 19)
                        .addComponent(txtRutaProyecto))
                    .addGroup(informacionProyectoPanelLayout.createSequentialGroup()
                        .addComponent(labelNombreProyecto)
                        .addGap(18, 18, 18)
                        .addComponent(txtNombreProyecto)))
                .addGap(10, 10, 10)
                .addComponent(rutaProyectoButton)
                .addContainerGap())
        );
        informacionProyectoPanelLayout.setVerticalGroup(
            informacionProyectoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(informacionProyectoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(informacionProyectoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombreProyecto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelNombreProyecto))
                .addGap(18, 18, 18)
                .addGroup(informacionProyectoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRutaProyecto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelRutaProyecto)
                    .addComponent(rutaProyectoButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        crearProyectoButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        crearProyectoButton.setText("Crear Proyecto");
        crearProyectoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crearProyectoButtonActionPerformed(evt);
            }
        });

        informacionPerspectivaPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Información de perspectiva"));

        labelDescCheck.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        labelDescCheck.setText("Seleccione las perspectivas por defecto para su proyecto");

        faceRecorderCheck.setText("Face Recorder");
        faceRecorderCheck.setToolTipText("Esta perspectiva muestra información del rostro del usuario frente a la  pantalla");
        faceRecorderCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                faceRecorderCheckActionPerformed(evt);
            }
        });

        activityRenderCheck.setText("Activity Render");
        activityRenderCheck.setToolTipText("Esta perspectiva muestra información de lo que está viendo el usuario en la  pantalla");

        agregarPerspectivaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/webcam_add.png"))); // NOI18N
        agregarPerspectivaButton.setText("Agregar Perspectiva");
        agregarPerspectivaButton.setToolTipText("Presione si desea configurar una nueva perspectiva");
        agregarPerspectivaButton.setBorder(null);
        agregarPerspectivaButton.setBorderPainted(false);
        agregarPerspectivaButton.setContentAreaFilled(false);
        agregarPerspectivaButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        agregarPerspectivaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarPerspectivaButtonActionPerformed(evt);
            }
        });

        perspectivaPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelDirIP.setText("Dirección IP:");

        buscarButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        buscarButton.setText("Buscar");
        buscarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarButtonActionPerformed(evt);
            }
        });

        labelDispositivos.setText("Dispositivos detectados:");

        comboDispositivos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione dispositivo" }));
        comboDispositivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboDispositivosActionPerformed(evt);
            }
        });

        labelDescPerspectiva.setText("Descripción:");

        txtDescPerspectiva.setColumns(20);
        txtDescPerspectiva.setRows(5);
        descPerspectivaScrollPanel.setViewportView(txtDescPerspectiva);

        buscarProgressBar.setString("Buscando dispositivos");
        buscarProgressBar.setStringPainted(true);

        javax.swing.GroupLayout perspectivaPanelLayout = new javax.swing.GroupLayout(perspectivaPanel);
        perspectivaPanel.setLayout(perspectivaPanelLayout);
        perspectivaPanelLayout.setHorizontalGroup(
            perspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(perspectivaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(perspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboDispositivos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(perspectivaPanelLayout.createSequentialGroup()
                        .addGroup(perspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelDispositivos, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelDescPerspectiva)
                            .addGroup(perspectivaPanelLayout.createSequentialGroup()
                                .addComponent(labelDirIP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(perspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(buscarProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtDirIP, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buscarButton)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(descPerspectivaScrollPanel))
                .addContainerGap())
        );
        perspectivaPanelLayout.setVerticalGroup(
            perspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(perspectivaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(perspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDirIP)
                    .addComponent(txtDirIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buscarButton))
                .addGap(1, 1, 1)
                .addComponent(buscarProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDispositivos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(comboDispositivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelDescPerspectiva)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(descPerspectivaScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        faceRecorderIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/male.png"))); // NOI18N

        activityRenderIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/developer-api-coding-screen.png"))); // NOI18N

        javax.swing.GroupLayout informacionPerspectivaPanelLayout = new javax.swing.GroupLayout(informacionPerspectivaPanel);
        informacionPerspectivaPanel.setLayout(informacionPerspectivaPanelLayout);
        informacionPerspectivaPanelLayout.setHorizontalGroup(
            informacionPerspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(informacionPerspectivaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(informacionPerspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelDescCheck)
                    .addGroup(informacionPerspectivaPanelLayout.createSequentialGroup()
                        .addComponent(faceRecorderIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(faceRecorderCheck))
                    .addGroup(informacionPerspectivaPanelLayout.createSequentialGroup()
                        .addComponent(activityRenderIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(activityRenderCheck))
                    .addComponent(agregarPerspectivaButton)
                    .addComponent(perspectivaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        informacionPerspectivaPanelLayout.setVerticalGroup(
            informacionPerspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, informacionPerspectivaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDescCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(informacionPerspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(faceRecorderCheck)
                    .addComponent(faceRecorderIcon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(informacionPerspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(activityRenderCheck)
                    .addComponent(activityRenderIcon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(agregarPerspectivaButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(perspectivaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        reconocedorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Reconocedor"));

        comboReconocedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione reconocedor" }));

        javax.swing.GroupLayout reconocedorPanelLayout = new javax.swing.GroupLayout(reconocedorPanel);
        reconocedorPanel.setLayout(reconocedorPanelLayout);
        reconocedorPanelLayout.setHorizontalGroup(
            reconocedorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reconocedorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(comboReconocedor, 0, 346, Short.MAX_VALUE)
                .addContainerGap())
        );
        reconocedorPanelLayout.setVerticalGroup(
            reconocedorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reconocedorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(comboReconocedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout proyectoPanelLayout = new javax.swing.GroupLayout(proyectoPanel);
        proyectoPanel.setLayout(proyectoPanelLayout);
        proyectoPanelLayout.setHorizontalGroup(
            proyectoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(proyectoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(proyectoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(proyectoPanelLayout.createSequentialGroup()
                        .addComponent(informacionPerspectivaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(proyectoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(informacionProyectoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(reconocedorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(414, 414, 414))
                    .addGroup(proyectoPanelLayout.createSequentialGroup()
                        .addComponent(crearProyectoButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        proyectoPanelLayout.setVerticalGroup(
            proyectoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(proyectoPanelLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(proyectoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(proyectoPanelLayout.createSequentialGroup()
                        .addComponent(informacionProyectoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reconocedorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(informacionPerspectivaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(crearProyectoButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabPanelPrincipal.addTab("Crear", new javax.swing.ImageIcon(getClass().getResource("/resources/project.png")), proyectoPanel); // NOI18N

        fuentesPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                fuentesPanelComponentShown(evt);
            }
        });

        confPerspectivaPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Configuración de perspectivas"));

        labelFaceRecorder.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        labelFaceRecorder.setText("Face Recorder");

        labelActivityRender.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        labelActivityRender.setText("Activity Render");

        labelPerspExt.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        labelPerspExt.setText("Perspectiva Externa");

        labelFaceRecorderIcon.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        labelFaceRecorderIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/male3.png"))); // NOI18N
        labelFaceRecorderIcon.setText("Grabación del rostro del usuario frente a la pantalla del computador");

        labelActivityRenderIcon.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        labelActivityRenderIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/developer-api-coding-screen2.png"))); // NOI18N
        labelActivityRenderIcon.setText("Grabación de la sesión que está realizando el usuario - con posible audio");

        labelPerspExtIcon.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        labelPerspExtIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/video-camera.png"))); // NOI18N
        labelPerspExtIcon.setText("Grabación desde una cámara externa a otras zonas del cuerpo del usuario");

        simbolosPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Simbología"));

        labelOk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/OK.png"))); // NOI18N
        labelOk.setText("Disponible");

        labelNok.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/No-entry.png"))); // NOI18N
        labelNok.setText("No disponible");

        javax.swing.GroupLayout simbolosPanelLayout = new javax.swing.GroupLayout(simbolosPanel);
        simbolosPanel.setLayout(simbolosPanelLayout);
        simbolosPanelLayout.setHorizontalGroup(
            simbolosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(simbolosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(simbolosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelOk)
                    .addComponent(labelNok))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        simbolosPanelLayout.setVerticalGroup(
            simbolosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(simbolosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelOk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelNok)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        faceRecorderGrabando.setString("");
        faceRecorderGrabando.setStringPainted(true);

        activityRenderGrabando.setString("");
        activityRenderGrabando.setStringPainted(true);

        perspExtGrabando.setString("");
        perspExtGrabando.setStringPainted(true);

        faceRecorderVerButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        faceRecorderVerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/eye_preview_see_seen_view.png"))); // NOI18N
        faceRecorderVerButton.setText("Vista Previa");
        faceRecorderVerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                faceRecorderVerButtonActionPerformed(evt);
            }
        });

        actRenderVerButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        actRenderVerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/eye_preview_see_seen_view.png"))); // NOI18N
        actRenderVerButton.setText("Vista Previa");
        actRenderVerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actRenderVerButtonActionPerformed(evt);
            }
        });

        perspExtVerButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        perspExtVerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/eye_preview_see_seen_view.png"))); // NOI18N
        perspExtVerButton.setText("Vista Previa");
        perspExtVerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                perspExtVerButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout confPerspectivaPanelLayout = new javax.swing.GroupLayout(confPerspectivaPanel);
        confPerspectivaPanel.setLayout(confPerspectivaPanelLayout);
        confPerspectivaPanelLayout.setHorizontalGroup(
            confPerspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(confPerspectivaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(confPerspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelFaceRecorder)
                    .addComponent(labelActivityRender)
                    .addComponent(labelPerspExt)
                    .addComponent(simbolosPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(confPerspectivaPanelLayout.createSequentialGroup()
                        .addGroup(confPerspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(confPerspectivaPanelLayout.createSequentialGroup()
                                .addComponent(labelActivityRenderIcon)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(actRenderVerButton))
                            .addGroup(confPerspectivaPanelLayout.createSequentialGroup()
                                .addComponent(labelFaceRecorderIcon)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(faceRecorderVerButton))
                            .addGroup(confPerspectivaPanelLayout.createSequentialGroup()
                                .addComponent(labelPerspExtIcon)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                                .addComponent(perspExtVerButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(confPerspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(perspExtGrabando, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(activityRenderGrabando, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(faceRecorderGrabando, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        confPerspectivaPanelLayout.setVerticalGroup(
            confPerspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, confPerspectivaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(simbolosPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelFaceRecorder)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(confPerspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFaceRecorderIcon)
                    .addComponent(faceRecorderVerButton)
                    .addComponent(faceRecorderGrabando, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelActivityRender)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(confPerspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelActivityRenderIcon)
                    .addComponent(actRenderVerButton)
                    .addComponent(activityRenderGrabando, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelPerspExt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(confPerspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(perspExtGrabando, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(confPerspectivaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelPerspExtIcon)
                        .addComponent(perspExtVerButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        controlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Panel de control"));

        iniciarButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        iniciarButton.setForeground(java.awt.Color.blue);
        iniciarButton.setText("Iniciar");
        iniciarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iniciarButtonActionPerformed(evt);
            }
        });

        detenerButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        detenerButton.setForeground(java.awt.Color.red);
        detenerButton.setText("Detener");
        detenerButton.setEnabled(false);
        detenerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detenerButtonActionPerformed(evt);
            }
        });

        guardarFuentesButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        guardarFuentesButton.setText("Guardar Fuentes");
        guardarFuentesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarFuentesButtonActionPerformed(evt);
            }
        });

        descartarFuentesButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        descartarFuentesButton.setText("Descartar Fuentes");
        descartarFuentesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descartarFuentesButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout controlPanelLayout = new javax.swing.GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setHorizontalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(iniciarButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(detenerButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guardarFuentesButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(descartarFuentesButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        controlPanelLayout.setVerticalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iniciarButton)
                    .addComponent(detenerButton)
                    .addComponent(guardarFuentesButton)
                    .addComponent(descartarFuentesButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout fuentesPanelLayout = new javax.swing.GroupLayout(fuentesPanel);
        fuentesPanel.setLayout(fuentesPanelLayout);
        fuentesPanelLayout.setHorizontalGroup(
            fuentesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fuentesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fuentesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(confPerspectivaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(controlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        fuentesPanelLayout.setVerticalGroup(
            fuentesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fuentesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(confPerspectivaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(controlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabPanelPrincipal.addTab("Grabar", new javax.swing.ImageIcon(getClass().getResource("/resources/video-record.png")), fuentesPanel); // NOI18N

        procesamientoPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                procesamientoPanelComponentShown(evt);
            }
        });

        preliminarPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Información preliminar de secuencias"));

        deteccionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tiempo Detección", "Tiempo Inicio", "Tiempo Termino"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tableScrollPane1.setViewportView(deteccionTable);

        javax.swing.GroupLayout preliminarPanelLayout = new javax.swing.GroupLayout(preliminarPanel);
        preliminarPanel.setLayout(preliminarPanelLayout);
        preliminarPanelLayout.setHorizontalGroup(
            preliminarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preliminarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 321, Short.MAX_VALUE))
        );
        preliminarPanelLayout.setVerticalGroup(
            preliminarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preliminarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                .addContainerGap())
        );

        marcadoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Fuentes"));
        marcadoPanel.setAutoscrolls(true);

        labelVideoMaster.setText("Seleccione video master:");

        txtVideoMaster.setEnabled(false);

        marcadorButton.setText("Filtrar");
        marcadorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                marcadorButtonActionPerformed(evt);
            }
        });

        videoMasterButton.setText("...");
        videoMasterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                videoMasterButtonActionPerformed(evt);
            }
        });

        labelVideoSec.setText("Video secundario:");

        txtVideoSec.setEnabled(false);

        labelVideoExt.setText("Video externo:");

        txtVideoExt.setEnabled(false);

        procesandoProgressBar.setString("");
        procesandoProgressBar.setStringPainted(true);

        labelFPS.setText("FPS:");

        txtFPS1.setEnabled(false);

        txtFPS2.setEnabled(false);

        txtFPS3.setEnabled(false);

        labelHolgura.setText("Holgura:");

        labelSeg.setText("segundos");

        javax.swing.GroupLayout marcadoPanelLayout = new javax.swing.GroupLayout(marcadoPanel);
        marcadoPanel.setLayout(marcadoPanelLayout);
        marcadoPanelLayout.setHorizontalGroup(
            marcadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(marcadoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(marcadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelVideoMaster)
                    .addComponent(labelVideoSec)
                    .addComponent(labelVideoExt)
                    .addComponent(marcadorButton)
                    .addComponent(labelHolgura))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(marcadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(marcadoPanelLayout.createSequentialGroup()
                        .addComponent(procesandoProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, marcadoPanelLayout.createSequentialGroup()
                        .addGroup(marcadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtVideoSec, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVideoMaster, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVideoExt, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, marcadoPanelLayout.createSequentialGroup()
                                .addComponent(txtHolgura, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelSeg)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(videoMasterButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(marcadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labelFPS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFPS1)
                            .addComponent(txtFPS2)
                            .addComponent(txtFPS3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        marcadoPanelLayout.setVerticalGroup(
            marcadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, marcadoPanelLayout.createSequentialGroup()
                .addComponent(labelFPS)
                .addGap(18, 18, 18)
                .addGroup(marcadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(marcadoPanelLayout.createSequentialGroup()
                        .addGroup(marcadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(videoMasterButton)
                            .addComponent(txtFPS1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFPS2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFPS3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(marcadoPanelLayout.createSequentialGroup()
                        .addGroup(marcadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelVideoMaster)
                            .addComponent(txtVideoMaster, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(marcadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelVideoSec)
                            .addComponent(txtVideoSec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(marcadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelVideoExt)
                            .addComponent(txtVideoExt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(marcadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHolgura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelHolgura)
                    .addComponent(labelSeg))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(marcadoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(procesandoProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(marcadorButton))
                .addContainerGap())
        );

        filtroPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtro"));

        labelReconocedor.setText("Reconocedor:");

        txtReconecedor.setEnabled(false);
        txtReconecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtReconecedorActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel1.setText("<html><body><p>El filtro corta los videos de acuerdo al reconocedor aplicado en el video MASTER.</p></body></html>");

        javax.swing.GroupLayout filtroPanelLayout = new javax.swing.GroupLayout(filtroPanel);
        filtroPanel.setLayout(filtroPanelLayout);
        filtroPanelLayout.setHorizontalGroup(
            filtroPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filtroPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(filtroPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(filtroPanelLayout.createSequentialGroup()
                        .addComponent(labelReconocedor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtReconecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        filtroPanelLayout.setVerticalGroup(
            filtroPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filtroPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(filtroPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelReconocedor)
                    .addComponent(txtReconecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout procesamientoPanelLayout = new javax.swing.GroupLayout(procesamientoPanel);
        procesamientoPanel.setLayout(procesamientoPanelLayout);
        procesamientoPanelLayout.setHorizontalGroup(
            procesamientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(procesamientoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(procesamientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(preliminarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(procesamientoPanelLayout.createSequentialGroup()
                        .addComponent(marcadoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filtroPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        procesamientoPanelLayout.setVerticalGroup(
            procesamientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(procesamientoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(procesamientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(marcadoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(filtroPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(preliminarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabPanelPrincipal.addTab("Filtrar", new javax.swing.ImageIcon(getClass().getResource("/resources/filter_data.png")), procesamientoPanel); // NOI18N

        visualizacionPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                visualizacionPanelComponentShown(evt);
            }
        });

        infoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Información preliminar"));
        infoPanel.setAutoscrolls(true);

        labelMaster.setText("Video master:");

        txtMaster.setEnabled(false);

        cortarButton.setText("Generar");
        cortarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cortarButtonActionPerformed(evt);
            }
        });

        labelSec.setText("Video secundario:");

        txtSec.setEnabled(false);

        labelExt.setText("Video externo:");

        txtExt.setEnabled(false);

        cortandoProgressBar.setString("");
        cortandoProgressBar.setStringPainted(true);

        labelFPS2.setText("FPS:");

        txtFPS4.setEnabled(false);

        txtFPS5.setEnabled(false);

        txtFPS6.setEnabled(false);

        javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelExt)
                    .addComponent(labelSec)
                    .addComponent(labelMaster)
                    .addComponent(cortarButton))
                .addGap(14, 14, 14)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMaster)
                    .addComponent(txtSec)
                    .addGroup(infoPanelLayout.createSequentialGroup()
                        .addComponent(cortandoProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtExt))
                .addGap(18, 18, 18)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelFPS2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtFPS4)
                    .addComponent(txtFPS5)
                    .addComponent(txtFPS6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addComponent(labelFPS2)
                .addGap(18, 18, 18)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoPanelLayout.createSequentialGroup()
                        .addComponent(txtFPS4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFPS5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFPS6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(infoPanelLayout.createSequentialGroup()
                        .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelMaster)
                            .addComponent(txtMaster, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelSec)
                            .addComponent(txtSec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelExt)
                            .addComponent(txtExt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cortarButton)
                    .addComponent(cortandoProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        secuenciasPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Información de secuencias"));

        secList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { " " };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        secScrollPane.setViewportView(secList);

        visualizarButton.setText("Visualizar");
        visualizarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visualizarButtonActionPerformed(evt);
            }
        });

        labelSecMaster.setText("Secuencias generadas desde video master:");

        javax.swing.GroupLayout secuenciasPanelLayout = new javax.swing.GroupLayout(secuenciasPanel);
        secuenciasPanel.setLayout(secuenciasPanelLayout);
        secuenciasPanelLayout.setHorizontalGroup(
            secuenciasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secuenciasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(secuenciasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visualizarButton)
                    .addComponent(labelSecMaster)
                    .addComponent(secScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(293, Short.MAX_VALUE))
        );
        secuenciasPanelLayout.setVerticalGroup(
            secuenciasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secuenciasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelSecMaster)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(secScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(visualizarButton)
                .addContainerGap())
        );

        filtroPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtro"));

        txtReco.setEnabled(false);

        labelReco.setText("Reconocedor:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel2.setText("<html><body><p>El filtro corta los videos de acuerdo al reconocedor aplicado en el video MASTER.</p></body></html>");

        javax.swing.GroupLayout filtroPanel2Layout = new javax.swing.GroupLayout(filtroPanel2);
        filtroPanel2.setLayout(filtroPanel2Layout);
        filtroPanel2Layout.setHorizontalGroup(
            filtroPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filtroPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, filtroPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelReco)
                .addGap(14, 14, 14)
                .addComponent(txtReco, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        filtroPanel2Layout.setVerticalGroup(
            filtroPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, filtroPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(filtroPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtReco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelReco))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout visualizacionPanelLayout = new javax.swing.GroupLayout(visualizacionPanel);
        visualizacionPanel.setLayout(visualizacionPanelLayout);
        visualizacionPanelLayout.setHorizontalGroup(
            visualizacionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(visualizacionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(visualizacionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(visualizacionPanelLayout.createSequentialGroup()
                        .addComponent(secuenciasPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 2, Short.MAX_VALUE))
                    .addGroup(visualizacionPanelLayout.createSequentialGroup()
                        .addComponent(infoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filtroPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        visualizacionPanelLayout.setVerticalGroup(
            visualizacionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(visualizacionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(visualizacionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(filtroPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(infoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(secuenciasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabPanelPrincipal.addTab("Generar", new javax.swing.ImageIcon(getClass().getResource("/resources/Cut-20.png")), visualizacionPanel); // NOI18N

        statusBarPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelDondeEstoy.setText("Usted está en Pantalla de Bienvenida");

        javax.swing.GroupLayout statusBarPanelLayout = new javax.swing.GroupLayout(statusBarPanel);
        statusBarPanel.setLayout(statusBarPanelLayout);
        statusBarPanelLayout.setHorizontalGroup(
            statusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusBarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDondeEstoy)
                .addContainerGap(650, Short.MAX_VALUE))
        );
        statusBarPanelLayout.setVerticalGroup(
            statusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusBarPanelLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(labelDondeEstoy)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        menuBarPrincipal.setBackground(java.awt.Color.gray);

        menuProyecto.setBackground(java.awt.Color.gray);
        menuProyecto.setForeground(java.awt.Color.white);
        menuProyecto.setText("Proyecto");

        itemMenuNuevo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        itemMenuNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/PRTIFIconFoldersPlus_16x16.png"))); // NOI18N
        itemMenuNuevo.setText("Nuevo Proyecto");
        itemMenuNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemMenuNuevoActionPerformed(evt);
            }
        });
        menuProyecto.add(itemMenuNuevo);

        itemMenuAbrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        itemMenuAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/PRTIFIconFolderOpen_16x16.png"))); // NOI18N
        itemMenuAbrir.setText("Abrir Proyecto");
        itemMenuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemMenuAbrirActionPerformed(evt);
            }
        });
        menuProyecto.add(itemMenuAbrir);
        menuProyecto.add(separadorMenuArchivo);

        itemMenuSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/PRTIFIconCross_16x16.png"))); // NOI18N
        itemMenuSalir.setText("Salir");
        itemMenuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemMenuSalirActionPerformed(evt);
            }
        });
        menuProyecto.add(itemMenuSalir);

        menuBarPrincipal.add(menuProyecto);

        setJMenuBar(menuBarPrincipal);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusBarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabPanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(tabPanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(statusBarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void itemMenuNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemMenuNuevoActionPerformed
        // TODO add your handling code here:
        faceRecorderCheck.setSelected(false);
        activityRenderCheck.setSelected(false);
        agregarPerspectivaButton.setIcon(new ImageIcon(getClass().getResource("/resources/webcam_add.png")));
        agregarPerspectivaButton.setText("Agregar Perspectiva");
        agregarPerspectivaButton.setToolTipText("Presione si desea configurar una nueva perspectiva");
        txtDirIP.setText("");
        comboDispositivos.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccione dispositivo"}));
        txtDescPerspectiva.setText("");
        txtNombreProyecto.setText("");
        txtRutaProyecto.setText("");
        txtFPS1.setText("");
        txtFPS2.setText("");
        txtFPS3.setText("");
        txtHolgura.setText("");
        txtVideoMaster.setText("");
        txtVideoSec.setText("");
        txtVideoExt.setText("");
        deteccionTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Tiempo Detección", "Tiempo Inicio", "Tiempo Termino"
                }
        ) {
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        txtFPS4.setText("");
        txtFPS5.setText("");
        txtFPS6.setText("");
        txtMaster.setText("");
        txtSec.setText("");
        txtExt.setText("");
        secList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = {" "};

            public int getSize() {
                return strings.length;
            }

            public String getElementAt(int i) {
                return strings[i];
            }
        });

        rutaProyectoButton.setEnabled(true);
        crearProyectoButton.setText("Crear Proyecto");

        labelDondeEstoy.setText("Usted está en pestaña Creación de Proyecto");
        tabPanelPrincipal.setVisible(true);
        tabPanelPrincipal.setSelectedIndex(0);
        tabPanelPrincipal.setEnabledAt(1, false);
        tabPanelPrincipal.setEnabledAt(2, false);
        tabPanelPrincipal.setEnabledAt(3, false);
        txtDirIP.setEnabled(false);
        buscarButton.setEnabled(false);
        comboDispositivos.setEnabled(false);
        txtDescPerspectiva.setEnabled(false);
        txtRutaProyecto.setEnabled(false);
        txtNombreProyecto.setEnabled(true);
        rutaProyectoButton.setVisible(true);
        setLocationRelativeTo(null);
        setBounds(0, 0, 842, 643);

    }//GEN-LAST:event_itemMenuNuevoActionPerformed

    private void itemMenuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemMenuAbrirActionPerformed
        // TODO add your handling code here:

        seleccionarArchivo(JFileChooser.FILES_ONLY, "Abrir", txtRutaProyecto, "sbox", "C:\\");

        if (!txtRutaProyecto.getText().endsWith(".sbox")) {
            JOptionPane.showMessageDialog(this, "Proyecto no valido", "Error", JOptionPane.ERROR_MESSAGE);
            seleccionarArchivo(JFileChooser.FILES_ONLY, "Abrir", txtRutaProyecto, "sbox", "C:\\");
        } else {
            txtFPS1.setText("");
            txtFPS2.setText("");
            txtFPS3.setText("");
            txtHolgura.setText("");
            txtVideoMaster.setText("");
            txtVideoSec.setText("");
            txtVideoExt.setText("");
            deteccionTable.setModel(new javax.swing.table.DefaultTableModel(
                    new Object[][]{},
                    new String[]{
                        "Tiempo Detección", "Tiempo Inicio", "Tiempo Termino"
                    }
            ) {
                Class[] types = new Class[]{
                    java.lang.String.class, java.lang.String.class, java.lang.String.class
                };

                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }
            });
            txtFPS4.setText("");
            txtFPS5.setText("");
            txtFPS6.setText("");
            txtMaster.setText("");
            txtSec.setText("");
            txtExt.setText("");
            secList.setModel(new javax.swing.AbstractListModel<String>() {
                String[] strings = {" "};

                public int getSize() {
                    return strings.length;
                }

                public String getElementAt(int i) {
                    return strings[i];
                }
            });
            try {

                labelDondeEstoy.setText("Usted está en pestaña Creación de Proyecto");
                tabPanelPrincipal.setVisible(true);
                tabPanelPrincipal.setSelectedIndex(0);
                tabPanelPrincipal.setEnabledAt(1, true);
                tabPanelPrincipal.setEnabledAt(2, false);
                tabPanelPrincipal.setEnabledAt(3, false);
                setLocationRelativeTo(null);
                setBounds(0, 0, 842, 643);
                Properties p = new Properties();
                rutaProperties = txtRutaProyecto.getText();
                p.load(new FileInputStream(rutaProperties));
                txtNombreProyecto.setEnabled(false);
                txtNombreProyecto.setText(p.getProperty("sbox.proyecto.nombre"));
                setTitle("Proyecto " + txtNombreProyecto.getText());
                String replace = txtRutaProyecto.getText().replace(txtNombreProyecto.getText() + "\\properties.sbox", "");
                txtRutaProyecto.setText(replace);
                txtRutaProyecto.setEnabled(false);
                if ("true".equals(p.getProperty("sbox.proyecto.perspectiva1"))) {
                    faceRecorderCheck.setSelected(true);
                }
                if ("true".equals(p.getProperty("sbox.proyecto.perspectiva2"))) {
                    activityRenderCheck.setSelected(true);
                }
                if ("true".equals(p.getProperty("sbox.proyecto.perspectiva3"))) {
                    agregarPerspectivaButton.setText("Modificar Perspectiva");
                    agregarPerspectivaButton.setToolTipText("Presione si desea modificar la perspectiva");
                    agregarPerspectivaButton.setIcon(new ImageIcon(getClass().getResource("/resources/webcam_add.png")));
                    buscarButton.setVisible(true);
                    txtDirIP.setText(p.getProperty("sbox.proyecto.perspectiva3.ip"));
                    comboDispositivos.setModel(new DefaultComboBoxModel<>(new String[]{p.getProperty("sbox.proyecto.perspectiva3.dispositivo")}));
                    txtDescPerspectiva.setText(p.getProperty("sbox.proyecto.perspectiva3.descripcion"));
                    buscarButton.setEnabled(false);
                    txtDirIP.setEnabled(false);
                    comboDispositivos.setEnabled(false);
                    txtDescPerspectiva.setEnabled(false);
                } else {
                    agregarPerspectivaButton.setText("Agregar Perspectiva");
                    agregarPerspectivaButton.setToolTipText("Presione si desea configurar una nueva perspectiva");
                    agregarPerspectivaButton.setIcon(new ImageIcon(getClass().getResource("/resources/webcam_add.png")));
                    txtDirIP.setText("");
                    comboDispositivos.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccione dispositivo"}));
                    txtDescPerspectiva.setText("");
                    txtDirIP.setEnabled(false);
                    comboDispositivos.setEnabled(false);
                    txtDescPerspectiva.setEnabled(false);
                }
                comboReconocedor.setSelectedItem(p.getProperty("sbox.proyecto.reconocedor.origen"));
                crearProyectoButton.setText("Guardar Cambios");
                rutaProyectoButton.setVisible(false);
                JOptionPane.showMessageDialog(this, "Proyecto cargado con éxito", "Información", JOptionPane.INFORMATION_MESSAGE);
                proyectoCargado = true;
                creacionProyecto = true;
                Properties propLog = new Properties();
                propLog.load(new FileInputStream("log4j.properties"));
                propLog.setProperty("log4j.appender.ARCHIVO.File", txtRutaProyecto.getText() + txtNombreProyecto.getText() + "\\" + txtNombreProyecto.getText() + ".log");
                FileOutputStream outLog = new FileOutputStream("log4j.properties");
                propLog.store(outLog, null);
                PropertyConfigurator.configure("log4j.properties");
                log.info("***************** Proyecto retomado con exito *****************");

            } catch (IOException ex) {
                log.error(ex);
                JOptionPane.showMessageDialog(this, "Proyecto no valido", "Error", JOptionPane.ERROR_MESSAGE);
                seleccionarArchivo(JFileChooser.FILES_ONLY, "Abrir", txtRutaProyecto, "sbox", "C:\\");
            }
        }
    }//GEN-LAST:event_itemMenuAbrirActionPerformed

    private void itemMenuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemMenuSalirActionPerformed
        // TODO add your handling code here:
//        if(pc!=null){
//            pc.enviarInstruccion("CERRAR"); 
//        }
        dispose();
    }//GEN-LAST:event_itemMenuSalirActionPerformed

    private void crearProyectoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crearProyectoButtonActionPerformed
        // TODO add your handling code here:
        if (validarProyecto()) {
            JOptionPane.showMessageDialog(this, "Todos los campos del formulario son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                String nombreProyecto = txtNombreProyecto.getText();
                String rutaProyecto = txtRutaProyecto.getText();
                String path = "";
                if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
                    path = rutaProyecto + nombreProyecto;
                } else {
                    path = rutaProyecto + "\\" + nombreProyecto;
                }
                Properties propLog = new Properties();
                propLog.load(new FileInputStream("log4j.properties"));
                propLog.setProperty("log4j.appender.ARCHIVO.File", path + "\\" + nombreProyecto + ".log");
                FileOutputStream outLog = new FileOutputStream("log4j.properties");
                propLog.store(outLog, null);

                carpetaPrincipal = new File(path);
                File prop = new File(path, "properties.sbox");
                Properties p = new Properties();
                File perspectiva1 = new File(path + "\\perspectiva1");
                File perspectiva2 = new File(path + "\\perspectiva2");
                File perspectiva3 = new File(path + "\\perspectiva3");
                rutaProperties = prop.getAbsolutePath();
                if ("Crear Proyecto".equals(crearProyectoButton.getText())) {

                    if (!carpetaPrincipal.exists()) {
                        if (carpetaPrincipal.mkdirs()) {

                            prop.createNewFile();

                            p.load(new FileInputStream(prop.getAbsolutePath()));

                            try {
                                PropertyConfigurator.configure("log4j.properties");
                                p.setProperty("sbox.proyecto.experimentos", "0");
                                p.setProperty("sbox.proyecto.nombre", txtNombreProyecto.getText());
                                p.setProperty("sbox.proyecto.destino", txtRutaProyecto.getText());
                                log.info("**************************** S-Box ****************************");
                                log.info("Nombre proyecto: " + txtNombreProyecto.getText());
                                if (faceRecorderCheck.isSelected()) {
                                    if (perspectiva1.mkdirs()) {
                                        p.setProperty("sbox.proyecto.perspectiva1", "true");
                                        log.info("FaceRecoder: Habilitado");
                                    }
                                } else {
                                    p.setProperty("sbox.proyecto.perspectiva1", "false");
                                    log.info("FaceRecoder: Deshabilitado");
                                }

                                if (activityRenderCheck.isSelected()) {
                                    if (perspectiva2.mkdirs()) {
                                        p.setProperty("sbox.proyecto.perspectiva2", "true");
                                        log.info("ActivityRender: Habilitado");
                                    }
                                } else {
                                    p.setProperty("sbox.proyecto.perspectiva2", "false");
                                    log.info("ActivityRender: Deshabilitado");
                                }

                                if (agregarPerspectiva) {
                                    if (perspectiva3.mkdirs()) {
                                        p.setProperty("sbox.proyecto.perspectiva3", "true");
                                        p.setProperty("sbox.proyecto.perspectiva3.ip", txtDirIP.getText());
                                        p.setProperty("sbox.proyecto.perspectiva3.dispositivo", comboDispositivos.getSelectedItem().toString());
                                        p.setProperty("sbox.proyecto.perspectiva3.descripcion", txtDescPerspectiva.getText());
                                        log.info("Canal de grabacion externo: Habilitado");
                                        log.info("Descripcion de canal externo: " + txtDescPerspectiva.getText());
                                        log.info("IP de canal externo: " + txtDirIP.getText());
                                        log.info("Dispositivo de grabación de canal externo: " + comboDispositivos.getSelectedItem().toString());
                                    }
                                } else {
                                    p.setProperty("sbox.proyecto.perspectiva3", "false");
                                    log.info("Canal de grabacion externo: Deshabilitado");
                                }

                                p.setProperty("sbox.proyecto.reconocedor.origen", (String) comboReconocedor.getSelectedItem());
                                log.info("Reconocedor: " + comboReconocedor.getSelectedItem());
                                p.setProperty("sbox.fuentes.obtener", "false");
                                FileOutputStream out = new FileOutputStream(prop.getAbsolutePath());
                                p.store(out, null);

                                obtencionFuentes = false;
                                JOptionPane.showMessageDialog(this, "Proyecto creado con éxito", "Información", JOptionPane.INFORMATION_MESSAGE);
                                setTitle(txtNombreProyecto.getText());
                                creacionProyecto = true;
                                tabPanelPrincipal.setEnabledAt(1, true);
                                tabPanelPrincipal.setEnabledAt(2, false);
                                tabPanelPrincipal.setEnabledAt(3, false);
                                log.info("****************** Proyecto creado con exito ******************");

                                if (agregarPerspectiva) {
                                    agregarPerspectivaButton.setText("Modificar Perspectiva");
                                    agregarPerspectivaButton.setToolTipText("Presione si desea modificar una nueva perspectiva");
                                    agregarPerspectivaButton.setIcon(new ImageIcon(getClass().getResource("/resources/webcam_add.png")));
                                    buscarButton.setVisible(true);
                                    txtDirIP.setText(p.getProperty("sbox.proyecto.perspectiva3.ip"));
                                    comboDispositivos.setModel(new DefaultComboBoxModel<>(new String[]{p.getProperty("sbox.proyecto.perspectiva3.dispositivo")}));
                                    txtDescPerspectiva.setText(p.getProperty("sbox.proyecto.perspectiva3.descripcion"));
                                    buscarButton.setEnabled(false);
                                    txtDirIP.setEnabled(false);
                                    comboDispositivos.setEnabled(false);
                                    txtDescPerspectiva.setEnabled(false);
                                } else {
                                    agregarPerspectivaButton.setText("Agregar Perspectiva");
                                }

                                crearProyectoButton.setText("Guardar Cambios");
                                txtNombreProyecto.setEnabled(false);
                                rutaProyectoButton.setVisible(false);
                                proyectoCargado = true;
                            } catch (IOException ex) {
                                log.error(ex);
                                JOptionPane.showMessageDialog(this, "Error al crear el proyecto", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            new Thread("").start();
                        } else {
                            JOptionPane.showMessageDialog(this, "Error al crear el proyecto", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        txtNombreProyecto.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
                        JOptionPane.showMessageDialog(this, "Ya existe un proyecto con el mismo nombre", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } else if ("Guardar Cambios".equals(crearProyectoButton.getText())) {
                    if (!prop.exists()) {
                        prop.createNewFile();
                    }
                    p.load(new FileInputStream(prop.getAbsolutePath()));

                    if (faceRecorderCheck.isSelected()) {
                        p.setProperty("sbox.proyecto.perspectiva1", "true");
                        if (!perspectiva1.exists()) {
                            perspectiva1.mkdirs();
                        }
                    } else if (perspectiva1.exists()) {
                        if (perspectiva1.listFiles().length == 0) {
                            perspectiva1.delete();
                            p.setProperty("sbox.proyecto.perspectiva1", "false");
                        } else {
                            int i = JOptionPane.showConfirmDialog(this, "Si desactiva la perspectiva faceRecorder, perderá los fuentes asociados ¿Desea continuar?", "Advertencia", JOptionPane.WARNING_MESSAGE);
                            switch (i) {
                                case JOptionPane.OK_OPTION:
                                    File[] archivos = perspectiva1.listFiles();
                                    for (File archivo : archivos) {
                                        archivo.delete();
                                    }
                                    perspectiva1.delete();
                                    p.setProperty("sbox.proyecto.perspectiva1", "false");
                                    faceRecorderCheck.setSelected(false);
                                    break;
                                case JOptionPane.CANCEL_OPTION:
                                    faceRecorderCheck.setSelected(true);
                                    break;
                                default:
                                    break;
                            }
                        }

                    }

                    if (activityRenderCheck.isSelected()) {
                        p.setProperty("sbox.proyecto.perspectiva2", "true");
                        if (!perspectiva2.exists()) {
                            perspectiva2.mkdirs();
                        }
                    } else if (perspectiva2.exists()) {
                        if (perspectiva2.listFiles().length == 0) {
                            perspectiva2.delete();
                            p.setProperty("sbox.proyecto.perspectiva2", "false");
                        } else {
                            int i = JOptionPane.showConfirmDialog(this, "Si desactiva la perspectiva activityRender, perderá los fuentes asociados ¿Desea continuar?", "Advertencia", JOptionPane.WARNING_MESSAGE);
                            switch (i) {
                                case JOptionPane.OK_OPTION:
                                    File[] archivos = perspectiva2.listFiles();
                                    for (File archivo : archivos) {
                                        archivo.delete();
                                    }
                                    perspectiva2.delete();
                                    p.setProperty("sbox.proyecto.perspectiva2", "false");
                                    activityRenderCheck.setSelected(false);
                                    break;
                                case JOptionPane.CANCEL_OPTION:
                                    activityRenderCheck.setSelected(true);
                                    break;
                                default:
                                    break;
                            }

                        }
                    }

                    if ("Bloquear Perspectiva".equals(agregarPerspectivaButton.getText()) || "Quitar Perspectiva".equals(agregarPerspectivaButton.getText())) {
                        p.setProperty("sbox.proyecto.perspectiva3", "true");
                        p.setProperty("sbox.proyecto.perspectiva3.ip", txtDirIP.getText());
                        p.setProperty("sbox.proyecto.perspectiva3.dispositivo", comboDispositivos.getSelectedItem().toString());
                        p.setProperty("sbox.proyecto.perspectiva3.descripcion", txtDescPerspectiva.getText());
                        if (!perspectiva3.exists()) {
                            perspectiva3.mkdirs();
                        }

                    }

                    p.setProperty("sbox.proyecto.reconocedor.origen", (String) comboReconocedor.getSelectedItem());
                    FileOutputStream out = new FileOutputStream(prop.getAbsolutePath());
                    p.store(out, null);
                    JOptionPane.showMessageDialog(this, "Proyecto modificado con éxito", "Información", JOptionPane.INFORMATION_MESSAGE);

                    agregarPerspectivaButton.setIcon(new ImageIcon(getClass().getResource("/resources/webcam_add.png")));
                    agregarPerspectivaButton.setText("Modificar Perspectiva");
                    agregarPerspectivaButton.setToolTipText("Presione si desea modificar la perspectiva");
                    txtDirIP.setEnabled(false);
                    buscarButton.setEnabled(false);
                    comboDispositivos.setEnabled(false);
                    txtDescPerspectiva.setEnabled(false);
                }
            } catch (IOException ex) {
                log.error(ex);
            }

        }
    }//GEN-LAST:event_crearProyectoButtonActionPerformed

    private void buscarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarButtonActionPerformed
        // TODO add your handling code here:
        if (!"".equals(txtDirIP.getText()) && !ipOld.equals(txtDirIP.getText())) {
            buscarProgressBar.setIndeterminate(true);
            buscarProgressBar.setVisible(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    pc = new PerspectivaCliente(txtDirIP.getText());
                    if (PerspectivaCliente.con) {
                        pc.start();
                        pc.enviarInstruccion("DISPOSITIVOS");
                        System.out.println("Enviando instruccion a servidor: DISPOSITIVOS");
                        while (!PerspectivaCliente.listaDispositivos) {
                            System.out.println("Esperando dispositivos...");
                        }
                        List<String> dispositivos = pc.getDispositivos();
                        String[] dis = new String[dispositivos.size()];
                        for (int i = 0; i < dispositivos.size(); i++) {
                            dis[i] = dispositivos.get(i);
                            System.out.println(dispositivos.get(i));
                        }
                        comboDispositivos.setModel(new DefaultComboBoxModel<>(dis));
                        comboDispositivos.setEnabled(true);
                        txtDescPerspectiva.setEnabled(true);
                        buscarProgressBar.setIndeterminate(false);
                        buscarProgressBar.setVisible(false);
                    } else {
                        buscarProgressBar.setIndeterminate(false);
                        buscarProgressBar.setVisible(false);
                        JOptionPane.showMessageDialog(null, "No se puede conectar con IP ingresada", "Error", JOptionPane.ERROR_MESSAGE);

                    }

                }
            }).start();

        } else if ("".equals(txtDirIP.getText())) {
            JOptionPane.showMessageDialog(this, "Debe ingresar IP", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (ipOld.equals(txtDirIP.getText())) {
            JOptionPane.showMessageDialog(this, "Ingresó la misma IP", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_buscarButtonActionPerformed

    private void comboDispositivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboDispositivosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboDispositivosActionPerformed

    private void agregarPerspectivaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarPerspectivaButtonActionPerformed

        if (null != agregarPerspectivaButton.getText()) // TODO add your handling code here:
        {
            switch (agregarPerspectivaButton.getText()) {
                case "Agregar Perspectiva":
                    txtDirIP.setEnabled(true);
                    buscarButton.setEnabled(true);
                    agregarPerspectivaButton.setIcon(new ImageIcon(getClass().getResource("/resources/webcam_delete.png")));
                    agregarPerspectivaButton.setText("Quitar Perspectiva");
                    agregarPerspectivaButton.setToolTipText("Presione si desea quitar la perspectiva");
                    agregarPerspectiva = true;
                    break;
                case "Quitar Perspectiva":
                    txtDirIP.setEnabled(false);
                    buscarButton.setEnabled(false);
                    comboDispositivos.setEnabled(false);
                    txtDescPerspectiva.setEnabled(false);
                    txtDirIP.setText("");
                    comboDispositivos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Seleccione dispositivo"}));
                    txtDescPerspectiva.setText("");
                    txtDirIP.setBorder(javax.swing.BorderFactory.createLoweredSoftBevelBorder());
                    comboDispositivos.setBorder(javax.swing.BorderFactory.createLoweredSoftBevelBorder());
                    txtDescPerspectiva.setBorder(javax.swing.BorderFactory.createLoweredSoftBevelBorder());
                    agregarPerspectivaButton.setIcon(new ImageIcon(getClass().getResource("/resources/webcam_add.png")));
                    agregarPerspectivaButton.setText("Agregar Perspectiva");
                    agregarPerspectivaButton.setToolTipText("Presione si desea configurar una nueva perspectiva");
                    agregarPerspectiva = false;
                    break;
                case "Modificar Perspectiva":
                    txtDirIP.setEnabled(true);
                    buscarButton.setEnabled(true);
                    comboDispositivos.setEnabled(true);
                    txtDescPerspectiva.setEnabled(true);
                    agregarPerspectivaButton.setIcon(new ImageIcon(getClass().getResource("/resources/webcam-delete.png")));
                    agregarPerspectivaButton.setText("Bloquear Perspectiva");
                    agregarPerspectivaButton.setToolTipText("Presione si desea bloquear la perspectiva");
                    break;
                case "Bloquear Perspectiva":
                    txtDirIP.setEnabled(false);
                    buscarButton.setEnabled(false);
                    comboDispositivos.setEnabled(false);
                    txtDescPerspectiva.setEnabled(false);
                    agregarPerspectivaButton.setIcon(new ImageIcon(getClass().getResource("/resources/webcam_add.png")));
                    agregarPerspectivaButton.setText("Modificar Perspectiva");
                    agregarPerspectivaButton.setToolTipText("Presione si desea modificar la perspectiva");
                    break;
                default:
                    break;
            }
        }

    }//GEN-LAST:event_agregarPerspectivaButtonActionPerformed

    private void faceRecorderCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_faceRecorderCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_faceRecorderCheckActionPerformed

    private void procesamientoPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_procesamientoPanelComponentShown
        // TODO add your handling code here:
        labelDondeEstoy.setText("Usted está en pestaña Sincronización de Fuentes");
        txtReconecedor.setText(comboReconocedor.getSelectedItem().toString());
        procesandoProgressBar.setVisible(false);
    }//GEN-LAST:event_procesamientoPanelComponentShown

    private void visualizacionPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_visualizacionPanelComponentShown
        // TODO add your handling code here:
        labelDondeEstoy.setText("Usted está en pestaña Visualización de Secuencias");
        txtReco.setText(txtReconecedor.getText());
        txtMaster.setText(txtVideoMaster.getText());
        txtSec.setText(txtVideoSec.getText());
        txtExt.setText(txtVideoExt.getText());
        txtFPS4.setText(txtFPS1.getText());
        txtFPS5.setText(txtFPS2.getText());
        txtFPS6.setText(txtFPS3.getText());
        cortandoProgressBar.setVisible(false);
    }//GEN-LAST:event_visualizacionPanelComponentShown

    private void proyectoPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_proyectoPanelComponentShown
        // TODO add your handling code here:
        if (tabPanelPrincipal.isVisible()) {
            labelDondeEstoy.setText("Usted está en pestaña Creación de Proyecto");

        }
    }//GEN-LAST:event_proyectoPanelComponentShown

    private void rutaProyectoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rutaProyectoButtonActionPerformed
        // TODO add your handling code here:
        seleccionarArchivo(JFileChooser.DIRECTORIES_ONLY, "Guardar", txtRutaProyecto, "", "C:\\");
    }//GEN-LAST:event_rutaProyectoButtonActionPerformed

    private void fuentesPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_fuentesPanelComponentShown
        // TODO add your handling code here:

        labelDondeEstoy.setText("Usted está en pestaña Experimentos");

        Properties p = new Properties();
        try {
            p.load(new FileInputStream(rutaProperties));
        } catch (FileNotFoundException ex) {
            log.error(ex);
        } catch (IOException ex) {
            log.error(ex);
        }
        faceRecorderVerButton.setVisible(false);
        actRenderVerButton.setVisible(false);
        perspExtVerButton.setVisible(false);
        if ("false".equalsIgnoreCase(p.getProperty("sbox.fuentes.obtener"))) {

            guardarFuentesButton.setVisible(false);
            descartarFuentesButton.setVisible(false);
            iniciarButton.setEnabled(true);
            detenerButton.setEnabled(false);
            obtencionFuentes = false;
        } else if ("true".equalsIgnoreCase(p.getProperty("sbox.fuentes.obtener"))) {
//            guardarFuentesButton.setVisible(true);
//            guardarFuentesButton.setEnabled(true);
//            guardarFuentesButton.setText("Modificar Fuentes");
//            descartarFuentesButton.setVisible(false);
//            iniciarButton.setEnabled(false);
            guardarFuentesButton.setVisible(false);
            descartarFuentesButton.setVisible(false);
            iniciarButton.setEnabled(true);
            detenerButton.setEnabled(false);
            obtencionFuentes = true;
            tabPanelPrincipal.setEnabledAt(0, true);
            tabPanelPrincipal.setEnabledAt(1, true);
            tabPanelPrincipal.setEnabledAt(2, true);
            tabPanelPrincipal.setEnabledAt(3, false);
        }
        boolean archivo = false;
        File fuentes = null;
        File[] archivos = null;
        String nombreProyecto = txtNombreProyecto.getText();
        String rutaProyecto = txtRutaProyecto.getText();
        String path = "";
        if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
            path = rutaProyecto + nombreProyecto + "\\";
        } else {
            path = rutaProyecto + "\\" + nombreProyecto + "\\";
        }
        if ("true".equals(p.getProperty("sbox.proyecto.perspectiva1"))) {
//            if (null != p.getProperty("sbox.fuentes.vista.previa") && !"false".equals(p.getProperty("sbox.fuentes.vista.previa"))) {
//                faceRecorderVerButton.setVisible(true);
//            } else {
//                faceRecorderVerButton.setVisible(false);
//            }
            labelFaceRecorder.setIcon(new ImageIcon(getClass().getResource("/resources/OK.png")));
            fuentes = new File(path + "perspectiva1");
            archivos = fuentes.listFiles();
            if (archivos.length > 0) {
                archivo = true;
            }
            faceRecorder = true;
        } else {
            labelFaceRecorder.setIcon(new ImageIcon(getClass().getResource("/resources/No-entry.png")));
            faceRecorder = false;
        }
        if ("true".equals(p.getProperty("sbox.proyecto.perspectiva2"))) {
//            if (null != p.getProperty("sbox.fuentes.vista.previa") && !"false".equals(p.getProperty("sbox.fuentes.vista.previa"))) {
//                actRenderVerButton.setVisible(true);
//            } else {
//                actRenderVerButton.setVisible(false);
//            }
            labelActivityRender.setIcon(new ImageIcon(getClass().getResource("/resources/OK.png")));
            fuentes = new File(path + "perspectiva2");
            archivos = fuentes.listFiles();
            if (archivos.length > 0) {
                archivo = true;
            }
            activityRender = true;
        } else {
            labelActivityRender.setIcon(new ImageIcon(getClass().getResource("/resources/No-entry.png")));
            activityRender = false;
        }
        if ("true".equals(p.getProperty("sbox.proyecto.perspectiva3"))) {
//            if (null != p.getProperty("sbox.fuentes.vista.previa") && !"false".equals(p.getProperty("sbox.fuentes.vista.previa"))) {
//                perspExtVerButton.setVisible(true);
//            } else {
//                perspExtVerButton.setVisible(false);
//            }
            labelPerspExt.setIcon(new ImageIcon(getClass().getResource("/resources/OK.png")));
            fuentes = new File(path + "perspectiva3");
            archivos = fuentes.listFiles();
            if (archivos.length > 0) {
                archivo = true;
            }
            perspExt = true;
//                    labelPerspExtIcon.setText(p.getProperty("sbox.proyecto.perspectiva3.descripcion"));
        } else {
            labelPerspExt.setIcon(new ImageIcon(getClass().getResource("/resources/No-entry.png")));
            perspExt = false;

        }
        tabPanelPrincipal.setEnabledAt(2, archivo);
        faceRecorderGrabando.setVisible(false);
        activityRenderGrabando.setVisible(false);
        perspExtGrabando.setVisible(false);
        confPerspectivaPanel.setVisible(true);
        controlPanel.setVisible(true);


    }//GEN-LAST:event_fuentesPanelComponentShown

    private void iniciarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iniciarButtonActionPerformed
        // TODO add your handling code here:
        log.info("********************* Experimento iniciado *********************");
        if (faceRecorder) {
            faceRecorderGrabando.setString("Preparando cámara...");
            faceRecorderGrabando.setVisible(true);
            faceRecorderGrabando.setIndeterminate(true);
        }
        if (activityRender) {
            activityRenderGrabando.setString("Preparando cámara...");
            activityRenderGrabando.setVisible(true);
            activityRenderGrabando.setIndeterminate(true);
        }
        if (perspExt) {
            perspExtGrabando.setString("Preparando cámara...");
            perspExtGrabando.setVisible(true);
            perspExtGrabando.setIndeterminate(true);
        }
        tabPanelPrincipal.setEnabledAt(0, false);
        tabPanelPrincipal.setEnabledAt(1, true);
        tabPanelPrincipal.setEnabledAt(2, false);
        tabPanelPrincipal.setEnabledAt(3, false);
        iniciarButton.setEnabled(false);
        detenerButton.setEnabled(true);
        guardarFuentesButton.setEnabled(false);
        descartarFuentesButton.setEnabled(false);
        faceRecorderVerButton.setVisible(false);
        actRenderVerButton.setVisible(false);
        perspExtVerButton.setVisible(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Properties p = new Properties();
                try {
                    p.load(new FileInputStream(rutaProperties));
                } catch (FileNotFoundException ex) {
                    log.error(ex);
                } catch (IOException ex) {
                    log.error(ex);
                }
                p.setProperty("sbox.fuentes.obtener", "false");
                obtencionFuentes = false;

                FileOutputStream out;
                try {
                    out = new FileOutputStream(rutaProperties);
                    p.store(out, null);
                } catch (FileNotFoundException ex) {
                    log.error(ex);
                } catch (IOException ex) {
                    log.error(ex);
                }

                experimentos = Integer.parseInt(p.getProperty("sbox.proyecto.experimentos"));
                experimentos = experimentos + 1;
                if ("true".equalsIgnoreCase(p.getProperty("sbox.proyecto.perspectiva3"))) {
                    String device = "";
                    if ("Cámara integrada".equals(comboDispositivos.getSelectedItem().toString())) {
                        device = "0";
                    } else if ("Cámara externa".equals(comboDispositivos.getSelectedItem().toString())) {
                        device = "1";
                    }

                    perspExt = true;
                    if (pc != null) {
                        pc.enviarInstruccion("FRAMES;" + device);
                    } else {
                        pc = new PerspectivaCliente(txtDirIP.getText());
                        if (PerspectivaCliente.con) {
                            pc.start();
                            pc.enviarInstruccion("FRAMES;" + device);
                        } else {
                            JOptionPane.showMessageDialog(null, "No se puede conectar con IP ingresada", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    }
                }

                if ("true".equalsIgnoreCase(p.getProperty("sbox.proyecto.perspectiva1"))) {

                    faceRecorder = true;
                    String nombreProyecto = txtNombreProyecto.getText();
                    String rutaProyecto = txtRutaProyecto.getText();
                    String path = "";
                    if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
                        path = rutaProyecto + nombreProyecto + "\\perspectiva1\\";
                    } else {
                        path = rutaProyecto + "\\" + nombreProyecto + "\\perspectiva1\\";
                    }
                    CameraSwingWorker cameraSwingWorker = new CameraSwingWorker(path, nombreProyecto);
                    cameraSwingWorker.execute();
                }

                while (!ProyectoMain.ScreenGo && faceRecorder) {
                    System.out.println("Esperando que inicie faceRecorder");
                }
                if (faceRecorder) {
                    faceRecorderGrabando.setString("Grabando...");
                    faceRecorderGrabando.setVisible(true);
                    faceRecorderGrabando.setIndeterminate(true);
                }
                while (!ProyectoMain.ScreenGo && perspExt) {
                    System.out.println("Esperando que inicie canal externo");
                }
                if (perspExt) {
                    perspExtGrabando.setString("Grabando...");
                    perspExtGrabando.setVisible(true);
                    perspExtGrabando.setIndeterminate(true);
                }
                if ("true".equalsIgnoreCase(p.getProperty("sbox.proyecto.perspectiva2"))) {
                    try {
                        activityRenderGrabando.setString("Grabando...");
                        activityRenderGrabando.setVisible(true);
                        activityRenderGrabando.setIndeterminate(true);
                        activityRender = true;
                        String nombreProyecto = txtNombreProyecto.getText();
                        String rutaProyecto = txtRutaProyecto.getText();
                        String path = "";
                        if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
                            path = rutaProyecto + nombreProyecto + "\\perspectiva2";
                        } else {
                            path = rutaProyecto + "\\" + nombreProyecto + "\\perspectiva2";
                        }
                        init = new initScreenRecorder();
                        init.start(path, nombreProyecto, experimentos);
                    } catch (IOException | AWTException ex) {
                        log.error(ex);
                    }
                }
            }
        }).start();

    }//GEN-LAST:event_iniciarButtonActionPerformed

    private void detenerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detenerButtonActionPerformed
        // TODO add your handling code here:
        detenerButton.setEnabled(false);
        Reproductor r = new Reproductor();
        String duration = "";

        if (perspExt) {
            perspExtGrabando.setString("Transfiriendo...");
            String nombreProyecto = txtNombreProyecto.getText();
            String rutaProyecto = txtRutaProyecto.getText();
            String path = "";
            if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
                path = rutaProyecto + nombreProyecto + "\\perspectiva3\\";
            } else {
                path = rutaProyecto + "\\" + nombreProyecto + "\\perspectiva3\\";
            }
            pc.setPath(path);
            pc.setNombreProyecto(nombreProyecto);
            pc.setExperimentos(String.valueOf(experimentos));
            pc.enviarInstruccion("DETENER");
            perspExtGrabando.setVisible(false);
            perspExtVerButton.setVisible(true);
            while (!PerspectivaCliente.video) {
                System.out.println("Esperando video externo...");
            }
            videoExt = pc.getNombreProyecto() + "_" + pc.getNombreVideo() + "_" + pc.getExperimentos();

            PerspectivaCliente.video = false;
//            duration = r.getDuration(new File(path + videoExt + ".avi"));
//            log.info("Se ha generado el archivo '" + videoExt + ".avi' - Duración: " + duration);
            log.info("Se ha generado el archivo '" + videoExt + ".avi'");
            ProyectoMain.ScreenStop = true;
        }

        if (faceRecorder) {
            try {
                faceRecorderGrabando.setVisible(false);
                videoFace = capture.stopCamera();
//                _video.setIsVisibleDefault(false);
                String nombreProyecto = txtNombreProyecto.getText();
                String rutaProyecto = txtRutaProyecto.getText();
                String path = "";
                if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
                    path = rutaProyecto + nombreProyecto + "\\perspectiva1\\";
                } else {
                    path = rutaProyecto + "\\" + nombreProyecto + "\\perspectiva1\\";
                }
//                File frame = new File(path + "\\frame\\");
//                
//                if (frame.exists()) {
//                    if (frame.listFiles().length == 0) {
//                        frame.delete();
//                    } else {
//                        File[] archivos = frame.listFiles();
//                        for (File archivo : archivos) {
//                            archivo.delete();
//                        }
//                        frame.delete();
//                    }
//                }
                faceRecorderVerButton.setVisible(true);
//                duration = r.getDuration(new File(path + videoFace + ".avi"));
//                log.info("Se ha generado el archivo '" + videoFace + ".avi' - Duración: " + duration);
                log.info("Se ha generado el archivo '" + videoFace + ".avi'");
                ProyectoMain.ScreenStop = true;
            } catch (FrameRecorder.Exception | FrameGrabber.Exception ex) {
                log.error(ex);
            }
        }

        while (!ProyectoMain.ScreenStop && perspExt) {
            System.out.println("Esperando que termine canal externo");
        }
        if (activityRender) {
            activityRenderGrabando.setVisible(false);
            actRenderVerButton.setVisible(true);
            try {
                videoScreen = init.stop();
                String nombreProyecto = txtNombreProyecto.getText();
                String rutaProyecto = txtRutaProyecto.getText();
                String path = "";
                if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
                    path = rutaProyecto + nombreProyecto + "\\perspectiva2\\";
                } else {
                    path = rutaProyecto + "\\" + nombreProyecto + "\\perspectiva2\\";
                }
//                File p2 = new File(path);
//                File rename = new File(path + "\\activityRender.avi");
//                if (p2.exists()) {
//                    File[] archivos = p2.listFiles();
//                    for (File archivo : archivos) {
//                        archivo.renameTo(rename);
//                    }
//                }
//                duration = r.getDuration(new File(path + videoScreen + ".avi"));
//                log.info("Se ha generado el archivo '" + videoScreen + ".avi' - Duración: " + duration);
                log.info("Se ha generado el archivo '" + videoScreen + ".avi'");
            } catch (IOException ex) {
                log.error(ex);
            }
        }

        guardarFuentesButton.setEnabled(true);
        descartarFuentesButton.setEnabled(true);
        guardarFuentesButton.setVisible(true);
        descartarFuentesButton.setVisible(true);
        descartarFuentesButton.setText("Descartar Fuentes");
        guardarFuentesButton.setText("Guardar Fuentes");
        ProyectoMain.ScreenGo = false;

        log.info("******************** Experimento finalizado ********************");
        String nombreProyecto = txtNombreProyecto.getText();
        String rutaProyecto = txtRutaProyecto.getText();
        String path = "";
        if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
            path = rutaProyecto + nombreProyecto;
        } else {
            path = rutaProyecto + "\\" + nombreProyecto;
        }
        carpetaPrincipal = new File(path);
        File prop = new File(path, "properties.sbox");
        Properties p = new Properties();
        try {
            if (!prop.exists()) {
                prop.createNewFile();
            }
            p.load(new FileInputStream(prop.getAbsolutePath()));
            p.setProperty("sbox.proyecto.experimentos", String.valueOf(experimentos));
            FileOutputStream out = new FileOutputStream(prop.getAbsolutePath());
            p.store(out, null);
        } catch (IOException | NumberFormatException ex) {
            log.error(ex);
        }
    }//GEN-LAST:event_detenerButtonActionPerformed

    private void perspExtVerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_perspExtVerButtonActionPerformed
        // TODO add your handling code here:
        String nombreProyecto = txtNombreProyecto.getText();
        String rutaProyecto = txtRutaProyecto.getText();
        String path = "";
        if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
            path = rutaProyecto + nombreProyecto + "\\perspectiva3\\" + videoExt + ".avi";
        } else {
            path = rutaProyecto + "\\" + nombreProyecto + "\\perspectiva3\\" + videoExt + ".avi";
        }
        Reproductor preview = new Reproductor(new File(path));
        preview.setVisible(true);
    }//GEN-LAST:event_perspExtVerButtonActionPerformed

    private void descartarFuentesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descartarFuentesButtonActionPerformed
        // TODO add your handling code here:
        if ("Descartar Fuentes".equals(descartarFuentesButton.getText())) {
            log.info("*************** Iniciando eliminación de fuentes ***************");

            Properties p = new Properties();
            try {
                p.load(new FileInputStream(rutaProperties));
            } catch (FileNotFoundException ex) {
                log.error(ex);
            } catch (IOException ex) {
                log.error(ex);
            }
            p.setProperty("sbox.fuentes.vista.previa", "false");
            File fuentes = null;
            File[] archivos = null;
            String nombreProyecto = txtNombreProyecto.getText();
            String rutaProyecto = txtRutaProyecto.getText();
            String path = "";
            if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
                path = rutaProyecto + nombreProyecto + "\\";
            } else {
                path = rutaProyecto + "\\" + nombreProyecto + "\\";
            }
            boolean archivo = false;
            if (faceRecorder) {
                fuentes = new File(path + "perspectiva1\\" + videoFace + ".avi");
                if (fuentes.delete()) {
                    log.info("Se ha eliminado el archivo '" + videoFace + ".avi'");
                }
                fuentes = new File(path + "perspectiva1");
                archivos = fuentes.listFiles();
                if (archivos.length > 0) {
                    archivo = true;
                }
            }

            if (activityRender) {
                fuentes = new File(path + "perspectiva2\\" + videoScreen + ".avi");
                if (fuentes.delete()) {
                    log.info("Se ha eliminado el archivo '" + videoScreen + ".avi'");
                }
                fuentes = new File(path + "perspectiva2");
                archivos = fuentes.listFiles();
                if (archivos.length > 0) {
                    archivo = true;
                }
            }

            if (perspExt) {
                fuentes = new File(path + "perspectiva3\\" + videoExt + ".avi");
                if (fuentes.delete()) {
                    log.info("Se ha eliminado el archivo '" + videoExt + ".avi'");
                }
                fuentes = new File(path + "perspectiva3");
                archivos = fuentes.listFiles();
                if (archivos.length > 0) {
                    archivo = true;
                }
            }
            guardarFuentesButton.setVisible(false);
            descartarFuentesButton.setVisible(false);
            iniciarButton.setEnabled(true);
            faceRecorderVerButton.setVisible(false);
            actRenderVerButton.setVisible(false);
            perspExtVerButton.setVisible(false);
            tabPanelPrincipal.setEnabledAt(0, true);
            tabPanelPrincipal.setEnabledAt(1, true);
            tabPanelPrincipal.setEnabledAt(2, archivo);
            tabPanelPrincipal.setEnabledAt(3, false);
            FileOutputStream out;
            try {
                out = new FileOutputStream(rutaProperties);
                p.store(out, null);
            } catch (FileNotFoundException ex) {
                log.error(ex);
            } catch (IOException ex) {
                log.error(ex);
            }
            log.info("************** Finalizando eliminación de fuentes **************");
        }
//        else if ("Cancelar".equals(descartarFuentesButton.getText())) {
//            guardarFuentesButton.setText("Modificar Fuentes");
//            obtencionFuentes = true;
//            iniciarButton.setEnabled(false);
//            descartarFuentesButton.setText("Descartar Fuentes");
//            descartarFuentesButton.setVisible(false);
//            guardarFuentesButton.setEnabled(true);
//            tabPanelPrincipal.setEnabledAt(0, true);
//            tabPanelPrincipal.setEnabledAt(1, true);
//            tabPanelPrincipal.setEnabledAt(2, true);
//            tabPanelPrincipal.setEnabledAt(3, false);
//        }

    }//GEN-LAST:event_descartarFuentesButtonActionPerformed

    private void guardarFuentesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarFuentesButtonActionPerformed
        // TODO add your handling code here:
        if ("Guardar Fuentes".equals(guardarFuentesButton.getText())) {
//            guardarFuentesButton.setText("Modificar Fuentes");
            faceRecorderVerButton.setVisible(false);
            actRenderVerButton.setVisible(false);
            perspExtVerButton.setVisible(false);

            if (faceRecorder) {
                faceRecorderGrabando.setString("Alineando...");
                faceRecorderGrabando.setIndeterminate(true);
                faceRecorderGrabando.setVisible(true);
            }
            if (activityRender) {
                activityRenderGrabando.setString("Alineando...");
                activityRenderGrabando.setIndeterminate(true);
                activityRenderGrabando.setVisible(true);
            }
            if (perspExt) {
                perspExtGrabando.setString("Alineando...");
                perspExtGrabando.setIndeterminate(true);
                perspExtGrabando.setVisible(true);
            }

            guardarFuentesButton.setEnabled(false);
            descartarFuentesButton.setEnabled(false);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String nombreProyecto = txtNombreProyecto.getText();
                    String rutaProyecto = txtRutaProyecto.getText();
                    String path = "";
                    if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
                        path = rutaProyecto + nombreProyecto;
                    } else {
                        path = rutaProyecto + "\\" + nombreProyecto;
                    }
                    if (faceRecorder) {
                        setTime_p1(new File(path + "\\perspectiva1\\" + videoFace + ".avi"));
                    }
                    if (activityRender) {
                        setTime_p2(new File(path + "\\perspectiva2\\" + videoScreen + ".avi"));
                    }
                    if (perspExt) {
                        setTime_p3(new File(path + "\\perspectiva3\\" + videoExt + ".avi"));
                    }
                    iniciarButton.setEnabled(true);
                    descartarFuentesButton.setVisible(false);
                    guardarFuentesButton.setVisible(false);
                    Properties p = new Properties();
                    try {
                        p.load(new FileInputStream(rutaProperties));
                    } catch (FileNotFoundException ex) {
                        log.error(ex);
                    } catch (IOException ex) {
                        log.error(ex);
                    }
                    p.setProperty("sbox.fuentes.obtener", "true");
//            p.setProperty("sbox.fuentes.vista.previa", "true");
                    obtencionFuentes = true;
                    tabPanelPrincipal.setEnabledAt(0, true);
                    tabPanelPrincipal.setEnabledAt(1, true);
                    tabPanelPrincipal.setEnabledAt(2, true);
                    tabPanelPrincipal.setEnabledAt(3, false);
                    FileOutputStream out;
                    try {
                        out = new FileOutputStream(rutaProperties);
                        p.store(out, null);
                    } catch (FileNotFoundException ex) {
                        log.error(ex);
                    } catch (IOException ex) {
                        log.error(ex);
                    }

                    if (faceRecorder) {
                        faceRecorderGrabando.setVisible(false);
                    }
                    if (activityRender) {
                        activityRenderGrabando.setVisible(false);
                    }
                    if (perspExt) {
                        perspExtGrabando.setVisible(false);
                    }
                    JOptionPane.showMessageDialog(null, "Fuentes guardadas con éxito", "Información", JOptionPane.INFORMATION_MESSAGE);
                }
            }).start();
        }
//        else if ("Modificar Fuentes".equals(guardarFuentesButton.getText())) {
//            guardarFuentesButton.setText("Guardar Fuentes");
//            guardarFuentesButton.setEnabled(false);
//            descartarFuentesButton.setVisible(true);
//            iniciarButton.setEnabled(true);
//            descartarFuentesButton.setText("Cancelar");
//            tabPanelPrincipal.setEnabledAt(0, false);
//            tabPanelPrincipal.setEnabledAt(1, true);
//            tabPanelPrincipal.setEnabledAt(2, false);
//            tabPanelPrincipal.setEnabledAt(3, false);
//        }
    }//GEN-LAST:event_guardarFuentesButtonActionPerformed

    private void faceRecorderVerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_faceRecorderVerButtonActionPerformed
        // TODO add your handling code here:
        String nombreProyecto = txtNombreProyecto.getText();
        String rutaProyecto = txtRutaProyecto.getText();
        String path = "";
        if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
            path = rutaProyecto + nombreProyecto + "\\perspectiva1\\" + videoFace + ".avi";
        } else {
            path = rutaProyecto + "\\" + nombreProyecto + "\\perspectiva1\\" + videoFace + ".avi";
        }
        Reproductor preview = new Reproductor(new File(path));
        preview.setVisible(true);
    }//GEN-LAST:event_faceRecorderVerButtonActionPerformed

    private void actRenderVerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actRenderVerButtonActionPerformed
        // TODO add your handling code here:
        String nombreProyecto = txtNombreProyecto.getText();
        String rutaProyecto = txtRutaProyecto.getText();
        String path = "";
        if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
            path = rutaProyecto + nombreProyecto + "\\perspectiva2\\" + videoScreen + ".avi";
        } else {
            path = rutaProyecto + "\\" + nombreProyecto + "\\perspectiva2\\" + videoScreen + ".avi";
        }
        Reproductor preview = new Reproductor(new File(path));
        preview.setVisible(true);
    }//GEN-LAST:event_actRenderVerButtonActionPerformed

    private void tabPanelPrincipalComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabPanelPrincipalComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_tabPanelPrincipalComponentShown

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosing

    private void marcadorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_marcadorButtonActionPerformed
        // TODO add your handling code here:
        if ("Filtrar".equals(marcadorButton.getText())) {
            if (!"".equals(txtVideoMaster.getText()) && !"".equals(txtHolgura.getText())) {
                marcadorButton.setText("Abortar");
                procesandoProgressBar.setVisible(true);
                procesandoProgressBar.setIndeterminate(true);
                procesandoProgressBar.setString("Preparando los datos...");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File videoMaster = new File(txtVideoMaster.getText());
                        File faceDetect = new File(System.getProperty("user.dir") + "\\src\\resources\\haarcascades\\haarcascade_frontalface_default.xml");
                        File smileDetect = new File(System.getProperty("user.dir") + "\\src\\resources\\haarcascades\\" + txtReconecedor.getText());
                        long ini = System.currentTimeMillis();
                        listTimeDetection = videoDetectionM.getDetection(videoMaster, faceDetect, smileDetect, Integer.parseInt(txtHolgura.getText()), procesandoProgressBar);
                        long fin = System.currentTimeMillis();
                        log.info("Duración proceso: " + videoDetectionM.getTimeDetect(fin - ini));
                        if (!abortar) {
                            Object[][] data = new Object[listTimeDetection.size()][3];
                            int i = 0;
                            for (Map.Entry<Integer, TimeDetection> entry : listTimeDetection.entrySet()) {
                                data[i][0] = entry.getKey();
                                data[i][1] = entry.getValue().getStartTime();
                                data[i][2] = entry.getValue().getStopTime();
                                i++;
                            }
                            DefaultTableModel model = new DefaultTableModel();
                            String columnNames[] = {"Tiempo Detección", "Tiempo Inicio", "Tiempo Termino"};
                            model.setDataVector(data, columnNames);
                            deteccionTable.setModel(model);
                            tabPanelPrincipal.setEnabledAt(0, true);
                            tabPanelPrincipal.setEnabledAt(1, true);
                            tabPanelPrincipal.setEnabledAt(2, true);
                            if (!listTimeDetection.isEmpty()) {
                                tabPanelPrincipal.setEnabledAt(3, true);
                            } else {
                                tabPanelPrincipal.setEnabledAt(3, false);
                            }
                            procesandoProgressBar.setVisible(false);
                            procesandoProgressBar.setIndeterminate(false);
                            marcadorButton.setText("Filtrar");
                        } else {
                            abortar = false;
                        }
                    }
                }).start();

            } else if ("".equals(txtVideoMaster.getText())) {
                JOptionPane.showMessageDialog(this, "Debe ingresar video master", "Error", JOptionPane.ERROR_MESSAGE);
            } else if ("".equals(txtHolgura.getText())) {
                JOptionPane.showMessageDialog(this, "Debe ingresar holgura", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if ("Abortar".equals(marcadorButton.getText())) {
            int ax = JOptionPane.showConfirmDialog(this, "¿Está seguro de abortar el proceso?");
            if (ax == JOptionPane.YES_OPTION) {
                abortar = true;
                videoDetectionM.stop();
                txtVideoMaster.setText("");
                txtVideoSec.setText("");
                txtVideoExt.setText("");
                txtFPS1.setText("");
                txtFPS2.setText("");
                txtFPS3.setText("");
                txtHolgura.setText("");

                marcadorButton.setText("Filtrar");
                procesandoProgressBar.setVisible(false);
            }
//            else if (ax == JOptionPane.NO_OPTION) {
//                JOptionPane.showMessageDialog(null, "Has seleccionado NO.");
//            }

        }
    }//GEN-LAST:event_marcadorButtonActionPerformed

    private void cortarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cortarButtonActionPerformed
        // TODO add your handling code here:
        if ("Generar".equals(cortarButton.getText())) {
//            cortarButton.setText("Abortar");
            cortandoProgressBar.setIndeterminate(true);
            cortandoProgressBar.setVisible(true);
            cortandoProgressBar.setString("Preparando los datos...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int cantPers = 0, cantSec = 0, n = 0, total = 0;
                        if (!"".equals(txtFPS1.getText())) {
                            cantPers++;
                        }
                        if (!"".equals(txtFPS2.getText())) {
                            cantPers++;
                        }
                        if (!"".equals(txtFPS3.getText())) {
                            cantPers++;
                        }

                        total = listTimeDetection.size() * cantPers;
                        long ini = System.currentTimeMillis();
                        Reproductor r = new Reproductor();
                        int i = 0;
                        File videoMaster = new File(txtMaster.getText());
                        File f;
                        String nombreProyecto = txtNombreProyecto.getText(), rutaProyecto = txtRutaProyecto.getText(), fileSec, fileSecMaster = null, perspectiva, nomVideo;
                        if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
                            fileSec = rutaProyecto + nombreProyecto;
                        } else {
                            fileSec = rutaProyecto + "\\" + nombreProyecto;
                        }

                        File prop = new File(fileSec, "properties.sbox");
                        Properties p = new Properties();
                        p.load(new FileInputStream(prop.getAbsolutePath()));

                        fileSec = fileSec + "\\secuencias\\" + experimento + "\\";

                        log.info("*************** Inicio proceso cortador de vídeos ***************");
                        cortandoProgressBar.setIndeterminate(false);

                        cortandoProgressBar.setValue(n);
                        cortandoProgressBar.setString(String.valueOf(n) + "%");
                        if (Boolean.parseBoolean(p.getProperty("sbox.proyecto.perspectiva1"))) {
                            perspectiva = "perspectiva1\\";
                            String fileSecP = fileSec + perspectiva;
                            fileSecMaster = fileSecP;
                            f = new File(fileSecP);
                            if (f.mkdirs()) {
                                nomVideo = nombreProyecto + "_videoDetect_faceRecorder_";
                                fileSecP = fileSecP + nomVideo;
                                for (Integer key : listTimeDetection.keySet()) {
//                                    if (!abortar) {
                                    i++;
                                    r.cutVideo(videoMaster, new File(fileSecP + i + ".avi"), listTimeDetection.get(key).getStartTime(), listTimeDetection.get(key).getStopTime());
                                    cantSec++;
                                    n = cantSec * 100 / total;
                                    cortandoProgressBar.setValue(n);
                                    cortandoProgressBar.setString(String.valueOf(n) + "%");
//                                    } else {
//                                        break;
//                                    }
                                }
                            }
                        }

                        if (Boolean.parseBoolean(p.getProperty("sbox.proyecto.perspectiva2"))) {
                            i = 0;
                            perspectiva = "perspectiva2\\";
                            String fileSecP = fileSec + perspectiva;
                            f = new File(fileSecP);
                            if (f.mkdirs()) {
                                nomVideo = nombreProyecto + "_videoDetect_activityRender_";
                                fileSecP = fileSecP + nomVideo;
                                for (Integer key : listTimeDetection.keySet()) {
//                                    if (!abortar) {
                                    i++;
                                    r.cutVideo(new File(txtSec.getText()), new File(fileSecP + i + ".avi"), listTimeDetection.get(key).getStartTime(), listTimeDetection.get(key).getStopTime());
                                    cantSec++;
                                    n = cantSec * 100 / total;
                                    cortandoProgressBar.setValue(n);
                                    cortandoProgressBar.setString(String.valueOf(n) + "%");
//                                    } else {
//                                        break;
//                                    }
                                }
                            }
                        }

                        if (Boolean.parseBoolean(p.getProperty("sbox.proyecto.perspectiva3"))) {
                            i = 0;
                            perspectiva = "perspectiva3\\";
                            String fileSecP = fileSec + perspectiva;
                            f = new File(fileSecP);
                            if (f.mkdirs()) {
                                nomVideo = nombreProyecto + "_videoDetect_canalExterno_";
                                fileSecP = fileSecP + nomVideo;
                                for (Integer key : listTimeDetection.keySet()) {
//                                    if (!abortar) {
                                    i++;
                                    r.cutVideo(new File(txtExt.getText()), new File(fileSecP + i + ".avi"), listTimeDetection.get(key).getStartTime(), listTimeDetection.get(key).getStopTime());
                                    cantSec++;
                                    n = cantSec * 100 / total;
                                    cortandoProgressBar.setValue(n);
                                    cortandoProgressBar.setString(String.valueOf(n) + "%");
//                                    } else {
//                                        break;
//                                    }
                                }
                            }
                        }

//                        if (abortar) {
//                            abortar = false;
//                            r.stop();
//                            //BORRAR SECUENCIAS YA GENERADAS
//                            File pers = new File(fileSecMaster);
//                            File[] archivos = pers.listFiles();
//                            for (File s : archivos) {
//                                s.delete();
//                            }
//                            pers.delete();
//                            if (Boolean.parseBoolean(p.getProperty("sbox.proyecto.perspectiva2"))) {
//                                pers = new File(pers.getParent() + "\\perspectiva2");
//                                archivos = pers.listFiles();
//                                for (File s : archivos) {
//                                    s.delete();
//                                }
//                                pers.delete();
//                            }
//                            if (Boolean.parseBoolean(p.getProperty("sbox.proyecto.perspectiva3"))) {
//                                pers = new File(pers.getParent() + "\\perspectiva3");
//                                archivos = pers.listFiles();
//                                for (File s : archivos) {
//                                    s.delete();
//                                }
//                                pers.delete();
//                            }
//                            pers.getParentFile().delete();
//                        } else {
                        log.info("*************** Fin proceso cortador de vídeos ***************");
                        VideoDetection videoDetection = new VideoDetection();

                        f = new File(fileSecMaster);
                        File[] archivos = f.listFiles();
                        DefaultListModel<String> model = new DefaultListModel<>();
                        for (File s : archivos) {
                            model.addElement(s.getAbsolutePath());
                        }
                        secList.setModel(model);
                        cortandoProgressBar.setIndeterminate(false);
                        cortandoProgressBar.setVisible(false);
                        long fin = System.currentTimeMillis();
                        log.info("Duración proceso: " + videoDetection.getTimeDetect(fin - ini));
//                        }
                        cortarButton.setText("Generar");
                    } catch (IOException ex) {
                        log.error(ex);
                    } finally {
                        cortandoProgressBar.setIndeterminate(false);
                        cortandoProgressBar.setVisible(false);
                    }
                }
            }).start();
        }
//        else if ("Abortar".equals(cortarButton.getText())) {
//            int ax = JOptionPane.showConfirmDialog(this, "¿Está seguro de abortar el proceso?");
//            if (ax == JOptionPane.YES_OPTION) {
//                abortar = true;
//                txtMaster.setText("");
//                txtSec.setText("");
//                txtExt.setText("");
//                txtFPS4.setText("");
//                txtFPS5.setText("");
//                txtFPS6.setText("");
//                cortandoProgressBar.setVisible(false);
//                cortarButton.setText("Generar");
//                log.info("Proceso de corte abortado");
//            }
//        }
    }//GEN-LAST:event_cortarButtonActionPerformed

    private void visualizarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visualizarButtonActionPerformed
        // TODO add your handling code here:
        ReproductorSec rSec = null;
        if (!secList.isSelectionEmpty()) {
            try {
                File f, secSecundario = null, secExterno = null;
                File[] archivos;
                String nombreProyecto = txtNombreProyecto.getText(), rutaProyecto = txtRutaProyecto.getText(), path, perspectiva;
                if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
                    path = rutaProyecto + nombreProyecto;
                } else {
                    path = rutaProyecto + "\\" + nombreProyecto;
                }
                File secMaster = new File(secList.getSelectedValue());
                String[] a = secMaster.getName().split(".avi");
                String[] b = a[0].split("_");
                int l = b.length;
                secuencia = b[l - 1];
                File prop = new File(path, "properties.sbox");
                Properties p = new Properties();
                p.load(new FileInputStream(prop.getAbsolutePath()));
                String secMasterParent = secMaster.getParentFile().getParent();
                if (Boolean.parseBoolean(p.getProperty("sbox.proyecto.perspectiva2"))) {
                    perspectiva = "\\perspectiva2\\";
                    String secMasterParentP = secMasterParent + perspectiva;
                    f = new File(secMasterParentP);
                    archivos = f.listFiles();
                    for (File s : archivos) {
                        a = s.getName().split(".avi");
                        b = a[0].split("_");
                        l = b.length;
                        if (b[l - 1].equals(secuencia)) {
                            secSecundario = new File(s.getAbsolutePath());
                        }
                    }
                }
                if (Boolean.parseBoolean(p.getProperty("sbox.proyecto.perspectiva3"))) {
                    perspectiva = "\\perspectiva3\\";
                    String secMasterParentP = secMasterParent + perspectiva;
                    f = new File(secMasterParentP);
                    archivos = f.listFiles();
                    for (File s : archivos) {
                        a = s.getName().split(".avi");
                        b = a[0].split("_");
                        l = b.length;
                        if (b[l - 1].equals(secuencia)) {
                            secExterno = new File(s.getAbsolutePath());
                        }
                    }
                }

                rSec = new ReproductorSec(secMaster, secSecundario, secExterno);
                rSec.setVisible(true);
            } catch (FileNotFoundException ex) {
                log.info(ex);
            } catch (IOException ex) {
                log.info(ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una secuencia master", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_visualizarButtonActionPerformed

    private void videoMasterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_videoMasterButtonActionPerformed
        try {
            // TODO add your handling code here:
            VideoDetection vd = new VideoDetection();
            Reproductor r = new Reproductor();
            File f;
            File[] archivos;
            String nombreProyecto = txtNombreProyecto.getText(), rutaProyecto = txtRutaProyecto.getText(), fileSec, perspectiva;
            if ("C:\\".equalsIgnoreCase(rutaProyecto)) {
                fileSec = rutaProyecto + nombreProyecto;
            } else {
                fileSec = rutaProyecto + "\\" + nombreProyecto;
            }
            seleccionarArchivo(JFileChooser.FILES_ONLY, "Abrir", txtVideoMaster, "avi", fileSec + "\\perspectiva1\\Alineado");

            File videoMaster = new File(txtVideoMaster.getText());
            String[] a = videoMaster.getName().split(".avi");
            String[] b = a[0].split("_");
            int l = b.length;
            experimento = b[l - 1];
            f = new File(fileSec + "\\secuencias\\" + String.valueOf(experimento));
            if (!f.exists()) {
                txtFPS1.setText(r.getFps(new File(txtVideoMaster.getText())));
                File prop = new File(fileSec, "properties.sbox");
                Properties p = new Properties();
                p.load(new FileInputStream(prop.getAbsolutePath()));

                if (Boolean.parseBoolean(p.getProperty("sbox.proyecto.perspectiva2"))) {
                    perspectiva = "\\perspectiva2\\Alineado";
                    
                    f = new File(fileSec + perspectiva);
                    archivos = f.listFiles();
                    for (File s : archivos) {
                        a = s.getName().split(".avi");
                        b = a[0].split("_");
                        l = b.length;
                        if (b[l - 1].equals(experimento)) {
                            txtVideoSec.setText(s.getAbsolutePath());
                            txtFPS2.setText(r.getFps(new File(s.getAbsolutePath())));
                        }
                    }
                    if ("".equals(txtVideoSec.getText())) {
                        txtVideoSec.setText("Video no disponible");
                    }
                } else {
                    txtVideoSec.setText("Perspectiva no configurada para este experimento");
                }

                if (Boolean.parseBoolean(p.getProperty("sbox.proyecto.perspectiva3"))) {
                    perspectiva = "\\perspectiva3\\Alineado";
                    
                    f = new File(fileSec + perspectiva);
                    archivos = f.listFiles();
                    for (File s : archivos) {
                        a = s.getName().split(".avi");
                        b = a[0].split("_");
                        l = b.length;
                        if (b[l - 1].equals(experimento)) {
                            txtVideoExt.setText(s.getAbsolutePath());
                            txtFPS3.setText(r.getFps(new File(s.getAbsolutePath())));

                        }
                    }
                    if ("".equals(txtVideoExt.getText())) {
                        txtVideoExt.setText("video no disponible");
                    }
                } else {
                    txtVideoExt.setText("Perspectiva no configurada para este experimento");
                }
            } else {
                int ax = JOptionPane.showConfirmDialog(this, "El video que seleccionó ya ha sido procesado ¿Desea seleccionar otro?");
                if (ax == JOptionPane.YES_OPTION) {

                    videoMasterButtonActionPerformed(evt);
                } else {
                    txtVideoMaster.setText("");
                }
            }

        } catch (IOException ex) {
            log.info(ex);
        }
    }//GEN-LAST:event_videoMasterButtonActionPerformed

    private void txtReconecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtReconecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReconecedorActionPerformed

    /**
     *
     * @return
     */
    private boolean validarProyecto() {
        boolean respuesta = false;
        if ("".equals(txtNombreProyecto.getText())) {
            respuesta = true;
            txtNombreProyecto.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
        } else {
            txtNombreProyecto.setBorder(javax.swing.BorderFactory.createLoweredSoftBevelBorder());
        }

        if ("".equals(txtRutaProyecto.getText())) {
            respuesta = true;
            txtRutaProyecto.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
        } else {
            txtRutaProyecto.setBorder(javax.swing.BorderFactory.createLoweredSoftBevelBorder());
        }

        if (!faceRecorderCheck.isSelected() && !activityRenderCheck.isSelected()) {
            respuesta = true;
            faceRecorderCheck.setForeground(Color.red);
            activityRenderCheck.setForeground(Color.red);
        } else {
            faceRecorderCheck.setForeground(Color.black);
            activityRenderCheck.setForeground(Color.black);
        }

        if ("Seleccione reconocedor".equals(comboReconocedor.getSelectedItem())) {
            respuesta = true;
            comboReconocedor.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
        } else {
            comboReconocedor.setBorder(javax.swing.BorderFactory.createLoweredSoftBevelBorder());
        }

        if (agregarPerspectiva) {
            if ("Seleccione dispositivo".equals(comboDispositivos.getSelectedItem().toString()) || "".equals(comboDispositivos.getSelectedItem().toString()) || comboDispositivos.getSelectedItem() == null) {
                respuesta = true;
                comboDispositivos.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
            } else {
                comboDispositivos.setBorder(javax.swing.BorderFactory.createLoweredSoftBevelBorder());
            }

            if ("".equals(txtDirIP.getText())) {
                respuesta = true;
                txtDirIP.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
            } else {
                txtDirIP.setBorder(javax.swing.BorderFactory.createLoweredSoftBevelBorder());
            }

            if ("".equals(txtDescPerspectiva.getText())) {
                respuesta = true;
                txtDescPerspectiva.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
            } else {
                txtDescPerspectiva.setBorder(javax.swing.BorderFactory.createLoweredSoftBevelBorder());
            }

        }

        return respuesta;
    }

    private void seleccionarArchivo(int fileSelectionMode, String approveButtonText, JTextField field, String fileFilter, String currentDirectory) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(fileSelectionMode);
        if (!"".equals(fileFilter)) {
            FileNameExtensionFilter filtro = new FileNameExtensionFilter("*." + fileFilter, fileFilter.toLowerCase(), fileFilter.toUpperCase());
            fileChooser.setFileFilter(filtro);
        }
//        fileChooser.setCurrentDirectory(new File("C:\\"));
        fileChooser.setCurrentDirectory(new File(currentDirectory));
        int result = fileChooser.showDialog(this, approveButtonText);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            field.setText(selectedFile.getAbsolutePath());
        }
    }

    public void setTime_p1(File source) {
        File alineado = new File(source.getParent() + "\\Alineado");
        Mat mat = new Mat();
        Frame frame = new Frame();
        IplImage iplImage = null;
        int captureWidth = 1366, captureHeight = 768;
        long startTime = 0, videoTS;
        OpenCVFrameConverter.ToIplImage converter = null;
        VideoCapture cap = null;
        FFmpegFrameRecorder recorder = null;
        CvMemStorage storage;
        CvFont mCvFont = new CvFont();
        cvInitFont(mCvFont, CV_FONT_HERSHEY_TRIPLEX, 0.5f, 1.0f, 0, 1, 8);
        try {
            cap = new VideoCapture(source.getAbsolutePath());
            converter = new OpenCVFrameConverter.ToIplImage();
            if (!alineado.exists()) {
                alineado.mkdirs();
            }
            recorder = new FFmpegFrameRecorder(alineado.getAbsolutePath() + "\\" + source.getName(), captureWidth, captureHeight, 2);
            recorder.setInterleaved(true);
            recorder.setVideoOption("tune", "zerolatency");
            recorder.setVideoOption("preset", "ultrafast");
            recorder.setVideoOption("crf", "28");
            recorder.setVideoBitrate(2000000);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("avi");
            recorder.setFrameRate(20);
            recorder.setGopSize(40);
            recorder.start();
            initReloj_p1();
            t_p1.start();
            //iniciarCronometro();
            while (cap.grab()) {
                if (cap.retrieve(mat)) {
                    frame = converter.convert(mat);
                    iplImage = converter.convert(frame);
                    storage = CvMemStorage.create();
                    cvClearMemStorage(storage);
                    int x = 950;
                    int y = 700;
                    cvPutText(iplImage, initReloj_p1(), cvPoint(x, y), mCvFont, CvScalar.BLACK);
                    //cvPutText(iplImage, getTimeText(), cvPoint(x, y), mCvFont, CvScalar.RED);
                    if (startTime == 0) {
                        startTime = System.currentTimeMillis();
                    }
                    videoTS = 1000 * (System.currentTimeMillis() - startTime);

                    if (videoTS > recorder.getTimestamp()) {
                        recorder.setTimestamp(videoTS);
                    }
                    recorder.record(converter.convert(iplImage));
                }
            }

            recorder.stop();
            cap.release();
            pararCronometro();
            if (t_p1.isRunning()) {
                t_p1.stop();
            }
        } catch (FrameRecorder.Exception ex) {
            log.error(ex);
        } finally {
            try {
                recorder.stop();
                cap.release();
                pararCronometro();
                if (t_p1.isRunning()) {
                    t_p1.stop();
                }
            } catch (FrameRecorder.Exception ex) {
                log.error(ex);
            }
        }

    }
    public void setTime_p2(File source) {
        File alineado = new File(source.getParent() + "\\Alineado");
        Mat mat = new Mat();
        Frame frame = new Frame();
        IplImage iplImage = null;
        int captureWidth = 1366, captureHeight = 768;
        long startTime = 0, videoTS;
        OpenCVFrameConverter.ToIplImage converter = null;
        VideoCapture cap = null;
        FFmpegFrameRecorder recorder = null;
        CvMemStorage storage;
        CvFont mCvFont = new CvFont();
        cvInitFont(mCvFont, CV_FONT_HERSHEY_TRIPLEX, 0.5f, 1.0f, 0, 1, 8);
        try {
            cap = new VideoCapture(source.getAbsolutePath());
            converter = new OpenCVFrameConverter.ToIplImage();
            if (!alineado.exists()) {
                alineado.mkdirs();
            }
            recorder = new FFmpegFrameRecorder(alineado.getAbsolutePath() + "\\" + source.getName(), captureWidth, captureHeight, 2);
            recorder.setInterleaved(true);
            recorder.setVideoOption("tune", "zerolatency");
            recorder.setVideoOption("preset", "ultrafast");
            recorder.setVideoOption("crf", "28");
            recorder.setVideoBitrate(2000000);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("avi");
            recorder.setFrameRate(20);
            recorder.setGopSize(40);
            recorder.start();
            initReloj_p2();
            t_p2.start();
            //iniciarCronometro();
            while (cap.grab()) {
                if (cap.retrieve(mat)) {
                    frame = converter.convert(mat);
                    iplImage = converter.convert(frame);
                    storage = CvMemStorage.create();
                    cvClearMemStorage(storage);
                    int x = 950;
                    int y = 700;
                    cvPutText(iplImage, initReloj_p2(), cvPoint(x, y), mCvFont, CvScalar.BLACK);
                    //cvPutText(iplImage, getTimeText(), cvPoint(x, y), mCvFont, CvScalar.RED);
                    if (startTime == 0) {
                        startTime = System.currentTimeMillis();
                    }
                    videoTS = 1000 * (System.currentTimeMillis() - startTime);

                    if (videoTS > recorder.getTimestamp()) {
                        recorder.setTimestamp(videoTS);
                    }
                    recorder.record(converter.convert(iplImage));
                }
            }

            recorder.stop();
            cap.release();
            pararCronometro();
            if (t_p2.isRunning()) {
                t_p2.stop();
            }
        } catch (FrameRecorder.Exception ex) {
            log.error(ex);
        } finally {
            try {
                recorder.stop();
                cap.release();
                pararCronometro();
                if (t_p2.isRunning()) {
                    t_p2.stop();
                }
            } catch (FrameRecorder.Exception ex) {
                log.error(ex);
            }
        }

    }
    public void setTime_p3(File source) {
        File alineado = new File(source.getParent() + "\\Alineado");
        Mat mat = new Mat();
        Frame frame = new Frame();
        IplImage iplImage = null;
        int captureWidth = 1366, captureHeight = 768;
        long startTime = 0, videoTS;
        OpenCVFrameConverter.ToIplImage converter = null;
        VideoCapture cap = null;
        FFmpegFrameRecorder recorder = null;
        CvMemStorage storage;
        CvFont mCvFont = new CvFont();
        cvInitFont(mCvFont, CV_FONT_HERSHEY_TRIPLEX, 0.5f, 1.0f, 0, 1, 8);
        try {
            cap = new VideoCapture(source.getAbsolutePath());
            converter = new OpenCVFrameConverter.ToIplImage();
            if (!alineado.exists()) {
                alineado.mkdirs();
            }
            recorder = new FFmpegFrameRecorder(alineado.getAbsolutePath() + "\\" + source.getName(), captureWidth, captureHeight, 2);
            recorder.setInterleaved(true);
            recorder.setVideoOption("tune", "zerolatency");
            recorder.setVideoOption("preset", "ultrafast");
            recorder.setVideoOption("crf", "28");
            recorder.setVideoBitrate(2000000);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("avi");
            recorder.setFrameRate(20);
            recorder.setGopSize(40);
            recorder.start();
            initReloj_p3();
            t_p3.start();
            //iniciarCronometro();
            while (cap.grab()) {
                if (cap.retrieve(mat)) {
                    frame = converter.convert(mat);
                    iplImage = converter.convert(frame);
                    storage = CvMemStorage.create();
                    cvClearMemStorage(storage);
                    int x = 950;
                    int y = 700;
                    cvPutText(iplImage, initReloj_p3(), cvPoint(x, y), mCvFont, CvScalar.BLACK);
                    //cvPutText(iplImage, getTimeText(), cvPoint(x, y), mCvFont, CvScalar.RED);
                    if (startTime == 0) {
                        startTime = System.currentTimeMillis();
                    }
                    videoTS = 1000 * (System.currentTimeMillis() - startTime);

                    if (videoTS > recorder.getTimestamp()) {
                        recorder.setTimestamp(videoTS);
                    }
                    recorder.record(converter.convert(iplImage));
                }
            }

            recorder.stop();
            cap.release();
            pararCronometro();
            if (t_p3.isRunning()) {
                t_p3.stop();
            }
        } catch (FrameRecorder.Exception ex) {
            log.error(ex);
        } finally {
            try {
                recorder.stop();
                cap.release();
                pararCronometro();
                if (t_p3.isRunning()) {
                    t_p3.stop();
                }
            } catch (FrameRecorder.Exception ex) {
                log.error(ex);
            }
        }

    }

    public String initReloj_p1() {
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
        final String date = formateador.format(ahora);
        t_p1 = new Timer(1, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent ae) {
                ++cs_p1;
                if (cs_p1 == 100) {
                    cs_p1 = 0;
                    ++s_p1;
                }
                if (s_p1 == 60) {
                    s_p1 = 0;
                    ++m_p1;
                }
                if (m_p1 == 60) {
                    m_p1 = 0;
                    ++h_p1;
                }
                cronometro_p1 = new StringBuilder();
                cronometro_p1.append(date)
                        .append(" ")
                        .append((h_p1 <= 9 ? "0" : ""))
                        .append(h_p1)
                        .append(":")
                        .append((m_p1 <= 9 ? "0" : ""))
                        .append(m_p1)
                        .append(":")
                        .append((s_p1 <= 9 ? "0" : ""))
                        .append(s_p1)
                        .append(":")
                        .append((cs_p1 <= 9 ? "0" : ""))
                        .append(cs_p1);
            }
        });
        return cronometro_p1.toString();
    }
    public String initReloj_p2() {
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
        final String date = formateador.format(ahora);
        t_p2 = new Timer(1, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent ae) {
                ++cs_p2;
                if (cs_p2 == 100) {
                    cs_p2 = 0;
                    ++s_p2;
                }
                if (s_p2 == 60) {
                    s_p2 = 0;
                    ++m_p2;
                }
                if (m_p2 == 60) {
                    m_p2 = 0;
                    ++h_p2;
                }
                cronometro_p2 = new StringBuilder();
                cronometro_p2.append(date)
                        .append(" ")
                        .append((h_p2 <= 9 ? "0" : ""))
                        .append(h_p2)
                        .append(":")
                        .append((m_p2 <= 9 ? "0" : ""))
                        .append(m_p2)
                        .append(":")
                        .append((s_p2 <= 9 ? "0" : ""))
                        .append(s_p2)
                        .append(":")
                        .append((cs_p2 <= 9 ? "0" : ""))
                        .append(cs_p2);
            }
        });
        return cronometro_p2.toString();
    }
    public String initReloj_p3() {
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
        final String date = formateador.format(ahora);
        t_p3 = new Timer(1, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent ae) {
                ++cs_p3;
                if (cs_p3 == 100) {
                    cs_p3 = 0;
                    ++s_p3;
                }
                if (s_p3 == 60) {
                    s_p3 = 0;
                    ++m_p3;
                }
                if (m_p3 == 60) {
                    m_p3 = 0;
                    ++h_p3;
                }
                cronometro_p3 = new StringBuilder();
                cronometro_p3.append(date)
                        .append(" ")
                        .append((h_p3 <= 9 ? "0" : ""))
                        .append(h_p3)
                        .append(":")
                        .append((m_p3 <= 9 ? "0" : ""))
                        .append(m_p3)
                        .append(":")
                        .append((s_p3 <= 9 ? "0" : ""))
                        .append(s_p3)
                        .append(":")
                        .append((cs_p3 <= 9 ? "0" : ""))
                        .append(cs_p3);
            }
        });
        return cronometro_p3.toString();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows Classic".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            log.error(ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ProyectoMain().setVisible(true);
            }
        });
    }

    //Socket
    private final String ipOld = "";
    private PerspectivaCliente pc = null;

    //Flag pestañas
    private boolean proyectoCargado = false;
    private boolean creacionProyecto = false;
    private boolean obtencionFuentes = false;
    private boolean syncFuentes = false;
    private boolean visualizacion = false;

    //Flag Perspectivas
    private boolean agregarPerspectiva = false;
    private boolean faceRecorder = false;
    private boolean activityRender = false;
    private boolean perspExt = false;

    //Grabacion
    private WebcamAndMicrophoneCapture capture = null;
    private initScreenRecorder init = null;

    //File
    private File carpetaPrincipal = null;
    private String rutaProperties = "";
    private String videoFace = "";
    private String videoScreen = "";
    private String videoExt = "";
    private String rutaHaarcascades = "";
    private int experimentos = 0;
    private String experimento = "";
    private String secuencia = "";

    public static boolean ScreenGo = false;
    public static boolean ScreenStop = false;
    public boolean abortar = false;

    private StringBuilder cronometro_p1 = new StringBuilder();
    private StringBuilder cronometro_p2 = new StringBuilder();
    private StringBuilder cronometro_p3 = new StringBuilder();
    private Timer t_p1,t_p2,t_p3;
    private int h_p1, m_p1, s_p1, cs_p1;
    private int h_p2, m_p2, s_p2, cs_p2;
    private int h_p3, m_p3, s_p3, cs_p3;

    private Map<Integer, TimeDetection> listTimeDetection = new HashMap<Integer, TimeDetection>();
    private VideoDetection videoDetectionM = new VideoDetection();
    //Log
    private final static Logger log = Logger.getLogger(ProyectoMain.class.getName());
    Thread hilo;
    boolean cronometroActivo;
    private String timeText;

    public String getTimeText() {
        return timeText;
    }

    public void setTimeText(String timeText) {
        this.timeText = timeText;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton actRenderVerButton;
    private javax.swing.JCheckBox activityRenderCheck;
    private javax.swing.JProgressBar activityRenderGrabando;
    private javax.swing.JLabel activityRenderIcon;
    private javax.swing.JButton agregarPerspectivaButton;
    private javax.swing.JButton buscarButton;
    private javax.swing.JProgressBar buscarProgressBar;
    private javax.swing.JComboBox<String> comboDispositivos;
    private javax.swing.JComboBox<String> comboReconocedor;
    private javax.swing.JPanel confPerspectivaPanel;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JProgressBar cortandoProgressBar;
    private javax.swing.JButton cortarButton;
    private javax.swing.JButton crearProyectoButton;
    private javax.swing.JScrollPane descPerspectivaScrollPanel;
    private javax.swing.JButton descartarFuentesButton;
    private javax.swing.JTable deteccionTable;
    private javax.swing.JButton detenerButton;
    private javax.swing.JCheckBox faceRecorderCheck;
    private javax.swing.JProgressBar faceRecorderGrabando;
    private javax.swing.JLabel faceRecorderIcon;
    private javax.swing.JButton faceRecorderVerButton;
    private javax.swing.JPanel filtroPanel;
    private javax.swing.JPanel filtroPanel2;
    private javax.swing.JPanel fuentesPanel;
    private javax.swing.JButton guardarFuentesButton;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JPanel informacionPerspectivaPanel;
    private javax.swing.JPanel informacionProyectoPanel;
    private javax.swing.JButton iniciarButton;
    private javax.swing.JMenuItem itemMenuAbrir;
    private javax.swing.JMenuItem itemMenuNuevo;
    private javax.swing.JMenuItem itemMenuSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel labelActivityRender;
    private javax.swing.JLabel labelActivityRenderIcon;
    private javax.swing.JLabel labelDescCheck;
    private javax.swing.JLabel labelDescPerspectiva;
    private javax.swing.JLabel labelDirIP;
    private javax.swing.JLabel labelDispositivos;
    private javax.swing.JLabel labelDondeEstoy;
    private javax.swing.JLabel labelExt;
    private javax.swing.JLabel labelFPS;
    private javax.swing.JLabel labelFPS2;
    private javax.swing.JLabel labelFaceRecorder;
    private javax.swing.JLabel labelFaceRecorderIcon;
    private javax.swing.JLabel labelHolgura;
    private javax.swing.JLabel labelMaster;
    private javax.swing.JLabel labelNok;
    private javax.swing.JLabel labelNombreProyecto;
    private javax.swing.JLabel labelOk;
    private javax.swing.JLabel labelPerspExt;
    private javax.swing.JLabel labelPerspExtIcon;
    private javax.swing.JLabel labelReco;
    private javax.swing.JLabel labelReconocedor;
    private javax.swing.JLabel labelRutaProyecto;
    private javax.swing.JLabel labelSec;
    private javax.swing.JLabel labelSecMaster;
    private javax.swing.JLabel labelSeg;
    private javax.swing.JLabel labelVideoExt;
    private javax.swing.JLabel labelVideoMaster;
    private javax.swing.JLabel labelVideoSec;
    private javax.swing.JPanel marcadoPanel;
    private javax.swing.JButton marcadorButton;
    private javax.swing.JMenuBar menuBarPrincipal;
    private javax.swing.JMenu menuProyecto;
    private javax.swing.JProgressBar perspExtGrabando;
    private javax.swing.JButton perspExtVerButton;
    private javax.swing.JPanel perspectivaPanel;
    private javax.swing.JPanel preliminarPanel;
    private javax.swing.JPanel procesamientoPanel;
    private javax.swing.JProgressBar procesandoProgressBar;
    private javax.swing.JPanel proyectoPanel;
    private javax.swing.JPanel reconocedorPanel;
    private javax.swing.JButton rutaProyectoButton;
    private javax.swing.JList<String> secList;
    private javax.swing.JScrollPane secScrollPane;
    private javax.swing.JPanel secuenciasPanel;
    private javax.swing.JPopupMenu.Separator separadorMenuArchivo;
    private javax.swing.JPanel simbolosPanel;
    private javax.swing.JPanel statusBarPanel;
    private javax.swing.JTabbedPane tabPanelPrincipal;
    private javax.swing.JScrollPane tableScrollPane1;
    private javax.swing.JTextArea txtDescPerspectiva;
    private javax.swing.JTextField txtDirIP;
    private javax.swing.JTextField txtExt;
    private javax.swing.JTextField txtFPS1;
    private javax.swing.JTextField txtFPS2;
    private javax.swing.JTextField txtFPS3;
    private javax.swing.JTextField txtFPS4;
    private javax.swing.JTextField txtFPS5;
    private javax.swing.JTextField txtFPS6;
    private javax.swing.JTextField txtHolgura;
    private javax.swing.JTextField txtMaster;
    private javax.swing.JTextField txtNombreProyecto;
    private javax.swing.JTextField txtReco;
    private javax.swing.JTextField txtReconecedor;
    private javax.swing.JTextField txtRutaProyecto;
    private javax.swing.JTextField txtSec;
    private javax.swing.JTextField txtVideoExt;
    private javax.swing.JTextField txtVideoMaster;
    private javax.swing.JTextField txtVideoSec;
    private javax.swing.JButton videoMasterButton;
    private javax.swing.JPanel visualizacionPanel;
    private javax.swing.JButton visualizarButton;
    // End of variables declaration//GEN-END:variables
}
