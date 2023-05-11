package com.example.chatsystem.model;

import javafx.scene.image.Image;

import java.io.Serializable;

public class Chatter implements UserInterface,Serializable
{
    private int VIAid;
    private String username;
    private String password;
    private String imageUrl;
    private final int CHARACTER_NUMBER_OFF_PASSWORD = 8;
    private final int CHARACTER_NUMBER_OFF_USERNAME = 4;

    public Chatter(int VIAid, String username, String password)
    {
        if(!(validateUsername(username) && validatePassword(password)))
            return;
        this.VIAid= VIAid;
        this.username = username;
        this.password = password;
        this.imageUrl = Data.getDefaultImageUrl();
    }

    @Override
    public Image getImage()
    {
        return new Image(getClass().getResourceAsStream(imageUrl));
    }


    private boolean validateUsername(String username)
    {
        if(username == null || username.length() < CHARACTER_NUMBER_OFF_USERNAME)
            throw new IllegalArgumentException("Username should contain at least 8 characters.");

        boolean lowercase = false;
        boolean digit = false;

        for(int i = 0; i < username.length(); i++) {
            char ch = username.charAt(i);
            lowercase = Character.isLowerCase(ch);
            digit = Character.isDigit(ch);
        }
        if (!(lowercase || digit)) {
            throw new IllegalArgumentException("Username needs both lower case letters and digits.");
        }
        return true;
    }

    private boolean validatePassword(String password)
    {
        if(password == null || password.length() < CHARACTER_NUMBER_OFF_PASSWORD)
            throw new IllegalArgumentException("Password should contain at least 8 characters.");

        boolean lowercase = false;
        boolean digit = false;
        for(int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            lowercase = Character.isLowerCase(ch);
            digit = Character.isDigit(ch);
        }
        if (!(lowercase || digit)) {
            throw new IllegalArgumentException("Password needs both lower case letters and digits.");
        }

        return true;
    }
    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public String getPassword()
    {
        return password;
    }

    @Override
    public String getImageUrl()
    {
        return imageUrl;
    }

    @Override
    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null || obj.getClass() != getClass())
            return false;

        Chatter u = (Chatter) obj;
        return u.username.equals(username) && u.password.equals(password);
    }
}

