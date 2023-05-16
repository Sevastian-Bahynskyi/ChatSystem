package main.java.com.example.chatsystem.model.DatabaseManagers;

import com.example.chatsystem.model.Channel;
import com.example.chatsystem.model.DatabaseManagers.ChannelDBManager;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

class ChannelDBTest
{
  ChannelDBManager channelDBManager = null;


  @BeforeEach void setUp() throws SQLException
  {
    channelDBManager = new ChannelDBManager();
  }

  @Test
  void create_Channel_Returns_Channel_Object_with_given_name()
  {
    Channel test = channelDBManager.createChannel("Channel_1", 2);
    assertEquals("Channel_1",test.getName());
  }
  @Test
  void create_Channel_in_room_doesnt_exist_throws_sql_exception()
  {
    AtomicReference<Channel> test = new AtomicReference<>();
    assertThrows(RuntimeException.class, () -> test.set(
        channelDBManager.createChannel("Channel_Shouldnt_exist", 10)));
  }

  @Test
  void deleteChannel_by_id_deletes_channel() throws SQLException
  {
    Channel temp = channelDBManager.getChannelByName("Channel_1").get(0);
    ArrayList<Channel> emptyArray = new ArrayList<>();
    channelDBManager.deleteChannelById(temp.getId());
    assertEquals(emptyArray,channelDBManager.getChannelByName("Channel_1"));
  }

  @Test
  void deleteChannel_by_none_existant_id_gets_null() throws SQLException
  {
    assertEquals(null, channelDBManager.deleteChannelById(0));
  }

  @Test
  void getChannelsById_returns_a_channel_that_has_that_id() throws SQLException
  {
    Channel test = channelDBManager.getChannelById(1);
    assertEquals("Welcome",test.getName());
  }

  @Test
  void getChannelsBy_Id_returns_null_if_not_found() throws SQLException
    {
      assertEquals(null,channelDBManager.getChannelById(0));
    }

  @Test
  void getChannelsByRoom_returns_a_collection_of_channels_in_that_room() throws SQLException
  {
    assertEquals(3,channelDBManager.getChannelsByRoom(1).size());
  }

  @Test
  void getChannelByName_gets_all_channels_have_that_name() throws SQLException
  {
    assertEquals(5,channelDBManager.getChannelByName("Welcome").size());
  }

  @Test
  void constructorConstructs()
  {
    assertEquals(ChannelDBManager.class,channelDBManager.getClass());
  }

}