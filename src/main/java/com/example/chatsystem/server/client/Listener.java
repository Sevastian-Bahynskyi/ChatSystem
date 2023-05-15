package com.example.chatsystem.server.client;

import com.example.chatsystem.model.*;
import com.example.chatsystem.server.shared.ServerModel;
import dk.via.remote.observer.RemotePropertyChangeEvent;
import dk.via.remote.observer.RemotePropertyChangeListener;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
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
                var messageList = ((ArrayList<Message>) data.getMessageDBManager().getAllMessagesForAChannel(modelManager.getChannelId()));

                modelManager.sendOthersMessage(messageList.get(messageList.size() - 1));
            }
            case "register" -> {
                var userList = data.getChatterDBManager().readAllByRoomID(modelManager.getRoomId());
                this.modelManager.receiveUsersInRoom((ArrayList<UserInterface>) userList);
            }
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
    public List<Message> getAllMessagesByChannel(int channelID) throws IOException
    {
        return serverModel.getAllMessagesByChannel(channelID);
    }

    @Override
    public Channel getChannel(int id) throws RemoteException, IOException, SQLException
    {
        return serverModel.getChannel(id);
    }

    @Override
    public Room getRoom(int id) throws RemoteException, IOException
    {
        return serverModel.getRoom(id);
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
