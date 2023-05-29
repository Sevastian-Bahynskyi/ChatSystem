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
    user = new Chatter("111122","Seashells","Seashore1");
    users.add(user);
    model.login("111122","Seashells","Seashore1");
    Channel newChannel = new Channel(15,"Music",5);
    model.receiveNewChannel(newChannel);
  }

  @Test void receiveUsersInRoom_calls_delegate_methods_and_adds_it_to_object()
  {
    model.receiveUsersInRoom(users);
    ArrayList<UserInterface> listFromModel = model.getUserList();
    ArrayList<UserInterface> testAgainstList = new ArrayList<>();
    testAgainstList.add(user);
    assertEquals(testAgainstList.get(0),listFromModel.get(0));
  }

  @Test void banUser_removes_the_user_from_the_chatter_room_list_of_that_room() throws SQLException
  {
    Room room = new Room(5,"roomname","doesntmater");
    model.joinRoom(room);
    model.banUser(user);
    Collection<UserInterface> users = Data.getInstance().getChatterDBManager().readAllByRoomID(5);
    ArrayList<UserInterface> usersArrayList = (ArrayList<UserInterface>) users;
    for (int i = 0; i < users.size(); i++)
    {
      if (usersArrayList.get(i).getUsername().equals(user.getUsername()))
      {
        assertTrue(true);
      }
    }
    assertTrue(false);
  }

  @Test void makeModerator()
  {
    model.makeModerator(user);
  }

  @Test void isModerator() throws RemoteException
  {
    model.isModerator(1);
  }

  @Test void isModeratorInRoom()
  {
    model.isModeratorInRoom(1);
  }

  @Test void getUserList()
  {
    model.getUserList();
  }

  @Test void addRoom()
  {
    model.addRoom("newRoom","code",null);
  }

  @Test void leaveRoom()
  {
    model.leaveRoom();
  }

  @Test void getChannelsInRoom()
  {
    model.getChannelsInRoom(1);
  }

  @Test void joinRoom()
  {
    Room room = new Room(9,"newRoom","code");
    model.joinRoom(room);
  }

  @Test void editRoom()
  {
    model.editRoom("newRoom","code",null);
  }

  @Test void getRoomId()
  {
    model.getRoomId();
  }

  @Test void receiveNewRoom()
  {
    model.receiveNewRoom(new Room(7,"newRoom","code"));
  }

  @Test void getChannelId()
  {
    model.getChannelId();
  }

  @Test void createChannel()
  {
    model.createChannel("newChannel");
  }

  @Test void receiveNewChannel()
  {
    model.receiveNewChannel(new Channel(11,"channel11",1));
  }

  @Test void editChannel()
  {
    model.editChannel(2,"newname");
  }

  @Test void reloadChannel()
  {
    model.reloadChannel(new Channel(12,"channel11",1));
  }

  @Test void deleteChannel()
  {
    model.deleteChannel(11);
  }

  @Test void receiveChannelToRemove()
  {
    model.receiveChannelToRemove(12);
  }

  @Test void sendOthersMessage()
  {
    model.sendOthersMessage(new Message("message",user,3));
  }

  @Test void deleteMessage()
  {
    model.deleteMessage(5);
  }

  @Test void editMessage()
  {
    model.editMessage(6,"newMessage");
  }

  @Test void getMessagesInChannel()
  {
    model.getMessagesInChannel(10);
  }

  @Test void reloadMessages()
  {
    model.reloadMessages(new ArrayList<>());
  }

  @Test void reloadMessage()
  {
    model.reloadMessage(new Message("message2",user,3));
  }

  @Test void addMessage()
  {
    model.addMessage("newMessage3");
  }

}
