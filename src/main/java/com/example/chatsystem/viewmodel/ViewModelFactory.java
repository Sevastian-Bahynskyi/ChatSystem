package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.Model;
import com.example.chatsystem.view.*;

import java.beans.PropertyChangeListener;
import java.util.LongSummaryStatistics;

public class ViewModelFactory
{
    private ChatViewModel chatViewModel;
    private LoginViewModel loginViewModel;
    private RoomViewModel roomViewModel;
    private Model model;
    public ViewModelFactory(Model model)
    {
        this.chatViewModel = new ChatViewModel(model);
        this.loginViewModel = new LoginViewModel(model);
        this.roomViewModel = new RoomViewModel(model);
        this.model = model;
    }
    public ViewModel getViewModel(WINDOW window)
    {
        return switch (window)
        {
            case CHAT -> chatViewModel;
            case LOG -> loginViewModel;
            case ADD_ROOM, EDIT_ROOM -> roomViewModel;
            default -> null;
        };
    }

    public Model getModel()
    {
        return model;
    }
}
