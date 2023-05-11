package com.example.chatsystem.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable
{
    private ArrayList<UserInterface> users;
    private ArrayList<Message> messages;

    private static String defaultImageUrl = "/com/example/chatsystem/images/default_user_avatar.png";
    private static Data instance;

    private Data()
    {
        users = new ArrayList<>();
        users.add(new Chatter("111111", "BobBobson", "bobspass"));
        users.add(new Chatter("222222", "Sevastian", "mypass123"));
        messages = new ArrayList<>();
    }

    public static synchronized Data getInstance()
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
