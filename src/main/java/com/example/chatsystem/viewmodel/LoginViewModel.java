package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.Model;
import com.example.chatsystem.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class LoginViewModel implements ViewModel
{
    private Model model;
    private SimpleStringProperty usernameProperty;
    private SimpleStringProperty passwordProperty;
    private SimpleStringProperty errorProperty;
    public LoginViewModel(Model model)
    {
        this.model = model;
        usernameProperty = new SimpleStringProperty();
        passwordProperty = new SimpleStringProperty();
        errorProperty = new SimpleStringProperty("");
    }

    public void onLogin()
    {
        try
        {
            model.setUser(usernameProperty.get(), passwordProperty.get());
        }
        catch (Exception e)
        {
            errorProperty.set(e.getMessage());
        }
    }

    public void bindUsername(StringProperty property)
    {
        usernameProperty.bind(property);
    }

    public void bindError(StringProperty property)
    {
        errorProperty.bindBidirectional(property);
    }

    public void bindPassword(StringProperty property)
    {
        passwordProperty.bind(property);
    }
}
