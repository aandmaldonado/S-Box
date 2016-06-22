/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.proyecto;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author aandmaldonado
 */
public class MACAddress {

    public static void main(String[] args) throws UnknownHostException, SocketException {

        String proyecto = "proyecto";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy'_'HH.mm.ss");
        dateFormat.format(new Date());
        String face=proyecto+"_faceRecorder_"+dateFormat.format(new Date());
        String screen=proyecto+"_activityRender_"+dateFormat.format(new Date());
        String ext=proyecto+"_canalExterno_"+dateFormat.format(new Date());
        
        System.out.println(face);
        System.out.println(screen);
        System.out.println(ext);
    }
}
