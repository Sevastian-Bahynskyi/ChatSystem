package com.example.chatsystem.view;


import com.example.chatsystem.viewmodel.ViewModelFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.*;
import java.util.Random;

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
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }
    private Image image = new Image(getClass().getResourceAsStream("/com/example/chatsystem/images/dead_screen.png"));


    public void openParallelView(WINDOW view) // todo -> delete
    {
        Stage parallelStage = new Stage();
        Region root = viewFactory.loadView(view);
        Scene parallelScene = new Scene(root);
        Random random = new Random();
        parallelStage.setScene(parallelScene);
        parallelStage.sizeToScene();
        parallelStage.setX(random.nextInt((int) (Toolkit.getDefaultToolkit().getScreenSize().width)));
        parallelStage.setY(random.nextInt((int) (Toolkit.getDefaultToolkit().getScreenSize().height)));
        parallelStage.setResizable(false);
        ImageView imageView = (ImageView) root.lookup("#deadImage"); // Replace "imageView" with the ID of your ImageView
        imageView.setImage(image);
        parallelStage.show();
    }
}
