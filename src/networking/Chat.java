/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
/**
 *
 * @author peanu
 */
public class Chat implements Serializable{
    Scanner input = new Scanner(System.in);
    String name;
    //all users connected to chat
    ArrayList<User> users;
    StringBuilder s = new StringBuilder();
    String text = "";
    PrintWriter pw;
    FileWriter fw;
    
    Chat(String name) {
        this.name = name;
    }
    
    //add user to chat
    public User addUser(String name, Socket socket) {
        User user = new User(name, socket);
        users.add(user);
        return user;
    }
    
    //receive texts from user
    public void receive(Socket socket, DataInputStream in) {
        try { 
            text = in.readUTF();
            s.append(text);
        } catch(IOException e) {
            System.out.println("receive error");
        }
    }
    
    //output most recent text from user
    public String output() {
        return text;
    }
    
    //print saved chat from file
    public void start() {
        try {
            Scanner file = new Scanner(new File("src/networking/Chat.txt"));
            while(file.hasNextLine()) {
                s.append(file.nextLine()).append("\n");
            }
        } catch(FileNotFoundException e) {
            System.out.println("start error");
        }
    }
    
    //credit to: geeksforgeeks.org/serialization-in-java/

    public void save() {
        try {
            //Saving of object in a file 
            FileOutputStream file = new FileOutputStream(name + ".ser");
            ObjectOutputStream out = new ObjectOutputStream(file);
            // Method for serialization of object 
            out.writeObject(this);
            out.close();
            file.close();
            System.out.println("Chat has been saved");
        } catch (IOException ex) {
            System.out.println("IOException is caught");
        }
    }
    
    public static Chat load(String name) {
        Chat chat = null;
        try {
            // Reading the object from a file 
            FileInputStream file = new FileInputStream(name + ".ser");
            ObjectInputStream in = new ObjectInputStream(file);
            // Method for deserialization of object 
            chat = (Chat) in.readObject();
            in.close();
            file.close();
            System.out.println("Chat has been loaded");
        } catch (IOException ex) {
            System.out.println("Load IOException");
        } catch (ClassNotFoundException ex) {
            System.out.println("Load ClassNotFoundException");
        }
        return chat;
    }
    
    public String getName() {
        return name;
    }
}
