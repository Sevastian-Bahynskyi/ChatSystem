package com.example.chatsystem.viewmodel;

import com.example.chatsystem.model.Model;
import com.example.chatsystem.view.ChatController;
import com.example.chatsystem.view.Controller;

public class ViewModelFactory
{
    private ChatViewModel chatViewModel;
    public ViewModelFactory(Model model)
    {
        this.chatViewModel = new ChatViewModel(model);
    }
    public ViewModel getViewModel(Controller controller)
    {
        if(controller instanceof ChatController)
        {
            return chatViewModel;
        }
//        else if(controller instanceof)

        return null;
    }
}
