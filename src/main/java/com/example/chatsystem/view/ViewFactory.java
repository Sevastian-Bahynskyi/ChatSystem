package com.example.chatsystem.view;

import com.example.chatsystem.viewmodel.BugViewModel;
import com.example.chatsystem.viewmodel.ViewModelFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;

import java.io.IOException;

enum WINDOW{
    CHAT, LOG, BUG
}
public class ViewFactory
{

    private final ViewHandler viewHandler;
    private final ViewModelFactory viewModelFactory;
    private Controller newController;
    private Controller currentController;

    public ViewFactory(ViewHandler viewHandler, ViewModelFactory viewModelFactory)
    {
        this.viewHandler = viewHandler;
        this.viewModelFactory = viewModelFactory;
        newController = null;
    }
    public Region loadView(WINDOW view)
    {
        String fxmlFile;
        switch (view)
        {
            case LOG -> fxmlFile = "login_view.fxml";
            case CHAT -> fxmlFile = "chat_view.fxml";
            case BUG ->
            {
                fxmlFile = "bug_view.fxml";
                return loadBugView(fxmlFile);
            }
            default -> throw new IllegalArgumentException("Didn't find the appropriate view.");
        }
        if (newController == null || currentController.getClass().equals(newController.getClass())) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlFile));
            try {
                Region root = loader.load();
                newController = loader.getController();
                newController.init(viewHandler, viewModelFactory.getViewModel(newController), root);

            } catch (IOException e) {
                e.printStackTrace();
            }
            currentController = newController;
        }
        return newController.getRoot();
    }

    private Region loadBugView(String fxmlFile)
    {
        BugController controller = null;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlFile));
        try {
            Region root = loader.load();
            controller = loader.getController();
            controller.init(viewHandler, new BugViewModel(), root);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return controller.getRoot();
    }
}
