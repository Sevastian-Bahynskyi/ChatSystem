package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

public class ChatViewModel implements ViewModel, PropertyChangeListener
{
    private Model model;
    private SimpleStringProperty textFieldProperty;
    private SimpleStringProperty currentMessage;
    private PropertyChangeSupport support;

    public ChatViewModel(Model model)
    {
        this.model = model;
        this.textFieldProperty = new SimpleStringProperty();
        support = new PropertyChangeSupport(this);
        currentMessage = new SimpleStringProperty();
        model.addPropertyChangeListener("new message", this);
    }

    public void onSendMessage(StringProperty messageProperty) throws IOException
    {
        messageProperty.set(textFieldProperty.get());
        bindCurrentMessageProperty(messageProperty);
        model.addMessage(textFieldProperty.get());
        textFieldProperty.set("");
    }

    public void loadMessages()
    {
        for (Message message:model.getMessages())
        {
            support.firePropertyChange("new message", null, message);
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
        if(evt.getPropertyName().equals("new message"))
        {
            support.firePropertyChange("new message", null, evt.getNewValue());
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
