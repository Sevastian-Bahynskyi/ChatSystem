package com.example.chatsystem.server.client;

import com.example.chatsystem.model.Data;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.ModelManager;
import com.example.chatsystem.model.User;
import com.example.chatsystem.server.shared.ServerModel;
import dk.via.remote.observer.RemotePropertyChangeEvent;
import dk.via.remote.observer.RemotePropertyChangeListener;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Listener extends UnicastRemoteObject implements RemotePropertyChangeListener<Data>, Serializable, ServerModel
{

    private ModelManager modelManager;
    private ServerModel serverModel;
    public Listener(ServerModel serverModel, ModelManager model) throws RemoteException
    {
        this.serverModel = serverModel;
        this.modelManager = model;
    }

    @Override
    public void propertyChange(RemotePropertyChangeEvent<Data> remotePropertyChangeEvent) throws RemoteException
    {

        Data data = remotePropertyChangeEvent.getNewValue();
        getData().getUsers().clear();
        getData().getUsers().addAll(data.getUsers());
        getData().getMessages().clear();
        getData().getMessages().addAll(data.getMessages());
        switch (remotePropertyChangeEvent.getPropertyName())
        {
            case "new message" -> {
                var messages = remotePropertyChangeEvent.getNewValue().getMessages();
                System.out.println("New message: " + messages.get(messages.size() - 1));
                modelManager.sendOthersMessage(messages.get(messages.size() - 1));
            }
            case "register" -> this.modelManager.receiveData(data);
        }
    }

    @Override
    public void sendMessage(Message message) throws RemoteException, IOException
    {
        serverModel.sendMessage(message);
    }

    @Override
    public List<Object> login(String username, String password) throws RemoteException, IOException
    {
        return serverModel.login(username, password);
    }

    @Override
    public void addPropertyChangeListener(RemotePropertyChangeListener<Data> listener) throws RemoteException
    {

    }

    @Override
    public void firePropertyChange(String propertyName, Data oldValue, Data newValue) throws RemoteException
    {
        serverModel.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public List<Object> register(String username, String password, String imageUrl) throws RemoteException, IOException
    {
        return serverModel.register(username, password, imageUrl);
    }

    @Override
    public ArrayList<User> getUserList() throws RemoteException, IOException
    {
        return serverModel.getUserList();
    }

    @Override
    public Data getData() throws RemoteException
    {
        return serverModel.getData();
    }
}
