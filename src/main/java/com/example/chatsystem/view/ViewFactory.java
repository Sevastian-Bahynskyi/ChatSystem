package com.example.chatsystem.view;

import com.example.chatsystem.viewmodel.ViewModelFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;

import java.io.IOError;
import java.io.IOException;

enum WINDOW{
    CHAT, LOG
}
public class ViewFactory
{

    private final ViewHandler viewHandler;
    private final ViewModelFactory viewModelFactory;
    private Controller controller;

    public ViewFactory(ViewHandler viewHandler, ViewModelFactory viewModelFactory)
    {
        this.viewHandler = viewHandler;
        this.viewModelFactory = viewModelFactory;
        controller = null;
    }
    public Region loadView(WINDOW view)
    {
        String fxmlFile;
        switch (view)
        {
            case LOG -> fxmlFile = "login_view.fxml";
            case CHAT -> fxmlFile = "chat_view.fxml";
            default -> throw new IllegalArgumentException("Didn't find the appropriate view.");
        }
        if (controller == null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlFile));
            try {
                Region root = loader.load();
                controller = loader.getController();
                controller.init(viewHandler, viewModelFactory.getViewModel(controller), root);

            } catch (IOException e) {
                throw new IOError(e);
            }
        }
        return controller.getRoot();
    }
}
