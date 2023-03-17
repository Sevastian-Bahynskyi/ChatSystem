package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChatViewModel implements ViewModel
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

    public void onSendMessage(StringProperty messageProperty)
    {
        messageProperty.set(textFieldProperty.get());
        bindCurrentMessageProperty(messageProperty);
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
}
