package Client.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {
    public static Stage stage;
    static Stage primaryStageObj;
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStageObj = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("../resources/view/LoginScreen.fxml"));
        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        stage = primaryStage;

        primaryStage.show();
    }


    public static Stage getPrimaryStage() {
        return primaryStageObj;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
