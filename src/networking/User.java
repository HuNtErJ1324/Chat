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
public class User implements Serializable {

    String username;
    String password;
    transient Socket socket;
    //chats that the user has at least once connected to
    ArrayList<String> chats = new ArrayList<>();

    User() {
        username = "";
        password = "";
        socket = null;
    }

    User(String username, String password, Socket socket) {
        this.username = username;
        this.password = password;
        this.socket = socket;
        System.out.println("Created new user");
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void addChat(Chat chat) {
        //get name XD LMAO
        
        for (int i = 0; i < chats.size(); i++) {
            if (chat.getName().equals(chats.get(i))) {
                return;
            }
        }
        chats.add(chat.getName());
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<String> getChats() {
        return chats;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setChats(ArrayList<String> chats) {
        this.chats = chats;
    }

}
