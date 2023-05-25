package com.example.chatsystem.server.client;

import com.example.chatsystem.model.*;
import com.example.chatsystem.server.shared.ServerModel;
import dk.via.remote.observer.RemotePropertyChangeListener;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Client implements Runnable, ServerModel
{
    public final int port;
    private ServerModel serverModel;
    private Listener listener;
    private Registry registry;
    private ModelManager model;

    public Client(int port, ModelManager modelManager)
    {
        // todo add file log
        this.port = port;
        model = modelManager;
    }

    @Override
    public void run()
    {
        try
        {
            registry = LocateRegistry.getRegistry(port);
            serverModel = (ServerModel) registry.lookup("ServerModel");
            listener = new Listener(serverModel, model);
            serverModel.addPropertyChangeListener(listener);
        } catch (RemoteException | NotBoundException e)
        {
            e.printStackTrace();
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void disconnect()
    {
        try
        {
            registry.unbind("ServerModel");
            System.exit(0);
        } catch (RemoteException | NotBoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ServerModel getServerModel()
    {
        return serverModel;
    }

    @Override
    public void sendMessage(Message message)
    {
        try
        {
            listener.sendMessage(message);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserInterface login(String username, String password)
    {
        try
        {
            return listener.login(username, password);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addPropertyChangeListener(RemotePropertyChangeListener<Integer> listener) throws RemoteException
    {
        serverModel.addPropertyChangeListener(listener);
    }

    @Override
    public void firePropertyChange(String propertyName, Integer oldValue, Integer newValue) throws RemoteException
    {
        listener.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public UserInterface register(String VIAid, String username, String password, String imageUrl)
    {
        return listener.register(VIAid, username, password, imageUrl);
    }

    @Override
    public ArrayList<UserInterface> getUserList()
    {
        try
        {
            return listener.getUserList();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Data getData() throws RemoteException
    {
        return listener.getData();
    }

    @Override
    public List<Message> getAllMessagesByChannel(int channelID)
    {
        try
        {
            return listener.getAllMessagesByChannel(channelID);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel getChannel(int id)
    {
        try
        {
            return listener.getChannel(id);
        } catch (IOException | SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Room getRoom(int id)
    {
        try
        {
            return listener.getRoom(id);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void editMessage(int id, String message, int channelID)
    {
        try
        {
            listener.editMessage(id, message, channelID);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteMessage(int id, int channelID)
    {
        try
        {
            listener.deleteMessage(id, channelID);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createChannel(String channelName, int roomId)
    {
        try
        {
            listener.createChannel(channelName, roomId);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Channel> getChannelsInTheRoom(int roomId)
    {
        try
        {
            return listener.getChannelsInTheRoom(roomId);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void editChannel(int id, String newChannelName)
    {
        try
        {
            listener.editChannel(id, newChannelName);
        } catch (IOException | SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteChannel(int id)
    {
        listener.deleteChannel(id);
    }

    @Override
    public boolean isModerator(String chatterId, int roomId) throws RemoteException
    {
        return listener.isModerator(chatterId, roomId);
    }

    @Override
    public void createRoom(UserInterface user, String name, String code) throws RemoteException
    {
        listener.createRoom(user, name, code);
    }

    @Override
    public ArrayList<Room> getRooms()
    {
        return listener.getRooms();
    }

    @Override
    public void editRoom(int roomId, String roomName, String roomCode, String imageUrl)
    {
        try
        {
            listener.editRoom(roomId, roomName, roomCode, imageUrl);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isModeratorInRoom(String viaId, int roomId) throws RemoteException
    {
        return listener.isModeratorInRoom(viaId, roomId);
    }

    @Override
    public ArrayList<UserInterface> getUserListInRoom(int roomId)
    {
        return listener.getUserListInRoom(roomId);
    }

    @Override
    public void banUser(int roomId, UserInterface user)
    {
        listener.banUser(roomId, user);
    }

    @Override
    public void makeModerator(UserInterface user, int roomId)
    {
        listener.makeModerator(user, roomId);
    }

    @Override
    public void addChatterToRoom(UserInterface user, Room room)
    {
        try
        {
            listener.addChatterToRoom(user, room);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void leaveRoom(String viaId, int roomId)
    {
        listener.leaveRoom(viaId, roomId);
    }
}
