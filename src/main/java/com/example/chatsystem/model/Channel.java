package com.example.chatsystem.model;

import java.util.ArrayList;

public class Channel
{
  private Data data;
  private int id;
  private String name;
  private int roomId;
  public Channel(int id, String name, int roomId)
  {
    data = Data.getInstance();
    this.id = id;
    this.name = name;
    this.roomId = roomId;
  }
  public ArrayList<Message> getMessages()
  {
    return data.getMessages();
    //Obviously, we need to modify data, this returns all the messages in the system
  }
  public int getId()
  {
    return id;
  }
  public int getRoomId()
  {
    return roomId;
  }
  public String getName()
  {
    return name;
  }
  public String setName(String newName)
  {
    name = newName;
    return getName();
  }
}
