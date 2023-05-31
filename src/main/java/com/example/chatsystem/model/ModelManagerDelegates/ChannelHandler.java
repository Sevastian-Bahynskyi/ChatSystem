package com.example.chatsystem.model.ModelManagerDelegates;

import com.example.chatsystem.model.Channel;
import com.example.chatsystem.model.Message;

import java.util.ArrayList;

public class ChannelHandler
{
    private ModelManager m;
    public ChannelHandler(ModelManager modelManager)
    {
        this.m = modelManager;
    }
    public int getChannelId()
    {
        return m.channel.getId();
    }

    public void createChannel(String channelName)
    {
        m.server.createChannel(channelName, m.room.getId());
    }

    public void receiveNewChannel(Channel channel)
    {
        this.m.channel = channel;
        m.support.firePropertyChange("new channel", null, channel);
    }

    public boolean editChannel(int id, String newChannelName)
    {
        if(m.user.isModerator())
            m.server.editChannel(id, newChannelName);
        return m.user.isModerator();
    }

    public void reloadChannel(Channel channel)
    {
        m.support.firePropertyChange("reload channel", null, channel);
    }

    public void deleteChannel(int id)
    {
        if(m.user.isModerator())
            m.server.deleteChannel(id);
        m.channel = m.channels.get(m.channels.size() - 1);
        m.support.firePropertyChange("select channel", null, m.channel);
    }

    public ArrayList<Message> getMessagesInChannel(int channelId)
    {
        if(channelId == -1)
            return m.messages;
        else {

            this.m.channel = m.server.getChannel(channelId);
            return (ArrayList<Message>) m.server.getAllMessagesByChannel(channelId);
        }
    }

    public void receiveChannelToRemove(int channelId)
    {
        m.support.firePropertyChange("delete channel", null, channelId);
    }
}
