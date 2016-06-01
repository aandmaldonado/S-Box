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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.URIReferenceException;
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

    class CameraSwingWorker extends SwingWorker<String, Object> {

        public CameraSwingWorker() {

        }

        @Override
        public String doInBackground() throws Exception {
            try {
                _video = new Video(temp.getAbsolutePath());
                _video.startCamera(Integer.parseInt(device));
            } catch (FrameGrabber.Exception | FrameRecorder.Exception | IOException | URISyntaxException | URIReferenceException e) {
                System.out.println(e.getMessage());
//              logger.error(e.getMessage());
            }
            return null;
        }

        @Override
        protected void done() {
            try {
//                        toggleButton(true, false, false);
            } catch (Exception ignore) {
//                        logger.error(ignore.getMessage());
            }
        }
    }

    public PerspectivaServidor() {
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
            dispositivos.add("Cámara por defecto");
        }

        if (cap1.isOpened()) {
            cap1.release();
            dispositivos.add("Cámara");
        }

        if (dispositivos.isEmpty()) {
            cap.release();
            cap1.release();
            dispositivos.add("No hay dispositivos");
        }

        enviarObj(dispositivos);
    }

    public void iniciarGrabacion(String cmd) {
        temp = new File("C:\\temp\\");
        if (!temp.exists()) {
            temp.mkdir();
        }
        String[] a = cmd.split(";");
        device = a[1];
        CameraSwingWorker cameraSwingWorker = new CameraSwingWorker();
        cameraSwingWorker.execute();
    }

    public void enviarArchivo(File f) {
        try {
            enviarObj("ARCHIVO");
            byte[] readAllBytes = Files.readAllBytes(f.toPath());
            enviarObj(readAllBytes);
            enviarObj("FIN ARCHIVO");
        } catch (IOException ex) {
            Logger.getLogger(PerspectivaServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void detenerGrabacion() {
        try {
            _video.stopCamera();

            enviarArchivo(new File("C:\\temp\\canalExterno.avi"));
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
            Logger.getLogger(PerspectivaServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cerrarConexion() {
        System.out.println("\nCerrando conexion");
        try {
            entrada.close();
            salida.close();
            conexion.close();
        } catch (IOException excepcionES) {
            excepcionES.printStackTrace();
        }
    }

    public void escuchar() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Esperando una conexion\n");
            conexion = serverSocket.accept();
            if (conexion != null) {
                salida = new ObjectOutputStream(conexion.getOutputStream());
                entrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                recibirInstrucciones();
            }
        } catch (IOException ex) {
            Logger.getLogger(PerspectivaServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void enviarObj(Object o) {
        System.out.println("enviarObj a cliente");
        try {
            salida.writeObject(o);
            salida.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recibirInstrucciones() {
        System.out.println("recibirInstrucciones()");
        System.out.println("escuchando");
        while (running) {
            try {

                if (entrada.ready()) {
                    String cmd = entrada.readLine();
                    System.out.println("cmd: " + cmd);
                    if ("DISPOSITIVOS".equalsIgnoreCase(cmd)) {
                        obtenerDispositivos();
                    }
                    if (cmd.contains("FRAMES")) {
                        iniciarGrabacion(cmd);
                        enviarObj("grabando...");
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
                Logger.getLogger(PerspectivaServidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
