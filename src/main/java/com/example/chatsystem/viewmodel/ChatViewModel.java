package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

public class ChatViewModel implements ViewModel, PropertyChangeListener
{
    private Model model;
    private SimpleStringProperty textFieldProperty;
    private SimpleStringProperty currentMessage;

    public ChatViewModel(Model model)
    {
        this.model = model;
        this.textFieldProperty = new SimpleStringProperty();
        currentMessage = new SimpleStringProperty();
    }

    public void onSendMessage(StringProperty messageProperty) throws IOException
    {
        messageProperty.set(textFieldProperty.get());
        bindCurrentMessageProperty(messageProperty);
        model.addMessage(textFieldProperty.get());
        textFieldProperty.set("");
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
            System.out.println(evt.getNewValue().toString());
        }
    }
}
