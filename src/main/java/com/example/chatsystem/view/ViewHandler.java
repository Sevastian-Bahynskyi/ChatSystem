package com.example.chatsystem.view;

import com.example.chatsystem.viewmodel.ViewModelFactory;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class ViewHandler
{
    private Stage stage;
    private Scene currentScene;
    private PropertyChangeSupport support;
    private final ViewFactory viewFactory;
    private ViewModelFactory viewModelFactory;


    public ViewHandler(ViewModelFactory viewModelFactory)
    {
        this.viewFactory = new ViewFactory(this, viewModelFactory);
        this.currentScene = new Scene(new Region());
        this.viewModelFactory = viewModelFactory;
        this.support = new PropertyChangeSupport(this);
    }

    public void start(Stage stage)
    {
        this.stage = stage;
        openView(WINDOW.LOG);
    }

    public void start(Stage stage, WINDOW window)
    {
        this.stage = stage;
        openView(window);
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
        stage.centerOnScreen();
        stage.setResizable(true);

        stage.show();

        if(view == WINDOW.CHAT)
        {
            stage.setOnCloseRequest(event ->
            {
                try
                {
                    viewModelFactory.getModel().disconnect();
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                Platform.exit();
            });
        }

    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }

    public Stage getStage(Node node)
    {
        return stage;
    }

    private final Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/chatsystem/images/dead_screen.png")));



    public void openParallelView(WINDOW view) // todo -> delete
    {
        Stage parallelStage = new Stage();
        Region root = viewFactory.loadView(view);
        Scene parallelScene = new Scene(root);
        parallelStage.setScene(parallelScene);
        parallelStage.sizeToScene();
        parallelStage.setResizable(true);
        parallelStage.setAlwaysOnTop(true);
        parallelStage.show();
    }

    public void closeWindow(Region root)
    {
        Stage window = (Stage) root.getScene().getWindow();
        window.close();
    }
}
