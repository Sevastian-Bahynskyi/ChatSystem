package com.example.chatsystem.viewmodel.ChatViewModelDelegates;

import com.example.chatsystem.model.Channel;

import java.util.ArrayList;

public class ChannelHandler
{
  private ChatViewModel vm;

  public ChannelHandler(ChatViewModel c)
  {
    this.vm = c;
  }

  public boolean loadChannelsByRoomIndex(int roomIndex)
  {
    ArrayList<Channel> channels = null;
    if(roomIndex == -1)
      channels = vm.model.getChannelsInRoom(-1);
    else
    {
      channels = vm.model.getChannelsInRoom(vm.roomList.get(roomIndex).getId());
    }

    if(channels == null)
      return false;

    vm.support.firePropertyChange("update user list", null, vm.model.getUserList());
    vm.support.firePropertyChange("clear channels", null, true);
    vm.channelList.clear();
    vm.channelList.addAll(channels);
    for (var ch:channels)
    {
      vm.support.firePropertyChange("new channel", null, ch);
    }

    if(!vm.channelList.isEmpty())
      vm.loadMessagesByChannelIndex(vm.channelList.size() - 1);

    return true;
  }

  public void addChannel(String channelName)
  {
    vm.model.createChannel(channelName);
    vm.support.firePropertyChange("clear messages", null, true);
  }

  public Channel getChannelByIndex(int index)
  {
    return vm.channelList.get(index);
  }

  public void editChannel(String oldChannelName, String newChannelName)
  {
    for (var ch: vm.channelList)
    {
      if(ch.getName().equals(oldChannelName) && vm.model.editChannel(ch.getId(), newChannelName))
      {
        ch.setName(newChannelName);
      }
      System.out.println(ch + ", ");
    }

//    if(!vm.channelList.isEmpty())
//      loadChannelsByRoomIndex(vm.getRoomIndex(vm.channelList.get(0).getRoomId()));
  }

  public void deleteChannel(int indexOfChannelToChange)
  {
    vm.model.deleteChannel(vm.channelList.get(indexOfChannelToChange).getId());
  }
}
