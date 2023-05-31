package com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Channel;
import com.example.chatsystem.model.Room;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ChannelDBManager
{
  private ArrayList<Channel> channels;
  public ChannelDBManager() throws SQLException
  {
    channels = getChannels();
  }

  public Channel createChannel(String name, int roomId)
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("INSERT INTO Channel (name, room_id) VALUES (?, ?);");
      statement.setString(1, name);
      statement.setInt(2, roomId);
      statement.executeUpdate();
      int id = getChannelId(name, roomId);
      return new Channel(id, name, roomId);
    }
    catch (SQLException e)
    {
      throw new RuntimeException(e);
    }
  }

  private ArrayList<Channel> getChannels() throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM Channel;");
      ResultSet resultSet = statement1.executeQuery();
      ArrayList<Channel> temp = new ArrayList<>();
      while (resultSet.next())
      {
        int id = resultSet.getInt("id");
        String names = resultSet.getString("name");
        int room_id = resultSet.getInt("room_id");
        Channel tempo = new Channel(id, names, room_id);
        temp.add(tempo);
      }
      return temp;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public ArrayList<Channel> getChannelsByRoomID(int roomId) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement ps = connection.prepareStatement("SELECT * FROM Channel WHERE room_id=? ORDER BY id DESC");
      ps.setInt(1, roomId);
      ResultSet resultSet = ps.executeQuery();
      ArrayList<Channel> temp = new ArrayList<>();
      while (resultSet.next())
      {
        int id = resultSet.getInt("id");
        String names = resultSet.getString("name");
        Channel tempo = new Channel(id, names, roomId);
        temp.add(tempo);
      }
      return temp;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public ArrayList<Channel> getChannelsInRoomByAsc(int roomId) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement ps = connection.prepareStatement("SELECT * FROM Channel WHERE room_id=? ORDER BY id");
      ps.setInt(1, roomId);
      ResultSet resultSet = ps.executeQuery();
      ArrayList<Channel> temp = new ArrayList<>();
      while (resultSet.next())
      {
        int id = resultSet.getInt("id");
        String names = resultSet.getString("name");
        Channel tempo = new Channel(id, names, roomId);
        temp.add(tempo);
      }
      return temp;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return null;
  }


  private int getChannelId(String name, int room_id)
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement1 = connection.prepareStatement("SELECT id FROM Channel WHERE name = ? AND room_id = ?");
      statement1.setString(1, name);
      statement1.setInt(2, room_id);
      ResultSet resultSet = statement1.executeQuery();

      if(resultSet.next())
      {
        return resultSet.getInt("id");
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return -1;
  }

  public Channel deleteChannelById(int id) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("DELETE FROM Channel WHERE id = ?");
      statement.setInt(1, id);
      statement.executeUpdate();
      for (int i = 0; i < channels.size(); i++)
      {
        if (channels.get(i).getId() == id)
        {
          return channels.remove(i);
        }
      }
    }
    return null;
  }

  public Collection<Channel> getChannelsByRoom(int roomId) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement preparedStatement = connection.prepareStatement(
          "SELECT * FROM Channel WHERE room_id = ? ORDER BY id DESC");
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

  public void updateChannelById(int id, String newChannelName)
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement preparedStatement = connection.prepareStatement("UPDATE channel set name=? WHERE id = ?");
      preparedStatement.setString(1, newChannelName);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public Channel getChannelById(int id) throws SQLException
  {
    Channel channel = null;
    try (Connection connection = getConnection())
    {
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM channel WHERE id = ?");
      preparedStatement.setInt(1, id);
      ResultSet rs = preparedStatement.executeQuery();
      if (rs.next())
      {
          channel = new Channel(rs.getInt(1), rs.getString(2), rs.getInt(3));
      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return channel;

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

  public Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection("jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2", "postgres","password");
  }
}