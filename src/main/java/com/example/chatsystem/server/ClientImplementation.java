package com.example.chatsystem.server;

import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.Model;
import com.example.chatsystem.model.User;
import com.google.gson.Gson;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientImplementation
{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Gson gson;
    private PropertyChangeSupport support;
    private MessageListener listener;
    private Model model;

    public ClientImplementation(Model model) throws IOException
    {
        this.model = model; // todo -> maybe runnable
    }

    public void sendMessage(Message message) throws IOException
    {
        out.println("prepare to get message");
        String response = in.readLine();
        if(!response.equals("message?"))
            return;
        String json = gson.toJson(message);
        out.println(json);
    }

    public void login(String username, String password) throws IOException
    {
        User user = new User(username, password);
        out.println("prepare to get user");
        String response = in.readLine();
        if(!response.equals("user?"))
            return;
        String json = gson.toJson(user);
        out.println(json);
    }

    public void connect(String host, int port, String groupAddress, int groupPort) throws IOException
    {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        gson = new Gson();
        support = new PropertyChangeSupport(this);
        listener = new MessageListener(this, groupAddress, groupPort);
        Thread thread = new Thread(listener);
        thread.start();


        out.println("connect");
        String connectResponse = in.readLine();
        if(connectResponse.equals("Disconnected"))
        {
            closeConnection();
        }
    }

    public void disconnect() throws IOException
    {
        out.println("disconnect");
        String connectResponse = in.readLine();
        if(connectResponse.equals("disconnected"))
        {
            closeConnection();
        }
    }


    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        support.removePropertyChangeListener(listener);
    }

    public void closeConnection() throws IOException
    {
        socket.close();
        out.close();
        in.close();
    }

    public void receiveBroadcast(String str) throws IOException
    {
        Message message = gson.fromJson(str, Message.class);
        support.firePropertyChange("message", null, message);
    }
}
