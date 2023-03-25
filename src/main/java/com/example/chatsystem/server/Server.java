package com.example.chatsystem.server;

import com.example.chatsystem.log.DefaultLog;
import com.example.chatsystem.model.Data;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    public static void main(String[] args) throws IOException
    {
        String groupAddress = "230.0.0.0";
        int groupPort = 8888;
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);
        UDPBroadcaster broadcaster = new UDPBroadcaster(groupAddress, groupPort);
        Data data = Data.getInstance();
        DefaultLog defaultLog = DefaultLog.getInstance();

        defaultLog.log("Server is starting, server info: " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort() +
                ". Broadcaster info: " + groupAddress + ":" + groupPort);

        while (true)
        {
            Socket socket = serverSocket.accept();
            ClientCommunicator clientCommunicator = new ClientCommunicator(socket, broadcaster, data, defaultLog);
            Thread thread = new Thread(clientCommunicator);
            thread.start();
        }
    }
}
