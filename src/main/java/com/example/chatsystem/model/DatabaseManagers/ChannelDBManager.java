package com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Channel;
import com.example.chatsystem.model.Data;
import com.example.chatsystem.model.Message;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ChannelDBManager
{
  private int nextId = 1;
  private int roomId;
  public ChannelDBManager()
  {

  }

  public Channel createChannel(String name, int roomId)
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("INSERT INTO Channel (id, name, roomId) VALUES (?, ?, ?);");
      statement.setInt(1, nextId);
      statement.setString(2, name);
      statement.setInt(3, roomId);
      nextId++;
      statement.executeUpdate();
      return new Channel(nextId - 1, name, roomId);
    }
    catch (SQLException e)
    {
      throw new RuntimeException(e);
    }
  }
  public Collection<Channel> getChannelsByRoom(int roomId) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Channel WHERE room_id = ?");
      preparedStatement.setInt(1, roomId);
      ResultSet resultSet = preparedStatement.executeQuery();
      ArrayList<Channel> channels = new ArrayList<>();
      while (resultSet.next())
      {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        int room_id = resultSet.getInt("room_id");
        Channel temp = new Channel(id, name, room_id);
        channels.add(temp);
      }
      return channels;
    }
  }

  private static Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection("jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2", "postgres","password");
  }
}
