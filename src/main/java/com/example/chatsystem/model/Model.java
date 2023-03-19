package com.example.chatsystem.model;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

public interface Model
{
    void login(String username, String password) throws IOException;

    void addMessage(String message) throws IOException;

    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void register(String username, String password) throws IOException;

    ArrayList<Message> getMessages();

    User getUser();
}
