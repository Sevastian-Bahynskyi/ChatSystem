package com.example.chatsystem.model;

import java.util.ArrayList;

public class Channel
{
  private ArrayList<Message> messages;
  private int id;
  private String name;
  private int roomId;
  public Channel(int id, String name, int roomId)
  {
    messages = new ArrayList<>();
    this.id = id;
    this.name = name;
    this.roomId = roomId;
  }
  public ArrayList<Message> getMessages()
  {
    return messages;
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
  public String toString()
  {
    return "id: " + id + ", name: " + name + ", room id: " + roomId;
  }
}
