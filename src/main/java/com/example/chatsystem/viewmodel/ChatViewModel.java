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
import java.util.List;

public class ChatViewModel implements ViewModel, PropertyChangeListener
{
    private Model model;
    private SimpleStringProperty textFieldProperty;
    private SimpleStringProperty currentMessage;
    private Image userImage;
    private PropertyChangeSupport support;

    public ChatViewModel(Model model)
    {
        this.model = model;
        this.textFieldProperty = new SimpleStringProperty();
        support = new PropertyChangeSupport(this);
        currentMessage = new SimpleStringProperty();
        model.addPropertyChangeListener("new message", this);
        model.addPropertyChangeListener("user", this);
    }

    public void onSendMessage(StringProperty messageProperty) throws IOException
    {
        messageProperty.set(textFieldProperty.get());
        bindCurrentMessageProperty(messageProperty);
        model.addMessage(textFieldProperty.get());
        textFieldProperty.set("");
    }

    public Image getUserImage()
    {
        return userImage;
    }

    public void loadMessages()
    {
        for (Message message:model.getMessages())
        {
            support.firePropertyChange("new message", null,
                    List.of(message, message.getUser().equals(model.getUser())));
        }
    }

    public void bindCurrentMessageProperty(StringProperty property)
    {
        currentMessage.bind(property);
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
                support.firePropertyChange("new message", null, List.of(evt.getNewValue(), false));
            case "user" ->
                userImage = ((User) evt.getNewValue()).getImage();
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
}
