package client.auth;

/**
 * Sample Skeleton for 'authorisation.fxml' Controller Class
 */
import client.MainController;
import client.NetworkClient;
import common.AuthCommand;
import common.SignUpCommand;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane ancor1;

    @FXML
    private AnchorPane ancor2;

    @FXML
    private Label signInLabel;


    @FXML
    private Button auth_user;

    @FXML
    private Button registerUser;

    private PauseTransition pause;

    @FXML
    private PasswordField password_user;

    @FXML
    private TextField login_user;

    //принимаем объект главного контроллера GUI
//    private MainController mainController;

    @FXML
    void onAuthorizationBtnClick(ActionEvent event) {
//        demandAuthorisation(login_user.getText(), password_user.getText());
    }

    @FXML
    void onRegistrationLinkClick(ActionEvent event) {

    }

    @FXML
    void initialize() {
        registerUser.setOnAction(event -> {
            registerUser.getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/registration_form.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        });
    }

    public void setLabelError(String message) {
        Platform.runLater(() -> {
            signInLabel.setText(message);
            signInLabel.setTextFill(Color.TOMATO);
        });
    }

    public void setLabelOk(String message) {
        Platform.runLater(() -> {
            signInLabel.setText(message);
            signInLabel.setTextFill(Color.GREEN);
        });
    }

    public void startAuthentication() {
        if (!checkTextFields()) {
            return;
        }
        if (!checkLengthsTextFields()) {
            return;
        }
        String login = login_user.getText().trim();
        String password = password_user.getText().trim();
        NetworkClient.getInstance().sendCommandToServer(new AuthCommand(login, password));
        auth_user.setDisable(true);
        setLabelError("Ожидание ответа от сервера...");
        AuthCommand command = (AuthCommand) NetworkClient.getInstance().readCommandFromServer();
        if (command.isAuthorized()) {
            setLabelOk("Вход выполнен");
            NetworkClient.getInstance().setUserId(command.getUserID());
            runWithPause(200, event -> openMainWindow());
        } else {
            setLabelError(command.getMessage());
            auth_user.setDisable(false);
        }
    }

    public void startRegistration() {
        if (!checkTextFields()) {
            return;
        }
        if (!checkLengthsTextFields()) {
            return;
        }
        if (!password_user.getText().equals(password_user.getText())) {
            setLabelError("Пароли не совпадают!");
            return;
        }
        String login = login_user.getText().trim();
        String password = password_user.getText().trim();
        NetworkClient.getInstance().sendCommandToServer(new SignUpCommand(login, password));
        auth_user.setDisable(true);
        setLabelError("Ожидание ответа от сервера...");
        SignUpCommand command = (SignUpCommand) NetworkClient.getInstance().readCommandFromServer();
        if (command.isSignUp()) {
            setLabelOk("Регистрация выполнена успешно. Переход на окно авторизации");
            runWithPause(1500, event -> openSignInScreen());
        } else {
            setLabelError(command.getMessage());
            auth_user.setDisable(false);
        }
    }

    public void openSignUpScreen() {
        try {
            showNewStage((Stage) registerUser.getScene().getWindow(),
                    "/registration_form.fxml",
                    "Регистрация",
                    event -> {
                        NetworkClient.getInstance().stop();
                        Platform.exit();
                    });
        } catch (IOException e) {
            System.out.println("Ошибка загрузки экрана регистрации");
            e.printStackTrace();
        }
    }

    public void openSignInScreen() {
        try {
            showNewStage((Stage) registerUser.getScene().getWindow(),
                    "/registration_form.fxml",
                    "Авторизация",
                    event -> {
                        NetworkClient.getInstance().stop();
                        Platform.exit();
                    });
        } catch (IOException e) {
            System.out.println("Ошибка загрузки экрана авторизации");
            e.printStackTrace();
        }
    }


    private void showNewStage(Stage stage, String FXMLFile, String title, EventHandler<WindowEvent> onCloseEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(FXMLFile));
        Parent root = fxmlLoader.load();
        AuthController controller = fxmlLoader.getController();
        NetworkClient.getInstance().setAuthController(controller);
        Scene newScene = new Scene(root);
        newScene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (title.equals("Регистрация")) {
                    controller.startRegistration();
                } else if (title.equals("Авторизация")) {
                    controller.startAuthentication();
                }
            }
        });
//        newScene.getStylesheets().add((getClass().getResource("/css/style.css")).toExternalForm());
        stage.setTitle(title);
        stage.setScene(newScene);
        stage.setOnCloseRequest(onCloseEvent);
    }

    private boolean checkTextFields() {
        if (login_user.getText().trim().isEmpty() || password_user.getText().trim().isEmpty()) {
            setLabelError("Некорректный ввод данных");
            setTextFieldsZeroLength();
            return false;
        }
        return true;
    }

    private boolean checkLengthsTextFields() {
        if (login_user.getText().length() < 3) {
            setLabelError("Слишком короткое имя пользователя. Допустимо не менее 3-х символов.");
            return false;
        } else if (password_user.getText().length() < 3) {
            setLabelError("Слишком короткий пароль. Допустимо не менее 3-х символов.");
            setTextFieldsZeroLength();
            return false;
        }
        return true;
    }

    private void setTextFieldsZeroLength() {
//        loginText.setText("");
        password_user.setText("");
        if (password_user != null) {
            password_user.setText("");
        }
    }

    private void runWithPause(int duration, EventHandler<ActionEvent> event) {
        pause.setDuration(Duration.millis(duration));
        pause.setOnFinished(event);
        pause.play();
    }


    private void openMainWindow() {
        try {
            Stage mainWindow = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/mainWindow.fxml"));
            Scene mainScene = new Scene(loader.load());
            MainController controller = loader.getController();
            mainScene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
//                if (event.getCode().equals(KeyCode.DELETE)) {
//                    controller.deleteButtonAction();
//                }
//                if (event.getCode().equals(KeyCode.SPACE)) {
//                    if (controller.getMaximizeOperations().isDisabled()) {
//                        controller.minimizeOperationsTable();
//                    } else {
//                        controller.maximizeOperationsTable();
//                    }
//                }
            });
            mainScene.getStylesheets().add((getClass().getResource("/css/style.css")).toExternalForm());
            mainWindow.setTitle("Cloud Drive");
            mainWindow.setScene(mainScene);
//            mainWindow.setOnCloseRequest(controller::onExitAction);
            mainWindow.getIcons().add(new Image("img/network_drive.png"));

            mainWindow.setMinHeight(550);
            mainWindow.setMinWidth(920);

            auth_user.getScene().getWindow().hide();
            mainWindow.show();
        } catch (IOException e) {
            System.out.println("Ошибка загрузки главного экрана приложения");
            e.printStackTrace();
        }
    }

}
