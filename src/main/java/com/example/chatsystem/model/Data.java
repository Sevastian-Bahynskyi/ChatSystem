package com.example.chatsystem.model;

import java.util.ArrayList;

public class Data
{
    public ArrayList<User> users;
    public ArrayList<Message> messages;
    private static Data instance;

    private Data()
    {
        users = new ArrayList<>();
        messages = new ArrayList<>();
    }

    public static synchronized Data getInstance()
    {
        if(instance == null)
            instance = new Data();
        return instance;
    }
}
