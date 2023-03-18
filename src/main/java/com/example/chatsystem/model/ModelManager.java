package com.example.chatsystem.model;

import com.example.chatsystem.server.Client;
import com.example.chatsystem.server.ClientImplementation;
import com.example.chatsystem.server.ServerModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

public class ModelManager implements Model
{
    private ServerModel server;
    private User user;
    private final String host = "localhost";
    private final int port = 8080;
    private final String groupAddress = "230.0.0.0";
    private final int groupPort = 8888;
    private PropertyChangeSupport support;

    public ModelManager() throws IOException
    {
        this.server = new ClientImplementation(this);
        server.connect(host, port, groupAddress, groupPort);
        support = new PropertyChangeSupport(this);
        if(server.isConnected())
        {
            server.addPropertyChangeListener(evt ->
                    support.firePropertyChange("new message", null, evt.getNewValue()));
        }
        else {
            System.out.println("GUI is not connected to the server, app is running in test mode.");
        }
    }
    @Override
    public void setUser(String username, String password) throws IOException
    {
        user = server.login(username, password);
    }

    @Override
    public void addMessage(String message) throws IOException
    {
        if(user == null)
        {
            user = new User("Sevastian", "asus2004");
        }
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
}
