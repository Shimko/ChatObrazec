package ru.geekbrains.chat.MainServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class MainServer {

    private Vector<ClientHandler> clientHandlers;

    public void start() {
        ServerSocket server;
        Socket socket;


        clientHandlers = new Vector<>();

        try {
            AuthServer.connect();

            try (ServerSocket serverSocket = server = new ServerSocket(8189)) {
            }
            System.out.println("Сервер запущен");

            while (true) {
                //System.out.println(AuthServer.getNickByLoginPass("login1", "pass1"));
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(socket, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        AuthServer.disconnect();
    }

    public void sendToAll(String msg){
        for (ClientHandler client:
        clientHandlers){
            client.sendMsg(msg);
        }
    }

    public void subScribe(ClientHandler client){
        clientHandlers.add(client);
    }

    public void unSubScribe(ClientHandler client){
        clientHandlers.remove(client);
    }
}
