package com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Channel;

import java.sql.SQLException;

public class TestDBManager
{
    public static void main(String[] args) throws SQLException
    {
        ChannelDBManager channelDBManager = new ChannelDBManager();
        Channel channel = channelDBManager.getChannelById(1);
        System.out.println(channel);
    }
}
