/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author peanu
 */
//new thread created when you enter chat to get chat messages
public class Client {

    final static String MENU = "Choose option\n    1)Chats\n    2)Join\n    3)Create\n    4)Logout\n    5)Help\n>";
    int port = 8000;
    //static String host = "100.20.240.236";
    static String host = "localhost";
    //100.20.240.236
    DataInputStream in;
    DataOutputStream out;
    Socket socket;
    Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        Client client = new Client();
        client.login();
        client.menu();
        //socket.close();
    }

    Client() {
        try {
            socket = new Socket(host, port);
            System.out.println("Client connected to port " + port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("oopsies");
        }
    }

    public void menu() {
        try {
            int option;
            do {
                System.out.print(MENU);
                //input foolproofing
                while (true) {
                    option = getInt();
                    if (option > 5 || option < 1) {
                        System.out.print("Incorrect input\n>");
                    } else {
                        break;
                    }
                }
                //clears buffer of the \n
                input.nextLine();
                out.writeInt(option);
                out.flush();
                switch (option) {
                    case 1:
                        System.out.print(in.readUTF());
                        break;
                    case 2:
                        enterChat();
                        break;
                    case 3:
                        create();
                        break;
                    case 4:
                        //logout
                        logout();
                        break;
                    case 5:
                        //help
                        System.out.println("/back: Goes back to menu");
                        break;
                }
            } while (option != 4);
        } catch (IOException e) {
            System.out.println("Client menu error");
        }
    }

    public void login() {
        try {
            System.out.print(in.readUTF());
            int num = getInt();
            out.writeInt(num);
            out.flush();
            //returning user
            if (num == 1) {
                System.out.print("Username\n>");
                String username = input.next();
                out.writeUTF(username);
                out.flush();
                System.out.print("Password\n>");
                String password = input.next();
                out.writeUTF(password);
                out.flush();
                String check = in.readUTF();
                System.out.println(check);
                if (check.startsWith("Username")) {
                    login();
                }
            } //new user
            else if (num == 2) {
                User user;
                System.out.print("Welcome! \nType your username\n>");
                String username = input.next();
                out.writeUTF(username);
                out.flush();
                System.out.print("Type your password\n>");
                String password = input.next();
                out.writeUTF(password);
                out.flush();
            } else {
                System.out.println("Incorrect input");
                login();
            }
        } catch (IOException e) {
            System.out.println("Client side login error");
        }
    }

    public void enterChat() {
        try {
            do {
                System.out.print("What chat do you want to enter\n>");
                out.writeUTF(input.nextLine());
                out.flush();
                String x = in.readUTF();
                if (x.startsWith("No")) {
                    System.out.println(x);
                    return;
                } else {
                    System.out.print(x);
                    break;
                }
            } while (true);
            //start read thread
            Read r = new Read(socket);
            String text = "";
            while (true) {
                text = input.nextLine();
                //try to add timestamp and username
                out.writeUTF(text);
                out.flush();
                if (text.equals("/back")) {
                    //risky death scary
                    r.stopThread();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Client enterChat error");
        }
    }

    public void create() {
        try {
            System.out.print("Name of new chat\n>");
            out.writeUTF(input.nextLine());
            out.flush();
        } catch (IOException e) {
            System.out.println("client create error");
        }
    }

    public void logout() {
        System.out.println("Bye!");
    }

    public int getInt() {
        try {
            return input.nextInt();
        } catch (InputMismatchException e) {
            System.out.print("Incorrect input\n>");
            input.next();
            return getInt();
        }
    }

    private static class Read implements Runnable {

        DataInputStream in;
        Socket socket;
        DataOutputStream out;
        boolean running = true;
        Thread t;

        Read(Socket socket) {
            this.socket = socket;
            t = new Thread(this);
            t.start();
        }

        @Override
        public void run() {
            try {
                in = new DataInputStream(socket.getInputStream());
                do {
                    if (in.available() > 0) {
                        System.out.println(in.readUTF());
                    }
                } while (running);
            } catch (IOException e) {
                System.out.println("Read run error");
            }
        }

        public void stopThread() {
            running = false;
        }
    }
}
