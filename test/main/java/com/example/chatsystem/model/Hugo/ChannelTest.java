package main.java.com.example.chatsystem.model.Hugo;

import com.example.chatsystem.model.Channel;
import com.example.chatsystem.model.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.testng.AssertJUnit.assertEquals;

public class ChannelTest
{
  private Channel channel;
  @BeforeEach
  void setUp()
  {
    //This channel would not pass the validation through the server and into the database, but it is allowed to do in local memory for testing purposes
    channel = new Channel(0, "testChannel", 0);
  }

  @Test
  void gets_id_from_channel()
  {
    assertEquals(0, channel.getId());
  }
  @Test
  void gets_room_id_from_channel()
  {
    assertEquals(0, channel.getRoomId());
  }
  @Test
  void gets_name_from_the_channel()
  {
    assertEquals("testChannel", channel.getName());
  }
  @Test
  void changes_name_of_channel()
  {
    assertEquals("newName", channel.setName("newName"));
    channel.setName("testChannel");
  }

  @Test
  void gets_empty_ArrayList()
  {
    assertEquals(new ArrayList<Message>().size(), channel.getMessages().size());
  }
  @Test
  void toString_shows_expected_info()
  {
    String expected = "id: 0, name: testChannel, room id: 0";
    assertEquals(expected, channel.toString());
  }
}
