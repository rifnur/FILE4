package client;

import client.auth.AuthController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class MainClient extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{

//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/authorisation.fxml"));
        Parent root = fxmlLoader.load();
        AuthController controller = fxmlLoader.getController();
        NetworkClient.getInstance().setAuthController(controller);
        NetworkClient.getInstance().start();
        Scene scene = new Scene(root);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                controller.startAuthentication();
            }
        });
        primaryStage.setTitle("Файловый менеджер");
//        Scene scene = new Scene(root);
//        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}