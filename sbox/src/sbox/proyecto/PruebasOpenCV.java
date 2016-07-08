/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.proyecto;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Timer;

/**
 *
 * @author aandmaldonado
 */
public class PruebasOpenCV {

    private static int hora = 0, min = 0, seg = 0, ds = 0;//unidades de medida
    private static Thread hilo = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        hilo = new Thread() {//declaramos el hilo

            @Override
            public void run() {
                try {
                    while (true) {//ciclo infinito
                        if (ds == 99) {//si los decisegundos son iguales a 99
                            ds = 0;//decisegundo vuelve a empezar en cero
                            seg++;//y aumenta un segundo
                        }
                        if (seg == 59) {//si los segundos son iguales a 59
                            seg = 0;//segundo vuelve a empezar en cero
                            min++;//y aumenta un minuto
                        }
                        if (min == 59) {//si los minutos son iguales a 59
                            min = 0;//minuto vuelve a empezar en cero
                            hora++;//y aumenta una hora
                        }
                        ds++;//aumentan las decimas de segundo

                        System.out.println(hora + ":" + min + ":" + seg + ":" + ds);//se muestra en el jlabel

                        hilo.sleep(10);//que duerma una decima de segundo
                    }
                } catch (java.lang.InterruptedException ie) {
                    System.out.println(ie.getMessage());
                }
            }
        };
        hilo.start();
        
    }
}
