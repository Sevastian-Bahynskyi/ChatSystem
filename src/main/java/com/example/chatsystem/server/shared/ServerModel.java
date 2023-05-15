package com.example.chatsystem.server.shared;

import com.example.chatsystem.model.*;
import dk.via.remote.observer.RemotePropertyChangeListener;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ServerModel extends Remote
{
    void sendMessage(Message message) throws RemoteException, IOException;

    UserInterface login(String viaID, String username, String password) throws RemoteException, IOException;

    void addPropertyChangeListener(RemotePropertyChangeListener<Data> listener) throws RemoteException;

    void firePropertyChange(String propertyName, Data oldValue, Data newValue) throws RemoteException;

    UserInterface register(String VIAid, String username, String password, String imageUrl) throws RemoteException, IOException;

    ArrayList<UserInterface> getUserList() throws RemoteException, IOException;

    Data getData() throws RemoteException;

    List<Message> getAllMessagesByChannel(int channelID) throws RemoteException, IOException;
    Channel getChannel(int id) throws RemoteException, IOException, SQLException;
    Room getRoom(int id) throws RemoteException, IOException;
}