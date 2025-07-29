/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.perspectiva;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import sbox.proyecto.ProyectoMain;

/**
 *
 * @author Álvaro Andrés Maldonado Pinto
 */
@Slf4j
public class PerspectivaCliente extends Thread implements Serializable {

    private ObjectInputStream entrada;
    private PrintWriter salida;
    private boolean running = true;
    private String instruccion = "";
    private Socket cliente;
    private final int port = 3000;
    private List<String> dispositivos = new ArrayList<>();
    public static boolean listaDispositivos = false;
    public static boolean listaFrames = false;
    public static boolean video = false;
    private String path;
    public static boolean con = false;
    private String nombreVideo = "";
    private String nombreProyecto = "";
    private String experimentos = "";

    public String getExperimentos() {
        return experimentos;
    }

    public void setExperimentos(String experimentos) {
        this.experimentos = experimentos;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    public String getNombreVideo() {
        return nombreVideo;
    }

    public void setNombreVideo(String nombreVideo) {
        video = true;
        this.nombreVideo = nombreVideo;
    }

    public List<String> getDispositivos() {
        return dispositivos;
    }

    public void setDispositivos(List<String> dispositivos) {
        listaDispositivos = true;
        this.dispositivos = dispositivos;
    }

    public PerspectivaCliente(String servidor) {
        listaDispositivos = false;
        listaFrames = false;
        video = false;
        try {
            cliente = new Socket(InetAddress.getByName(servidor), port);

            if (cliente != null) {
                salida = new PrintWriter(cliente.getOutputStream());
                entrada = new ObjectInputStream(cliente.getInputStream());
                con = true;
            }
        } catch (IOException ex) {
            log.error("Error al conectar con servidor", ex);
        }
    }

    public PerspectivaCliente() {

    }

    public void enviarInstruccion(String cmd) {
        try {
            if (!"DISPOSITIVOS".equalsIgnoreCase(cmd)) {
                log.info("Enviando instruccion a servidor: {}", cmd);
            }
            instruccion = cmd;
            salida.println(cmd);
            salida.flush();
        } catch (Exception e) {
            log.error("Error al enviar instrucción", e);
        }
    }

    public Object recibirObj() {
        Object o = null;
        if (entrada != null) {
            try {
                o = entrada.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                log.error("Error al recibir objeto", ex);
            }
        }
        return o;
    }

    public void recibirArchivo() {
        try {
            if (entrada != null) {
                Object obj = entrada.readObject();
                if (obj instanceof String) {
                    path = (String) obj;
                    log.info("Archivo recibido: {}", path);
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            log.error("Error al recibir archivo", ex);
        }
    }

    @Override
    public synchronized void run() {
        while (running) {
            try {
                if (entrada != null) {
                    Object obj = recibirObj();
                    if (obj != null) {
                        if (obj instanceof String) {
                            String mensaje = (String) obj;
                            log.info("Mensaje recibido: {}", mensaje);
                            
                            if ("DISPOSITIVOS".equalsIgnoreCase(mensaje)) {
                                listaDispositivos = true;
                                log.info("Lista de dispositivos solicitada");
                            } else if ("FRAMES".equalsIgnoreCase(mensaje)) {
                                listaFrames = true;
                                log.info("Lista de frames solicitada");
                            } else if ("VIDEO".equalsIgnoreCase(mensaje)) {
                                video = true;
                                log.info("Video solicitado");
                            }
                        } else if (obj instanceof List) {
                            dispositivos = (List<String>) obj;
                            log.info("Dispositivos recibidos: {}", dispositivos.size());
                        }
                    }
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.error("Error en el hilo principal", e);
                running = false;
            }
        }
    }

    public void stopClient() {
        running = false;
        try {
            if (cliente != null) {
                cliente.close();
            }
        } catch (IOException e) {
            log.error("Error al cerrar cliente", e);
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
