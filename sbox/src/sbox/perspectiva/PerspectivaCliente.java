/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.perspectiva;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Álvaro Andrés Maldonado Pinto
 */
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
    private String path;

    public List<String> getDispositivos() {
        return dispositivos;
    }

    public void setDispositivos(List<String> dispositivos) {
        System.out.println("setDispositivos: " + dispositivos);
        listaDispositivos = true;
        this.dispositivos = dispositivos;
    }

    public PerspectivaCliente(String servidor) {
        listaDispositivos = false;
        listaFrames = false;
        try {
            cliente = new Socket(InetAddress.getByName(servidor), port);

            if (cliente != null) {
                salida = new PrintWriter(cliente.getOutputStream());
                entrada = new ObjectInputStream(cliente.getInputStream());
            }
        } catch (IOException ex) {
            Logger.getLogger(PerspectivaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PerspectivaCliente() {

    }

    public void enviarInstruccion(String cmd) {
        try {
            System.out.println("Enviando instruccion a servidor: " + cmd);
            instruccion = cmd;
            salida.println(cmd);
            salida.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object recibirObj() {
        Object o = null;
        if (entrada != null) {
            try {
                o = entrada.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        return o;
    }

    public void recibirArchivo() {
        byte[] content = (byte[]) recibirObj();
        try {
            Files.write(new File(path + "canalExterno.avi").toPath(), content);
        } catch (IOException ex) {
            Logger.getLogger(PerspectivaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void run() {
        while (running) {
            if (!"CERRAR".equalsIgnoreCase(instruccion)) {
                Object o = recibirObj();

                if (o != null) {
                    if (o instanceof List) {
                        System.out.println("Recibido: " + o);
                        if ("DISPOSITIVOS".equalsIgnoreCase(instruccion)) {
                            setDispositivos((List<String>) o);
                        }
                    }
                    if (o instanceof String) {
                        System.out.println("Recibido: " + o);
                        if (instruccion.contains("FRAMES")) {
                            System.out.println("grabando desde perspectiva externa...");
                        }
                        if (o.toString().equalsIgnoreCase("ARCHIVO")) {
                            recibirArchivo();
                        }
                        if (instruccion.contains("DETENER")) {
                            System.out.println("Deteniendo grabacion desde perspectiva externa...");
                        }

                    }
                }
            } else {
                running = false;
            }
        }
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
}
