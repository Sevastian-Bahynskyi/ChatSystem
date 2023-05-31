package main.java.com.example.chatsystem.model.Hugo;

import com.example.chatsystem.model.Chatter;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.UserInterface;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
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
    message3 = new Message("testMessage3", userInterface, 0);
    message4 = new Message();
  }

  @Test void creating_empty_message_constructor_makes_a_message_with_dummy_string()
  {
    Message messageTest = new Message();
    assertEquals("dummy",messageTest.getMessage());
  }
  @Test void create_simple_message_makes_message_object_with_parameters()
    {
      Timestamp date = (Timestamp) new Date();

      Message messageTest = new Message(1,"messageTestString",
          date,userInterface,2);

      assertTrue(((messageTest.getId() == 1 ) && (messageTest.getTime().equals(date.toString()) &&
          (messageTest.getMessage().equals("messageTestString")) && (messageTest.getChannelId() == 2)
          && (messageTest.getUser().getUsername().equals("testChatter")))));
    }
  @Test
  void gets_id_from_user_that_sent_message_throws_null_if_id_of_user_is_null()
  {
    assertThrows(NullPointerException.class, () -> message3.getUserId());
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
  }

  @Test void gets_proper_getMessageChannel_id()
  {
    assertEquals(0, message.getChannelId());
  }

  @Test void gets_content_of_message()
  {
    assertEquals("testMessage", message.getMessage());
  }

  @Test void changes_content_of_the_message()
  {
    message.setMessage("newMessage");
    assertEquals("newMessage", message.getMessage());
  }
  @Test
  void gets_the_user_that_sent_the_message()
  {
    assertEquals(userInterface, message.getUser());
  }
  @Test
  void gets_id_from_user_that_sent_message()
  {
    assertEquals(userInterface.getViaId(), message.getUserId());
  }

  @Test
  void toString_returns_expected_message()
  {
    String expected = "Message: " + message.getMessage() + "\nTime: " + message.getTime() +
        "\nUsername: " + userInterface.getUsername() + "\nImage url: "  +  userInterface.getImageUrl();

    assertEquals(expected, message.toString());
    assertThrows(NullPointerException.class, () -> message4.toString());
  }
  @Test
  void toString_when_called_on_empty_message_throws_null_()
  {
    assertThrows(NullPointerException.class, () -> message4.toString());
  }

  @Test
  void metadata_retuns_expected_data()
  {
    String expected = userInterface.getUsername() + " sent at: " + message.getTime();
    assertEquals(expected, message3.getMetadata());
    assertThrows(NullPointerException.class, () -> message4.getMetadata());
  }
  void method_call_throws_null_on_null_message()
  {
    assertThrows(NullPointerException.class, () -> message4.getMetadata());
  }
}
