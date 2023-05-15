package com.example.chatsystem.model;

import com.example.chatsystem.model.DatabaseManagers.*;

import java.io.Serializable;
import java.rmi.Remote;
import java.sql.SQLException;
import java.util.ArrayList;

public class Data
{
    private ChatterDBManager chatterDBManager;
    private ChannelDBManager channelDBManager;
    private RoomDBManager roomDBManager;
    private ModeratorDBManager moderatorDBManager;
    private MessageDBManager messageDBManager;

    private static String defaultImageUrl = "/com/example/chatsystem/images/default_user_avatar.png";
    private static Data instance;

    private Data() throws SQLException
    {
        this.channelDBManager = new ChannelDBManager();
        this.messageDBManager = new MessageDBManager();
        this.chatterDBManager = new ChatterDBManager();
        this.moderatorDBManager = new ModeratorDBManager();
        this.roomDBManager = new RoomDBManager();
    }

    public static synchronized Data getInstance() throws SQLException
    {
        if(instance == null)
            instance = new Data();
        return instance;
    }

    public ChannelDBManager getChannelDBManager()
    {
        return channelDBManager;
    }

    public ChatterDBManager getChatterDBManager()
    {
        return chatterDBManager;
    }

    public MessageDBManager getMessageDBManager()
    {
        return messageDBManager;
    }

    public ModeratorDBManager getModeratorDBManager()
    {
        return moderatorDBManager;
    }

    public RoomDBManager getRoomDBManager()
    {
        return roomDBManager;
    }

    public static String getDefaultImageUrl()
    {
        return defaultImageUrl;
    }

}
