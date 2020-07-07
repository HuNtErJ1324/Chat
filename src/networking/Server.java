/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 *
 * @author peanu
 */
public class Server {

    public static void main(String[] args) {
        int port = 8000;
        DataInputStream in;
        BufferedReader d;
        DataOutputStream out;
        ServerSocket server;
        Socket socket;
        Scanner input = new Scanner(System.in);
        String text = "";

        try {
            server = new ServerSocket(port);
            System.out.println("Server listening on port " + port);
            socket = server.accept();
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            while (!text.equals("end")) {
                System.out.println(in.readUTF());
                text = input.nextLine();
                out.writeUTF(text);
                out.flush();
            }
            server.close();
        } catch (IOException e) {
            System.out.println("oopsies");
        }

    }
}
