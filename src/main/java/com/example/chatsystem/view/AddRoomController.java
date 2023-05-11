package com.example.chatsystem.view;

import com.example.chatsystem.viewmodel.RoomViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.Random;

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
    }

    @FXML
    void onCreateRoom()
    {

    }

    @FXML
    void onGenerateCode()
    {
        int codeLength = 8;
        StringBuilder chars = new StringBuilder();
        for (char i = 'A'; i <= 'Z'; i++)
        {
            chars.append(i);
        }

        for (char i = '0'; i < '9'; i++)
        {
            chars.append(i);
        }

        for (int i = 'a'; i < 'z'; i++)
        {
            chars.append(i);
        }

        StringBuilder codeBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < codeLength; i++) {
            int index = random.nextInt(chars.length());
            codeBuilder.append(chars.charAt(index));
        }



    }

    @FXML
    void onKeyPressed(KeyEvent event) {

    }

    @FXML
    void onSetImage() {

    }





    @Override
    public Region getRoot()
    {
        return root;
    }
}
