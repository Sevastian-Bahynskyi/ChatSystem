package com.example.chatsystem.server.client;

import com.example.chatsystem.model.Data;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.Chatter;
import com.example.chatsystem.server.shared.ServerModel;
import dk.via.remote.observer.RemotePropertyChangeListener;
import dk.via.remote.observer.RemotePropertyChangeSupport;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ServerModelImplementation implements ServerModel
{
    private final RemotePropertyChangeSupport<Data> support;
    private final Data data;

    public ServerModelImplementation(Data data)
    {
        this.support = new RemotePropertyChangeSupport<>();
        this.data = data;
    }

    @Override
    public void sendMessage(Message message) throws IOException
    {
        data.getMessages().add(message);
        support.firePropertyChange("new message", null, data);
    }

    @Override
    public List<Object> login(String username, String password) throws RemoteException, IOException
    {
        List<Object> res = new ArrayList<>(List.of());
        Chatter user = new Chatter(username, password);
        if(data.isUserRegistered(user))
        {
            user = data.getUsers().get(data.getUsers().indexOf(user));
            res.add(user);
        }
        else
            throw new IllegalArgumentException("User is not registered.");

        res.add(data.getMessages());
        support.firePropertyChange("login", null, data);
        return res;
    }

    @Override
    public void addPropertyChangeListener(RemotePropertyChangeListener<Data> listener) throws RemoteException
    {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public List<Object> register(String username, String password, String imageUrl) throws RemoteException, IOException
    {
        List<Object> res = new ArrayList<>(List.of());
        Chatter user = new Chatter(username, password);
        if (imageUrl != null)
            user.setImageUrl(imageUrl);
        if(data.isUserRegistered(user))
        {
            throw new IllegalArgumentException("User ia already registered.");
        }

        data.getUsers().add(user);
        res.add(user);

        res.add(data.getMessages());
        support.firePropertyChange("register", null, data);
        return res;
    }

    @Override
    public ArrayList<Chatter> getUserList() throws RemoteException, IOException
    {
        return data.getUsers();
    }

    public void firePropertyChange(String propertyName, Data oldValue, Data newValue) throws RemoteException
    {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public Data getData()
    {
        return data;
    }
}
