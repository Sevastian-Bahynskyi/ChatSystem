package com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Channel;
import com.example.chatsystem.model.Chatter;
import com.example.chatsystem.model.Message;

import java.sql.SQLException;

public class TestDBManager
{
    public static void main(String[] args) throws SQLException
    {
       MessageDBManager messageDBManager = new MessageDBManager();
       messageDBManager.createMessage(new Message("rgqggq", new Chatter("111111", "Dumy_1", "password_1"), 2));
//       messageDBManager.readMessage(0);
    }
}
