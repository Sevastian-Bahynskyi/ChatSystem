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
    public ViewModel getViewModel(Controller controller)
    {
        if(controller instanceof ChatController)
        {
            return chatViewModel;
        }
        else if(controller instanceof LoginController)
            return loginViewModel;
        else if(controller instanceof AddRoomController)
            return roomViewModel;

        return null;
    }

    public Model getModel()
    {
        return model;
    }
}
