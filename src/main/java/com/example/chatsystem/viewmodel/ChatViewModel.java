package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.Model;
import com.example.chatsystem.model.User;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.effect.Light;
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
    private ObjectProperty<Image> userImage;
    private PropertyChangeSupport support;

    public ChatViewModel(Model model)
    {
        this.model = model;
        this.textFieldProperty = new SimpleStringProperty();
        support = new PropertyChangeSupport(this);
        userImage = new SimpleObjectProperty<>();
        model.addPropertyChangeListener("new message", this);
        model.addPropertyChangeListener("user", this);
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
            case "user" ->
            {
                userImage.set(((User) evt.getNewValue()).getImage());
            }
        }
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        support.removePropertyChangeListener(propertyName, listener);
    }

    public ArrayList<User> getUsers() throws IOException
    {
        return model.getUserList();
    }
}
