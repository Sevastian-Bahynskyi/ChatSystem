package com.example.chatsystem.model;

import com.example.chatsystem.server.client.Client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class ModelManager implements Model
{
    private Client server;
    private UserInterface user;
    private Room room;
    private Channel channel;
    private final int port = 5050;
    private PropertyChangeSupport support;

    private ArrayList<Message> messages;
    private ArrayList<Channel> channels;

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

        room = new Room(2, "dummy", "qeq3q3y");

    }

    @Override
    public void login(String viaID, String username, String password) throws IOException, InterruptedException
    {
        channels = server.getChannelsInTheRoom(room.getId());
        user = server.login(username, password);
        channel = channels.get(channels.size() - 1);
        this.messages = (ArrayList<Message>) server.getAllMessagesByChannel(channel.getId());

    }

    public void loadEverything()
    {
        support.firePropertyChange("load channels", null, channels);
        support.firePropertyChange("user", null, user);
    }

    @Override
    public void register(String viaID, String username, String password, String imageUrl) throws IOException
    {
        channels = server.getChannelsInTheRoom(room.getId());
        user = server.register(viaID, username, password, imageUrl);
        channel = channels.get(channels.size() - 1);
        this.messages = (ArrayList<Message>) server.getAllMessagesByChannel(channel.getId());
    }

    public void receiveUsersInRoom(ArrayList<UserInterface> users)
    {
        support.firePropertyChange("update user list", null, users);
    }

    @Override
    public ArrayList<Message> getMessages(int channelId) throws IOException
    {
        if(channelId == -1)
            return messages;
        else return (ArrayList<Message>) server.getAllMessagesByChannel(channelId);
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

    private Message currentMessage;

    @Override
    public Message addMessage(String message) throws IOException
    {
        Message mes = new Message(message, user, channel.getId());
        server.sendMessage(mes);
        return currentMessage;
    }


    public void addRoom(String name, String code, String imageURL)
    {
        Room room = new Room(1, name, code);
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
        currentMessage = message;
        if(!message.getUser().equals(user))
            support.firePropertyChange("new message", null, message);
    }

    @Override
    public int getRoomId()
    {
        return room.getId();
    }

    @Override
    public void deleteMessage(int id) throws IOException
    {
        server.deleteMessage(id, channel.getId());
    }

    @Override
    public void editMessage(int id, String message) throws IOException
    {
        server.editMessage(id, message, channel.getId());
    }

    @Override
    public void createChannel(String channelName) throws IOException
    {
        server.createChannel(channelName, channel.getRoomId());
    }

    public void reloadMessages(ArrayList<Message> messages)
    {
        support.firePropertyChange("reload messages", null, messages);
    }

    public void reloadMessage(Message mes)
    {
        support.firePropertyChange("reload message", null, mes);
    }

    public void receiveNewChannel(Channel channel)
    {
        this.channel = channel;
        support.firePropertyChange("new channel", null, channel);
    }

    @Override
    public boolean editChannel(int id, String newChannelName) throws IOException, SQLException
    {
        if(user.isModerator())
            server.editChannel(id, newChannelName);
        return user.isModerator();
    }

    public void reloadChannel(Channel channel)
    {
        support.firePropertyChange("reload channel", null, channel);
    }

    @Override
    public void deleteChannel(int id)
    {
        try
        {
            if(user.isModerator())
                server.deleteChannel(id);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void receiveChannelToRemove(int channelId)
    {
        support.firePropertyChange("delete channel", null, channelId);
    }

    @Override
    public boolean isModerator(int channelId) throws RemoteException
    {
        return server.isModerator(user.getViaId(), channelId);
    }
}
