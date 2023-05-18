package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class ChatViewModel implements ViewModel, PropertyChangeListener
{
    private Model model;
    private SimpleStringProperty textFieldProperty;
    private SimpleListProperty<Chatter> users;
    private ObjectProperty<Image> userImage;
    private ArrayList<Integer> messageIdList;
    private ArrayList<Channel> channelList;
    private PropertyChangeSupport support;


    public ChatViewModel(Model model)
    {
        this.model = model;
        this.textFieldProperty = new SimpleStringProperty();
        support = new PropertyChangeSupport(this);
        userImage = new SimpleObjectProperty<>();
        this.users = new SimpleListProperty<>();
        this.messageIdList = new ArrayList<>();
        this.channelList = new ArrayList<>();
        model.addPropertyChangeListener(this);
    }

    public Message onSendMessage() throws IOException
    {
        userImage.set(model.getUser().getImage());
        Message sentMessage = model.addMessage(textFieldProperty.get());
        messageIdList.add(sentMessage.getId());
        textFieldProperty.set("");
        return sentMessage;
    }

    public Image getUserImage()
    {
        return userImage.get();
    }

    public void loadMessages(int index) throws IOException
    {
        ArrayList<Message> messages = null;
        if(index == -1)
            messages = model.getMessages(-1);
        else
        {
            messages = model.getMessages(channelList.get(index).getId());
        }

        support.firePropertyChange("clear messages", null, true);
        for (Message message:messages)
        {
            messageIdList.add(message.getId());
            support.firePropertyChange("new message", null,
                    List.of(message, message.getUser().equals(model.getUser())));
        }
    }

    public void bindUserImage(ObjectProperty<Image> property)
    {
        userImage.bindBidirectional(property);
    }

    public void bindTextFieldProperty(StringProperty property)
    {
        property.bindBidirectional(textFieldProperty);
    }



    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        switch (evt.getPropertyName())
        {
            case "new message" ->
            {
                messageIdList.add(((Message) evt.getNewValue()).getId());
                userImage.set(((Message) evt.getNewValue()).getUser().getImage());
                support.firePropertyChange("new message", null, List.of(evt.getNewValue(), false));
            }

            case "user" -> {
                userImage.set(((Chatter) evt.getNewValue()).getImage());
                try
                {
                    loadMessages(-1);
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }

            case "update user list" -> {
                var newUsers = ((List<Chatter>) evt.getNewValue());
                Platform.runLater(() -> support.firePropertyChange("update user list", null, newUsers));
            }

            case "room added" -> {
                Room room = (Room) evt.getNewValue();
                Platform.runLater(() -> support.firePropertyChange("room added", null, room));
            }

            case "reload messages" -> {
                Platform.runLater(() -> support.firePropertyChange("reload messages", null, evt.getNewValue()));
            }

            case "reload message" -> {
                Message mes = (Message) evt.getNewValue();
                Platform.runLater(() -> support.firePropertyChange("reload message", null, List.of(mes, messageIdList.indexOf(mes.getId()))));
            }

            case "new channel" -> {

                Platform.runLater(() -> {
                    channelList.add(((Channel) evt.getNewValue()));
                    support.firePropertyChange("new channel", null, evt.getNewValue());
                });
            }

            case "load channels" -> {
                System.out.println(evt.getNewValue());
                ArrayList<Channel> channels = (ArrayList<Channel>) evt.getNewValue();
                Collections.reverse(channels);

                channelList.addAll(channels);


                support.firePropertyChange("load channels", null, evt.getNewValue());
            }

            case "reload channel" -> {
                Channel channel = (Channel) evt.getNewValue();
                int index = IntStream.range(0, channelList.size())
                        .filter(i -> channelList.get(i).getId() == channel.getId())
                        .findFirst()
                        .orElse(-1);

                Platform.runLater(() -> support.firePropertyChange("reload channel", null, List.of(channel, index)));
            }
        }
    }

    public boolean isMyMessage(Message message)
    {
        return message.getUser().equals(model.getUser());
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        support.removePropertyChangeListener(listener);
    }

    public void editUser()
    {
        System.out.println("edit option was chosen");
    }

    public void deleteUser()
    {
        System.out.println("delete option was chosen");
    }

    public void editMessage(int index, String newMessage)
    {
        try
        {

            model.editMessage(messageIdList.get(index), newMessage);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void deleteMessage(int index)
    {
        try
        {
            model.deleteMessage(messageIdList.get(index));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<UserInterface> getUsers() throws IOException
    {
        return model.getUserList();
    }

    public void addChannel(String channelName) throws IOException
    {
        model.createChannel(channelName);
    }

    public Channel getChannelByIndex(int index)
    {
        return this.channelList.get(index);
    }

    public void editChannel(String oldChannelName, String newChannelName) throws SQLException, IOException
    {
        for (var ch:channelList)
        {
            if(ch.getName().equals(oldChannelName))
                model.editChannel(ch.getId(), newChannelName);
        }
    }
}
