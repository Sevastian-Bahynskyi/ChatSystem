package com.example.chatsystem.view;

import com.example.chatsystem.model.Data;
import com.example.chatsystem.viewmodel.LoginViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class LoginController implements Controller
{
    @FXML
    private PasswordField passwordField;

    @FXML
    private Circle userImage;

    @FXML
    private TextField usernameField;
    @FXML
    private Label errorLabel;
    @FXML
    private ImageView imageTest;

    private ViewHandler viewHandler;
    private LoginViewModel viewModel;
    private Region root;


    @Override
    public void init(ViewHandler viewHandler, ViewModel viewModel, Region root)
    {
        this.viewHandler = viewHandler;
        this.root = root;
        this.viewModel = (LoginViewModel) viewModel;
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/chatsystem/images/default_user_avatar.png")));
        this.userImage.setFill(new ImagePattern(image));

        this.viewModel.bindPassword(passwordField.textProperty());
        this.viewModel.bindUsername(usernameField.textProperty());
        this.viewModel.bindError(errorLabel.textProperty());
    }

    @FXML
    void onLogin() {
        if(viewModel.onLogin()) // if login successfull
            viewHandler.openView(WINDOW.CHAT);
    }

    @FXML
    void onRegister() {

    }

    @Override
    public Region getRoot()
    {
        return root;
    }

    @FXML
    void onKeyPressed(KeyEvent event)
    {
        Object source = event.getSource();
        if (source.equals(usernameField) && event.getCode().equals(KeyCode.ENTER))
        {
            passwordField.requestFocus();
        } else if (source.equals(passwordField) && event.getCode().equals(KeyCode.ENTER))
        {
            onLogin();
        }
    }

    @FXML
    void onBug() throws InterruptedException
    {
        for (int i = 0; i < 100; i++)
        {
            Thread newThread = new Thread(() ->
            {
                Platform.runLater(() -> viewHandler.openParallelView(WINDOW.BUG));
            });
            newThread.setDaemon(true);
            newThread.start();

//            Thread.sleep(200);
        }
    }
}
