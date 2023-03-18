package com.example.chatsystem.model;

import java.io.IOException;

public interface Model
{
    void setUser(String username, String password) throws IOException;
    void addMessage(String message) throws IOException;
}
