package com.example.chatsystem.model.ModelManagerDelegates;

import com.example.chatsystem.model.*;
import com.example.chatsystem.model.ModelManagerDelegates.ChannelHandler;
import com.example.chatsystem.model.ModelManagerDelegates.MessageHandler;
import com.example.chatsystem.model.ModelManagerDelegates.RoomHandler;
import com.example.chatsystem.model.ModelManagerDelegates.UserHandler;
import com.example.chatsystem.server.client.Client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModelManager implements Model
{
    protected Client server;
    protected UserInterface user;
    protected Room room;
    protected Channel channel;
    protected Message currentMessage;
    protected final int port = 5050;
    protected PropertyChangeSupport support;

    protected ArrayList<Message> messages;
    protected ArrayList<Channel> channels;
    protected ArrayList<UserInterface> users;
    protected ArrayList<Room> rooms;



    private RoomHandler roomHandler;
    private ChannelHandler channelHandler;
    private MessageHandler messageHandler;
    private UserHandler userHandler;

    public ModelManager()
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
        roomHandler = new RoomHandler(this);
        channelHandler = new ChannelHandler(this);
        userHandler = new UserHandler(this);
        messageHandler = new MessageHandler(this);
    }

    public void loadEverythingToTheViewModel()
    {
        support.firePropertyChange("update user list", null, users);
        support.firePropertyChange("load rooms", null, rooms);
        support.firePropertyChange("load channels", null, channels);
        support.firePropertyChange("user", null, user);
    }



    protected void doFirstLoad()
    {
        rooms = server.getRooms();
        if(!rooms.isEmpty())
        {
            room = rooms.get(rooms.size() - 1);
            users = server.getUserListInRoom(room.getId());
        }

        channels = server.getChannelsInTheRoom(room.getId());

        if(!channels.isEmpty())
            channel = channels.get(0);

        if(channel != null)
            this.messages = (ArrayList<Message>) server.getAllMessagesByChannel(channel.getId());
        else this.messages = new ArrayList<>();

        if(isModeratorInRoom(room.getId()))
        {
            user = new Moderator(user);
        }
    }


    @Override
    public void login(String viaID, String username, String password)
    {
        userHandler.login(viaID, username, password);
    }

    @Override
    public void register(String viaID, String username, String password, String imageUrl)
    {
        userHandler.register(viaID, username, password, imageUrl);
    }

    public void receiveUsersInRoom(ArrayList<UserInterface> users)
    {
        userHandler.receiveUsersInRoom(users);
    }

    @Override
    public UserInterface getUser()
    {
        return userHandler.getUser();
    }

    @Override
    public void banUser(UserInterface user)
    {
        userHandler.banUser(user);
    }

    @Override
    public void makeModerator(UserInterface user)
    {
        userHandler.makeModerator(user);
    }

    @Override
    public boolean isModerator(String roomId, int channelId) throws RemoteException
    {
        return userHandler.isModerator(roomId, channelId);
    }

    @Override
    public boolean isModeratorInRoom(int roomId)
    {
        return userHandler.isModeratorInRoom(roomId);
    }

    @Override
    public ArrayList<UserInterface> getUserList()
    {
        return userHandler.getUserList();
    }

    public void addRoom(String name, String code, String imageURL)
    {
        roomHandler.addRoom(name, code, imageURL);
    }

    @Override
    public void leaveRoom()
    {
        roomHandler.leaveRoom();
    }

    @Override
    public ArrayList<Channel> getChannelsInRoom(int roomId)
    {
        return roomHandler.getChannelsInRoom(roomId);
    }

    @Override
    public void joinRoom(Room room)
    {
        roomHandler.joinRoom(room);
    }

    @Override
    public void editRoom(String roomName, String roomCode, String imageUrl)
    {
        roomHandler.editRoom(roomName, roomCode, imageUrl);
    }

    @Override
    public int getRoomId()
    {
        return roomHandler.getRoomId();
    }

    public void receiveNewRoom(Room room)
    {
        roomHandler.receiveNewRoom(room);
    }

    @Override
    public int getChannelId()
    {
        return channelHandler.getChannelId();
    }

    @Override
    public void createChannel(String channelName)
    {
        channelHandler.createChannel(channelName);
    }

    public void receiveNewChannel(Channel channel)
    {
        channelHandler.receiveNewChannel(channel);
    }

    @Override
    public boolean editChannel(int id, String newChannelName)
    {
        return channelHandler.editChannel(id, newChannelName);
    }

    public void reloadChannel(Channel channel)
    {
        channelHandler.reloadChannel(channel);
    }

    @Override
    public void deleteChannel(int id)
    {
        channelHandler.deleteChannel(id);
    }

    public void receiveChannelToRemove(int channelId)
    {
        channelHandler.receiveChannelToRemove(channelId);
    }

    public void sendOthersMessage(Message message)
    {
        // model delegates method of messageHandler
        messageHandler.sendOthersMessage(message);
    }

    @Override
    public void deleteMessage(int id)
    {
        messageHandler.deleteMessage(id);
    }

    @Override
    public void editMessage(int id, String message)
    {
        messageHandler.editMessage(id, message);
    }

    @Override
    public ArrayList<Message> getMessagesInChannel(int channelId)
    {
        return channelHandler.getMessagesInChannel(channelId);
    }

    public void reloadMessages(ArrayList<Message> messages)
    {
        messageHandler.reloadMessages(messages);
    }

    public void reloadMessage(Message mes)
    {
        messageHandler.reloadMessage(mes);
    }

    @Override
    public Message addMessage(String message)
    {
        return messageHandler.addMessage(message);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void disconnect() throws IOException
    {
        server.disconnect();
    }

}
