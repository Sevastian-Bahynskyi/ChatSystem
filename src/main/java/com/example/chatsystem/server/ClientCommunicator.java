package com.example.chatsystem.server;

import com.example.chatsystem.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class ClientCommunicator implements Runnable
{
    private final Socket socket;
    private final UDPBroadcaster broadcaster;
    private final Gson gson;
    private Data data;

    public ClientCommunicator(Socket socket, UDPBroadcaster broadcaster, Data data) throws IOException
    {
        this.broadcaster = broadcaster;
        this.socket = socket;
        this.gson = new GsonBuilder().registerTypeAdapter(Image.class, new ImageAdapter()).create();
        this.data = data;
    }

    private void communicate() throws IOException
    {
        try
        {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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
                        out.println("message?");
                        String json = in.readLine();
                        Message message = gson.fromJson(json, Message.class);
                        data.getMessages().add(message);
                        broadcaster.broadcast(json);
                    }
                    case "prepare to get user" -> {
                        out.println("user?");
                        String json = in.readLine();
                        User user = gson.fromJson(json, User.class);
                        if(data.addUser(user))
                            out.println("user is approved");
                        else out.println("user is not approved");

                        out.println("prepare to get messages");
                        if(!in.readLine().equals("messages?"))
                            return;
                        json = gson.toJson(data.getMessages());
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
