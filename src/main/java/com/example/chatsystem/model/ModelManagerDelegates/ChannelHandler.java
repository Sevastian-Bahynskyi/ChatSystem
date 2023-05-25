package com.example.chatsystem.model.ModelManagerDelegates;

import com.example.chatsystem.model.Channel;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.ModelManager;

import java.util.ArrayList;

public class ChannelHandler extends ModelManager
{
    @Override
    public int getChannelId()
    {
        return channel.getId();
    }

    @Override
    public void createChannel(String channelName)
    {
        server.createChannel(channelName, channel.getRoomId());
    }

    public void receiveNewChannel(Channel channel)
    {
        this.channel = channel;
        support.firePropertyChange("new channel", null, channel);
    }

    @Override
    public boolean editChannel(int id, String newChannelName)
    {
        if(user.isModerator())
            server.editChannel(id, newChannelName);
        return user.isModerator();
    }

    public void reloadChannel(Channel channel)
    {
        support.firePropertyChange("reload channel", null, channel);
    }

    @Override
    public void deleteChannel(int id)
    {
        if(user.isModerator())
            server.deleteChannel(id);
    }

    public ArrayList<Message> getMessagesInChannel(int channelId)
    {
        if(channelId == -1)
            return messages;
        else {

            this.channel = server.getChannel(channelId);
            return (ArrayList<Message>) server.getAllMessagesByChannel(channelId);
        }
    }
}
