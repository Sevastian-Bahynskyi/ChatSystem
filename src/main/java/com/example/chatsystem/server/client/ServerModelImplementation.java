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
    private final RemotePropertyChangeSupport<Integer> support;
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
        support.firePropertyChange("new message", null, 1);
    }

    @Override
    public UserInterface login(String username, String password) throws RemoteException, IOException
    {
        UserInterface user = data.getChatterDBManager().read(username, password);

        if(user != null)
        {
            if(user.getUsername().equals(username) && user.getPassword().equals(password))
                support.firePropertyChange("login", null, 1);
        }
        else
            throw new IllegalArgumentException("User is not registered.");

        return user;
    }

    @Override
    public void addPropertyChangeListener(RemotePropertyChangeListener<Integer> listener) throws RemoteException
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

        support.firePropertyChange("register", null, 1);
        return user;
    }

    @Override
    public ArrayList<UserInterface> getUserList() throws RemoteException, IOException
    {
        return (ArrayList<UserInterface>) data.getChatterDBManager().readAll();
    }

    public void firePropertyChange(String propertyName, Integer oldValue, Integer newValue) throws RemoteException
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
    public void editMessage(int id, String message, int channelID) throws RemoteException, IOException
    {
        Message mes = new Message();
        mes.setMessage(message);
        data.getMessageDBManager().updateMessage(id, mes);
        support.firePropertyChange("message was edited", null, id);
    }

    @Override
    public void deleteMessage(int id, int channelID) throws RemoteException, IOException
    {
        data.getMessageDBManager().deleteMessage(id);
        support.firePropertyChange("message was deleted", null, id);
    }

    @Override
    public void createChannel(String channelName, int roomId) throws RemoteException
    {
        Channel channel = data.getChannelDBManager().createChannel(channelName, roomId);
        support.firePropertyChange("new channel", null, channel.getId());
    }

    @Override
    public ArrayList<Channel> getChannelsInTheRoom(int roomId) throws RemoteException, IOException
    {
        try
        {
            return data.getChannelDBManager().getChannelsByRoomID(roomId);
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void editChannel(int id, String newChannelName) throws RemoteException, IOException, SQLException
    {
        Channel ch = data.getChannelDBManager().getChannelById(id);
        data.getChannelDBManager().updateChannelById(id, newChannelName);
        support.firePropertyChange("channel was edited", null, ch.getId());

    }

    @Override
    public void deleteChannel(int id) throws RemoteException, IOException
    {
        try
        {
            data.getChannelDBManager().deleteChannelById(id);
            support.firePropertyChange("channel was deleted", null, id);
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isModerator(String chatterId, int channelId)
    {
        Room room = data.getRoomDBManager().getRoomByChannel(channelId);
        return data.getModeratorDBManager().isModeratorInRoom(chatterId, room.getId());
    }

    @Override
    public void createRoom(String name, String code) throws RemoteException
    {
        Room room = data.getRoomDBManager().createRoom(name, code);
        support.firePropertyChange("room was added", null, room.getId());
    }

    @Override
    public ArrayList<Room> getRooms()
    {
        return (ArrayList<Room>) data.getRoomDBManager().getRooms();
    }
}
