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
public class User implements Serializable{ 
    String username;
    String password;
    Socket socket;
    //chats that the user has connected to
    ArrayList<Chat> chats;
    
    User() {
        username = "";
        password = "";
        socket = null;
        chats = null;
    }
    
    User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    User(String username, Socket socket) {
        this.username = username;
        this.socket = socket;
    }
    
    //to user
    public void sendUser(Socket socket) {
        
    }
    //socket
    
    public Socket getSocket() {
        return socket;
    }
    
    public String getUsername() {
        return username;
    }
    
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
    //set socket
    
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void addChat(Chat chat) {
        chats.add(chat);
    }
}
