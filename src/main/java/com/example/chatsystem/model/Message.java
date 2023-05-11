package com.example.chatsystem.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable
{
    private String message;
    private Date time;
    private UserInterface user;


    public Message(String message, UserInterface user)
    {
        this.message = message;
        time = new Date();
        this.user = user;
    }

    public String getTime()
    {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(time);
    }

    public String getMessage()
    {
        return message;
    }

    public UserInterface getUser()
    {
        return user;
    }

    @Override
    public String toString()
    {
        return "Message: " + message + "\nTime: " + getTime() +
                "\nUsername: " + user.getUsername() + "\nImage url: "  +  user.getImageUrl();
    }
}
