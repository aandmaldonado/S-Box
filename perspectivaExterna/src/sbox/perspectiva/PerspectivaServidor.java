/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.perspectiva;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.URIReferenceException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.bytedeco.javacpp.opencv_highgui.VideoCapture;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.jdesktop.swingworker.SwingWorker;

/**
 *
 * @author Álvaro Andrés Maldonado Pinto
 */
public class PerspectivaServidor {

    private ServerSocket serverSocket;
    private final int port = 3000;
    private BufferedReader entrada;
    private ObjectOutputStream salida;
    private Socket conexion;
    private boolean running = true;
    private Video _video = null;
    private String device = "";
    private File temp = null;
    private String nombreVideo = "";
    public static boolean grab = false;
    private final static Logger log = Logger.getLogger("perspectiva");

    class CameraSwingWorker extends SwingWorker<String, Object> {

        public CameraSwingWorker() {

        }

        @Override
        public String doInBackground() throws Exception {
            try {
                _video = new Video(temp.getAbsolutePath());
                _video.startCamera(Integer.parseInt(device));
            } catch (FrameGrabber.Exception | FrameRecorder.Exception | IOException | URISyntaxException | URIReferenceException e) {
                log.error(e);
            }
            return null;
        }

    }

    public PerspectivaServidor() {
        temp = new File("C:\\temp\\");
        if (!temp.exists()) {
            temp.mkdir();
        }
        PropertyConfigurator.configure("log4j.properties");
        escuchar();
    }

    public static void main(String[] args) {
        PerspectivaServidor p = new PerspectivaServidor();
    }

    public void obtenerDispositivos() {
        List<String> dispositivos = new ArrayList<>();
        VideoCapture cap = new VideoCapture(0);
        VideoCapture cap1 = new VideoCapture(1);
        if (cap.isOpened()) {
            cap.release();
            dispositivos.add("Cámara integrada");
        }

        if (cap1.isOpened()) {
            cap1.release();
            dispositivos.add("Cámara externa");
        }

        if (dispositivos.isEmpty()) {
            cap.release();
            cap1.release();
            dispositivos.add("No hay dispositivos");
        }

        enviarObj(dispositivos);
    }

    public void iniciarGrabacion(String cmd) {
        String[] a = cmd.split(";");
        device = a[1];
        CameraSwingWorker cameraSwingWorker = new CameraSwingWorker();
        cameraSwingWorker.execute();
        while (!PerspectivaServidor.grab) {
            System.out.println("Esperando grabación");
        }
        enviarObj("Canal externo: Inicio de grabación");
    }

    public void enviarArchivo(File f) {
        try {
            enviarObj("nombreVideo:" + nombreVideo);
            enviarObj("Canal externo: Transfiriendo video");
            byte[] readAllBytes = Files.readAllBytes(f.toPath());
            enviarObj(readAllBytes);
            enviarObj("Canal externo: Video Recibido con exito");
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    public void detenerGrabacion() {
        try {
            nombreVideo = _video.stopCamera();
            while (PerspectivaServidor.grab) {                
                System.out.println("Deteniendo grabación");  
            }
            enviarObj("Canal externo: Fin de grabación");
            enviarArchivo(new File("C:\\temp\\" + nombreVideo + ".avi"));
            //Eliminar temporales
            if (temp.exists()) {
                if (temp.listFiles().length == 0) {
                    temp.delete();
                } else {
                    File[] archivos = temp.listFiles();
                    for (File archivo : archivos) {
                        archivo.delete();
                    }
                    temp.delete();
                }
            }

        } catch (FrameRecorder.Exception | FrameGrabber.Exception ex) {
            log.error(ex);
        }
    }

    public void cerrarConexion() {
        log.info("\nCerrando conexion");
        try {
            entrada.close();
            salida.close();
            conexion.close();
        } catch (IOException excepcionES) {
            log.error(excepcionES);
        }
    }

    public void escuchar() {
        try {
            serverSocket = new ServerSocket(port);
            log.info("Esperando una conexion\n");
            conexion = serverSocket.accept();
            if (conexion != null) {
                salida = new ObjectOutputStream(conexion.getOutputStream());
                entrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                recibirInstrucciones();
            }
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    public void enviarObj(Object o) {
        log.info("Enviando respuesta a cliente: " + o);
        try {
            salida.writeObject(o);
            salida.flush();
        } catch (Exception e) {
            log.error(e);
        }
    }

    public void recibirInstrucciones() {
        log.info("Escuchando...");
        while (running) {
            try {

                if (entrada.ready()) {
                    String cmd = entrada.readLine();
                    log.info("Recibiendo instruccion de cliente: " + cmd);
                    if ("DISPOSITIVOS".equalsIgnoreCase(cmd)) {
                        obtenerDispositivos();
                    }
                    if (cmd.contains("FRAMES")) {
                        iniciarGrabacion(cmd);
                    }
                    if ("CERRAR".equalsIgnoreCase(cmd)) {
                        cerrarConexion();
                        running = false;
                    }
                    if ("DETENER".equalsIgnoreCase(cmd)) {
                        detenerGrabacion();
                    }
                }
            } catch (IOException ex) {
                log.error(ex);
            }
        }
    }
}
