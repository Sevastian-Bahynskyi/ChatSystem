package com.example.chatsystem.model.ModelManagerDelegates;

import com.example.chatsystem.model.Message;

import java.util.ArrayList;

public class MessageHandler
{
    private ModelManager m;
    public MessageHandler(ModelManager m)
    {
        this.m = m;
    }
    public void sendOthersMessage(Message message)
    {
        m.currentMessage = message;
        if(!message.getUser().equals(m.user))
            m.support.firePropertyChange("new message", null, message);
        // if the user who wrote the message is not the one who get the notification - notify viewmodel
        // the author's client got UI update right in the controller at the end of the onSendMessage method
    }

    public void deleteMessage(int id)
    {
        m.server.deleteMessage(id, m.channel.getId());
    }

    public void editMessage(int id, String message)
    {
        m.server.editMessage(id, message, m.channel.getId());
    }

    public void reloadMessages(ArrayList<Message> messages)
    {
        m.support.firePropertyChange("reload messages", null, messages);
    }

    public void reloadMessage(Message mes)
    {
        m.support.firePropertyChange("reload message", null, mes);
    }

    public Message addMessage(String message)
    {
        Message mes = new Message(message, m.user, m.channel.getId());
        m.server.sendMessage(mes);
        return m.currentMessage;
    }
}
