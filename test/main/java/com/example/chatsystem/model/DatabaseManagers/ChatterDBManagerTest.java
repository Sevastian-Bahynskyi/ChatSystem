package main.java.com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Chatter;
import com.example.chatsystem.model.DatabaseManagers.ChatterDBManager;
import com.example.chatsystem.model.UserInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatterDBManagerTest
{
  ChatterDBManager chatterDBManager;

  @BeforeEach
  public void setUp() throws SQLException
  {
    chatterDBManager = new ChatterDBManager();
  }
  @Test
  public void create_a_new_user_updates_DBS() throws SQLException
  {
    Chatter test = new Chatter("123457", "test", "testPassword1");
    Chatter get = null;
    chatterDBManager.insert("123457", "test", "testPassword1");
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM Chatter WHERE viaid = '123457'");
      ResultSet resultSet = statement.executeQuery();
      resultSet.next();
      get = new Chatter(resultSet.getString("viaid"), resultSet.getString("username"), resultSet.getString("password"));
    }
    assertEquals(test, get);
  }
  @Test
  public void deletes_a_user() throws SQLException
  {
    ArrayList<Chatter> empty = new ArrayList<>();
    chatterDBManager.delete("123457");
    ArrayList<Chatter> get = new ArrayList<>();
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM Chatter WHERE viaid = '123457'");
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next())
      {
      get.add(new Chatter(resultSet.getString("viaid"), resultSet.getString("username"), resultSet.getString("password")));
      }
    }
    assertEquals(empty, get);
  }

  @Test
  public void reads_all_properly() throws SQLException
  {
    ArrayList<UserInterface> get = (ArrayList<UserInterface>) chatterDBManager.readAll();
    ArrayList<UserInterface> test = new ArrayList<>();
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM Chatter");
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        test.add(new Chatter(resultSet.getString("viaid"), resultSet.getString("username"), resultSet.getString("password")));
      }
      assertEquals(test, get);
    }
  }
  @Test
  public void read_by_us_pass() throws SQLException
  {
    Chatter expected = new Chatter("111111", "Dumy_1", "password_1");
    Chatter result;
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM Chatter WHERE username = 'Dumy_1' AND password = 'password_1'");
      ResultSet resultSet = statement.executeQuery();
      resultSet.next();
      result = new Chatter(resultSet.getString("viaid"), resultSet.getString("username"), resultSet.getString("password"));
    }
    assertEquals(expected, result);
  }

  @Test
  public void read_by_viaid() throws SQLException
  {
    Chatter expected = new Chatter("111111", "Dumy_1", "password_1");
    Chatter result;
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM Chatter WHERE viaid = '111111'");
      ResultSet resultSet = statement.executeQuery();
      resultSet.next();
      result = new Chatter(resultSet.getString("viaid"), resultSet.getString("username"), resultSet.getString("password"));
    }
    assertEquals(expected, result);
  }
  private Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection("jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2", "postgres","password");
  }
}
