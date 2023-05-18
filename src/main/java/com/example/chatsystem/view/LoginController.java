package com.example.chatsystem.view;

import com.example.chatsystem.view.setimage.GetImageAsFile;
import com.example.chatsystem.viewmodel.LoginViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.Objects;

public class LoginController implements Controller
{
    @FXML
    private PasswordField passwordField;

    @FXML
    private Circle userImage;

    @FXML
    private TextField usernameField, idField;
    @FXML
    private Label errorLabel, setImageLabel;
    @FXML
    private ImageView imageTest;
    @FXML
    private VBox parentNode;
    @FXML
    private HBox idBox;

    @FXML
    private Button loginButton;

    private ViewHandler viewHandler;
    private LoginViewModel viewModel;
    private Region root;

    private boolean isCurrentStateLogin = true;




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
        this.viewModel.bindId(idField.textProperty());
        this.setImageLabel.setManaged(false);
        this.idBox.setVisible(false);
        this.idBox.setManaged(false);
    }

    @FXML
    void onLogin()
    {
        if(isCurrentStateLogin && viewModel.onLogin())
            viewHandler.openView(WINDOW.CHAT);
        else if (!isCurrentStateLogin && viewModel.onRegister())
        {
            viewHandler.openView(WINDOW.CHAT);
        }
    }

    @FXML
    void onRegister(MouseEvent event)
    {
        Label register = (Label) event.getSource();
        isCurrentStateLogin = !isCurrentStateLogin;
        if(isCurrentStateLogin)
        {
            loginButton.setText("Login");
            register.setText("Register");
            usernameField.requestFocus();
            setImageLabel.setManaged(!isCurrentStateLogin);
        }
        else
        {
            loginButton.setText("Register");
            register.setText("Login");
        }

        idBox.setVisible(!idBox.isVisible());
        idBox.setManaged(idBox.isVisible());
        idField.requestFocus();

        setImageLabel.setVisible(!isCurrentStateLogin);
        setImageLabel.setManaged(!isCurrentStateLogin);
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
        if(event.getCode() == KeyCode.ENTER)
        {
            if (source.equals(usernameField))
            {
                passwordField.requestFocus();
            } else if (source.equals(passwordField))
            {
                onLogin();
            }
            else if(source.equals(idField))
            {
                usernameField.requestFocus();
            }
        }

    }

    @FXML
    void onSetImage(MouseEvent event)
    {
        try
        {
            String imageURL = GetImageAsFile.getImage(root.getScene().getWindow());
            assert imageURL != null;
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imageURL)));

            userImage.setFill(new ImagePattern(image));
            this.viewModel.setImageUrl(image.getUrl());
        } catch (Exception e)
        {
            errorLabel.setText(e.getMessage());
        }
    }

}
