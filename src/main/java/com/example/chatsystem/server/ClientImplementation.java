package com.example.chatsystem.server;

import com.example.chatsystem.model.ImageAdapter;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.Model;
import com.example.chatsystem.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.image.Image;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientImplementation implements ServerModel
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

    public User login(String username, String password) throws IOException
    {
        User user = new User(username, password);
        out.println("prepare to get user");
        String response = in.readLine();
        if(!response.equals("user?"))
            return null;
        String json = gson.toJson(user);
        out.println(json);
        return user;
    }

    public void connect(String host, int port, String groupAddress, int groupPort) throws IOException
    {
        try
        {
            socket = new Socket(host, port);
        }catch (Exception e)
        {
            System.err.println("Server is not connected.");
            return;
        }
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        gson = new Gson();
        gson = new GsonBuilder().registerTypeAdapter(Image.class, new ImageAdapter()).create();
        support = new PropertyChangeSupport(this);
        listener = new MessageListener(this, groupAddress, groupPort);
        Thread thread = new Thread(listener);
        thread.start();


        out.println("connect");
        String connectResponse = in.readLine();
        if(!connectResponse.equals("Connect"))
        {
            closeConnection();
        }

        System.out.println("Client was connected");
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

    @Override
    public boolean isConnected()
    {
        return socket != null && socket.isConnected();
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
        System.out.println(message.toString());
        support.firePropertyChange("new message", null, message);
    }
}
