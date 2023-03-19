package com.example.chatsystem.view;


import com.example.chatsystem.viewmodel.ViewModelFactory;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class ViewHandler
{
    private Stage stage;
    private Scene currentScene;
    private final ViewFactory viewFactory;

    public ViewHandler(ViewModelFactory viewModelFactory)
    {
        this.viewFactory = new ViewFactory(this, viewModelFactory);
        this.currentScene = new Scene(new Region());
    }

    public void start(Stage stage)
    {
        this.stage = stage;
        openView(WINDOW.LOG);
    }

    public void openView(WINDOW view)
    {
        Region root = viewFactory.loadView(view);
        currentScene.setRoot(root);
        if (root.getUserData() == null) {
            stage.setTitle("");
        } else {
            stage.setTitle(root.getUserData().toString());
        }
        stage.setScene(currentScene);
        stage.sizeToScene();
        stage.show();
    }
}
