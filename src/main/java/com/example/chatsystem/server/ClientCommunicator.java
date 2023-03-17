package com.example.chatsystem.server;

import com.example.chatsystem.model.Data;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.Model;
import com.example.chatsystem.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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
        this.gson = new Gson();
        this.data = data;
    }

    private void communicate() throws IOException
    {
        try
        {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while(true)
            {
                String request = in.readLine();

                switch (request)
                {
                    case "connect" -> {
                        out.println("Connect");
                    }
                    case "disconnect" -> {
                        out.println("Disconnect");
                    }
                    case "prepare to get message" -> {
                        out.println("message?");
                        String json = in.readLine();
                        Message message = gson.fromJson(json, Message.class);
                        data.messages.add(message);
                        broadcaster.broadcast(json);
                    }
                    case "prepare to get user" -> {
                        out.println("user?");
                        String json = in.readLine();
                        User user = gson.fromJson(json, User.class);
                        data.users.add(user);
                    }
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
