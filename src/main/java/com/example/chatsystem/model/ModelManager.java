package com.example.chatsystem.model;

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
    private Client server;
    private UserInterface user;
    private Room room;
    private Channel channel;
    private final int port = 5050;
    private PropertyChangeSupport support;

    private ArrayList<Message> messages;
    private ArrayList<Channel> channels;
    private ArrayList<UserInterface> users;
    private ArrayList<Room> rooms;

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
    }

    @Override
    public void login(String viaID, String username, String password)
    {
        user = server.login(username, password);
        doFirstLoad();
    }

    public void loadEverything()
    {
        support.firePropertyChange("update user list", null, users);
        support.firePropertyChange("load rooms", null, rooms);
        support.firePropertyChange("load channels", null, channels);
        support.firePropertyChange("user", null, user);
    }

    @Override
    public void register(String viaID, String username, String password, String imageUrl)
    {
        user = server.register(viaID, username, password, imageUrl);
        doFirstLoad();
    }

    private void doFirstLoad()
    {
        rooms = server.getRooms();
        if(!rooms.isEmpty())
        {
            room = rooms.get(0);
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

    public void receiveUsersInRoom(ArrayList<UserInterface> users)
    {
        support.firePropertyChange("update user list", null, users);
    }

    @Override
    public ArrayList<Message> getMessages(int channelId)
    {
        if(channelId == -1)
            return messages;
        else {

            this.channel = server.getChannel(channelId);
            return (ArrayList<Message>) server.getAllMessagesByChannel(channelId);
        }
    }

    @Override
    public ArrayList<Channel> getChannels(int roomId)
    {
        if(roomId == -1)
            return channels;
        else {
            try
            {
                this.room = server.getRoom(roomId);
                users = server.getUserListInRoom(room.getId());
                if(server.isModerator(user.getViaId(), room.getId()))
                    user = new Moderator(user);
                else
                    user = new Chatter(user);

                if(!users.contains(user))
                {
                    support.firePropertyChange("join a room", null, List.of(room, user));
                    return null;
                }

                System.out.println(users);
                System.out.println(user);
                // user list is empty all the time

            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            return server.getChannelsInTheRoom(roomId);
        }
    }

    @Override
    public void joinRoom(Room room)
    {
        server.addChatterToRoom(user, room);
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
    public void banUser(UserInterface user)
    {
        server.banUser(room.getId(), user);
    }

    @Override
    public void makeModerator(UserInterface user)
    {
        server.makeModerator(user, room.getId());
    }

    private Message currentMessage;

    @Override
    public Message addMessage(String message)
    {
        Message mes = new Message(message, user, channel.getId());
        server.sendMessage(mes);
        return currentMessage;
    }


    public void addRoom(String name, String code, String imageURL)
    {
        try
        {
            server.createRoom(user, name, code);
            user = new Moderator(user);
            System.out.println(user);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }

    public ArrayList<UserInterface> getUserList()
    {
        return users;
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
    public void deleteMessage(int id)
    {
        server.deleteMessage(id, channel.getId());
    }

    @Override
    public void editMessage(int id, String message)
    {
        server.editMessage(id, message, channel.getId());
    }

    @Override
    public void createChannel(String channelName)
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
    public boolean editChannel(int id, String newChannelName)
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
        if(user.isModerator())
            server.deleteChannel(id);
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

    @Override
    public boolean isModeratorInRoom(int roomId)
    {
        try
        {
            return server.isModeratorInRoom(user.getViaId(), roomId);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void receiveNewRoom(Room room)
    {
        if(rooms.contains(room))
            support.firePropertyChange("reload room", null, room);
        else
            support.firePropertyChange("room added", null, room);
    }

    @Override
    public void editRoom(String roomName, String roomCode, String imageUrl)
    {
        server.editRoom(room.getId(), roomName, roomCode, imageUrl);
    }
}
