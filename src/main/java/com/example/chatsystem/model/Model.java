package com.example.chatsystem.model;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface Model
{
    Message addMessage(String message) throws IOException;
    void addPropertyChangeListener(PropertyChangeListener listener);

    void login(String viaID, String username, String password) throws IOException, InterruptedException;

    void register(String VIAid, String username, String password, String imageUrl) throws IOException;

    ArrayList<Message> getMessages(int channelId);
    ArrayList<Channel> getChannels(int roomId);

    UserInterface getUser();

    void disconnect() throws IOException;

    ArrayList<UserInterface> getUserList() throws IOException;

    int getChannelId();

    int getRoomId();

    void deleteMessage(int id) throws IOException;

    void editMessage(int id, String message) throws IOException;

    void createChannel(String channelName) throws IOException;

    boolean editChannel(int id, String newChannelName) throws IOException, SQLException;

    void deleteChannel(int id);

    boolean isModerator(int channelId) throws RemoteException;
    void loadEverything();

    void editRoom(String roomName, String roomCode, String imageUrl);

    boolean isModeratorInRoom(int roomId);
}
