package com.example.chatsystem.viewmodel.ChatViewModelDelegates;

import com.example.chatsystem.model.Channel;

import java.util.ArrayList;

public class ChannelHandler
{
  private ChatViewModel c;

  public ChannelHandler(ChatViewModel c)
  {
    this.c = c;
  }

  public boolean loadChannelsByRoomIndex(int roomIndex)
  {
    ArrayList<Channel> channels = null;
    if(roomIndex == -1)
      channels = c.model.getChannelsInRoom(-1);
    else
    {
      channels = c.model.getChannelsInRoom(c.roomList.get(roomIndex).getId());
    }

    if(channels == null)
      return false;

    c.support.firePropertyChange("update user list", null, c.model.getUserList());
    c.support.firePropertyChange("clear channels", null, true);
    c.channelList.clear();
    c.channelList.addAll(channels);
    for (var ch:channels)
    {
      c.support.firePropertyChange("new channel", null, ch);
    }

    if(!c.channelList.isEmpty())
      c.loadMessagesByChannelIndex(c.channelList.size() - 1);

    return true;
  }

  public void addChannel(String channelName)
  {
    c.model.createChannel(channelName);
  }

  public Channel getChannelByIndex(int index)
  {
    return c.channelList.get(index);
  }

  public void editChannel(String oldChannelName, String newChannelName)
  {
    for (var ch:c.channelList)
    {
      if(ch.getName().equals(oldChannelName) && c.model.editChannel(ch.getId(), newChannelName))
      {
        ch.setName(newChannelName);
      }
    }
  }

  public void deleteChannel(int indexOfChannelToChange)
  {
    c.model.deleteChannel(c.channelList.get(indexOfChannelToChange).getId());
  }
}
