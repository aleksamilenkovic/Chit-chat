package Client.controllers;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import message.Message;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class LoginController  implements Initializable {
    public JFXTextField usernameField;
    public JFXPasswordField passwordField;
    private static Socket socket;
    public Label close_btn;
    private static ObjectInputStream input;

    public static ObjectInputStream getInput() {
        return input;
    }

    public static ObjectOutputStream getOutput() {
        return output;
    }

    private static ObjectOutputStream output;
    public Message msg;
    public static Socket getSocket() {
        return socket;
    }

    public static String getUsername() {
        return username;
    }

    private static String username;
    private static String password;
    public AnchorPane parent;
    private Stage stage;
    private double xOffSet = 0;
    private double yOffSet = 0;
    @FXML
    private JFXButton register_button;
    @FXML
    private JFXButton login_button;

    @FXML
    private void login(ActionEvent event)  {
        username = usernameField.getText();
        password = passwordField.getText();
        try{
            socket = new Socket("localhost",16114);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            Message loginINFO = new Message();
            loginINFO.setMsg(username+"/"+password+"/"+"LOGIN");
            output.writeObject(loginINFO);
            Message conf = (Message)input.readObject();
            if(conf.getMsg().equals("CONFIRMED")){
                this.changeToNextScene(event);
                System.out.println("prelazak na drugu scenu");}
            else{
                socket.close();
                System.out.println("saljemo odg klijentu");
            }

        }catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void register(ActionEvent event) {
        username = usernameField.getText();
        password = passwordField.getText();
        try{
            socket = new Socket("localhost",16114);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            Message loginINFO = new Message();
            loginINFO.setMsg(username+"/"+password+"/"+"REGISTER");
            output.writeObject(loginINFO);
            Message conf = (Message)input.readObject();
            if(conf.getMsg().equals("CONFIRMED")){
                this.changeToNextScene(event);
                System.out.println("prelazak na drugu scenu");}
            else{
                socket.close();
                System.out.println("saljemo odg klijentu");
            }

        }catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }





    @FXML
    private void handleEmptyFields(){
        String username = usernameField.getText().trim();
        String password = this.passwordField.getText().trim();

        boolean disableButton = username.isEmpty() || password.isEmpty();

        this.login_button.setDisable(disableButton);
        this.register_button.setDisable(disableButton);
    }

    private void makeStageDraggable() {
        parent.setOnMousePressed((event) -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        parent.setOnMouseDragged((event) -> {
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffSet);
            stage.setY(event.getScreenY() - yOffSet);
            stage.setOpacity(0.8f);
        });
        parent.setOnDragDone((event) -> {
            stage.setOpacity(1.0f);
        });
        parent.setOnMouseReleased((event) -> {
            stage.setOpacity(1.0f);
        });
    }

    @FXML
    private void minimize_app() {
        Main.stage.setIconified(true);
    }
    public void close_app(MouseEvent mouseEvent) {
       System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeStageDraggable();
    }

    public void changeToNextScene(ActionEvent event){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("../resources/view/MenuScreen.fxml"));

            Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
            primaryStage.getScene().setRoot(root);
            //primaryStage.hide();
            primaryStage.setWidth(1040);
            primaryStage.setHeight(620);
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
