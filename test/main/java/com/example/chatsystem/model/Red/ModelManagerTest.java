package main.java.com.example.chatsystem.model.Red;

import com.example.chatsystem.model.*;
import com.example.chatsystem.model.DatabaseManagers.ChannelDBManager;
import com.example.chatsystem.model.ModelManagerDelegates.*;
import com.example.chatsystem.server.client.ServerModelImplementation;
import com.example.chatsystem.server.server.Server;
import com.example.chatsystem.server.shared.ServerModel;
import com.example.chatsystem.view.WINDOW;
import com.example.chatsystem.viewmodel.ChatViewModelDelegates.ChatViewModel;
import com.example.chatsystem.viewmodel.ChatViewModelDelegates.RoomHandler;
import com.example.chatsystem.viewmodel.ViewModel;
import com.example.chatsystem.viewmodel.ViewModelFactory;
import javafx.application.Platform;
import org.checkerframework.checker.units.qual.C;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.junit.Assert.*;

import static com.example.chatsystem.view.WINDOW.CHAT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class ModelManagerTest
{
  private ModelManager model = null;
  private UserInterface user = null;
  private ArrayList<UserInterface> users = new ArrayList();


  @BeforeEach void setUp() throws SQLException
  {
    model = new ModelManager();
    user = new Chatter("111111","Dumy_1","password_1");
    users.add(user);
    model.login("111111","Dumy_1","password_1");
    Channel newChannel = new Channel(15,"Music",5);
    model.receiveNewChannel(newChannel);
  }

  @Test void banUser_removes_the_user_from_the_chatter_room_list_of_that_room()
      throws SQLException, InterruptedException
  {
    model.banUser(user);
    Collection<UserInterface> users = Data.getInstance().getChatterDBManager().readAllByRoomID(6);
    ArrayList<UserInterface> usersArrayList = (ArrayList<UserInterface>) users;
    for (int i = 0; i < users.size(); i++)
    {
      if (usersArrayList.get(i).getViaId().equals(user.getViaId()))
      {
        assertTrue(false);
      }
    }
    assertTrue(true);
  }

  @Test void makeModerator() throws SQLException
  {
    Room temp = new Room(7,"doesntmatter","doesntmatter");
    model.makeModerator(user);
    assertTrue(Data.getInstance().getModeratorDBManager().isModeratorInRoom("111111",7));
  }

  @Test void isModerator_checks_the_database_and_returns_true_when_true() throws RemoteException
  {
    assertTrue(model.isModeratorInRoom(1));
  }

  @Test void isModerator_checks_the_database_and_returns_false_when_false() throws RemoteException
  {
    assertFalse(model.isModeratorInRoom(3));
  }

  @Test void addRoom_adds_a_room_to_the_database_with_the_next_id()
      throws SQLException
  {
    model.addRoom("newRoom","longercode",null);
    int roomid = model.getRoomId();
    Data.getInstance().getRoomDBManager().deleteRoom(11);
    assertEquals(11,roomid);
  }

  @Test void leaveRoom_removes_chatter_from_Chatter_roomlist()
      throws SQLException, InterruptedException
  {
    model.addRoom("newRoomGetId","longercode",null);
    Room room = new Room(5,"newRoomGetId","longercode");
    model.receiveNewRoom(room);
    model.joinRoom(room);
    Collection<UserInterface> whenJoined = Data.getInstance().getChatterDBManager().readAllByRoomID(5);
    int whenJoinedSize = whenJoined.size();
    model.leaveRoom();
    Collection<UserInterface> whenLeft = Data.getInstance().getChatterDBManager().readAllByRoomID(5);
    int whenLeftSize = whenLeft.size();
    assertEquals(whenJoinedSize-1,whenLeftSize);
  }

  @Test void getChannelsInRoom_returns_channels_in_room_from_database()
      throws SQLException
  {
   ArrayList<Channel> channelArrayListFromModel = model.getChannelsInRoom(1);
    ArrayList<Channel> channelArrayListFromDB = Data.getInstance().getChannelDBManager()
        .getChannelsByRoomID(1);
    assertEquals(channelArrayListFromDB.get(0).getId(),channelArrayListFromModel.get(0).getId());
  }

  @Test void joinRoom_adds_user_to_roomlist_in_database() throws SQLException
  {
    Room room = new Room(3,"newRoom","longercode");
    model.joinRoom(room);
    Collection<UserInterface> chatterRoomListFromDB = Data.getInstance().getChatterDBManager().readAllByRoomID(3);
    model.leaveRoom();
    assertTrue(chatterRoomListFromDB.contains(user));
  }

  @Test void editRoom_goes_to_database_and_changes_room() throws SQLException
  {
    model.addRoom("newRoom","longercode",null);
    Room room = new Room(7,"newRoom","longercode");
    model.joinRoom(room);
    model.editRoom("roomNameEdited","longercode",null);
    Room gotRoom = Data.getInstance().getRoomDBManager().readRoom(7);
    assertEquals("roomNameEdited",gotRoom.getName());
  }

  @Test void getRoomId_gets_the_id_of_current_rooom() throws SQLException
  {
    model.addRoom("newRoom","longercode",null);
    Room room = new Room(9,"newRoom","longercode");
    model.joinRoom(room);
    int temp = model.getRoomId();
    model.leaveRoom();
    assertEquals(9,temp);
  }

  @Test void getChannelId_gets_the_current_channel_the_user_is_in()
      throws SQLException
  {
    model.addRoom("newRoom","longercode",null);
    Room room = new Room(8,"newRoom","longercode");
    model.joinRoom(room);

    model.createChannel("newChannel");
    int tempid = model.getChannelId();

    model.leaveRoom();

    assertEquals(17,tempid);
  }

  @Test void createChannel() throws SQLException
  {
    model.addRoom("newRoom","longercode",null);
    Room room = new Room(6,"newRoom","longercode");
    model.joinRoom(room);

    model.createChannel("newChannel");
    Channel temp = Data.getInstance().getChannelDBManager().getChannelById(model.getChannelId());

    model.leaveRoom();

    assertEquals("newChannel",temp.getName());

  }


  @Test void editChannel() throws SQLException
  {
    model.addRoom("newRoom","longercode",null);
    Room room = new Room(6,"newRoom","longercode");
    model.joinRoom(room);

    model.createChannel("newChannel");
    model.editChannel(model.getChannelId(),"newName");
    Channel temp = Data.getInstance().getChannelDBManager().getChannelById(model.getChannelId());

    model.leaveRoom();

    assertEquals("newName",temp.getName());

  }

  @Test void editMessage_changes_the_specified_message_in_the_database() throws SQLException
  {
    model.editMessage(13,"newMessage");
    assertEquals(   "newMessage",Data.getInstance().getMessageDBManager().getLastMessage(14).getMessage());
  }

  @Test void getMessagesInChannel_from_database_matches_messages_in_channel_in_database()
      throws SQLException
  {
    ArrayList<Message> messagesList = model.getMessagesInChannel(10);
    Message expected = Data.getInstance().getMessageDBManager().readMessage(9);
    Message returned = messagesList.get(0);
    assertEquals(expected.getMessage(),returned.getMessage());
  }

  @Test void addMessage_adds_message_to_the_database() throws SQLException
  {
    model.addRoom("newRoom","longercode",null);
    Room room = new Room(12,"newRoom","longercode");
    model.joinRoom(room);
    model.createChannel("tempChannel");
    model.addMessage("newMessage3");
    Message temp = Data.getInstance().getMessageDBManager().getLastMessage(19);
    assertEquals("newMessage3",temp.getMessage());
  }

}
