package Client.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import message.Message;
import java.io.IOException;

public class PopupController {

    public TextField friendName;

    public void addFriend(ActionEvent event) throws IOException {
        Message createMessage = new Message();
        createMessage.setMsg("ADD/FRIEND");
        createMessage.setReciever(friendName.getText());
        createMessage.setSender(MenuController.getUsername());
        MenuController.getOutput().writeObject(createMessage);
        MenuController.getOutput().flush();

    }
}
