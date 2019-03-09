package Client.controllers;

import Client.messages.bubble.BubbleSpec;
import Client.messages.bubble.BubbledLabel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import message.Message;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MenuController implements Initializable{
    private static Socket socket;
    public ListView chatPane;
    private static String username;
    public TextArea messageBox;
    public ImageView userImageView;
    public BorderPane borderPane;
    public ListView<String>  userList;
    public ObservableList<String> friends;
    private double xOffset;
    private double yOffset;
    private static ReceiverSenderThread thread;
    private static ObjectOutputStream output;

    public static String getUsername() {
        return username;
    }


    public static ObjectOutputStream getOutput() {
        return output;
    }

    public MenuController (){
        this.socket=LoginController.getSocket();
        this.username= LoginController.getUsername();
        thread= new ReceiverSenderThread(socket, username,this);
        thread.start();
        output = LoginController.getOutput();
       // ObservableList<String> items = FXCollections.observableArrayList();
        Platform.runLater(() -> {
            userList = new ListView<>();
            userList.setCellFactory(new UserListCell());
            userList.setEditable(true);
            friends  =FXCollections.observableArrayList();
        });

    }
    public void sendButtonAction() throws IOException {
        String msg = messageBox.getText();
        if (!messageBox.getText().isEmpty()) {
            send(msg);
            messageBox.clear();
        }
    }
    @FXML
    public void sendMethod(KeyEvent keyEvent) throws IOException{
        if(keyEvent.getCode() == KeyCode.ENTER)
            sendButtonAction();
    }
    public  void send(String msg) throws IOException{
        Message createMessage = new Message();
        createMessage.setMsg(msg);
        String selectedUser = userList.getSelectionModel().getSelectedItem();
        createMessage.setReciever(selectedUser);
        createMessage.setSender(username);
        output.writeObject(createMessage);
        addToChat(createMessage,username);
        output.flush();
    }
    public synchronized void addToChat(Message msg, String sender){
        if(sender.equals("FROM OTHERS")){
            BubbledLabel bl = new BubbledLabel();
            bl.setText(msg.getSender() + ": "+msg.getMsg());
            bl.setBackground(new Background(new BackgroundFill(Color.WHITE,null, null)));
            bl.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
            HBox x = new HBox();
            x.setMaxWidth(chatPane.getWidth() - 20);
            x.setAlignment(Pos.TOP_LEFT);
            x.getChildren().addAll(bl);
            chatPane.getItems().add(x);
        }
        else if(sender.equals(username)){
            BubbledLabel bl = new BubbledLabel();
            bl.setText(msg.getSender() + ": "+msg.getMsg());
            bl.setBackground(new Background(new BackgroundFill( Color.rgb(209,86,80),null, null)));
            bl.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);
            HBox x = new HBox();
            x.setMaxWidth(chatPane.getWidth() - 20);
            x.setAlignment(Pos.TOP_RIGHT);
            x.getChildren().addAll(bl);
            chatPane.getItems().add(x);
        }
    }

	public void closeApplication() {
        Platform.exit();
        System.exit(0);
    }

    public void setImageLabel() throws IOException {
        this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("image/avatar.png").toString()));
    }
	
	@Override
    public void initialize(URL location, ResourceBundle resources) {
       /* try {
            setImageLabel();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
                /* Drag and Drop */
        borderPane.setOnMousePressed(event -> {
            xOffset = Main.getPrimaryStage().getX() - event.getScreenX();
            yOffset = Main.getPrimaryStage().getY() - event.getScreenY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });

        borderPane.setOnMouseDragged(event -> {
            Main.getPrimaryStage().setX(event.getScreenX() + xOffset);
            Main.getPrimaryStage().setY(event.getScreenY() + yOffset);

        });

        borderPane.setOnMouseReleased(event -> {
            borderPane.setCursor(Cursor.DEFAULT);
        });
	}



    public void addToList(String user){
        friends.add(user);
        userList.setItems(friends);
        userList.setCellFactory(new UserListCell());
      /*  Platform.runLater(() -> {
            ObservableList<String> users = FXCollections.observableList(msg.getUsers());
            userList.setItems(users);
            userList.setCellFactory(new UserListCell());

        });*/
    }

    public void add(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../resources/view/PopupAdd_friend.fxml"));
        //Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Add your friend");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }
}
