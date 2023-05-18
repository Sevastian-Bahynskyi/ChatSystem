package com.example.chatsystem.server.client;

import com.example.chatsystem.model.*;
import com.example.chatsystem.server.shared.ServerModel;
import dk.via.remote.observer.RemotePropertyChangeListener;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Client implements Runnable, ServerModel
{
    public final int port;
    private ServerModel serverModel;
    private Listener listener;
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
            Registry registry = LocateRegistry.getRegistry(port);
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

    public ServerModel getServerModel()
    {
        return serverModel;
    }

    @Override
    public void sendMessage(Message message) throws RemoteException, IOException
    {
        listener.sendMessage(message);
    }

    @Override
    public UserInterface login(String username, String password) throws RemoteException, IOException
    {
        return listener.login(username, password);
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
    public UserInterface register(String VIAid, String username, String password, String imageUrl) throws RemoteException, IOException
    {
        return listener.register(VIAid, username, password, imageUrl);
    }

    @Override
    public ArrayList<UserInterface> getUserList() throws RemoteException, IOException
    {
        return listener.getUserList();
    }

    @Override
    public Data getData() throws RemoteException
    {
        return listener.getData();
    }

    @Override
    public List<Message> getAllMessagesByChannel(int channelID) throws IOException
    {
        return listener.getAllMessagesByChannel(channelID);
    }

    @Override
    public Channel getChannel(int id) throws RemoteException, IOException, SQLException
    {
        return listener.getChannel(id);
    }

    @Override
    public Room getRoom(int id) throws RemoteException, IOException
    {
        return listener.getRoom(id);
    }

    @Override
    public void editMessage(int id, String message, int channelID) throws RemoteException, IOException
    {
        listener.editMessage(id, message, channelID);
    }

    @Override
    public void deleteMessage(int id, int channelID) throws RemoteException, IOException
    {
        listener.deleteMessage(id, channelID);
    }
}
