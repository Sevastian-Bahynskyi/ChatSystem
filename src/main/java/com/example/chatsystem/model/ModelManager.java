package com.example.chatsystem.model;

import com.example.chatsystem.server.ClientImplementation;
import com.example.chatsystem.server.ServerModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;

public class ModelManager implements Model
{
    private ServerModel server;
    private User user;
    private final String host = "localhost";
    private final int port = 8080;
    private final String groupAddress = "230.0.0.0";
    private final int groupPort = 8888;
    private PropertyChangeSupport support;
    private ArrayList<Message> messages;

    public ModelManager() throws IOException, InterruptedException
    {
        this.server = new ClientImplementation(host, port, groupAddress, groupPort);
        server.connect();
        support = new PropertyChangeSupport(this);
        if(server.isConnected())
        {
            server.addPropertyChangeListener(evt ->
            {
                if(!((Message) evt.getNewValue()).getUser().equals(user))
                    support.firePropertyChange("new message", null, evt.getNewValue());
            });
        }
        else {
            System.out.println("GUI is not connected to the server, app is running in the test mode.");
        }

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
        if(imageUrl != null)
            user.setImageUrl(imageUrl);
        var messages = (ArrayList<Message>) res.get(1);
        this.messages = messages;
        support.firePropertyChange("user", null, user);
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
        server.disconnect();
    }

    @Override
    public void addMessage(String message) throws IOException
    {
        server.sendMessage(new Message(message, user));
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        support.removePropertyChangeListener(propertyName, listener);
    }

    public ArrayList<User> getUserList() throws IOException
    {
        return server.getUserList();
    }

}
