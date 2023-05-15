package com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Chatter;
import com.example.chatsystem.model.Data;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.UserInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class  MessageDBManager
{
  public MessageDBManager()
  {}

  public void createMessage(Message message)
  {
    try(Connection connection = getConnection())
    {
      PreparedStatement ps = connection.prepareStatement("INSERT INTO Message VALUES(?)");
      ps.setString(1,message.toString());
      ps.executeUpdate();
    }
    catch (SQLException e)
    {
      System.err.println(e.getMessage());
    }
  }
  public Message readMessage(int id)
  {
    Message message = new Message();
    try(Connection connection = getConnection())
    {
      PreparedStatement ps = connection.prepareStatement("SELECT * FROM Message WHERE id = ?");

      ps.setInt(1, id);

      ResultSet rs = ps.executeQuery();
      UserInterface tempChatter = new Chatter("000000","dummy","dummy");

      if(rs.next())
      {
        // message should be id,Message,timestamp,chatter_id,channel_id
        for (int i = 0; i < Data.getInstance().getUsers().size(); i++)
        {
          tempChatter = Data.getInstance().getUsers().get(i);
          if(tempChatter.getViaId() == rs.getString("chatter_id"))
          {
            break;
          }
        }
        message = new Message(rs.getInt("id"),rs.getString("message"),rs.getTimestamp("timestamp"),tempChatter,rs.getInt("channel_id"));
      }
    }
    catch (SQLException e)
    {
      System.err.println(e.getMessage());
    }
    return message;
  }

  public Collection<Message> getAllMessagesForAChannel(int channelId)
  {
    ArrayList<Message> messageArrayList = new ArrayList<>();
    try(Connection connection = getConnection())
    {
      PreparedStatement ps = connection.prepareStatement("SELECT * FROM Message WHERE channel_id = ?");

      ps.setInt(1, channelId);

      ResultSet rs = ps.executeQuery();
      UserInterface tempChatter = new Chatter("000000","dummy","dummy");

      if(rs.next())
      {
        for (int i = 0; i < Data.getInstance().getUsers().size(); i++)
        {
          tempChatter = Data.getInstance().getUsers().get(i);
          if(tempChatter.getViaId() == rs.getString("chatter_id"))
          {
            break;
          }
        }
        // message should be id,Message,timestamp,chatter_id,channel_id
        messageArrayList.add(new Message(rs.getInt("id"),rs.getString("message"),rs.getTimestamp("timestamp"),tempChatter,rs.getInt("channel_id")));
      }
    }
    catch (SQLException e)
    {
      System.err.println(e.getMessage());
    }
    return messageArrayList;
  }

  public void updateMessage(int id, Message newMessage)
  {
    try(Connection connection = getConnection())
    {
      PreparedStatement ps = connection.prepareStatement("UPDATE Message SET message=? WHERE id=?");

      ps.setString(1, newMessage.getMessage());
      ps.setInt(2,id);

      ps.executeUpdate();
    }
    catch (SQLException e)
    {
      System.err.println(e.getMessage());
    }
  }

  public void deleteMessage(int id)
  {
    Message message = new Message();
    try(Connection connection = getConnection())
    {
      String temp = "deleted message";

      PreparedStatement ps = connection.prepareStatement("UPDATE Message SET message=? WHERE id=?");

      ps.setString(1,temp);
      ps.setInt(2,id);

      ps.executeUpdate();
    }
    catch (SQLException e)
    {
      System.err.println(e.getMessage());
    }
  }



  private Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2","postgres","password");
  }
}
