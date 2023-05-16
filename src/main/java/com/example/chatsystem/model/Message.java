package com.example.chatsystem.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class Message implements Serializable
{
    private int id;
    private String message;
    private Timestamp time;
    private UserInterface user;

    private int channel_id;
    // message should be id,Message,timestamp,chatter_id,channel_id

    public Message( int id,String message, Timestamp date,UserInterface user,int channel_id)
    {
        this.id = id;
        this.message = message;
        this.time = date;
        this.user = user;
        this.channel_id = channel_id;
    }

    public Message(int id, String message, UserInterface user, int channel_id)
    {
        this.id = id;
        this.message = message;
        this.time = Timestamp.valueOf(LocalDateTime.now());
        this.user = user;
        this.channel_id = channel_id;
    }

    public Message(String message, UserInterface user, int channel_id)
    {
        this.message = message;
        this.time = Timestamp.valueOf(LocalDateTime.now());
        this.user = user;
        this.channel_id = channel_id;
    }

    public Message()
    {
        this.message = "dummy";
        time = Timestamp.valueOf(LocalDateTime.now());
        //empty constructor to make dummy message
    }

    public String getTime()
    {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(time);
    }

    public Timestamp getTimeStamp()
    {
        return time;
    }

    public int getId()
    {
        return id;
    }

    public int getChannelId()
    {
        return channel_id;
    }



    public String getMessage()
    {
        return message;
    }

    public UserInterface getUser()
    {
        return user;
    }

    public String getUserId()
    {
        return user.getViaId();
    }

    @Override
    public String toString()
    {
        return "Message: " + message + "\nTime: " + getTime() +
                "\nUsername: " + user.getUsername() + "\nImage url: "  +  user.getImageUrl();
    }

    public String getMetadata()
    {
        String metadeta = new String();
        metadeta = user.getUsername() + " sent at: " + getTime();
        return metadeta;
    }
}
