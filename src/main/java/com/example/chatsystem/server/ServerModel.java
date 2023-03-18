package com.example.chatsystem.server;

import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.User;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;

public interface ServerModel
{
    void sendMessage(Message message) throws IOException;

    List<Object> login(String username, String password) throws IOException;

    void connect(String host, int port, String groupAddress, int groupPort) throws IOException;

    void disconnect() throws IOException;

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    boolean isConnected();
}
