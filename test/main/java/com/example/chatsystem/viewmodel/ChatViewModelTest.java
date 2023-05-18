package main.java.com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.DatabaseManagers.ChannelDBManager;
import com.example.chatsystem.model.Model;
import com.example.chatsystem.viewmodel.ChatViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ChatViewModelTest
{
  private Model modelMock;

  @BeforeEach void setUp() throws SQLException
  {
    modelMock = Mockito.mock(Model.class);
    ChatViewModel chatViewModel = new ChatViewModel(modelMock);
    Mockito.when()
  }

  @Test void onSendMessage()
  {

  }

  @Test void deleteMessage()
  {
  }

  @Test void getUsers()
  {
  }
}