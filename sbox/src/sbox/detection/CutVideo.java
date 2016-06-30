/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.detection;

import java.io.File;
import sbox.proyecto.Reproductor;

/**
 *
 * @author Álvaro Andrés Maldonado Pinto
 */
public class CutVideo extends Thread implements Runnable{

    private File videoMaster, videoSec;
    private String ini, fin;

    public CutVideo(File videoMaster, File videoSec, String ini, String fin) {
        this.videoMaster = videoMaster;
        this.videoSec = videoSec;
        this.ini = ini;
        this.fin = fin;
    }

    @Override
    public void run() {
        Reproductor r = new Reproductor();
        r.cutVideo(videoMaster, videoSec, ini, fin);
    }

}
