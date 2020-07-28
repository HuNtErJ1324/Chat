/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import static networking.Server.users;

/**
 *
 * @author peanu
 */
public class Client {
    final static String menu = "Choose option\n    1)Chats\n    2)Join\n    3)Create\n    4)Logout\n    5)Help";

    public static void main(String[] args) {
        int port = 8000;
        String host = "100.20.240.236";
        DataInputStream in;
        DataOutputStream out;
        Socket socket;
        Scanner input = new Scanner(System.in);
        String text = "";

        try {
            socket = new Socket(host, port);
            System.out.println("Client connected to port " + port);
            out = new DataOutputStream(socket.getOutputStream());

            do {
                text = input.nextLine();
                out.writeUTF(text);
                out.flush();
            } while (!text.equalsIgnoreCase("end"));
            socket.close();
        } catch (IOException e) {
            System.out.println("oopsies");
        }
    }

    public static class Read implements Runnable {

        Socket socket;
        DataInputStream in;
        DataOutputStream out;
        Scanner input = new Scanner(System.in);

        Read(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                login();
                System.out.println(menu);
                String option = input.next();
                out.writeUTF(option);
            } catch (IOException e) {
                System.out.println("client run error");
            }
        }

        public void login() {
            try {
                in.readUTF();
                int num = input.nextInt();
                out.writeInt(num);
                out.flush();
                //returning user
                if (num == 1) {
                    System.out.print("Username: ");
                    String username = input.next();
                    System.out.print("Password: ");
                    String password = input.next();
                    String check = in.readUTF();
                    System.out.println(check);
                    if (check.startsWith("Username")) {
                        login();
                    }
                } //new user
                else if (num == 2) {
                    User user;
                    System.out.print("Welcome! \nType your new username: ");
                    String username = input.next();
                    out.writeUTF(username);
                    out.flush();
                    System.out.print("Type your password: ");
                    String password = input.next();
                    out.writeUTF(password);
                    out.flush();
                }
            } catch (IOException e) {
                System.out.println("Client side login error");
            }
        }
    }
}
