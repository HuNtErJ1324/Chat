/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

/**
 *
 * @author peanu
 */
//
//
public final class Server implements Serializable { //serialize everything except client

    //users that use server
    ArrayList<User> users;
    //all the chats in the server
    transient static ArrayList<Chat> chats;
    int port = 8000;
    transient static ServerSocket server;
    transient Socket socket;
    transient Thread t;
    transient Menu m;
    transient DataOutputStream out;

    public static void main(String[] args) {
        Server serve = new Server();
//        Server serve = Server.load();
//        serve.start();
    }

    Server() {
        try {
            users = new ArrayList<>();
            server = new ServerSocket(port);
            while (true) {
                System.out.println("Server listening on port " + port);
                socket = server.accept();
                System.out.println("User accepted");
                out = new DataOutputStream(socket.getOutputStream());
                popChats();
                m = new Menu(socket, this);
                t = new Thread(m);
                t.start();
            }
            //server.close();
        } catch (IOException e) {
            System.out.println("server error");
        }
    }

    //populate chats
    public void popChats() {
        //for loop scans through chats
        chats = new ArrayList<>();
        File chatDirectory = new File("src/chats");
        File[] chatFileNames = chatDirectory.listFiles();
        for (File chatFileName : chatFileNames) {
            chats.add(Chat.load(chatFileName.getName()));
        }
    }

    //to do: look at server chats arraylist when second user connects 
    //first user joined and left chat
    public Chat getChat(String name) {
        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).getName().equals(name)) {
                return chats.get(i);
            }
        }
        return null;
    }

    public User getUser(String username) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                return users.get(i);
            }
        }
        return null;
    }

    //serialize ArrayList<Chat> chats
    //credit to: geeksforgeeks.org/serialization-in-java/
    public void save(Server server) {
        try {
            //Saving of object in a file 
            FileOutputStream file = new FileOutputStream("src/database/Server.ser");
            ObjectOutputStream out = new ObjectOutputStream(file);
            // Method for serialization of object 
            out.writeObject(server);
            out.close();
            file.close();
            System.out.println("Server has been saved");
        } catch (IOException ex) {
            System.out.println("Server save error");
        }
    }

    public static Server load() {
        Server server = null;
        try {
            // Reading the object from a file 
            FileInputStream file = new FileInputStream("src/database/Server.ser");
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

    public void start() {
        try {
            server = new ServerSocket(port);
            while (true) {
                System.out.println("Server listening on port " + port);
                socket = server.accept();
                System.out.println("User accepted");
                out = new DataOutputStream(socket.getOutputStream());
                popChats();
                m = new Menu(socket, this);
                t = new Thread(m);
                t.start();
            }
            //server.close();
        } catch (IOException e) {
            System.out.println("server error");
        }
    }

    private class Menu implements Runnable {

        Server server;
        DataInputStream in;
        Socket socket;
        DataOutputStream out;

        Menu(Socket socket, Server server) {
            this.socket = socket;
            this.server = server;
        }

        @Override
        public void run() {
            User user = null;
            try {
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());
                user = login();
                save(server);
                int option;
                do {
                    option = in.readInt();
                    switch (option) {
                        case 1:
                            //list of chats
                            out.writeUTF(listChats(user));
                            out.flush();
                            break;
                        case 2:
                            //enter a chat
                            enterChat(user);
                            break;
                        case 3:
                            //create
                            create(user);
                            break;
                        case 4:
                            //logout
                            logout(user);
                            break;
                    }
                } while (option != 4);
            } catch (SocketException ex) {
                try {
                    System.out.println(user.getUsername() + " has Left");
                } catch (NullPointerException exx) {
                    System.out.println("User has left");
                }
            } catch (IOException e) {
                System.out.println("Run error");
            }
        }

        public User login() {
            try {
                out.writeUTF("Are you a \n1)Returning user\n2)New user\n> ");
                out.flush();
                int d = in.readInt();
                //returning user
                if (d == 1) {
                    String username = in.readUTF();
                    System.out.println("Username accepted " + username);
                    String password = in.readUTF();
                    System.out.println("Password accepted");
                    if (checkUser(username, password)) {
                        out.writeUTF("Welcome " + username + "!");
                        out.flush();
                        getUser(username).setSocket(socket);
                        return getUser(username);
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

                    user = new User(username, password, socket);
                    users.add(user);
                    return user;
                } else {
                    login();
                }
            } catch (IOException e) {
                System.out.println("login errror");
            }
            return null;
        }

        public String listChats(User user) {
            try {
                StringBuilder s = new StringBuilder();
                ArrayList<String> uChats = user.getChats();
                if (uChats.isEmpty()) {
                    return "No chats\n";
                } else {
                    for (int i = 0; i < uChats.size(); i++) {
                        s.append(i + 1).append(": ").append(uChats.get(i)).append("\n");
                    }
                    return s.toString();
                }
            } catch (NullPointerException e) {
                System.out.println("Server listChats error");
            }
            return null;
        }

        public void enterChat(User user) {
            try {
                String name;
                do {
                    name = in.readUTF();
                    if (getChat(name) == null) {
                        out.writeUTF("No such chat");
                        out.flush();
                        return;
                    } else {
                        getChat(name).addUser(user);
                        user.addChat(getChat(name));
                        System.out.println("User has entered Chat");
                        out.writeUTF(getChat(name).toString());
                        out.flush();
                        break;
                    }
                } while (true);
                //start read thread
                Read r = new Read(socket, getChat(name), user);
                Thread t = new Thread(r);
                t.start();
                t.join();
            } catch (IOException e) {
                System.out.println("Server enterChat error");
            } catch (InterruptedException ex) {
                System.out.println("Server enterChat error 2");
            }
        }

        public void create(User user) {
            try {
                String name = in.readUTF();
                Chat chat = new Chat(name);
                chat.save();
                chats.add(chat);
                user.addChat(chat);
            } catch (IOException e) {
                System.out.println("Server create error");
            }
        }

        public void logout(User user) {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("logout error");
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

    //Read class does the reading
    private static class Read implements Runnable {

        DataInputStream in;
        Socket socket;
        Chat chat;
        User user;
        String text;

        Read(Socket socket, Chat chat, User user) {
            this.socket = socket;
            this.chat = chat;
            this.user = user;
            text = "";
        }

        @Override
        public void run() {
            try {
                in = new DataInputStream(socket.getInputStream());
                do {
                    text = in.readUTF();
                    chat.save();
                    if (text.equals("/back")) {
                        chat.removeUser(user);
                        chat.save();
                        break;
                    }
                    Message message = new Message(user, text);
                    chat.addMessage(message);
                } while (true);
            } catch (SocketException ex) {
                chat.removeUser(user);
                chat.save();
                System.out.println("Server socket error");
            } catch (IOException e) {
                System.out.println("Read run error");
            }
        }
    }
}
