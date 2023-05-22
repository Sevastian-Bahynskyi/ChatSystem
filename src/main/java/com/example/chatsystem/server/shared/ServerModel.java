package com.example.chatsystem.server.shared;

import com.example.chatsystem.model.*;
import dk.via.remote.observer.RemotePropertyChangeListener;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ServerModel extends Remote
{
    void sendMessage(Message message) throws RemoteException, IOException;

    UserInterface login(String username, String password) throws RemoteException, IOException;

    void addPropertyChangeListener(RemotePropertyChangeListener<Integer> listener) throws RemoteException;

    void firePropertyChange(String propertyName, Integer oldValue, Integer newValue) throws RemoteException;

    UserInterface register(String VIAid, String username, String password, String imageUrl) throws RemoteException, IOException;

    ArrayList<UserInterface> getUserList() throws RemoteException, IOException;
    ArrayList<UserInterface> getUserListInRoom(int roomId) throws RemoteException, IOException;

    Data getData() throws RemoteException;

    List<Message> getAllMessagesByChannel(int channelID) throws RemoteException, IOException;
    Channel getChannel(int id) throws RemoteException, IOException, SQLException;
    Room getRoom(int id) throws RemoteException, IOException;

    void editMessage(int id, String message, int channelID) throws RemoteException, IOException;

    void deleteMessage(int id, int channelID) throws RemoteException, IOException;

    void createChannel(String channelName, int roomId) throws RemoteException, IOException;

    ArrayList<Channel> getChannelsInTheRoom(int roomId) throws RemoteException, IOException;

    void editChannel(int id, String newChannelName) throws RemoteException, IOException, SQLException;

    void deleteChannel(int id) throws RemoteException, IOException;

    boolean isModerator(String chatterId, int channelId) throws RemoteException;

    void createRoom(UserInterface user, String name, String code) throws RemoteException;

    ArrayList<Room> getRooms() throws RemoteException;

    void editRoom(int roomId, String roomName, String roomCode, String imageUrl) throws RemoteException;

    boolean isModeratorInRoom(String viaId, int roomId) throws RemoteException;

    void banUser(int roomId, UserInterface user) throws RemoteException;

    void makeModerator(UserInterface user, int roomId) throws RemoteException;

    void addChatterToRoom(UserInterface user, Room room) throws RemoteException;
}