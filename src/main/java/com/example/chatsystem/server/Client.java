package com.example.chatsystem.server;

import com.example.chatsystem.model.Data;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.ModelManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

public class Client
{
    public static void main(String[] args) throws IOException
    {
        ServerModel client = new ClientImplementation(new ModelManager());
        client.connect("localhost", 8080, "230.0.0.0", 8888);

        client.addPropertyChangeListener(evt -> System.out.println(evt.getNewValue()));

        client.login("Sevastian", "Fneoiwfge23");
        client.sendMessage(new Message("iruehuieruibgge", Data.getInstance().getUsers().get(0)));
    }
}
