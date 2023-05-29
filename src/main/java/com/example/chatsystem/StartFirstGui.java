package com.example.chatsystem;

import com.example.chatsystem.model.Model;
import com.example.chatsystem.model.ModelManagerDelegates.ModelManager;
import com.example.chatsystem.view.ViewHandler;
import com.example.chatsystem.view.WINDOW;
import com.example.chatsystem.viewmodel.ViewModelFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class StartFirstGui extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Model model = new ModelManager();
        model.login("111115", "Dumy_5", "password_5");
        ViewModelFactory viewModelFactory = new ViewModelFactory(model);
        ViewHandler viewHandler = new ViewHandler(viewModelFactory);
        viewHandler.start(primaryStage, WINDOW.CHAT);
    }
}
