/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import networking.*;

/**
 *
 * @author peanu
 */
public class Server implements Serializable { //serialize everything except client

    //users that use server
    static ArrayList<User> users;
    //all the chats in the server
    transient static ArrayList<Chat> chats;
    //menu string

    public static void main(String[] args) {
        int port = 8000;
        ServerSocket server;
        Socket socket;
        Thread t1;
        Read r1;
        DataOutputStream out;
        try {
            server = new ServerSocket(port);
            while (true) {
                System.out.println("Server listening on port " + port);
                socket = server.accept();
                System.out.println("User accepted");
                out = new DataOutputStream(socket.getOutputStream());
                r1 = new Read(socket);
                t1 = new Thread(r1);
                t1.start();
            }
            //server.close();
        } catch (IOException e) {
            System.out.println("server error");
        }
    }

    //populate chats
    public void popChats() {
        //for loop scans through chats 
        File chatDirectory = new File("src/chats");
        File[] chatFileNames = chatDirectory.listFiles();
        for (int i = 0; i < chatFileNames.length; i++) {
            chats.add(Chat.load(chatFileNames[i].getName()));
        }
    }

    //populate users
    public void popUsers() {
        try {
            Scanner file = new Scanner(new File("src/database/users.txt"));
            while (file.hasNextLine()) {
                //store next line into string
                User user;
                String line = file.nextLine();
                String[] properties = line.split(" ");
                user = new User(properties[0], properties[1]);
                //add chats to user
                for (int i = 2; i < properties.length; i++) {
                    user.addChat(getChat(properties[i]));
                }
                users.add(user);
            }

        } catch (FileNotFoundException e) {
            System.out.println("checkUser error");
        }
    }

    //get chat using chat name
    public static Chat getChat(String name) {
        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).getName().equals(name)) {
                return chats.get(i);
            }
        }
        //if it gets here code has turned evil
        return null;
    }

    public static User getUser(String username) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                return users.get(i);
            }
        }
        return null;
    }

    //serialize everything
    //credit to: geeksforgeeks.org/serialization-in-java/
    public void save() {
        try {
            //Saving of object in a file 
            FileOutputStream file = new FileOutputStream("Server.ser");
            ObjectOutputStream out = new ObjectOutputStream(file);
            // Method for serialization of object 
            out.writeObject(this);
            out.close();
            file.close();
            System.out.println("Server has been saved");
        } catch (IOException ex) {
            System.out.println("IOException is caught");
        }
    }

    public static Server load() {
        Server server = null;
        try {
            // Reading the object from a file 
            FileInputStream file = new FileInputStream("Server.ser");
            ObjectInputStream in = new ObjectInputStream(file);
            // Method for deserialization of object 
            server = (Server) in.readObject();
            in.close();
            file.close();
            System.out.println("Server has been loaded");
        } catch (IOException ex) {
            System.out.println("Load IOException");
        } catch (ClassNotFoundException ex) {
            System.out.println("Load ClassNotFoundException");
        }
        return server;
    }

    private static class Read implements Runnable {

        Chat chat;
        DataInputStream in;
        Socket socket;
        DataOutputStream out;
        FileWriter fw;
        PrintWriter pw;

        Read(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());
                login();
                int option = in.readInt();
                //display menu
                switch (option) {
                    case 1:
                        //list of chats
                        break;
                    case 2:
                        //join
                        break;
                    case 3:
                        //create
                        break;
                    case 4:
                        //logout
                        break;
                    case 5:
                        //help
                        break;
                }
            } catch (IOException e) {
                System.out.println("Run error");
            }
        }

        public void listChats() {

        }

        //login 
        public void login() {
            try {
                out.writeUTF("Are you a \n1)Returning user\n2)New user");
                int d = in.readInt();
                //returning user
                if (d == 1) {
                    String username = in.readUTF();
                    String password = in.readUTF();
                    if (checkUser(username, password)) {
                        out.writeUTF("Welcome " + username + "!");
                        out.flush();
                        User user = getUser(username);
                    } else {
                        out.writeUTF("Username or password incorrect");
                        out.flush();
                        login();
                    }
                } //new user
                else if (d == 2) {
                    User user;
                    String username = in.readUTF();
                    String password = in.readUTF();
                    user = new User(username, password);
                    users.add(user);
                }

            } catch (IOException e) {
                System.out.println("login errror");
            }
        }

        //add user
        public void addUser(User user) {
            users.add(user);
        }

        //check username ArrayList
        public boolean checkUser(String username, String password) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUsername().equals(username)) {
                    return users.get(i).checkPassword(password);
                }
            }
            return false;
        }
    }
}
