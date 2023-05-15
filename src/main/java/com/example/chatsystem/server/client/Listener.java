package com.example.chatsystem.server.client;

import com.example.chatsystem.model.*;
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
        switch (remotePropertyChangeEvent.getPropertyName())
        {
            case "new message" -> {
                var userList = ((ArrayList<Message>) data.getMessageDBManager().getAllMessagesForAChannel(modelManager.getChannelId()));

                modelManager.sendOthersMessage(userList.get(userList.size() - 1));
            }
            case "register" -> this.modelManager.receiveData();
        }
    }

    @Override
    public void sendMessage(Message message) throws RemoteException, IOException
    {
        serverModel.sendMessage(message);
    }

    @Override
    public UserInterface login(String viaID, String username, String password) throws RemoteException, IOException
    {
        return serverModel.login(viaID, username, password);
    }



    @Override
    public UserInterface register(String VIAid, String username, String password, String imageUrl) throws RemoteException, IOException
    {
        return serverModel.register(VIAid, username, password, imageUrl);
    }

    @Override
    public ArrayList<UserInterface> getUserList() throws RemoteException, IOException
    {
        return serverModel.getUserList();
    }

    @Override
    public Data getData() throws RemoteException
    {
        return serverModel.getData();
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
}
