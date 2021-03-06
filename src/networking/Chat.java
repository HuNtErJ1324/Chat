/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 *
 * @author peanu
 */
public class Chat implements Serializable {

    private final int LATEST_MESSAGES = 30;

    transient Scanner input = new Scanner(System.in);
    String name;
    //ArrayList of all users connected to chat
    transient ArrayList<User> users;
    ArrayList<Message> messages;

    Chat(String name) {
        this.name = name;
        users = new ArrayList<>();
        messages = new ArrayList<>();
        System.out.println("New " + name + " created");
        save();
    }

    //add user to chat
    public void addUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (user.equals(users.get(i))) {
                return;
            }
        }
        users.add(user);
    }

    //print saved chat from file
    @Deprecated
    public void start() {
        StringBuilder s = new StringBuilder();
        try {
            Scanner file = new Scanner(new File("src/networking/Chat.txt"));
            while (file.hasNextLine()) {
                s.append(file.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("start error");
        }
    }

    //credit to: geeksforgeeks.org/serialization-in-java/
    //save after every message
    public void save() {
        try {
            //Saving of object in a file               
            FileOutputStream file = new FileOutputStream(new File("src/chats/" + name + ".ser"));
            ObjectOutputStream out = new ObjectOutputStream(file);
            // Method for serialization of object 
            out.writeObject(this);
            out.close();
            file.close();
            System.out.println("Chat has been saved");
        } catch (IOException ex) {
            System.out.println("Chat save error");
            ex.printStackTrace();
        }
    }

    public static Chat load(String name) {
        Chat chat = null;
        try {
            // Reading the object from a file 
            FileInputStream file = new FileInputStream("src/chats/" + name);
            ObjectInputStream in = new ObjectInputStream(file);
            // Method for deserialization of object 
            chat = (Chat) in.readObject();
            in.close();
            file.close();
            chat.users = new ArrayList<>();
            System.out.println("Chat has been loaded");
        } catch (IOException ex) {
            System.out.println("Chat load IOException");
        } catch (ClassNotFoundException ex) {
            System.out.println("Chat load ClassNotFoundException");
        }
        return chat;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < messages.size(); i++) {
            s.append(messages.get(i).toString() + "\n");
        }
        return s.toString();
    }

    public void addMessage(Message message) {
        messages.add(message);
        for (int i = 0; i < users.size(); i++) {
            try {
                DataOutputStream out = new DataOutputStream(users.get(i).getSocket().getOutputStream());
                out.writeInt(3);
                out.flush();
                out.writeUTF(name + " " + message.toString());
                out.flush();
            } catch (IOException e) {
                System.out.println("addMessage error");
            } catch (NullPointerException ex) {
                users.remove(i--);
                ex.printStackTrace();
            }
        }
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void removeUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).equals(user)) {
                users.remove(i);
                break;
            }
        }
    }

    public ArrayList<String> getLatest() {
        List<Message> latest;
        if (messages.size() >= LATEST_MESSAGES) {
            latest = messages.subList(messages.size() - LATEST_MESSAGES, messages.size());
        } else {
            latest = messages;
        }
        ArrayList<String> latestMessages = new ArrayList<>();;
        for (int i = 0; i < latest.size(); i++) {
            latestMessages.add(latest.get(i).toString());
            System.out.println(latest.get(i).toString());
        }
        System.out.println(latestMessages);
        return latestMessages;
    }
}
