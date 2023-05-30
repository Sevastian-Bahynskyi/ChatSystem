package com.example.chatsystem.viewmodel.ChatViewModelDelegates;

import com.example.chatsystem.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageHandler
{
  private ChatViewModel vm;

  public MessageHandler(ChatViewModel c)
  {
    this.vm = c;
  }

  public Message onSendMessage()
  {
    // images are half done, but they are not stored in the database
    // so all the user images are default, as if user didn't choose the image during registrations
    vm.userImage.set(vm.model.getUser().getImage());
    // view model calls model and pass string message
    Message sentMessage = vm.model.addMessage(vm.textFieldProperty.get());
    // new message is added to messageIdList
    // such a decision was made, because if user choose to edit message or delete it. Controller doesn't know ID of the message.
    // so it passes index to viewmodel, here it gets the ID by provided by controller index and passes the ID to the model
    vm.messageIdList.add(sentMessage.getId());
    // reset textfield
    vm.textFieldProperty.set("");
    return sentMessage;
  }

  public void loadMessagesByChannelIndex(int channelIndex)
  {
    ArrayList<Message> messages = null;
    if(channelIndex == -1)
      messages = vm.model.getMessagesInChannel(-1);
    else
    {
      messages = vm.model.getMessagesInChannel(vm.channelList.get(channelIndex).getId());
    }

    vm.support.firePropertyChange("clear messages", null, true);
    vm.messageIdList.clear();
    for (Message message:messages)
    {
      vm.messageIdList.add(message.getId());
      vm.support.firePropertyChange("new message", null,
          List.of(message, message.getUser().equals(vm.model.getUser())));
    }
  }

  public boolean isMyMessage(Message message)
  {
    return message.getUser().equals(vm.model.getUser());
  }

  public void editMessage(int index, String newMessage)
  {
    vm.model.editMessage(vm.messageIdList.get(index), newMessage);
  }

  public void deleteMessage(int index)
  {
    try
    {
      vm.model.deleteMessage(vm.messageIdList.get(index));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}