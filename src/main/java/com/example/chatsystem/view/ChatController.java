package com.example.chatsystem.view;

import com.example.chatsystem.viewmodel.ChatViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ChatController implements Controller
{
    @FXML
    private VBox chatPane;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea textField;

    @FXML
    private Button usersButton;

    private ViewHandler viewHandler;
    private ChatViewModel chatViewModel ;
    private Region root;

    @Override
    public void init(ViewHandler viewHandler, ViewModel viewModel, Region root)
    {
        this.viewHandler = viewHandler;
        this.chatViewModel = (ChatViewModel) viewModel;
        this.root = root;
    }
    @FXML
    void onSendMessage(ActionEvent event)
    {

    }

    @FXML
    void onUsers(ActionEvent event)
    {

    }

    public Region getRoot()
    {
        return root;
    }


}
