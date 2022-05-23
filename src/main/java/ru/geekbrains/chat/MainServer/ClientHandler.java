package ru.geekbrains.chat.MainServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    Socket socket;
    ServerMain server;
    DataOutputStream out;
    DataInputStream in;

    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public ClientHandler(Socket socket, ServerMain serverMain) {
        this.socket = socket;
        this.server = serverMain;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {

                    while (true){
                        String str = in.readUTF();

                        if (str.startsWith("/auth")){
                            String[] creds = str.split(" ");
                            nickname = AuthServer.getNickByLoginPass(creds[1], creds[2]);

                            if(isUserCorrect(nickname, server)){
                                break;
                            }
                        }
                        if (str.startsWith("/reg")){
                            String[] creds = str.split(" ");
                            String login = creds[1];
                            String password = creds[2];
                            if (server.isNickFree(login)){
                                AuthServer.registerUser(login, password);
                                nickname = login;
                                if(isUserCorrect(login, server)) break;
                            } else {
                                sendMsg("Nickname is incorrect");
                            }
                        }
                    }

                    while (true) {
                        String str;
                        str = in.readUTF();
                        if (str.equals("/end")) {
                            out.writeUTF("/end");
                            break;
                        }
                        if (str.startsWith("/show")){
                            server.sendOnlineUsers();
                        }
                        serverMain.sendToAll(nickname + ": " + str);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        out.writeUTF("/end");
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
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                server.unsubscribe(ClientHandler.this);
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isUserCorrect(String nickname, ServerMain server) {
        if(server.isNickFree(nickname)){
            server.subscribe(ClientHandler.this);
            sendServiceMsg("/authok " + "Вы залогинены под ником: " + nickname);
            server.sendOnlineUsers();
            return true;
        } else {
            sendMsg("Wrong Login/Password");
            return false;
        }
    }

    public void sendMsg(String msg){
        System.out.println("Client send message: " + msg);
        try {
            out.writeUTF(msg + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendServiceMsg(String msg){
        System.out.println("Client send message: " + msg);
        try {
            out.writeUTF(msg + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
