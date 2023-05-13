package com.example.chatsystem.view;

import com.example.chatsystem.view.setimage.GetImageAsFile;
import com.example.chatsystem.viewmodel.RoomViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.Objects;

public class AddRoomController implements Controller
{

    @FXML
    private Button createRoomButton;

    @FXML
    private Label errorLabel, generateCode, setImageLabel;

    @FXML
    private VBox parentNode;

    @FXML
    private Circle roomImage;

    @FXML
    private TextField roomNameField, codeField;


    private Region root;
    private RoomViewModel viewModel;
    private ViewHandler viewHandler;


    @Override
    public void init(ViewHandler viewHandler, ViewModel viewModel, Region root) throws IOException
    {
        this.viewHandler = viewHandler;
        this.viewModel = (RoomViewModel) viewModel;
        this.root = root;

        this.viewModel.bindCodeField(codeField.textProperty());
        this.viewModel.bindNameField(roomNameField.textProperty());
        this.viewModel.bindErrorField(errorLabel.textProperty());
    }

    @FXML
    void onCreateRoom()
    {
        viewModel.onCreateRoom();
        viewHandler.closeWindow(root);
    }

    @FXML
    void onGenerateCode()
    {
        viewModel.generateCode();
    }

    @FXML
    void onKeyPressed(KeyEvent event)
    {
        if(event.getCode() == KeyCode.ENTER)
        {

            if (event.getSource().equals(roomNameField))
            {
                codeField.requestFocus();
            } else if (event.getSource().equals(codeField))
            {
                onCreateRoom();
            }
        }
    }

    @FXML
    void onSetImage()
    {
        try
        {
            String imageURL = GetImageAsFile.getImage(root.getScene().getWindow());
            if(imageURL == null)
                return;
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imageURL)));

            roomImage.setFill(new ImagePattern(image));
            this.viewModel.setImageUrl(imageURL);

        } catch (Exception e)
        {
            e.printStackTrace();
            errorLabel.setText(e.getMessage());
        }
    }





    @Override
    public Region getRoot()
    {
        return root;
    }
}
