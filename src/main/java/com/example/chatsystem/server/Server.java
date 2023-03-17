package com.example.chatsystem.server;

import com.example.chatsystem.model.Model;
import com.example.chatsystem.model.ModelManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLPermission;

public class Server
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(8080);
        UDPBroadcaster broadcaster = new UDPBroadcaster("230.0.0.0",8888);
        Model model = new ModelManager();

        while (true)
        {
            Socket socket = serverSocket.accept();
            ClientCommunicator clientCommunicator = new ClientCommunicator(socket, broadcaster, model);
            Thread thread = new Thread(clientCommunicator);
            thread.start();
        }
    }
}
