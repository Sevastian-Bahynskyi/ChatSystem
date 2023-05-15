package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.Model;
import com.example.chatsystem.model.ModelManager;
import com.example.chatsystem.model.Room;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Random;

public class RoomViewModel implements ViewModel
{
    private ModelManager modelManager;
    private StringProperty nameFieldProperty, codeFieldProperty, errorLabelProperty;
    private String imageUrl;

    public RoomViewModel(Model model)
    {
        this.modelManager = (ModelManager) model;
        this.nameFieldProperty = new SimpleStringProperty();
        this.codeFieldProperty = new SimpleStringProperty();
        this.errorLabelProperty = new SimpleStringProperty();
    }

    public void bindNameField(StringProperty property)
    {
        this.nameFieldProperty.bindBidirectional(property);
    }

    public void bindCodeField(StringProperty property)
    {
        this.codeFieldProperty.bindBidirectional(property);
    }

    public void bindErrorField(StringProperty property)
    {
        property.bindBidirectional(errorLabelProperty);
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void generateCode()
    {
        int codeLength = 8;
        StringBuilder chars = new StringBuilder();
        for (char i = 'A'; i <= 'Z'; i++)
        {
            chars.append(i);
        }

        for (char i = '0'; i < '9'; i++)
        {
            chars.append(i);
        }

        for (int i = 'a'; i < 'z'; i++)
        {
            chars.append(i);
        }

        StringBuilder codeBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < codeLength; i++) {
            int index = random.nextInt(chars.length());
            codeBuilder.append(chars.charAt(index));
        }

        codeFieldProperty.setValue(codeBuilder.toString());
    }


    public void onCreateRoom()
    {
        try
        {
            modelManager.addRoom(nameFieldProperty.getValue(), codeFieldProperty.getValue(), imageUrl);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errorLabelProperty.setValue(e.getMessage());
        }
    }

}
