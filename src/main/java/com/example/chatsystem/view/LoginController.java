package com.example.chatsystem.view;

import com.example.chatsystem.viewmodel.LoginViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class LoginController implements Controller
{
    @FXML
    private PasswordField passwordField;

    @FXML
    private Circle userImage;

    @FXML
    private TextField usernameField;
    @FXML
    private Label errorLabel, setImageLabel;
    @FXML
    private ImageView imageTest;
    @FXML
    private VBox parentNode;

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
        this.setImageLabel.setManaged(false);
    }

    @FXML
    void onLogin()
    {
        if(isCurrentStateLogin && viewModel.onLogin()) // if login successfull
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
            setImageLabel.setManaged(!isCurrentStateLogin);
        }
        else
        {
            loginButton.setText("Register");
            register.setText("Login");
        }
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
        if (source.equals(usernameField) && event.getCode().equals(KeyCode.ENTER))
        {
            passwordField.requestFocus();
        } else if (source.equals(passwordField) && event.getCode().equals(KeyCode.ENTER))
        {
            onLogin();
        }
    }

    @FXML
    void onSetImage(MouseEvent event)
    {
        AtomicReference<String> imageURL = new AtomicReference<>();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        File recordsDir = new File(System.getProperty("user.home") + "/Pictures");
        fileChooser.setInitialDirectory(recordsDir);
        Window window = parentNode.getScene().getWindow();
        File selectedImage = fileChooser.showOpenDialog(window);
        if(selectedImage == null)
            return;
        Path sourcePath = selectedImage.toPath();
        AtomicReference<Path> destinationPath = new AtomicReference<>(Paths.get("src/main/resources/com/example/chatsystem/images/" + selectedImage.getName()));
        
        
        Platform.runLater(() -> {
            try
            {
                Files.copy(sourcePath, destinationPath.get(), StandardCopyOption.REPLACE_EXISTING);
                destinationPath.set(Paths.get("target/classes/com/example/chatsystem/images/" + selectedImage.getName()));
                Files.copy(sourcePath, destinationPath.get(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e)
            {
                errorLabel.setText("Unable to choose image.");
            }
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/chatsystem/images/" + selectedImage.getName())));
            userImage.setFill(new ImagePattern(image));
            imageURL.set("/com/example/chatsystem/images/" + selectedImage.getName());
            System.out.println(imageURL.get());
            this.viewModel.setImageUrl(imageURL.get());
        });

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
