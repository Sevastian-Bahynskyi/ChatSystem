package com.example.chatsystem.viewmodel.ChatViewModelDelegates;

import com.example.chatsystem.model.*;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class ChatViewModel implements ViewModel, PropertyChangeListener
{
    protected PropertyChangeSupport support;
    protected Model model;
    protected SimpleStringProperty textFieldProperty;
    protected SimpleListProperty<Chatter> users;
    protected ObjectProperty<Image> userImage;
    protected ArrayList<Integer> messageIdList;
    protected ArrayList<Channel> channelList;
    protected ArrayList<Room> roomList;
    private ChannelHandler channelHandler;
    private MessageHandler messageHandler;
    private RoomHandler roomHandler;
    private UserHandler userHandler;


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
        channelHandler = new ChannelHandler(this);
        messageHandler = new MessageHandler(this);
        roomHandler = new RoomHandler(this);
        userHandler = new UserHandler(this);
    }

    public Message onSendMessage()
    {
        return messageHandler.onSendMessage();
    }

    public Image getUserImage()
    {
        return userImage.get();
    }

    public void loadMessagesByChannelIndex(int channelIndex)
    {
        messageHandler.loadMessagesByChannelIndex(channelIndex);
    }

    public boolean loadChannelsByRoomIndex(int roomIndex)
    {
        return channelHandler.loadChannelsByRoomIndex(roomIndex);
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
                Platform.runLater(() -> {
                    for (var ch:channelList)
                    {
                        if(ch.getId() == (int) evt.getNewValue())
                        {
                            int index = channelList.indexOf(ch);
                            support.firePropertyChange("delete channel", null, index);
                            channelList.remove(index);
                        }
                    }
                });

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
        return messageHandler.isMyMessage(message);
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
       messageHandler.editMessage(index,newMessage);
    }

    public void deleteMessage(int index)
    {
        messageHandler.deleteMessage(index);
    }

    public ArrayList<UserInterface> getUsers()
    {
        return model.getUserList();
    }

    public void addChannel(String channelName)
    {
        channelHandler.addChannel(channelName);
    }

    public Channel getChannelByIndex(int index)
    {
        return channelHandler.getChannelByIndex(index);
    }

    public void editChannel(String oldChannelName, String newChannelName)
    {
        channelHandler.editChannel(oldChannelName,newChannelName);
    }

    public void deleteChannel(int indexOfChannelToChange)
    {
        channelHandler.deleteChannel(indexOfChannelToChange);
    }

    public boolean isModerator(String userId, int channelId)
    {
        return  userHandler.isModerator(userId, channelId);
    }

    public boolean isModeratorInRoom(int roomId)
    {
        return userHandler.isModeratorInRoom(roomId);
    }

    public void loadEverything()
    {
        model.loadEverythingToTheViewModel();
    }

    public boolean amIModerator()
    {
        return userHandler.amIModerator();
    }

    public void banUser(UserInterface user)
    {
        userHandler.banUser(user);
    }

    public void makeModerator(UserInterface user)
    {
        userHandler.makeModerator(user);
    }

    public void joinRoom(Room room)
    {
        roomHandler.joinRoom(room);
    }

    public int getRoomIndex(int id)
    {
        return roomHandler.getRoomIndex(id);
    }

    public void leaveRoom()
    {
        roomHandler.leaveRoom();
    }
}
