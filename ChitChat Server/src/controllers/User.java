package controllers;

import message.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class User {
    public static String username;
    public static ObjectOutputStream output;

    public String getUsername(){
        return username;
    }

    public User(String usrnm, ObjectOutputStream out){
        this.username= usrnm;
        this.output = out;
    }
    public synchronized static void send(Message msg) throws IOException {
        output.writeObject(msg);
        output.flush();
    }
}