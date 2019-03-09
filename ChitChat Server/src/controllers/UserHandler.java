package controllers;

import message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class UserHandler extends Thread{
    private static Socket socket;
    private static ObjectInputStream input;
    private static String username;
    private static ObjectOutputStream output;

    public static String getUsername() {
        return username;
    }

    public UserHandler(Socket sc){
        this.socket=sc;
    }


    @Override
    public void run() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());


            Message loginINFO = (Message)input.readObject();
            String info[]= loginINFO.getMsg().split("/");
            username = info[0];
            String  password=info[1],action=info[2];

            // LOGINOVANJE I REGISTRACIJA
            if(action.equals("LOGIN")){
                if(Server.isRegistered(username,password)){
                    if(alreadyLogged(username)){
                        Message error= new Message();
                        error.setMsg("User: "+username+" is already logged in.");
                        output.writeObject(error);
                        socket.close();
                        this.interrupt();
                        return;
                    }
                    Message conf = new Message();
                    conf.setMsg("CONFIRMED");
                    output.writeObject(conf);
                    output.flush();
                    User usr = new User(username,output);
                    Server.onlineUsers.add(usr);

                }
                else{
                    Message error= new Message();
                    error.setMsg("USER "+username+" IS NOT REGISTERED OR PASSWORD IS INCORRECT.");
                    output.writeObject(error);
                    socket.close();
                    this.interrupt();
                    return;
                }
            }
            else if(action.equals("REGISTER")){
                if(!Server.isRegistered(username)){
                    Server.insertIntoDatabase(username,password);
                    Message conf = new Message();
                    conf.setMsg("CONFIRMED");
                    output.writeObject(conf);
                    output.flush();
                    User usr = new User(username,output);
                    Server.onlineUsers.add(usr);
                }
                else{
                    Message error= new Message();
                    error.setMsg("USER: "+username+" ALREADY EXIST.");
                    output.writeObject(error);
                    socket.close();
                    this.interrupt();
                    return;
                }
            }
            output.flush();

            // POCETAK PRIMANAJ PORUKA ZA CETOVANJE
            while(socket.isConnected()){

                Message msg= (Message)input.readObject();
                if(msg.getMsg().equals("ADD/FRIEND")){
                    Message request = new Message();
                    request.setReciever(msg.getSender());
                    request.setSender(msg.getReciever());
                    if(Server.isRegistered(msg.getReciever())){
                        request.setMsg("ADD/FRIEND:CONFIRMED");
                    }else request.setMsg("ADD/FRIEND:ERROR");
                    for(User usr: Server.onlineUsers){
                        if(usr.getUsername().equals(msg.getSender()))
                            usr.send(request);
                    }
                    continue;
                }
                for(User usr: Server.onlineUsers){
                    if(usr.getUsername().equals(msg.getReciever()))
                        usr.send(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            closeConnection();
        }
    }

    public boolean alreadyLogged(String user){
        for(int i=0;i<Server.onlineUsers.size();i++)
            if (Server.onlineUsers.get(i).getUsername().equals(user))
                return true;

        return false;
    }

    public void closeConnection(){

        for(User usr: Server.onlineUsers){
            if(usr.getUsername().equals(username))
                Server.onlineUsers.remove(usr);
        }
        try {
            input.close();
            output.close();
            socket.close();
            this.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
