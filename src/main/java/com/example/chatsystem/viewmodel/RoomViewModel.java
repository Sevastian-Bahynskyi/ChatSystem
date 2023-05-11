package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.Model;
import com.example.chatsystem.model.ModelManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Random;

public class RoomViewModel implements ViewModel
{
    private ModelManager modelManager;
    private StringProperty nameFieldProperty, codeFieldProperty;

    public RoomViewModel(Model model)
    {
        this.modelManager = (ModelManager) model;
        this.nameFieldProperty = new SimpleStringProperty();
        this.codeFieldProperty = new SimpleStringProperty();
    }

    public void bindNameField(StringProperty nameFieldProperty)
    {
        this.nameFieldProperty.bindBidirectional(nameFieldProperty);
    }

    public void bindCodeField(StringProperty codeFieldProperty)
    {
        this.codeFieldProperty.bindBidirectional(codeFieldProperty);
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
}
