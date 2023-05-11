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
    private String imageUrl;
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
            errorProperty.set(e.getMessage());
            return false;
        }

        return true;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
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
            //needs update from GUI
            model.register("123456",usernameProperty.get(), passwordProperty.get(), imageUrl);
        } catch (Exception e)
        {
            errorProperty.set(e.getMessage());
            return false;
        }

        return true;
    }
}
