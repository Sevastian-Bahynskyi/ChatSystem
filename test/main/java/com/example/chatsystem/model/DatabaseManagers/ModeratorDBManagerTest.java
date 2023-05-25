package main.java.com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Chatter;
import com.example.chatsystem.model.DatabaseManagers.ChatterDBManager;
import com.example.chatsystem.model.DatabaseManagers.ModeratorDBManager;
import com.example.chatsystem.model.Moderator;
import com.example.chatsystem.model.UserInterface;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ModeratorDBManagerTest
{
  ModeratorDBManager moderatorDBManager;
  ChatterDBManager chatterDBManager;

  @BeforeEach public void setUp() throws SQLException
  {
    moderatorDBManager = new ModeratorDBManager();
    chatterDBManager = new ChatterDBManager();
  }

  @Test void chatter_becomes_new_moderator() throws SQLException
  {
    chatterDBManager.insert("135790", "Paco", "Contraseña");
    moderatorDBManager.makeModerator("135790", 1);
    Moderator result = null;
    UserInterface expected = new Moderator("135790", "Paco", "Contraseña");
    try (Connection connection = getConnection())
    {
      PreparedStatement preparedStatement = connection.prepareStatement(
          "SELECT * FROM chatter WHERE viaid = '135790';");
      ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.next();

      result = new Moderator(resultSet.getString("viaid"),
          resultSet.getString("username"), resultSet.getString("password"));
      moderatorDBManager.deleteModeratorByID("135790");
      chatterDBManager.delete("135790");
    }
    assertEquals(expected.getUsername(), result.getUsername());
  }

  @Test public void deletes_a_moderator() throws SQLException
  {
    UserInterface expected = new Chatter("123457", "test", "PasswordTest1");
    UserInterface result = null;
    chatterDBManager.insert("123457", "test", "PasswordTest1");
    moderatorDBManager.makeModerator("123457", 1);
    try (Connection connection = getConnection())
    {
      //Deletes the element
      moderatorDBManager.deleteModeratorByID(expected.getViaId());
      //Tries to find the element
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM chatterroomlist;");
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        if (resultSet.getString("chatter_id").equals(expected.getViaId()))
        {
          result = new Chatter(resultSet.getString("chatter_id"), expected.getUsername(), expected.getPassword());
        }
      }
    }
    //Compares the results with an ArrayList that's empty, as the element should be deleted before the Select query
    assertEquals(null, result);
    chatterDBManager.delete("123457");
  }

  @Test public void read_by_viaid() throws SQLException
  {
    //Gets a specific chatter
    //Chatter result = (Chatter) moderatorDBManager.read("111111");
    Chatter expected;
    try (Connection connection = getConnection())
    {
      //Statement for getting the same chatter as above
      PreparedStatement statement = connection.prepareStatement(
          "SELECT * FROM Chatter WHERE viaid = '111111'");
      ResultSet resultSet = statement.executeQuery();
      resultSet.next();
      //Ideally, stores the same chatter as the DBManager stored before
      expected = new Chatter(resultSet.getString("viaid"),
          resultSet.getString("username"), resultSet.getString("password"));
    }
    //assertEquals(expected, result);
  }



  private Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2", "postgres",
        "password");
  }
}
