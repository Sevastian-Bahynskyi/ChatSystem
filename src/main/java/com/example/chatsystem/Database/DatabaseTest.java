package com.example.chatsystem.Database;

import java.sql.*;

public class DatabaseTest
{
  public static void main(String args[]) throws SQLException
  {
    Driver driver = new org.postgresql.Driver();
    DriverManager.registerDriver(driver);
    Connection connection = DriverManager.getConnection("jdbc:postgresql://familydrive.duckdns.org:5433/sep2?currentSchema=sep2", "postgres","password");
    PreparedStatement statement = connection.prepareStatement("SELECT username FROM Chatter");
    ResultSet rs = statement.executeQuery();
    while(rs.next())
    {
      System.out.println(rs.getString("username"));
    }
    connection.close();
  }
}
