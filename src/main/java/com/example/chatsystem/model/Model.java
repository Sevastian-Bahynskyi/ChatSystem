package com.example.chatsystem.model;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

public interface Model
{
    void addMessage(String message) throws IOException;
    void addPropertyChangeListener(PropertyChangeListener listener);

    void login(String viaID, String username, String password) throws IOException;

    void register(String VIAid, String username, String password, String imageUrl) throws IOException;

    ArrayList<Message> getMessages();

    UserInterface getUser();

    void disconnect() throws IOException;

    ArrayList<UserInterface> getUserList() throws IOException;

    int getChannelId();

    int getRoomId();
}
