package com.example.chatsystem.server;

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

    public ClientCommunicator(Socket socket, UDPBroadcaster broadcaster, Data data) throws IOException
    {
        this.broadcaster = broadcaster;
        this.socket = socket;
        this.gson = new GsonBuilder().registerTypeAdapter(Image.class, new ImageAdapter()).create();
        this.data = data;
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
                        System.out.println("Client trying to connect.");
                    }
                    case "disconnect" -> {
                        out.println("Disconnect");
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

    private boolean processUser(boolean isRegistered) throws IOException
    {
        out.println("user?");
        String json = in.readLine();
        User user = gson.fromJson(json, User.class);

        if(data.isUserRegistered(user) == isRegistered)
        {
            out.println("user is approved");
            return true; // user is processed everything is fine
        }
        else
        {
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
