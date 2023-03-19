package com.example.chatsystem.view;

import com.example.chatsystem.model.Data;
import com.example.chatsystem.viewmodel.LoginViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.util.Objects;

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


    private ViewHandler viewHandler;
    private LoginViewModel viewModel;
    private Region root;


    @Override
    public void init(ViewHandler viewHandler, ViewModel viewModel, Region root)
    {
        this.viewHandler = viewHandler;
        this.root = root;
        this.viewModel = (LoginViewModel) viewModel;
        this.userImage.setFill(new ImagePattern(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(Data.getDefaultImageUrl())))));
        this.viewModel.bindPassword(passwordField.textProperty());
        this.viewModel.bindUsername(usernameField.textProperty());
        this.viewModel.bindError(errorLabel.textProperty());
    }

    @FXML
    void onLogin() {
        viewModel.onLogin();
    }

    @FXML
    void onRegister() {

    }

    @Override
    public Region getRoot()
    {
        return root;
    }


}
