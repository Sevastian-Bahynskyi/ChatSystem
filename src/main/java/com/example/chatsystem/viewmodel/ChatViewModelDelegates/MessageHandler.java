package com.example.chatsystem.viewmodel.ChatViewModelDelegates;

import com.example.chatsystem.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageHandler
{
  private ChatViewModel c;

  public MessageHandler(ChatViewModel c)
  {
    this.c = c;
  }

  public Message onSendMessage()
  {
    c.userImage.set(c.model.getUser().getImage());
    Message sentMessage = c.model.addMessage(c.textFieldProperty.get());
    c.messageIdList.add(sentMessage.getId());
    c.textFieldProperty.set("");
    return sentMessage;
  }

  public void loadMessagesByChannelIndex(int channelIndex)
  {
    ArrayList<Message> messages = null;
    if(channelIndex == -1)
      messages = c.model.getMessagesInChannel(-1);
    else
    {
      messages = c.model.getMessagesInChannel(c.channelList.get(channelIndex).getId());
    }

    c.support.firePropertyChange("clear messages", null, true);
    c.messageIdList.clear();
    for (Message message:messages)
    {
      c.messageIdList.add(message.getId());
      c.support.firePropertyChange("new message", null,
          List.of(message, message.getUser().equals(c.model.getUser())));
    }
  }

  public boolean isMyMessage(Message message)
  {
    return message.getUser().equals(c.model.getUser());
  }

  public void editMessage(int index, String newMessage)
  {
    c.model.editMessage(c.messageIdList.get(index), newMessage);
  }

  public void deleteMessage(int index)
  {
    try
    {
      c.model.deleteMessage(c.messageIdList.get(index));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}