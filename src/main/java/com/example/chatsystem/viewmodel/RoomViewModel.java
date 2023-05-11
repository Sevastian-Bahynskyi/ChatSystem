package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.Model;
import com.example.chatsystem.model.ModelManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
}
