package com.example.chatsystem.server.shared;

import com.example.chatsystem.model.Data;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.User;
import dk.via.remote.observer.RemotePropertyChangeListener;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface ServerModel extends Remote
{
    void sendMessage(Message message) throws RemoteException, IOException;

    List<Object> login(String username, String password) throws RemoteException, IOException;

    void addPropertyChangeListener(RemotePropertyChangeListener<Data> listener) throws RemoteException;

    void firePropertyChange(String propertyName, Data oldValue, Data newValue) throws RemoteException;

    List<Object> register(String username, String password, String imageUrl) throws RemoteException, IOException;

    ArrayList<User> getUserList() throws RemoteException, IOException;

    Data getData() throws RemoteException;
}