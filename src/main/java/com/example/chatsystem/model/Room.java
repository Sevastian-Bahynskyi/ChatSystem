package com.example.chatsystem.model;

import javafx.scene.image.Image;

import java.io.Serializable;
import java.util.Objects;

public class Room implements Serializable
{
    public static final int MIN_ROOM_NAME_LENGTH = 4;
    public static final int MAX_ROOM_NAME_LENGTH = 20;
    public static final int MIN_ROOM_CODE_LENGTH = 6;
    public static final int MAX_ROOM_CODE_LENGTH = 20;
    private int id;
    private String name;
    private String code;
    private String imageUrl;



    public Room(int id, String name, String code)
    {
        this.id = id;
        validateRoomName(name);
        validateRoomCode(code);
        this.name = name;
        this.code = code;
    }

    public Room(String name, String code)
    {
        validateRoomName(name);
        validateRoomCode(code);
        this.name = name;
        this.code = code;
    }

    public Image getImage()
    {
        if(imageUrl == null)
            return null;
        return new Image(getClass().getResourceAsStream(imageUrl));
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getCode()
    {
        return code;
    }

    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    private void validateRoomName(String name)
    {
        if(name.length() < MIN_ROOM_NAME_LENGTH || name.length() > MAX_ROOM_NAME_LENGTH)
            throw new IllegalArgumentException("Room name should have length from " + MIN_ROOM_NAME_LENGTH +
                    " to " + MAX_ROOM_NAME_LENGTH + ".");

        for (int i = 0; i < name.length(); i++)
        {
            if(!Character.isDigit(name.charAt(i)) && !Character.isAlphabetic(name.charAt(i)))
                throw new IllegalArgumentException("Room contains prohibited characters.");
        }
    }


    private void validateRoomCode(String code)
    {
        if(code.length() < MIN_ROOM_CODE_LENGTH || code.length() > MAX_ROOM_CODE_LENGTH)
            throw new IllegalArgumentException("Room code should have length from " + MIN_ROOM_CODE_LENGTH +
                    " to " + MAX_ROOM_CODE_LENGTH + ".");

        for (int i = 0; i < code.length(); i++)
        {
            if(!Character.isDigit(code.charAt(i)) && !Character.isAlphabetic(code.charAt(i)))
                throw new IllegalArgumentException("Room contains prohibited characters.");
        }
    }

    public String toString()
    {
        return "Id: " + id + "; name: " + name + "; code: " + code;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Room room)) return false;
        return id == room.id;
    }
}
