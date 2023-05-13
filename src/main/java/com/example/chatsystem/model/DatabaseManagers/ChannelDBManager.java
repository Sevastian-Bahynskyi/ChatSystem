package com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Channel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ChannelDBManager
{
  private int nextId = 1;

  public ChannelDBManager()
  {
    updateNextId();
  }

  private void updateNextId()
  {
    nextId = 1;
    while (true)
    {
      try (Connection connection = getConnection())
      {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO Channel (id, name, room_id) VALUES (?, ?, ?);");
        statement.setInt(1, nextId);
        statement.setString(2, "test");
        statement.setInt(3, 1);
        statement.executeUpdate();
        PreparedStatement statement1 = connection.prepareStatement("DELETE FROM Channel WHERE id = ?;");
        statement1.setInt(1, nextId);
        statement1.executeUpdate();
        break;
      }
      catch (SQLException e)
      {
        nextId++;
      }
    }
  }

  public Channel createChannel(String name, int roomId)
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("INSERT INTO Channel (id, name, room_id) VALUES (?, ?, ?);");
      statement.setInt(1, nextId);
      statement.setString(2, name);
      statement.setInt(3, roomId);
      int temp = nextId;
      statement.executeUpdate();
      updateNextId();
      return new Channel(temp, name, roomId);
    }
    catch (SQLException e)
    {
      throw new RuntimeException(e);
    }
  }

  public void deleteChannelById(int id) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("DELETE FROM Channel WHERE id = ?;");
      statement.setInt(1, id);
      statement.executeUpdate();
      updateNextId();
    }
  }

  public Collection<Channel> getChannelsByRoom(int roomId) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement preparedStatement = connection.prepareStatement(
          "SELECT * FROM Channel WHERE room_id = ?;");
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

  public Channel getChannelById(int id) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Channel WHERE id = ?;");
      preparedStatement.setInt(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.next();
      int idTemp = resultSet.getInt("id");
      String name = resultSet.getString("name");
      int room_id = resultSet.getInt("room_id");
      Channel temp = new Channel(idTemp, name, room_id);
      return temp;
    }
  }

  public ArrayList<Channel> getChannelByName(String name) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement preparedStatement = connection.prepareStatement(
          "SELECT * FROM Channel WHERE name like ?;");
      preparedStatement.setString(1, "%" + name + "%");
      ResultSet resultSet = preparedStatement.executeQuery();
      ArrayList<Channel> temp = new ArrayList<>();
      while (resultSet.next())
      {
        int id = resultSet.getInt("id");
        String nameTemp = resultSet.getString("name");
        int room_id = resultSet.getInt("room_id");
        Channel tempo = new Channel(id, nameTemp, room_id);
        temp.add(tempo);
      }
      return temp;
    }
  }

  public static Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection("jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2", "postgres","password");
  }

  public static void main(String[] args) throws SQLException
  {
    Driver driver = new org.postgresql.Driver();
    DriverManager.registerDriver(driver);
    ChannelDBManager channelDBManager = new ChannelDBManager();
    channelDBManager.deleteChannelById(1);
    System.out.println(channelDBManager.createChannel("Welcome", 1));
  }
}