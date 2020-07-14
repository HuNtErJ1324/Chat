/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 *
 * @author peanu
 */
public class InetStuff {

    public static void main(String[] args) {
        try {
            InetAddress address = InetAddress.getByName("ec2-35-166-23-110.us-west-2.compute.amazonaws.com");
            System.out.println(Arrays.toString(address.getAddress()));
        } catch (UnknownHostException e) {
            System.out.println("oopsies");
        }
    }

}
