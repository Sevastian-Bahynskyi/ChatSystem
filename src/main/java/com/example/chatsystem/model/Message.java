package com.example.chatsystem.model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

public class Message
{
    private String message;
    private Date time;
    private User user;


    public Message(String message, User user)
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

    @Override
    public String toString()
    {
        return "Message: " + message + "\nTime: " + time + "\nUsername: " + user.getUsername();
    }
}
