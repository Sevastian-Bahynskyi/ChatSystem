package com.example.chatsystem.server;

import com.example.chatsystem.log.DefaultLog;
import com.example.chatsystem.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientCommunicator implements Runnable
{
    private final Socket socket;
    private final UDPBroadcaster broadcaster;
    private final Gson gson;
    private Data data;
    private PrintWriter out;
    private BufferedReader in;
    private DefaultLog log;

    public ClientCommunicator(Socket socket, UDPBroadcaster broadcaster, Data data, DefaultLog log) throws IOException
    {
        this.broadcaster = broadcaster;
        this.socket = socket;
        this.gson = new GsonBuilder().registerTypeAdapter(Image.class, new ImageAdapter()).create();
        this.data = data;
        this.log = log;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void communicate() throws IOException
    {
        try
        {
            loop: while(true)
            {
                String request = in.readLine();

                switch (request)
                {
                    case "connect" -> {
                        out.println("Connect");
                        log.log("Client " + socket.getInetAddress() + ":" + socket.getPort() + " was connected to the server.");
                    }
                    case "disconnect" -> {
                        out.println("Disconnect");
                        log.log(in.readLine());
                        break loop;
                    }
                    case "prepare to get message" -> {
                        processMessage();
                    }
                    case "prepare to login user" -> {
                        boolean isUserApproved = processUser(true);
                        if(isUserApproved)
                            sendMessagesToClient();
                    }
                    case "prepare to register user" -> {
                        boolean isUserApproved = processUser(false);
                        if(isUserApproved)
                            sendMessagesToClient();
                    }
                    case "User list?" -> {
                        String json = gson.toJson(data.getUsers());
                        out.println(json);
                    }
                    default -> throw new IllegalArgumentException("Unknown request.");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            synchronized (broadcaster) {
            }
            socket.close();
        }
    }

    private boolean processUser(boolean isActionLogin) throws IOException
    {
        out.println("user?");
        String json = in.readLine();
        User user = gson.fromJson(json, User.class);

        if(data.isUserRegistered(user) == isActionLogin)
        {
            if(!isActionLogin)
            {
                data.getUsers().add(user);
            }
            log.log(user.getUsername() + (isActionLogin? " logged in the system." : " registered in the system."));
            out.println("user is approved");
            if(isActionLogin)
            {
                User userTemp = data.getUsers().get(data.getUsers().indexOf(user));
                String jsonTemp = gson.toJson(userTemp);
                out.println(jsonTemp);
            }
            return true; // user is processed everything is fine
        }
        else
        {
            log.log(user.getUsername() + (isActionLogin? " log is failed." : " registration is failed."));
            out.println("user is not approved");
            return false;
        }
    }

    private void processMessage() throws IOException
    {
        out.println("message?");
        String json = in.readLine();
        Message message = gson.fromJson(json, Message.class);
        data.getMessages().add(message);
        broadcaster.broadcast(json);
        log.log(message.getUser().getUsername() + " wrote new message:\n" + message.getMessage());
    }

    private void sendMessagesToClient() throws IOException
    {
        out.println("prepare to get messages");
        if(!in.readLine().equals("messages?"))
            return;
        String json = gson.toJson(data.getMessages());
        out.println(json);
    }

    public void run()
    {
        try
        {
            communicate();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
