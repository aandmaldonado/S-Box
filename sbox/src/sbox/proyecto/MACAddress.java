/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.proyecto;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author aandmaldonado
 */
public class MACAddress {

    public static void main(String[] args) throws UnknownHostException, SocketException {
//Obtenemos la interface en la cual estamos conectados.
        NetworkInterface a = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
//Obtenemos su MAC Address, pero nos devuelve un array de bytes
//Por lo que hay que convertirlos a Hexadecimal
        byte[] b = a.getHardwareAddress();
        for (int i = 0; i < b.length; i++) {
//Tratamos los valores que devuelven < 0 normalmente son el "3 y 5 par" 
            if (b[i] < 0) {
//Convertimos el byte a Hexadecimal con la clase Integer
                String tmp = Integer.toHexString(b[i]);
                /*Los numeros que son menores a cero al momento de convertirlo a string nos devuelven
una cadena de este tipo ffffffAA por lo que unicamente tomamos los ultimos 2 caracteres
que son lo que buscamos. y obtenemos esos ultimos caracteres con substring*/
                System.out.print(tmp.substring(tmp.length() - 2).toUpperCase());
                continue;
            }
//Aqui imprimimos directamente los bytes que son mayores a cero.
            System.out.print(Integer.toHexString(b[i]));


        }

    }
}
