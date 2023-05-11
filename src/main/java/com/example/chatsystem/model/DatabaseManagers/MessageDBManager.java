package com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Chatter;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.UserInterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class  MessageDBManager
{
  public MessageDBManager()
  {

  }

  public void createMessage(Message message)
  {
    try(Connection connection = getConnection())
    {
      //Do logic with the connection in here
    }
    catch (SQLException e)
    {
      System.err.println(e.getMessage());
    }
  }
  public Message readMessage(int id)
  {
    Chatter temp = new Chatter("123456","username","password");
    Message tempMessage = new Message("message",temp);
    return tempMessage;
  }

  public void updateMessage(int id)
  {

  }

  public void deleteMessage(int id)
  {

  }



  private Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2","postgres","password");
  }
}
