/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.net.Socket;

/**
 *
 * @author peanu
 */
public class User {
    String name;
    Socket socket;
    
    User(String name) {
        this.name = name;
    }
    
    //to user
    public void sendUser(Socket socket) {
        
    }
    //socket
    
    public Socket getSocket() {
        return socket;
    }
    
    public String getName() {
        return name;
    }
    
    public int getPass() {
        return pass;
    }
}
