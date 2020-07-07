/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author peanu
 */
public class Client {

    public static void main(String[] args) {
        int port = 8000;
        String host = "localhost";
        DataInputStream in;
        DataOutputStream out;
        Socket socket = null;
        Scanner input = new Scanner(System.in);
        String text = "";

        try {
            socket = new Socket(host, port);
            System.out.println("Client connected to port " + port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            while (!text.equals("end")) {
                text = input.nextLine();
                out.writeUTF(text);
                out.flush();
                System.out.println(in.readUTF());
            }
            socket.close();

        } catch (IOException e) {
            System.out.println("oopsies");
        }
    }
}
