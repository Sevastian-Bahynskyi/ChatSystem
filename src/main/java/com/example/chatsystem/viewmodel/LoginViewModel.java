package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.Model;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

    public boolean onLogin()
    {
        try
        {
            model.login(usernameProperty.get(), passwordProperty.get());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errorProperty.set(e.getMessage());
            return false;
        }

        return true;
    }

    public void bindUsername(StringProperty property)
    {
        usernameProperty.bind(property);
    }

    public void bindError(StringProperty property)
    {
        property.bindBidirectional(errorProperty);
    }

    public void bindPassword(StringProperty property)
    {
        passwordProperty.bind(property);
    }

    public boolean onRegister()
    {
        try
        {
            model.register(usernameProperty.get(), passwordProperty.get());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errorProperty.set(e.getMessage());
            return false;
        }

        return true;
    }
}
