package com.example.chatsystem.model.ModelManagerDelegates;

import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.ModelManager;

import java.util.ArrayList;

public class MessageHandler extends ModelManager
{
    public void sendOthersMessage(Message message)
    {
        currentMessage = message;
        if(!message.getUser().equals(user))
            support.firePropertyChange("new message", null, message);
    }

    @Override
    public void deleteMessage(int id)
    {
        server.deleteMessage(id, channel.getId());
    }

    @Override
    public void editMessage(int id, String message)
    {
        server.editMessage(id, message, channel.getId());
    }

    public void reloadMessages(ArrayList<Message> messages)
    {
        support.firePropertyChange("reload messages", null, messages);
    }

    public void reloadMessage(Message mes)
    {
        support.firePropertyChange("reload message", null, mes);
    }

    public Message addMessage(String message)
    {
        Message mes = new Message(message, user, channel.getId());
        server.sendMessage(mes);
        return currentMessage;
    }
}
