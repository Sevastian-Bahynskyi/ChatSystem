package com.example.chatsystem.server.client;

import com.example.chatsystem.model.*;
import com.example.chatsystem.server.shared.ServerModel;
import dk.via.remote.observer.RemotePropertyChangeEvent;
import dk.via.remote.observer.RemotePropertyChangeListener;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Listener extends UnicastRemoteObject implements RemotePropertyChangeListener<Integer>, Serializable, ServerModel
{

    private ModelManager modelManager;
    private ServerModel serverModel;
    private Data data = Data.getInstance();
    public Listener(ServerModel serverModel, ModelManager model) throws RemoteException, SQLException
    {
        this.serverModel = serverModel;
        this.modelManager = model;
    }

    @Override
    public void propertyChange(RemotePropertyChangeEvent<Integer> remotePropertyChangeEvent) throws RemoteException
    {
        switch (remotePropertyChangeEvent.getPropertyName())
        {
            case "new message" -> {
                var lastMessage = (data.getMessageDBManager().getLastMessage(modelManager.getChannelId()));

                modelManager.sendOthersMessage(lastMessage);
            }

            case "register" -> {
                var userList = (ArrayList<UserInterface>) data.getChatterDBManager().readAllByRoomID(modelManager.getRoomId());
                this.modelManager.receiveUsersInRoom(userList);
            }

            case "message was edited", "message was deleted" -> {
                Message mes = data.getMessageDBManager().readMessage(remotePropertyChangeEvent.getNewValue());
                this.modelManager.reloadMessage(mes);
            }

            case "new channel" -> {
                try
                {
                    int channelId = remotePropertyChangeEvent.getNewValue();

                    Channel channel = data.getChannelDBManager().getChannelById(channelId);
                    this.modelManager.receiveNewChannel(channel);
                } catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }
            }

            case "channel was edited" -> {
                try
                {
                    Channel channel = data.getChannelDBManager().getChannelById(remotePropertyChangeEvent.getNewValue());
                    modelManager.reloadChannel(channel);
                    System.out.println(channel.getName());
                } catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }
            }

            case "channel was deleted" -> {
                modelManager.receiveChannelToRemove(remotePropertyChangeEvent.getNewValue());
            }

            case "room was added", "room was edited" -> {
                Room room = data.getRoomDBManager().readRoom(remotePropertyChangeEvent.getNewValue());
                modelManager.receiveNewRoom(room);
            }
        }
    }

    @Override
    public void createChannel(String channelName, int roomId) throws IOException
    {
        serverModel.createChannel(channelName, roomId);
    }

    @Override
    public void sendMessage(Message message) throws RemoteException, IOException
    {
        serverModel.sendMessage(message);
    }

    @Override
    public UserInterface login(String username, String password) throws RemoteException, IOException
    {
        return serverModel.login(username, password);
    }

    @Override
    public UserInterface register(String VIAid, String username, String password, String imageUrl)
    {
        try
        {
            return serverModel.register(VIAid, username, password, imageUrl);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<UserInterface> getUserList() throws RemoteException, IOException
    {
        return serverModel.getUserList();
    }

    @Override
    public Data getData() throws RemoteException
    {
        return serverModel.getData();
    }

    @Override
    public List<Message> getAllMessagesByChannel(int channelID) throws IOException
    {
        return serverModel.getAllMessagesByChannel(channelID);
    }

    @Override
    public Channel getChannel(int id) throws RemoteException, IOException, SQLException
    {
        return serverModel.getChannel(id);
    }

    @Override
    public Room getRoom(int id) throws RemoteException, IOException
    {
        return serverModel.getRoom(id);
    }

    @Override
    public void editMessage(int id, String message, int channelID) throws RemoteException, IOException
    {
        serverModel.editMessage(id, message, channelID);
    }

    @Override
    public void deleteMessage(int id, int channelID) throws RemoteException, IOException
    {
        serverModel.deleteMessage(id, channelID);
    }

    @Override
    public void addPropertyChangeListener(RemotePropertyChangeListener<Integer> listener) throws RemoteException
    {
        serverModel.addPropertyChangeListener(listener);
    }

    @Override
    public void firePropertyChange(String propertyName, Integer oldValue, Integer newValue) throws RemoteException
    {
        serverModel.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public ArrayList<Channel> getChannelsInTheRoom(int roomId) throws RemoteException, IOException
    {
        return serverModel.getChannelsInTheRoom(roomId);
    }

    @Override
    public void editChannel(int id, String newChannelName) throws RemoteException, IOException, SQLException
    {
        serverModel.editChannel(id, newChannelName);
    }

    @Override
    public void deleteChannel(int id)
    {
        try
        {
            serverModel.deleteChannel(id);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isModerator(String chatterId, int roomId)
    {
        try
        {
            return serverModel.isModerator(chatterId, roomId);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createRoom(UserInterface user, String name, String code)
    {
        try
        {
            serverModel.createRoom(user, name, code);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Room> getRooms()
    {
        try
        {
            return serverModel.getRooms();
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void editRoom(int roomId, String roomName, String roomCode, String imageUrl) throws RemoteException
    {
        serverModel.editRoom(roomId, roomName, roomCode, imageUrl);
    }

    @Override
    public boolean isModeratorInRoom(String viaId, int roomId)
    {
        try
        {
            return serverModel.isModeratorInRoom(viaId, roomId);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<UserInterface> getUserListInRoom(int roomId)
    {

        try
        {
            return serverModel.getUserListInRoom(roomId);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void banUser(int roomId, UserInterface user)
    {
        try
        {
            serverModel.banUser(roomId, user);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void makeModerator(UserInterface user, int roomId)
    {
        try
        {
            serverModel.makeModerator(user, roomId);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addChatterToRoom(UserInterface user, Room room) throws RemoteException
    {
        serverModel.addChatterToRoom(user, room);
    }

    @Override
    public void leaveRoom(String viaId, int roomId)
    {
        try
        {
            serverModel.leaveRoom(viaId, roomId);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }
}
