package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.Model;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ChatViewModel implements ViewModel
{
    private Model model;
    private SimpleStringProperty textFieldProperty;

    public ChatViewModel(Model model)
    {
        this.model = model;
        this.textFieldProperty = new SimpleStringProperty();
    }

    public void onSendMessage()
    {

    }

    public void bindTextFieldProperty(StringProperty property)
    {
        property.bind(textFieldProperty);
    }
}
