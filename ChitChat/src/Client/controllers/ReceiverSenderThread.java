package Client.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import message.Message;

public class ReceiverSenderThread extends Thread {
    private static Socket socket;
    private static String username ;
    private static ObjectInputStream input;
    private static MenuController controller;
    ReceiverSenderThread(Socket socket, String username, MenuController controller){
        this.socket=socket;
        this.username=username;
        this.controller=controller;

            input = LoginController.getInput();
    }
    @Override
    public void run() {
        try {
        while(socket.isConnected()){

                Message msg=null;
                msg=(Message)input.readObject();

                if(msg!=null){
                    if(msg.getMsg().equals("ADD/FRIEND:CONFIRMED")){
                        controller.addToList(msg.getSender());
                    }else if(msg.getMsg().equals("ADD/FRIEND:ERROR")){
                        // ALERT UBACUJEM OVDE
                        System.out.println("GRESKA NIJE PREBACIO PRIJATELJA");
                    }
                     controller.addToChat(msg,"FROM OTHERS");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
