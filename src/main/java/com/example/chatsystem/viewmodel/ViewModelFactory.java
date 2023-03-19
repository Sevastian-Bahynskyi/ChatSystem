package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.Model;
import com.example.chatsystem.view.ChatController;
import com.example.chatsystem.view.Controller;
import com.example.chatsystem.view.LoginController;

public class ViewModelFactory
{
    private ChatViewModel chatViewModel;
    private LoginViewModel loginViewModel;
    public ViewModelFactory(Model model)
    {
        this.chatViewModel = new ChatViewModel(model);
        this.loginViewModel = new LoginViewModel(model);
    }
    public ViewModel getViewModel(Controller controller)
    {
        if(controller instanceof ChatController)
        {
            return chatViewModel;
        }
        else if(controller instanceof LoginController)
            return loginViewModel;

        return null;
    }
}
