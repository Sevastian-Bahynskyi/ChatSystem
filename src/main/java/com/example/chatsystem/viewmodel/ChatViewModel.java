package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private ArrayList<Room> roomList;
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
        this.roomList = new ArrayList<>();
        model.addPropertyChangeListener(this);
    }

    public Message onSendMessage()
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

    public void loadMessagesByChannelIndex(int channelIndex)
    {
        ArrayList<Message> messages = null;
        if(channelIndex == -1)
            messages = model.getMessages(-1);
        else
        {
            messages = model.getMessages(channelList.get(channelIndex).getId());
        }

        support.firePropertyChange("clear messages", null, true);
        messageIdList.clear();
        for (Message message:messages)
        {
            messageIdList.add(message.getId());
            support.firePropertyChange("new message", null,
                    List.of(message, message.getUser().equals(model.getUser())));
        }
    }

    public boolean loadChannelsByRoomIndex(int roomIndex)
    {
        ArrayList<Channel> channels = null;
        if(roomIndex == -1)
            channels = model.getChannels(-1);
        else
        {
            channels = model.getChannels(roomList.get(roomIndex).getId());
        }

        if(channels == null)
            return false;

        support.firePropertyChange("update user list", null, model.getUserList());
        support.firePropertyChange("clear channels", null, true);
        channelList.clear();
        channelList.addAll(channels);
        for (var ch:channels)
        {
            support.firePropertyChange("new channel", null, ch);
        }

        if(!channelList.isEmpty())
            loadMessagesByChannelIndex(channelList.size() - 1);

        return true;
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
                userImage.set(((UserInterface) evt.getNewValue()).getImage());
                loadMessagesByChannelIndex(-1);
            }

            case "update user list" -> {
                var newUsers = ((List<Chatter>) evt.getNewValue());
                Platform.runLater(() -> support.firePropertyChange("update user list", null, newUsers));
            }

            case "room added" -> {
                Room room = (Room) evt.getNewValue();
                roomList.add(room);
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
                channelList.add(((Channel) evt.getNewValue()));
                support.firePropertyChange("new channel", null, evt.getNewValue());
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

            case "delete channel" -> {
                for (var ch:channelList)
                {
                    if(ch.getId() == (int) evt.getNewValue())
                    {
                        int index = channelList.indexOf(ch);
                        support.firePropertyChange("delete channel", null, index);
                        channelList.remove(index);
                    }
                }
            }

            case "load rooms" -> {
                ArrayList<Room> rooms = (ArrayList<Room>) evt.getNewValue();
                roomList.addAll(rooms);
                support.firePropertyChange("load rooms", null, evt.getNewValue());
            }

            case "reload room" -> {
                Room room = (Room) evt.getNewValue();
                int index = IntStream.range(0, roomList.size())
                        .filter(i -> roomList.get(i).getId() == room.getId())
                        .findFirst()
                        .orElse(-1);

                Platform.runLater(() -> support.firePropertyChange("reload room", null, List.of(room, index)));
            }

            case "join a room" -> {
                Platform.runLater(() -> support.firePropertyChange("join a room", null, evt.getNewValue()));
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

    public void editMessage(int index, String newMessage)
    {
        model.editMessage(messageIdList.get(index), newMessage);
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

    public ArrayList<UserInterface> getUsers()
    {
        return model.getUserList();
    }

    public void addChannel(String channelName)
    {
        model.createChannel(channelName);
    }

    public Channel getChannelByIndex(int index)
    {
        return this.channelList.get(index);
    }

    public void editChannel(String oldChannelName, String newChannelName)
    {
        for (var ch:channelList)
        {
            if(ch.getName().equals(oldChannelName) && model.editChannel(ch.getId(), newChannelName))
            {
                ch.setName(newChannelName);
            }
        }
    }

    public void deleteChannel(int indexOfChannelToChange)
    {
        model.deleteChannel(channelList.get(indexOfChannelToChange).getId());
    }

    public boolean isModerator(int channelId)
    {
        try
        {
            return model.isModerator(channelId);
        } catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean isModeratorInRoom(int roomId)
    {
        return model.isModeratorInRoom(roomId);
    }

    public void loadEverything()
    {
        model.loadEverything();
    }

    public boolean amIModerator()
    {
        return model.getUser().isModerator();
    }

    public void banUser(UserInterface user)
    {
        model.banUser(user);
    }

    public void makeModerator(UserInterface user)
    {
        user = new Moderator(user);
        model.makeModerator(user);
    }

    public void selectRoom(Room room)
    {
        for (var r:roomList)
        {
            if(r.getId() == room.getId())
            {
                loadChannelsByRoomIndex(roomList.indexOf(r));
            }
        }
    }

    public void joinRoom(Room room)
    {
        model.joinRoom(room);
    }

    public int getRoomIndex(int id)
    {
        for (var r:roomList)
        {
            if(r.getId() == id)
                return roomList.indexOf(r);
        }
        return -1;
    }
}
