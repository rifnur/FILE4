package client;

//import commands.CreateFolderCommand;
//import commands.DeleteFileCommand;
//import commands.RenameFileCommand;
//import controllers.AuthWindowsController;
import client.auth.AuthController;
import common.AbstractMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;


public class NetworkClient {

    private static final String DEFAULT_SERVER_ADDRESS = "localhost";
    private static final int DEFAULT_PORT = 8202;

    private static volatile NetworkClient instance;
    private static Socket socket;
    private static ObjectEncoderOutputStream out;
    private static ObjectDecoderInputStream in;
    private static boolean connectionSuccess;

    //    private ObjectEncoderOutputStream out;
//    private ObjectDecoderInputStream in;
//    private Socket socket;
    private String userId;

    private volatile AuthController authController;
//    private volatile boolean connectionSuccess = false;
//    private Thread repeatConnectionThread = null;

    private NetworkClient() {
    }


    public static NetworkClient getInstance() {
        if (instance == null) {
            synchronized (NetworkClient.class) {
                if (instance == null) {
                    instance = new NetworkClient();
                }
            }
        }
        return instance;
    }

    public void setAuthController(AuthController authController) {
        this.authController = authController;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String newUserId) {
        userId = newUserId;
    }

    public static void start() {
        try {
            socket = new Socket(DEFAULT_SERVER_ADDRESS, DEFAULT_PORT);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream(), 1024 * 1024 * 100);
            connectionSuccess = true;
            System.out.println("Соединение с сервером установленно");
        } catch (IOException e) {
            connectionSuccess = false;
            repeatConnection();
//            System.out.println("Ошибка связи с сервером.");
            e.printStackTrace();
        }
    }

    private static void repeatConnection() {
//        if (repeatConnectionThread == null || repeatConnectionThread.getState().equals(Thread.State.TERMINATED)) {
//            repeatConnectionThread = new Thread(() -> {
//                final int counts = 10;
//                final String message = "Отсутствует связь с сервером, повторное подключение через %d...%n";
                while (!connectionSuccess) {
                    int counts=0;
                    for (int i = 0; i < counts; i++) {
                        try {
//                            System.out.printf(message, (counts - i));
//                            authController.setLabelError(String.format(message, (counts - i)));
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                    start();
                }
//                authWindowsController.setLabelOk("Подключение к серверу установлено");
//            });
//            repeatConnectionThread.setDaemon(true);
//            repeatConnectionThread.start();
//        }
    }

//    public void stop() {
//        try {
//            if (in != null) in.close();
//            if (out != null) out.close();
//            if (socket != null) socket.close();
//            System.out.println("Закрыто соединение с сервером");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public <T> void sendCommandToServer(T command) {
        try {
            out.writeObject(command);
        } catch (IOException e) {
            System.out.printf("Ошибка отправки команды %s на сервер%n", command.toString());
            e.printStackTrace();
        }
    }
//
//    public void deleteFileFromServer(Path fileName) {
//        sendCommandToServer(new DeleteFileCommand(fileName.toString()));
//    }
//
//    public void renameFileOnServer(String oldFileName, String newFileName) {
//        sendCommandToServer(new RenameFileCommand(oldFileName, newFileName));
//    }
//
//    public void createNewFolderOnServer(String currentServerDir, String newFolderName) {
//        sendCommandToServer(new CreateFolderCommand(Paths.get(currentServerDir, newFolderName).toString()));
//    }

    public Object readCommandFromServer() {
        try {
            return in.readObject();
        } catch (Exception e) {
            System.out.println("Ошибка чтения команды от сервера");
            e.printStackTrace();
        }
        return null;

    }

    public static void stop() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static AbstractMessage readObject() throws ClassNotFoundException, IOException {
        Object obj = in.readObject();
        return (AbstractMessage) obj;
    }

    public static boolean sendMsg(AbstractMessage msg) {
        try {
            out.writeObject(msg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
