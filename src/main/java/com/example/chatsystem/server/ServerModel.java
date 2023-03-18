package com.example.chatsystem.server;

import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.User;
import com.google.gson.Gson;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public interface ServerModel
{
    void sendMessage(Message message) throws IOException;

    User login(String username, String password) throws IOException;

    void connect(String host, int port, String groupAddress, int groupPort) throws IOException;

    void disconnect() throws IOException;

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    boolean isConnected();
}
