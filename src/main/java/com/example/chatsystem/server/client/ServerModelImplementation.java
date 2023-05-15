package com.example.chatsystem.server.client;

import com.example.chatsystem.model.Data;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.Chatter;
import com.example.chatsystem.model.UserInterface;
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
        data.getMessageDBManager().createMessage(message);
        support.firePropertyChange("new message", null, data);
    }

    @Override
    public UserInterface login(String viaID, String username, String password) throws RemoteException, IOException
    {
        UserInterface user = data.getChatterDBManager().read(viaID);

        if(user != null)
        {
            if(user.getUsername().equals(username) && user.getPassword().equals(password))
                support.firePropertyChange("login", null, data);
        }
        else
            throw new IllegalArgumentException("User is not registered.");

        return user;
    }

    @Override
    public void addPropertyChangeListener(RemotePropertyChangeListener<Data> listener) throws RemoteException
    {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public UserInterface register(String VIAid, String username, String password, String imageUrl) throws RemoteException, IOException
    {
        Chatter user = new Chatter(VIAid, username, password);
        if (imageUrl != null)
            user.setImageUrl(imageUrl);
        if(data.getChatterDBManager().read(VIAid) != null)
        {
            throw new IllegalArgumentException("User is already registered.");
        }

        data.getChatterDBManager().insert(VIAid, username, password);

        support.firePropertyChange("register", null, data);
        return user;
    }

    @Override
    public ArrayList<UserInterface> getUserList() throws RemoteException, IOException
    {
        return (ArrayList<UserInterface>) data.getChatterDBManager().readAll();
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
