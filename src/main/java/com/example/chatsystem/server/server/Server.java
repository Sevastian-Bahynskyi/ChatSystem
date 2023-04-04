package com.example.chatsystem.server.server;

import com.example.chatsystem.log.FileLog;
import com.example.chatsystem.model.Data;
import com.example.chatsystem.server.client.ServerModelImplementation;
import com.example.chatsystem.server.shared.ServerModel;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server
{
    public static void main(String[] args) throws RemoteException, AlreadyBoundException
    {
        Data data = Data.getInstance();
        FileLog log = FileLog.getInstance();
        Registry registry = LocateRegistry.createRegistry(8080);
        ServerModel serverModel = new ServerModelImplementation(data);
        Remote remote = UnicastRemoteObject.exportObject(serverModel, 0);
        registry.bind("ServerModel", remote);
        System.out.println("Server is running.");
    }
}
