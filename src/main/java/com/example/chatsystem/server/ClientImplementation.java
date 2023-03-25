package com.example.chatsystem.server;

import com.example.chatsystem.model.ImageAdapter;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.Model;
import com.example.chatsystem.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.scene.image.Image;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientImplementation implements ServerModel
{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Gson gson;
    private PropertyChangeSupport support;
    private MessageListener listener;

    public ClientImplementation(String host, int port, String groupAddress, int groupPort) throws IOException, InterruptedException
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

    public List<Object> login(String username, String password) throws IOException
    {
        var res = new ArrayList<>(List.of());
        User user = new User(username, password);
        sendAndProcessUser(user, false);

        if(!in.readLine().equals("user is approved"))
            throw new IllegalArgumentException("User with such username and password is not found!\nTry to register first.");

        String json = in.readLine();
        user = gson.fromJson(json, User.class);

        res.add(user);
        res.add(sendMessagesToServer());
        return res;
    }


    public List<Object> register(String username, String password, String imageUrl) throws IOException
    {
        var res = new ArrayList<>(List.of());
        User user = new User(username, password);
        if(imageUrl != null)
            user.setImageUrl(imageUrl);
        sendAndProcessUser(user, true);

        if(!in.readLine().equals("user is approved"))
            throw new IllegalArgumentException("User with such username and password is already exist!\nTry to login.");
        res.add(user);
        res.add(sendMessagesToServer());
        return res;
    }

    @Override
    public ArrayList<User> getUserList() throws IOException
    {
        out.println("User list?");
        String jsonUsers = in.readLine();
        ArrayList<User> users = gson.fromJson(jsonUsers, new TypeToken<ArrayList<User>>(){}.getType());
        return users;
    }

    // tries to send messages
    // returns messages if request that was got from server is not appropriate return null
    private ArrayList<Message> sendMessagesToServer() throws IOException
    {
        if(in.readLine().equals("prepare to get messages"))
        {
            out.println("messages?");
            String messagesJson = in.readLine();
            ArrayList<Message> messages = gson.fromJson(messagesJson, new TypeToken<ArrayList<Message>>(){}.getType());
            return messages;
        }
        else
            throw new IllegalArgumentException("Server or client is not connected!");
    }

    private void sendAndProcessUser(User user, boolean isActionRegister) throws IOException
    {
        if(isActionRegister)
            out.println("prepare to register user");
        else
            out.println("prepare to login user");
        String response = in.readLine();
        if(!response.equals("user?"))
            throw new IllegalArgumentException("Server or client is not connected.");
        String json = gson.toJson(user);
        out.println(json);
    }

    public void connect() throws IOException
    {
        out.println("connect");
        String connectResponse = in.readLine();
        if(!connectResponse.equals("Connect"))
        {
            closeConnection();
        }
    }

    public void disconnect() throws IOException
    {
        out.println("disconnect");
        String connectResponse = in.readLine();
        if(connectResponse.equals("Disconnect"))
        {
            out.println("Client " + socket.getInetAddress().getHostName() + ":" + socket.getPort() + " was disconnected to the server.");
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
        listener.close();
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
