package com.example.chatsystem.server.client;

import com.example.chatsystem.model.*;
import com.example.chatsystem.server.shared.ServerModel;
import dk.via.remote.observer.RemotePropertyChangeListener;
import dk.via.remote.observer.RemotePropertyChangeSupport;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServerModelImplementation implements ServerModel
{
    private final RemotePropertyChangeSupport<Boolean> support;
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
        support.firePropertyChange("new message", null, true);
    }

    @Override
    public UserInterface login(String username, String password) throws RemoteException, IOException
    {
        UserInterface user = data.getChatterDBManager().read(username, password);

        if(user != null)
        {
            if(user.getUsername().equals(username) && user.getPassword().equals(password))
                support.firePropertyChange("login", null, true);
        }
        else
            throw new IllegalArgumentException("User is not registered.");

        return user;
    }

    @Override
    public void addPropertyChangeListener(RemotePropertyChangeListener<Boolean> listener) throws RemoteException
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
            throw new IllegalArgumentException("User with such ID is already registered.");
        }

        data.getChatterDBManager().insert(VIAid, username, password);

        support.firePropertyChange("register", null, true);
        return user;
    }

    @Override
    public ArrayList<UserInterface> getUserList() throws RemoteException, IOException
    {
        return (ArrayList<UserInterface>) data.getChatterDBManager().readAll();
    }

    public void firePropertyChange(String propertyName, Boolean oldValue, Boolean newValue) throws RemoteException
    {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public Data getData()
    {
        return data;
    }

    @Override
    public List<Message> getAllMessagesByChannel(int channelID)
    {
        return (List<Message>) data.getMessageDBManager().getAllMessagesForAChannel(channelID);
    }

    @Override
    public Channel getChannel(int id) throws RemoteException, IOException, SQLException
    {
        return data.getChannelDBManager().getChannelById(id);
    }

    @Override
    public Room getRoom(int id) throws RemoteException, IOException
    {
        return null;
    }

    @Override
    public void editMessage(int index, String message, int channelID) throws RemoteException, IOException
    {
        var messageList = ((ArrayList<Message>) data.getMessageDBManager().getAllMessagesForAChannel(channelID));
        Message mes = messageList.get(index);
        mes.setMessage(message);
        data.getMessageDBManager().updateMessage(mes.getId(), mes);
        support.firePropertyChange("message was edited", null, true);
    }

    @Override
    public void deleteMessage(int index, int channelID) throws RemoteException, IOException
    {
        var messageList = ((ArrayList<Message>) data.getMessageDBManager().getAllMessagesForAChannel(channelID));
        Message mes = messageList.get(index);
        data.getMessageDBManager().deleteMessage(mes.getId());
        support.firePropertyChange("message was deleted", null, true);
    }
}
