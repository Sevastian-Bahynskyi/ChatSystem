package main.java.com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Chatter;
import com.example.chatsystem.model.DatabaseManagers.MessageDBManager;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.Moderator;
import com.example.chatsystem.model.UserInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MessageDBTest
{
  private MessageDBManager messageDBManager;
  @BeforeEach
  void setUp()
  {
    messageDBManager = new MessageDBManager();
  }

  @Test
  void creates_average_message() throws SQLException
  {
    Chatter chatter = new Chatter("111111", "Dumy_1", "password");
    Message expected = messageDBManager.createMessage(new Message(0, "test", chatter, 1));
    Message result;
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("Select * FROM Message WHERE id = ? AND message = ? AND timestamp = ? AND chatter_id = ? AND channel_id = ?;");
      statement.setInt(1, expected.getId());
      statement.setString(2, expected.getMessage());
      statement.setTimestamp(3, expected.getTimeStamp());
      statement.setString(4, expected.getUserId());
      statement.setInt(5, expected.getChannelId());
      ResultSet resultSet = statement.executeQuery();
      resultSet.next();
      int id = resultSet.getInt("id");
      result = new Message(resultSet.getInt("id"), resultSet.getString("message"), resultSet.getTimestamp("timestamp"), chatter, resultSet.getInt("channel_id"));
      realDelete(id);
    }
    assertEquals(expected.getMessage(), result.getMessage());
  }

  @Test
  void creates_spaces_message() throws SQLException
  {
    Chatter chatter = new Chatter("111111", "Dumy_1", "password");
    Message expected = messageDBManager.createMessage(new Message(0, " ", chatter, 1));
    Message result;
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("Select * FROM Message WHERE id = ? AND message = ? AND timestamp = ? AND chatter_id = ? AND channel_id = ?;");
      statement.setInt(1, expected.getId());
      statement.setString(2, expected.getMessage());
      statement.setTimestamp(3, expected.getTimeStamp());
      statement.setString(4, expected.getUserId());
      statement.setInt(5, expected.getChannelId());
      ResultSet resultSet = statement.executeQuery();
      resultSet.next();
      int id = resultSet.getInt("id");
      result = new Message(resultSet.getInt("id"), resultSet.getString("message"), resultSet.getTimestamp("timestamp"), chatter, resultSet.getInt("channel_id"));
      realDelete(id);
    }
    assertEquals(expected.getMessage(), result.getMessage());
  }

  @Test
  void creates_message_2999_characters() throws SQLException
  {
    Chatter chatter = new Chatter("111111", "Dumy_1", "password");
    String message = "";
    for (int i = 0; i < 2999; i++)
    {
      message += ".";
    }
    Message expected = messageDBManager.createMessage(new Message(0, message, chatter, 1));
    Message result;
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("Select * FROM Message WHERE id = ? AND message = ? AND timestamp = ? AND chatter_id = ? AND channel_id = ?;");
      statement.setInt(1, expected.getId());
      statement.setString(2, expected.getMessage());
      statement.setTimestamp(3, expected.getTimeStamp());
      statement.setString(4, expected.getUserId());
      statement.setInt(5, expected.getChannelId());
      ResultSet resultSet = statement.executeQuery();
      resultSet.next();
      result = new Message(resultSet.getInt("id"), resultSet.getString("message"), resultSet.getTimestamp("timestamp"), chatter, resultSet.getInt("channel_id"));
      int id = resultSet.getInt("id");
      realDelete(id);
    }
    assertEquals(expected.getMessage(), result.getMessage());
  }

  @Test
  void creates_message_3000_characters() throws SQLException
  {
    Chatter chatter = new Chatter("111111", "Dumy_1", "password_1");
    String message = "";
    for (int i = 0; i < 3000; i++)
    {
      message += ".";
    }
    String finalMessage = message;
    assertThrows(SQLException.class, () -> messageDBManager.createMessage(new Message(0, finalMessage, chatter, 1)));
  }

  @Test
  void read_message_that_exists() throws SQLException
  {
    Message result = messageDBManager.readMessage(1);
    Message expected = null;
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM message WHERE id = '1';");
      ResultSet rs = statement.executeQuery();
      if (rs.next())
      {
        Chatter chatter = new Chatter(rs.getString("chatter_id"), "Dumy_6", "password_6");
        expected = new Message(rs.getInt("id"), rs.getString("message"), rs.getTimestamp("timestamp"), chatter, rs.getInt("channel_id"));
      }
    }
    assertEquals(expected.getMessage(), result.getMessage());
  }

  @Test
  void read_message_doesnt_exist() throws SQLException
  {
    Message result = messageDBManager.readMessage(0);
    Message expected = null;
    assertEquals(expected, result.getUser());
  }

  @Test
  void read_all_messages_from_channel_that_exists() throws SQLException
  {
    ArrayList<Message> result = new ArrayList<>();
    result.addAll(messageDBManager.getAllMessagesForAChannel(1));
    ArrayList<Message> expected = new ArrayList<>();
    ArrayList<UserInterface> users = new ArrayList<>();
    try (Connection connection = getConnection())
    {
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM CHATTER;");
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next())
      {
        users.add(new Chatter(resultSet.getString("viaid"),
            resultSet.getString("username"), resultSet.getString("password")));
      }
      PreparedStatement statement =
          connection.prepareStatement("SELECT * FROM message WHERE channel_id = '1';");
      ResultSet rs = statement.executeQuery();
      while (rs.next())
      {
        UserInterface currentChatter = null;
        for (int i = 0; i < users.size(); i++)
        {
          if (rs.getString("chatter_id").equals(users.get(i).getViaId()));
          {
            currentChatter = users.get(i);
          }
        }
        expected.add(new Message(rs.getInt("id"),
            rs.getString("message"), rs.getTimestamp("timestamp"),
            currentChatter, rs.getInt("channel_id")));
      }
    }
    boolean trueBool = true;
    for (int i = 0; i < result.size(); i++)
    {
      if (result.get(i).equals(expected.get(i)))
      {
        trueBool = true;
      }
      else trueBool = false;
    }
    assertTrue(trueBool);
  }

  @Test
  void read_all_messages_from_channe_doesnt_exist() throws SQLException
  {
    ArrayList<Message> result = new ArrayList<>();
    result.addAll(messageDBManager.getAllMessagesForAChannel(0));
    ArrayList<Message> expected = new ArrayList<>();
    assertEquals(expected, result);
  }

  @Test
  void get_last_message_channel_exists() throws SQLException
  {
    Message result = messageDBManager.getLastMessage(1);
    Message expected = null;
    UserInterface userInterface = null;
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM Message WHERE channel_id = 1 ORDER BY id DESC LIMIT 1");
      ResultSet rs = statement.executeQuery();
      rs.next();
      PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM Chatter WHERE viaid = ?;");
      statement1.setString(1, rs.getString("chatter_id"));
      ResultSet resultSet = statement1.executeQuery();
      resultSet.next();
      userInterface = new Chatter(resultSet.getString("viaid"), resultSet.getString("username"), resultSet.getString("password"));
      expected = new Message(rs.getInt("id"), rs.getString("message"), rs.getTimestamp("timestamp"), userInterface, rs.getInt("channel_id"));
    }
    assertEquals(result.getMessage(), expected.getMessage());
  }

  @Test
  void get_last_message_channel_doesnt_exist() throws SQLException
  {
    Message result = messageDBManager.getLastMessage(0);
    Message expected = null;
    assertEquals(result, expected);
  }

  @Test
  void update_message_that_exists_modifies_database() throws SQLException
  {
    UserInterface userInterface = new Moderator("111116", "Dumy_6", "password_6");
    Message expected = new Message(1, "test2", userInterface, 2);
    messageDBManager.updateMessage(1, expected);
    Message result = null;
    UserInterface res;
    try (Connection connection = getConnection())
    {
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM message WHERE id = 1;");
      ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.next();
      String viaid = resultSet.getString("chatter_id");
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM chatter WHERE viaid = ?");
      statement.setString(1, viaid);
      ResultSet rs = statement.executeQuery();
      rs.next();
      res = new Chatter(rs.getString("viaid"), rs.getString("username"), rs.getString("password"));
      result = new Message(resultSet.getInt("id"), resultSet.getString("message"), res, resultSet.getInt("channel_id"));
    }
    assertEquals(expected.getMessage(), result.getMessage());
  }
  //If I try to update a message that doesn't exist, the database doesn't care, but if I want to retrieve that object, the query throws an error. Should I try to equal null to null? jajajja
  @Test
  void update_message_that_doesnt_exists() throws SQLException
  {

  }

  @Test
  void update_message_to_nul()
  {
    assertThrows(NullPointerException.class, () -> messageDBManager.updateMessage(1, null));
  }

  @Test
  void delete_message_that_exists() throws SQLException
  {
    Message result;
    UserInterface userInterface = new Moderator("111111", "Dumy_1", "password_1");
    Message test = new Message( "messageTest", userInterface, 1);
    Message temp = messageDBManager.createMessage(test);
    messageDBManager.deleteMessage(temp.getId());
    Message expected = messageDBManager.readMessage(temp.getId());
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM message WHERE id = ?;");
      statement.setInt(1, temp.getId());
      ResultSet resultSet = statement.executeQuery();
      resultSet.next();
      result = new Message(resultSet.getInt("id"), resultSet.getString("message"), userInterface, resultSet.getInt("channel_id"));
    }
    assertEquals(expected.getMessage(), result.getMessage());
  }

  @Test
  void delete_message_doesnt_exist() throws SQLException
  {
    Message result;
    UserInterface userInterface = new Moderator("111111", "Dumy_1", "password_1");
    messageDBManager.deleteMessage(0);
    //If there is no message found with this id, the program will return a message with an empty constructor, which will include some data
    Message expected = messageDBManager.readMessage(0);
    if (expected.getMessage().equals("dummy"))
    {
      assertEquals(expected.getMessage(), "dummy");
    }
    else
    {
      try (Connection connection = getConnection())
      {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM message WHERE id = ?;");
        statement.setInt(1, 0);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        result = new Message(resultSet.getInt("id"), resultSet.getString("message"), userInterface, resultSet.getInt("channel_id"));
      }
      assertEquals(expected.getMessage(), result.getMessage());
    }
  }
  private void realDelete(int id) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("DELETE FROM message WHERE id = ?;");
      statement.setInt(1, id);
      statement.executeUpdate();
    }
  }


  private Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection("jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2", "postgres","password");
  }
}
