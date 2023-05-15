package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatViewModel implements ViewModel, PropertyChangeListener
{
    private Model model;
    private SimpleStringProperty textFieldProperty;
    private SimpleListProperty<Chatter> users;
    private ObjectProperty<Image> userImage;
    private PropertyChangeSupport support;


    public ChatViewModel(Model model)
    {
        this.model = model;
        this.textFieldProperty = new SimpleStringProperty();
        support = new PropertyChangeSupport(this);
        userImage = new SimpleObjectProperty<>();
        this.users = new SimpleListProperty<>();
        model.addPropertyChangeListener(this);
    }

    public void onSendMessage() throws IOException
    {
        userImage.set(model.getUser().getImage());
        model.addMessage(textFieldProperty.get());
        textFieldProperty.set("");
    }

    public Image getUserImage()
    {
        return userImage.get();
    }

    public void loadMessages()
    {
        for (Message message:model.getMessages())
        {
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
                userImage.set(((Message) evt.getNewValue()).getUser().getImage());
                support.firePropertyChange("new message", null, List.of(evt.getNewValue(), false));
            }
            case "user" -> userImage.set(((Chatter) evt.getNewValue()).getImage());

            case "update user list" -> {
                var newUsers = ((List<Chatter>) evt.getNewValue());
                Platform.runLater(() -> support.firePropertyChange("update user list", null, newUsers));
            }

            case "room added" -> {
                Room room = (Room) evt.getNewValue();
                Platform.runLater(() -> support.firePropertyChange("room added", null, room));
            }
        }
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

    public ArrayList<UserInterface> getUsers() throws IOException
    {
        return model.getUserList();
    }
}
