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
            popChats();
            while (true) {
                System.out.println("Server listening on port " + port);
                socket = server.accept();
                System.out.println("User accepted");
                out = new DataOutputStream(socket.getOutputStream());
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
            popChats();
            while (true) {
                System.out.println("Server listening on port " + port);
                socket = server.accept();
                System.out.println("User accepted");
                out = new DataOutputStream(socket.getOutputStream());
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
                ArrayList<String> chats = user.getChats();
                for (int i = 0; i < chats.size(); i++) {
                    getChat(chats.get(i)).addUser(user);
                }
                out.writeUTF(initialChatMessages(user));
                out.flush();
                int option;
                do {
                    option = in.readInt();
                    switch (option) {
                        case 1:
                            //join a chat
                            joinChat(user, in.readUTF());
                            break;
                        case 2:
                            //create a chat
                            String name = create(user);
                            joinChat(user, name);
                            break;
                        case 3:
                            //read message 
                            read(user);
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

        public String initialChatMessages(User user) {
            try {
                StringBuilder s = new StringBuilder();
                ArrayList<String> uChats = user.getChats();
                System.out.println("User Chats" + uChats);
                if (uChats.isEmpty()) {
                    return "No chats";
                } else {
                    for (int i = 0; i < uChats.size(); i++) {
                        s.append("#").append(uChats.get(i)).append("\n");
                        ArrayList<String> lm = getChat(uChats.get(i)).getLatest();
                        for (int j = 0; j < lm.size(); j++) {
                            s.append(lm.get(j)).append("\n");
                        }
                    }
                    return s.toString();
                }
            } catch (NullPointerException e) {
                System.out.println("Server initialChatMessages error");
            }
            return null;
        }

        public User login() {
            try {
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
                        out.writeUTF("incorrect password or username");
                        out.flush();
                        return login();
                    }
                } //new user
                else if (d == 2) {
                    User user = null;
                    String username = in.readUTF();
                    String password = in.readUTF();
                    if (checkUsername(username)) {
                        user = new User(username, password, socket);
                        users.add(user);
                        out.writeUTF("Welcome");
                        out.flush();
                    } else {
                        out.writeUTF("Username is taken");
                        out.flush();
                        return login();
                    }
                    return user;
                }
            } catch (IOException e) {
                System.out.println("login errror");
            }
            return null;
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

        public boolean checkUsername(String username) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUsername().equals(username)) {
                    return false;
                }
            }
            return true;
        }

        public void joinChat(User user, String name) {
            try {
                if (getChat(name) == null) {
                    out.writeUTF("No such chat");
                    out.flush();
                    return;
                } else {
                    System.out.println("User has entered Chat");
                    getChat(name).addUser(user);
                    user.addChat(getChat(name));
                }
                out.writeInt(2);
                out.flush();
                StringBuilder s = new StringBuilder();
                ArrayList<String> lm = getChat(name).getLatest();
                for (int j = 0; j < lm.size(); j++) {
                    s.append(lm.get(j)).append("\n");
                }
                out.writeUTF(s.toString());
                out.flush();
            } catch (IOException e) {
                System.out.println("Server enterChat error");
            }
        }

        public String create(User user) {
            String name = "";
            try {
                name = in.readUTF();
                Chat chat = new Chat(name);
                chat.save();
                chat.addUser(user);
                chats.add(chat);
                user.addChat(chat);
            } catch (IOException e) {
                System.out.println("Server create error");
            }
            return name;
        }

        public void logout(User user) {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("logout error");
            }
        }

        public void read(User user) {
            try {
                String[] chatSplit = in.readUTF().split(" ", 2);
                Chat chat = getChat(chatSplit[0]);
                String text = chatSplit[1];
                Message message = new Message(user, text);
                //TODO add to people who "have" the chat in side bar
                chat.addMessage(message);
                chat.save();
            } catch (IOException e) {
                System.out.println("read server error");
            }
        }
    }
}
