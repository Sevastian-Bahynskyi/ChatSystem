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
  public void setUp()
  {
    chatterDBManager = new ChatterDBManager();
  }
  @Test
  public void create_a_new_user_updates_DBS() throws SQLException
  {
    Chatter expected = new Chatter("123457", "test", "testPassword1");
    Chatter result;
    //Inserts a new Chatter into the Database
    chatterDBManager.insert("123457", "test", "testPassword1");
    try (Connection connection = getConnection())
    {
      //Tries to find the new Chatter into the Database
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM Chatter WHERE viaid = '123457';");
      ResultSet resultSet = statement.executeQuery();
      resultSet.next();
      //Stores the result of the search. No need of while loop to search through the results, as viaid's will be unique, meaning that the result of this query will always be one
      result = new Chatter(resultSet.getString("viaid"), resultSet.getString("username"), resultSet.getString("password"));
      //Deletes the created element so that the Database doesn't get full of testing data
      PreparedStatement st = connection.prepareStatement("DELETE FROM Chatter WHERE viaid = '123457';");
      st.executeUpdate();
    }
    assertEquals(expected, result);
  }
  @Test
  public void deletes_a_user() throws SQLException
  {
    ArrayList<Chatter> expected = new ArrayList<>();
    ArrayList<Chatter> result = new ArrayList<>();
    try (Connection connection = getConnection())
    {
      //Creates the element to delete
      PreparedStatement st = connection.prepareStatement("INSERT INTO Chatter(viaid, username, password) VALUES(?,?,?);");
      st.setString(1, "123457");
      st.setString(2, "test");
      st.setString(3, "PasswordTest1");
      st.executeUpdate();
      //Deletes the element
      chatterDBManager.delete("123457");
      //Tries to find the element
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM Chatter WHERE viaid = '123457';");
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        //Adds to a list the elements found
        result.add(new Chatter(resultSet.getString("viaid"), resultSet.getString("username"), resultSet.getString("password")));
      }
    }
    //Compares the results with an ArrayList that's empty, as the element should be deleted before the Select query
    assertEquals(expected, result);
  }

  @Test
  public void reads_all_properly() throws SQLException
  {
    //Creates an ArrayList and stores the result of the readAll() method
    ArrayList<UserInterface> expected = (ArrayList<UserInterface>) chatterDBManager.readAll();
    ArrayList<UserInterface> result = new ArrayList<>();
    try (Connection connection = getConnection())
    {
      //Statement to read all information from the Chatter table of the database
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM Chatter");
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        //Adds to an empty ArrayList the results of the previous query
        result.add(new Chatter(resultSet.getString("viaid"), resultSet.getString("username"), resultSet.getString("password")));
      }
      //Both ArrayLists should have the same content inside
      assertEquals(expected, result);
    }
  }
  //Tests the read(...) method, using the username and password to identify the user
  @Test
  public void read_by_us_pass() throws SQLException
  {
    //
    Chatter result = (Chatter) chatterDBManager.read("Dumy_1", "password_1");
    Chatter expected;
    try (Connection connection = getConnection())
    {
      //Query to retrieve a Chatter from its username and its password
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM Chatter WHERE username = 'Dumy_1' AND password = 'password_1'");
      ResultSet resultSet = statement.executeQuery();
      resultSet.next();
      expected = new Chatter(resultSet.getString("viaid"), resultSet.getString("username"), resultSet.getString("password"));
    }
    assertEquals(expected, result);
  }

  @Test
  public void read_by_viaid() throws SQLException
  {
    //Gets a specific chatter
    Chatter result = (Chatter) chatterDBManager.read("111111");
    Chatter expected;
    try (Connection connection = getConnection())
    {
      //Statement for getting the same chatter as above
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM Chatter WHERE viaid = '111111'");
      ResultSet resultSet = statement.executeQuery();
      resultSet.next();
      //Ideally, stores the same chatter as the DBManager stored before
      expected = new Chatter(resultSet.getString("viaid"), resultSet.getString("username"), resultSet.getString("password"));
    }
    assertEquals(expected, result);
  }
  @Test
  public void edit_Chatter() throws SQLException
  {
    //Stores the information that should be displayed at the end
    Chatter expected = new Chatter("111111", "Pepe", "testPassword1");
    Chatter result;
    //Updates the value in the Database
    chatterDBManager.update("111111", "Pepe", "testPassword1");
    try (Connection connection = getConnection())
    {
      //Reads the information from the database, to see if the changes have been done properly
      PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM Chatter WHERE viaid = '111111'");
      ResultSet resultSet = statement1.executeQuery();
      resultSet.next();
      result = new Chatter(resultSet.getString("viaid"), resultSet.getString("username"), resultSet.getString("password"));
      //Returns to the initial value of the Database, so that the test can be repeated afterward
      PreparedStatement statement = connection.prepareStatement("UPDATE chatter set username = 'Dumy_1', password = 'password_1' WHERE viaid = '111111'");
      statement.executeUpdate();
    }
    assertEquals(expected, result);
  }

  @Test
  void reads_all_by_room_id() throws SQLException
  {
    ArrayList<UserInterface> expected = (ArrayList<UserInterface>) chatterDBManager.readAllByRoomID(1);
    ArrayList<UserInterface> result = new ArrayList<>();
    try (Connection connection = getConnection())
    {
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT chatter_id FROM chatterroomlist WHERE room_id = 1;");
      ResultSet resultSet = preparedStatement.executeQuery();
      ArrayList<String> ids = new ArrayList<>();
      while (resultSet.next())
      {
        ids.add(resultSet.getString("chatter_id"));
      }
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM chatter WHERE viaid = ?;");
      for (int i = 0; i < ids.size(); i++)
      {
        statement.setString(1, ids.get(i));
        ResultSet resultSet1 = statement.executeQuery();
        resultSet1.next();
        result.add(new Chatter(resultSet1.getString("viaid"),resultSet1.getString("username"), resultSet1.getString("password")));
      }
      assertEquals(expected, result);
    }
  }
  private Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection("jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2", "postgres","password");
  }
}
