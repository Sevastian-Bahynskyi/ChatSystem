package com.example.chatsystem.viewmodel.ChatViewModelDelegates;

import com.example.chatsystem.model.Moderator;
import com.example.chatsystem.model.UserInterface;

import java.rmi.RemoteException;

public class UserHandler
{
  private ChatViewModel c;

  public UserHandler(ChatViewModel c)
  {
    this.c = c;
  }

  public boolean isModerator(String roomId, int channelId)
  {
    try
    {
      return c.model.isModerator(roomId, channelId);
    } catch (RemoteException e)
    {
      throw new RuntimeException(e);
    }
  }
  public boolean isModeratorInRoom(int roomId)
  {
    return c.model.isModeratorInRoom(roomId);
  }

  public boolean amIModerator()
  {
    return c.model.getUser().isModerator();
  }

  public void banUser(UserInterface user)
  {
    c.model.banUser(user);
  }

  public void makeModerator(UserInterface user)
  {
    user = new Moderator(user);
    c.model.makeModerator(user);
  }

}
