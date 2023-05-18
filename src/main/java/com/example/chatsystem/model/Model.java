package com.example.chatsystem.model;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface Model
{
    Message addMessage(String message) throws IOException;
    void addPropertyChangeListener(PropertyChangeListener listener);

    void login(String viaID, String username, String password) throws IOException, InterruptedException;

    void register(String VIAid, String username, String password, String imageUrl) throws IOException;

    ArrayList<Message> getMessages(int channelId) throws IOException;

    UserInterface getUser();

    void disconnect() throws IOException;

    ArrayList<UserInterface> getUserList() throws IOException;

    int getChannelId();

    int getRoomId();

    void deleteMessage(int id) throws IOException;

    void editMessage(int id, String message) throws IOException;

    void createChannel(String channelName) throws IOException;

    void editChannel(int id, String newChannelName) throws IOException, SQLException;
}
