package com.example.chatsystem.model.DatabaseManagers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ModeratorDBManager
{
  public ModeratorDBManager(){};

  public void addModerator(){
    try(Connection connection = getConnection())
    {
      
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
