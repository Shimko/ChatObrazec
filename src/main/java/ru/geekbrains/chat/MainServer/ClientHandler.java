package ru.geekbrains.chat.MainServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    Socket socket;
    MainServer server;
    DataOutputStream out;
    DataInputStream in;

    public ClientHandler(Socket socket, MainServer mainServer) {
        this.socket = socket;
        this.server = mainServer;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();

                            if (str.startsWith("/auth")) {
                                String[] creds = str.split(" ");
                                String nick = AuthServer.getNickByLoginPass(creds[1], creds[2]);

                                if (nick != null) {
                                    sendMsg("/authok");
                                    server.subScribe(ClientHandler.this);
                                    break;
                                } else {
                                    sendMsg("Wrong Login/Password");
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
                            mainServer.sendToAll(str);
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
                    server.unSubScribe(ClientHandler.this);
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        System.out.println("Client send message: " + msg);
        try {
            out.writeUTF(msg + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        String str = null;
        String nick = null;
        try {
            str = in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (str.startsWith("/auth")) {
            String[] creds = str.split(" ");
            nick = AuthServer.getNickByLoginPass(creds[1], creds[2]);
        }
        return nick;
    }
}
