/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author peanu
 */
public class Message implements Serializable {

    User user;
    String message;
    Date time;

    Message() {
        user = null;
        message = "";
        time = new Date();
    }

    Message(User user, String message) {
        this.user = user;
        this.message = message;
        time = new Date();
    }

    @Override
    public String toString() {
        return getDate() + " " + user.getUsername() + " " + message;
    }

    public String getDate() {
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
        return ft.format(time);
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
