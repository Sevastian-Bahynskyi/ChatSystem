package com.example.chatsystem.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable
{
    private ArrayList<Chatter> users;
    private ArrayList<Message> messages;

    private static String defaultImageUrl = "/com/example/chatsystem/images/default_user_avatar.png";
    private static Data instance;

    private Data()
    {
        users = new ArrayList<>();
        users.add(new Chatter("BobBobson", "bobspass"));
        users.add(new Chatter("Sevastian", "mypass123"));
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

    public synchronized ArrayList<Chatter> getUsers()
    {
        return users;
    }


    public boolean isUserRegistered(Chatter user)
    {
        return users.contains(user);
    }

    public synchronized ArrayList<Message> getMessages()
    {
        return messages;
    }

}
