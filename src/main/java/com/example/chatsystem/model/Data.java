package com.example.chatsystem.model;

import com.example.chatsystem.model.DatabaseManagers.*;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

public class Data implements Serializable
{
    private ChatterDBManager chatterDBManager;
    private ChannelDBManager channelDBManager;
    private RoomDBManager roomDBManager;
    private ModeratorDBManager moderatorDBManager;
    private MessageDBManager messageDBManager;
    private ArrayList<UserInterface> users;
    private ArrayList<Message> messages;

    private static String defaultImageUrl = "/com/example/chatsystem/images/default_user_avatar.png";
    private static Data instance;

    private Data() throws SQLException
    {
        this.channelDBManager = new ChannelDBManager();
        this.messageDBManager = new MessageDBManager();
        this.chatterDBManager = new ChatterDBManager();
        this.moderatorDBManager = new ModeratorDBManager();
        users = new ArrayList<>();
        users.add(new Chatter("111111", "BobBobson", "bobspass"));
        users.add(new Chatter("222222", "Sevastian", "mypass123"));
        messages = new ArrayList<>();
    }

    public static synchronized Data getInstance() throws SQLException
    {
        if(instance == null)
            instance = new Data();
        return instance;
    }

    public static String getDefaultImageUrl()
    {
        return defaultImageUrl;
    }

    public synchronized ArrayList<UserInterface> getUsers()
    {
        return users;
    }


    public boolean isUserRegistered(UserInterface user)
    {
        return users.contains(user);
    }

    public synchronized ArrayList<Message> getMessages()
    {
        return messages;
    }

}
