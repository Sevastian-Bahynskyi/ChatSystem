package com.example.chatsystem;

import com.example.chatsystem.model.Model;
import com.example.chatsystem.model.ModelManager;
import com.example.chatsystem.view.ViewHandler;
import com.example.chatsystem.view.WINDOW;
import com.example.chatsystem.viewmodel.ViewModelFactory;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class StartSecondClient extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Model model = new ModelManager();
        ViewModelFactory viewModelFactory = new ViewModelFactory(model);
        ViewHandler viewHandler = new ViewHandler(viewModelFactory);
        model.login("111112", "Dumy_2", "password_2");
        viewHandler.start(primaryStage, WINDOW.CHAT);
    }
}
