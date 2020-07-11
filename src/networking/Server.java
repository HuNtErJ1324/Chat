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
import java.util.Scanner;

/**
 *
 * @author peanu
 */
public class Server {

    public static void main(String[] args) {
        int port = 8000;
        DataInputStream in = null;
        DataOutputStream out;
        ServerSocket server;
        Socket socket;
        Scanner input = new Scanner(System.in);
        String text = "";
        Thread t1;
        Read r1;

        try {
            server = new ServerSocket(port);
            while (true) {
                System.out.println("Server listening on port " + port);
                socket = server.accept();
                System.out.println("Client accepted");
                out = new DataOutputStream(socket.getOutputStream());
                r1 = new Read(socket);
                t1 = new Thread(r1);
                t1.start();
            }
            //server.close();
        } catch (IOException e) {
            System.out.println("oopsies");
        }

    }

    private static class Read implements Runnable {

        DataInputStream in;
        Socket socket;

        Read(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new DataInputStream(socket.getInputStream());
                System.out.println("bruh");
                String input;
                do {
                    input = in.readUTF();
                    System.out.println(input);
                } while (!input.equalsIgnoreCase("end"));
            } catch (IOException e) {
                System.out.println("oopsies");
            }
        }
    }
}
