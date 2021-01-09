package client;

import common.AbstractMessage;
import common.FileDeleteMessage;
import common.FileMessage;
import common.FileRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    ListView<String> filesList;
    @FXML
    ListView<String> filesListServer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("client_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalFilesList();
                        refreshServerFilesList();
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        refreshLocalFilesList();
        refreshServerFilesList();
    }

    public void pressOnUpdateServer(ActionEvent actionEvent) {
        refreshLocalFilesList();
        refreshServerFilesList();
    }
    public void pressOnUpdateClient(ActionEvent actionEvent) {
        refreshLocalFilesList();
    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        Network.sendMsg(new FileRequest(filesListServer.getSelectionModel().getSelectedItem()));
        refreshLocalFilesList();
        refreshServerFilesList();
    }

    public void pressOnUploadBtn(ActionEvent actionEvent) {
        try {
            Network.sendMsg(new FileMessage(Paths.get("client_storage/" + filesList.getSelectionModel().getSelectedItem())));
            refreshLocalFilesList();
            refreshServerFilesList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //удаляем файл на клиенте
    public void pressOnDelClientBtn(ActionEvent actionEvent){
        new File("client_storage/" + filesList.getSelectionModel().getSelectedItem()).delete();
        refreshLocalFilesList();
    }
//    удаляем файл на сервере
    public void pressOnDelServerBtn(ActionEvent actionEvent){
        Network.sendMsg(new FileDeleteMessage(filesListServer.getSelectionModel().getSelectedItem()));
//        refreshLocalFilesList();
        refreshServerFilesList();
    }

    public void refreshLocalFilesList() {
        Platform.runLater(() -> {
            try {
                filesList.getItems().clear();
                Files.list(Paths.get("client_storage"))
                        .filter(p -> !Files.isDirectory(p))
                        .map(p -> p.getFileName().toString())
                        .forEach(o -> filesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    public void refreshServerFilesList() {
        Platform.runLater(() -> {
            try {
                filesListServer.getItems().clear();
                Files.list(Paths.get("server_storage"))
                        .filter(p -> !Files.isDirectory(p))
                        .map(p -> p.getFileName().toString())
                        .forEach(o -> filesListServer.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
