package main.java.com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.*;
import com.example.chatsystem.model.DatabaseManagers.ChannelDBManager;
import com.example.chatsystem.server.client.Listener;
import com.example.chatsystem.view.ChatController;
import com.example.chatsystem.viewmodel.ChatViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ChatViewModelTest
{
  private Model modelMock;
  private ChatViewModel chatViewModel;
  private ChatController controllerMock;
  private String dummyString = "dummy message";
  private ArrayList<Message> messagesDummy = new ArrayList<>();
  private UserInterface Dummy = new Chatter("111111","Blahaj","shorks123");
  private Message dummyMessage = new Message("dummy message",Dummy,1);
  private ArrayList<UserInterface> dummyList = new ArrayList<>();

  private  ObjectProperty<Image> profileImage = new SimpleObjectProperty();
  private SimpleStringProperty textFieldProperty = new SimpleStringProperty();

  @BeforeEach void setUp() throws SQLException, IOException
  {
    dummyList.add(Dummy);
    modelMock  = Mockito.mock(Model.class);
    this.chatViewModel = new ChatViewModel(modelMock);
    Mockito.when(modelMock.addMessage(dummyString)).thenReturn(dummyMessage);
    Mockito.when(modelMock.getUserList()).thenReturn(dummyList);
    messagesDummy.add(dummyMessage);
    Mockito.when(modelMock.getMessages()).thenReturn(messagesDummy);

  }

  @Test void onSendMessage_calls_addmessage_in_model_and_returns_message()
      throws IOException
  {
    Mockito.when(modelMock.getUser()).thenReturn(Dummy);
    StringProperty temp = new SimpleStringProperty();
    chatViewModel.bindTextFieldProperty(temp);
    temp.setValue("dummy message");
    Message message = chatViewModel.onSendMessage();
    assertEquals(dummyMessage,message);
  }
  @Test void load_messages_throws_runs_the_for_loop()
  {
    Mockito.when(modelMock.getUser()).thenThrow(new ArithmeticException());
    assertThrows(ArithmeticException.class, () -> chatViewModel.loadMessages());
  }

  @Test void getUserImage_returns_an_Image_object()
  {
    String imageUrl =  "/com/example/chatsystem/images/default_user_avatar.png";
    Image tempImg = new Image(Chatter.class.getResourceAsStream(imageUrl));
    SimpleObjectProperty<Image> temp = new SimpleObjectProperty<>();
    chatViewModel.bindUserImage(temp);
    temp.setValue(tempImg);
    Image userImage;
    userImage = chatViewModel.getUserImage();
    assertEquals(tempImg.getPixelReader(),userImage.getPixelReader());
  }

  @Test void property_change_with_new_message_name_fires_next_property_change()
  {
    PropertyChangeEvent evt = new PropertyChangeEvent(chatViewModel,"new message",null,dummyMessage);
    chatViewModel.propertyChange(evt);
  }
  @Test void edit_user_allows_the_user_to_be_edited()
  {
    chatViewModel.editUser();
    assertTrue(false);
  }
  @Test void delete_user_allows_the_user_to_be_deleted()
  {
    chatViewModel.deleteUser();
    assertTrue(false);
  }
  @Test void edit_message_calls_model_edit_message() throws IOException
  {
    ArgumentCaptor<String> stringValue = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Integer> intValue = ArgumentCaptor.forClass(Integer.class);

    Mockito.doNothing().when(modelMock).editMessage(intValue.capture().intValue(),stringValue.capture());
    chatViewModel.editMessage(1,"dummy message captured");
    assertEquals("dummy message captured", stringValue.getValue());
  }
}