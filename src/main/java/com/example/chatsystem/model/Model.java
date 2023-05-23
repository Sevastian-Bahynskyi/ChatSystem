package com.example.chatsystem.model;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface Model
{
    Message addMessage(String message);
    void addPropertyChangeListener(PropertyChangeListener listener);

    void login(String viaID, String username, String password);

    void register(String VIAid, String username, String password, String imageUrl);

    ArrayList<Message> getMessages(int channelId);
    ArrayList<Channel> getChannels(int roomId);

    UserInterface getUser();

    void disconnect() throws IOException;

    ArrayList<UserInterface> getUserList();

    int getChannelId();

    int getRoomId();

    void deleteMessage(int id) throws IOException;

    void editMessage(int id, String message);

    void createChannel(String channelName);

    boolean editChannel(int id, String newChannelName);

    void deleteChannel(int id);

    boolean isModerator(int channelId) throws RemoteException;
    void loadEverything();

    void editRoom(String roomName, String roomCode, String imageUrl);

    boolean isModeratorInRoom(int roomId);

    void banUser(UserInterface user);

    void makeModerator(UserInterface user);

    void joinRoom(Room room);
}
