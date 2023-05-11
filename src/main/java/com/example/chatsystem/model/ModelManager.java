package com.example.chatsystem.model;

import com.example.chatsystem.server.client.Client;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ModelManager implements Model
{
    private Client server;
    private User user;
    private final int port = 5050;
    private PropertyChangeSupport support;

    private Data data;
    private ArrayList<Message> messages;

    public ModelManager() throws IOException, InterruptedException
    {
        this.server = new Client(port, this);
        server.run();

        support = new PropertyChangeSupport(this);
        messages = new ArrayList<>();
    }

    @Override
    public void login(String username, String password) throws IOException
    {
        var res = server.login(username, password);
        user = (User) res.get(0);
        var messages = (ArrayList<Message>) res.get(1);
        this.messages = messages;
        support.firePropertyChange("user", null, user);
    }

    @Override
    public void register(String username, String password, String imageUrl) throws IOException
    {
        var res = server.register(username, password, imageUrl);
        user = (User) res.get(0);

        var messages = (ArrayList<Message>) res.get(1);
        this.messages = messages;
        support.firePropertyChange("user", null, user);
    }

    public void receiveData(Data data)
    {
        this.data = data;
        support.firePropertyChange("update user list", null, data.getUsers());
    }

    public ArrayList<Message> getMessages()
    {
        return messages;
    }

    @Override
    public User getUser()
    {
        return user;
    }

    @Override
    public void disconnect() throws IOException
    {
//        server.disconnect();
    }

    @Override
    public void addMessage(String message) throws IOException
    {
        var mes = new Message(message, user);
        server.sendMessage(mes);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }

    public ArrayList<User> getUserList() throws IOException
    {
        return server.getUserList();
    }

    public void sendOthersMessage(Message message)
    {
        System.out.println("Got message: " + message.getMessage() + " from " + message.getUser().getUsername());
        if(!message.getUser().equals(user))
            support.firePropertyChange("new message", null, message);
    }
}
