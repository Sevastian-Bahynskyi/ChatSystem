package com.example.chatsystem.model;

import com.example.chatsystem.server.client.Client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class ModelManager implements Model
{
    private Client server;
    private UserInterface user;
    private Room room;
    private Channel channel;
    private final int port = 5050;
    private PropertyChangeSupport support;

    private ArrayList<Message> messages;

    public ModelManager() throws IOException, InterruptedException, SQLException
    {
        this.server = new Client(port, this);
        try{
            DriverManager.registerDriver(new org.postgresql.Driver());
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        server.run();
        support = new PropertyChangeSupport(this);
        messages = new ArrayList<>();

        channel = server.getChannel(2);
        messages = (ArrayList<Message>) server.getAllMessagesByChannel(channel.getId());

        int i = 0;
    }

    @Override
    public void login(String viaID, String username, String password) throws IOException
    {
        user = server.login(username, password);
        this.messages = (ArrayList<Message>) server.getAllMessagesByChannel(channel.getId());
        support.firePropertyChange("user", null, user);
    }

    @Override
    public void register(String viaID, String username, String password, String imageUrl) throws IOException
    {
        user = server.register(viaID, username, password, imageUrl);
        this.messages = (ArrayList<Message>) server.getAllMessagesByChannel(channel.getId());
        support.firePropertyChange("user", null, user);
    }

    public void receiveUsersInRoom(ArrayList<UserInterface> users)
    {
        support.firePropertyChange("update user list", null, users);
    }

    public ArrayList<Message> getMessages()
    {
        return messages;
    }

    @Override
    public UserInterface getUser()
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
        var mes = new Message(message, user, channel.getId());
        server.sendMessage(mes);
    }


    public void addRoom(String name, String code, String imageURL)
    {
        Room room = new Room(1, name, code);
        System.out.println(imageURL);
        room.setImageUrl(imageURL);
        // todo call server and create room in the table there
        support.firePropertyChange("room added", null, room);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }

    public ArrayList<UserInterface> getUserList() throws IOException
    {
        return server.getUserList();
    }

    @Override
    public int getChannelId()
    {
        return channel.getId();
    }

    public void sendOthersMessage(Message message)
    {
        System.out.println("Got message: " + message.getMessage() + " from " + message.getUser().getUsername());
        if(!message.getUser().equals(user))
            support.firePropertyChange("new message", null, message);
    }

    @Override
    public int getRoomId()
    {
        return room.getId();
    }
}
