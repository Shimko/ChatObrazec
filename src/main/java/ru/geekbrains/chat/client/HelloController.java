package ru.geekbrains.chat.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;


public class HelloController {

    private boolean isAuthorized;

    @FXML
    TextArea textArea;
    @FXML
    Button buttonSend;
    @FXML
    Button buttonClear;
    @FXML
    TextField textField;

    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;
    @FXML
    Button enter;
    @FXML
    HBox upperPanel;
    @FXML
    HBox buttonPanel;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    String IP_ADDRESS = "localhost";
    int PORT = 8189;

    public void setActive(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;

        if (!isAuthorized){
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            buttonPanel.setVisible(false);
            buttonPanel.setManaged(false);
        } else {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            buttonPanel.setVisible(true);
            buttonPanel.setManaged(true);
        }
    }

    public void sendMessage(){
        //textArea.appendText(textField.getText() + "\n");
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    protected void keyListener(KeyEvent event) {
        if (event.getCode().getCode() == 10) {
            sendMessage();
        }
    }

    @FXML
    public void clearTextArea(){
        textArea.clear();
        textField.requestFocus();
    }

    public void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true){
                            try {
                                String str = in.readUTF();
                                if (str.startsWith("/authok")) {
                                    setActive(true);
                                    break;
                                } else {
                                    textArea.appendText(str + "\n");
                                }
                            } catch (SocketException e) {
                                System.out.println("Сервер не отвечает");
                                break;
                            }
                        }
                        while (true) {
                            try {
                                String str = in.readUTF();
                                if (str.equals("/end")){
                                    break;
                                }
                                textArea.appendText(str + "\n");
                            } catch (SocketException e) {
                                System.out.println("Сервер не отвечает");
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void auth(){
        if (socket == null || socket.isClosed()){
            connect();
        }
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}