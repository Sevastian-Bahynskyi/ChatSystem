package com.example.chatsystem.viewmodel.ChatViewModelDelegates;

import com.example.chatsystem.model.Room;

public class RoomHandler
{
  private ChatViewModel c;

  public RoomHandler(ChatViewModel c)
  {
    this.c = c;
  }

  public void joinRoom(Room room)
  {
    c.model.joinRoom(room);
  }

  public int getRoomIndex(int id)
  {
    for (var r:c.roomList)
    {
      if(r.getId() == id)
        return c.roomList.indexOf(r);
    }
    return -1;
  }

  public void leaveRoom()
  {
    c.model.leaveRoom();
  }
}
