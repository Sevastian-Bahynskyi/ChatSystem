package com.example.chatsystem.model;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Objects;

public class Data
{
    private ArrayList<User> users;
    private ArrayList<Message> messages;

    private static String defaultImageUrl = "/com/example/chatsystem/images/default_user_avatar.png";
    private static Data instance;

    private Data()
    {
        users = new ArrayList<>();
        users.add(new User("Sevastian", "a2345678"));
        users.add(new User("Derek", "a2345678"));
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

    public synchronized ArrayList<User> getUsers()
    {
        return users;
    }


    public boolean isUserRegistered(User user)
    {
        return users.contains(user);
    }

    public synchronized ArrayList<Message> getMessages()
    {
        return messages;
    }
}
