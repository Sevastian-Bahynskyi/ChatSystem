package main.java.com.example.chatsystem.model.Hugo;

import com.example.chatsystem.model.Chatter;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.UserInterface;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.Assert.assertThrows;
import static org.testng.AssertJUnit.assertEquals;

public class MessageTest
{
  Message message;
  Message message2;
  Message message3;
  Message message4;
  UserInterface userInterface;

  @BeforeEach void setUp()
  {
    userInterface = new Chatter("000000", "testChatter", "testPassword1");
    message = new Message(0, "testMessage", new Timestamp(1), userInterface, 0);
    message2 = new Message(0, "testMessage2", userInterface, 0);
    message3 = new Message("testMessage3", userInterface, 0);
    message4 = new Message();
  }

  @Test void gets_time_from_message_with_proper_format()
  {
    assertEquals("01:00:00", message.getTime());
  }

  @Test void gets_timeStamp()
  {
    assertEquals(new Timestamp(1), message.getTimeStamp());
  }

  @Test void get_proper_message_id()
  {
    assertEquals(0, message.getId());
    assertEquals(0, message2.getId());
    assertEquals(0, message3.getId());
    //0 is the default value for an int
    assertEquals(0, message4.getId());
  }

  @Test void gets_proper_getMessageChannel_id()
  {
    assertEquals(0, message.getChannelId());
    assertEquals(0,message2.getChannelId());
    assertEquals(0, message3.getChannelId());
    assertEquals(0, message4.getChannelId());
  }

  @Test void gets_content_of_message()
  {
    assertEquals("testMessage", message.getMessage());
    assertEquals("testMessage2", message2.getMessage());
    assertEquals("testMessage3", message3.getMessage());
    assertEquals("dummy", message4.getMessage());
  }

  @Test void changes_content_of_the_message()
  {
    message.setMessage("newMessage");
    message2.setMessage("newMessage2");
    message3.setMessage("newMessage3");
    message4.setMessage("newMessage4");
    assertEquals("newMessage", message.getMessage());
    assertEquals("newMessage2", message2.getMessage());
    assertEquals("newMessage3", message3.getMessage());
    assertEquals("newMessage4", message4.getMessage());
  }
  @Test
  void gets_the_user_that_sent_the_message()
  {
    assertEquals(userInterface, message.getUser());
    assertEquals(userInterface, message2.getUser());
    assertEquals(userInterface, message3.getUser());
    assertEquals(null, message4.getUser());
  }
  @Test
  void gets_id_from_user_that_sent_message()
  {
    assertEquals(userInterface.getViaId(), message.getUserId());
    assertEquals(userInterface.getViaId(), message2.getUserId());
    assertEquals(userInterface.getViaId(), message3.getUserId());
    assertThrows(NullPointerException.class, () -> message4.getUserId());
  }
  @Test
  void toString_returns_expected_message()
  {
    String expected = "Message: " + message.getMessage() + "\nTime: " + message.getTime() +
        "\nUsername: " + userInterface.getUsername() + "\nImage url: "  +  userInterface.getImageUrl();
    String expected2 = "Message: " + message2.getMessage() + "\nTime: " + message2.getTime() +
        "\nUsername: " + userInterface.getUsername() + "\nImage url: "  +  userInterface.getImageUrl();
    String expected3 = "Message: " + message3.getMessage() + "\nTime: " + message3.getTime() +
        "\nUsername: " + userInterface.getUsername() + "\nImage url: "  +  userInterface.getImageUrl();

    assertEquals(expected, message.toString());
    assertEquals(expected2, message2.toString());
    assertEquals(expected3, message3.toString());
    assertThrows(NullPointerException.class, () -> message4.toString());
  }

  @Test
  void metadata_retuns_expected_data()
  {
    String expected = userInterface.getUsername() + " sent at: " + message.getTime();
    String expected2 = userInterface.getUsername() + " sent at: " + message2.getTime();
    String expected3 = userInterface.getUsername() + " sent at: " + message3.getTime();
    String expected4 = null + " sent at: " + message4.getTime();
    assertEquals(expected, message.getMetadata());
    assertEquals(expected2, message2.getMetadata());
    assertEquals(expected3, message3.getMetadata());
    assertThrows(NullPointerException.class, () -> message4.getMetadata());
  }
}
