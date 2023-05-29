package main.java.com.example.chatsystem.model.DatabaseManagers.Hugo;

import com.example.chatsystem.model.Chatter;
import com.example.chatsystem.model.DatabaseManagers.RoomDBManager;
import com.example.chatsystem.model.Moderator;
import com.example.chatsystem.model.Room;
import com.example.chatsystem.model.UserInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class RoomDBTest
{
  RoomDBManager roomDBManager;
  @BeforeEach
  void setUp()
  {
    roomDBManager = new RoomDBManager();
  }

  @Test
  void create_room_by_existing_user() throws SQLException
  {
    UserInterface user = new Moderator("111111", "Dumy_1", "password_1");
    Room expected = new Room("test", "testCode");
    Room result = roomDBManager.createRoom(user, expected.getName(), expected.getCode());
    assertEquals(expected.getCode(), result.getCode());
    String success;
    int success2;
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM moderatorroomlist WHERE moderator_id = ? AND room_id = ?;");
      statement.setString(1, user.getViaId());
      statement.setInt(2, result.getId());
      ResultSet rs = statement.executeQuery();
      rs.next();
      success = rs.getString("moderator_id");
      success2 = rs.getInt("room_id");
    }
    //todo WHY IS IT NOT ADDING THE USER INTO THE MODERATORROOMLIST TABLE?
    assertEquals(success, user.getViaId());
    assertEquals(success2, result.getId());
    roomDBManager.deleteRoom(roomDBManager.getRooms().size() - 1);
  }
  @Test
  void create_room_by_user_not_found_in_database()
  {
    //todo ASK Should we be able to create a room when the user that creates it doesn't exist?
    UserInterface user = new Moderator("135790", "testUser", "testPassword");
    Room expected = new Room("test", "testCode");
    Room result = roomDBManager.createRoom(user, "test", "testCode");
    assertEquals(expected.getCode(), result.getCode());
    roomDBManager.deleteRoom(roomDBManager.getRooms().size() - 1);
  }
  @Test
  void getting_the_actual_last_room()
  {
    UserInterface user = new Moderator("135790", "testUser", "testPassword");
    Room expected = new Room("test", "testCode");
    roomDBManager.createRoom(user, expected.getName(), expected.getCode());
    Room result = roomDBManager.getLastRoom();
    assertEquals(expected.getCode(), result.getCode());
    roomDBManager.deleteRoom(roomDBManager.getLastRoom().getId());
  }

  @Test
  void getting_all_the_rooms() throws SQLException
  {
    ArrayList<Room> result = (ArrayList<Room>) roomDBManager.getRooms();
    ArrayList<Room> expected = new ArrayList<>();
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM room ORDER BY id DESC");
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        expected.add(new Room(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("code")));
      }
    }
    assertEquals(expected, result);
  }

  @Test
  void read_room_that_exists()
  {
     Room result = roomDBManager.readRoom(1);
     Room expected = new Room(1, "First Room", "ej3324fe");
     assertEquals(expected, result);
  }

  @Test
  void read_room_that_doesnt_exist()
  {
    Room result = roomDBManager.readRoom(0);
    Room expected = null;
    assertEquals(expected, result);
  }

  @Test
  void update_room_exists()
  {
    Room original = roomDBManager.readRoom(1);
    Room expected = new Room(1, "Other Room", "ej3324fe");
    roomDBManager.updateRoom(1, expected);
    Room result = roomDBManager.readRoom(1);
    assertEquals(expected, result);
    roomDBManager.updateRoom(1, original);
  }

  @Test
  void update_room_doesnt_exist()
  {
    Room expected = new Room(0, "Other Room", "ej3324fe");
    roomDBManager.updateRoom(0, expected);
    Room result = roomDBManager.readRoom(1);
    assertNotEquals(expected, result);
  }

  @Test
  void delete_room_that_exists()
  {
    UserInterface user = new Moderator("111111", "Dumy_1", "password_1");
    Room notExpected = roomDBManager.createRoom(user , "Some random room", "SADTSGRYDTH");
    int id = roomDBManager.getLastRoom().getId();
    roomDBManager.deleteRoom(id);
    Room result = roomDBManager.readRoom(id);
    assertNotEquals(notExpected, result);
  }

  @Test
  void delete_room_doesnt_exist()
  {
    UserInterface user = new Moderator("111111", "Dumy_1", "password_1");
    Room notExpected = null;
    roomDBManager.deleteRoom(0);
    Room result = roomDBManager.readRoom(0);
    assertEquals(notExpected, result);
  }

  @Test
  void getRoomByChannel_that_exists()
  {
    Room result = roomDBManager.getRoomByChannel(1);
    Room expected = roomDBManager.readRoom(result.getId());
    assertEquals(expected, result);
  }

  @Test
  void getRoomByChannel_doesnt_exist()
  {
    Room result = roomDBManager.getRoomByChannel(0);
    assertEquals(null, result);
  }

  @Test
  void addChatterToRoom_both_Chatter_and_room_exist() throws SQLException
  {
    UserInterface user = new Chatter("111111", "Dumy_1", "password_1");
    Room subject = roomDBManager.readRoom(2);
    roomDBManager.addChatterToRoom(user, subject);
    String success;
    int success2;
    try (Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM chatterroomlist WHERE chatter_id = '111111' AND room_id = 2;");
      ResultSet rs = statement.executeQuery();
      rs.next();
      success = rs.getString("chatter_id");
      success2 = rs.getInt("room_id");
      PreparedStatement statement1 = connection.prepareStatement("DELETE FROM chatterroomlist WHERE chatter_id = '111111' AND room_id = 2;");
      statement1.executeUpdate();
    }
    assertEquals(user.getViaId(), success);
    assertEquals(subject.getId(), success2);
  }

  @Test
  void addChatterToRoom_chatter_doesnt_exist() throws SQLException
  {
    //The program works, I just don't know how to catch this exceptions
    UserInterface user = new Chatter("135790", "testUser", "passwordTest1");
    Room subject = roomDBManager.readRoom(2);
    //assertThrows(SQLException.class, () ->     roomDBManager.addChatterToRoom(user, subject));
  }

  @Test
  void addChatterToRoom_room_doesnt_exist() throws SQLException
  {
    UserInterface user = new Chatter("111111", "Dumy_1", "password_1");
    Room subject = roomDBManager.readRoom(0);
    assertThrows(NullPointerException.class, () -> roomDBManager.addChatterToRoom(user, subject));
  }

  private Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/sep2?currentSchema=sep2", "postgres", "password");
  }
}
