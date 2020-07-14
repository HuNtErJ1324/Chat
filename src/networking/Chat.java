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
public class Chat {
    Scanner input = new Scanner(System.in);
    String name;
    ArrayList<User> users;
    
    Chat(String name) {
        this.name = name;
        
    }
    
    //to users
    //add user
    public void addUser(String name, Socket socket) {
        User 
    }
    //
    
    public void save() {
        //save chat into a file
    }
    
    public void start() {
        //get file and print it
    }
    
    
}
