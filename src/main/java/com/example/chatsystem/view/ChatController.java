package com.example.chatsystem.view;

import com.example.chatsystem.viewmodel.ChatViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ChatController implements Controller
{
    @FXML
    private VBox chatPaneMy;

    @FXML
    private VBox chatPaneOthers;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea textField;

    @FXML
    private Button usersButton;

    @FXML
    private HBox myMessageBoxTemp;

    private ViewHandler viewHandler;
    private ChatViewModel viewModel ;
    private Region root;
    private Image profileImage;

    @Override
    public void init(ViewHandler viewHandler, ViewModel viewModel, Region root)
    {
        this.viewHandler = viewHandler;
        this.viewModel = (ChatViewModel) viewModel;
        this.root = root;
        this.viewModel.bindTextFieldProperty(textField.textProperty());
        chatPaneMy.setPrefSize(chatPaneMy.getMaxWidth(), chatPaneMy.getMaxHeight());
        chatPaneMy.setMaxSize(chatPaneMy.getMaxWidth(), chatPaneMy.getMaxHeight());
        profileImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/chatsystem/images/windturbine.jpg")));
//        messageBoxTemp.getChildren();
    }

    Label addMessage()
    {
        int labelIndex = -1;
        ArrayList<Node> copiedNodes = new ArrayList<>();

        for (Node node: myMessageBoxTemp.getChildren())
        {
            if(node instanceof Circle)
            {
                Circle circle = new Circle();
                circle.setFill(new ImagePattern(profileImage));
                circle.setRadius(((Circle) node).getRadius());

                copiedNodes.add(circle);
            } else if (node instanceof Label) {
                Label label = new Label(((Label) node).getText());

                label.setFont(((Label) node).getFont());
                label.setTextFill(((Label) node).getTextFill());
                label.setWrapText(((Label) node).isWrapText());

                copiedNodes.add(label);
                labelIndex = copiedNodes.indexOf(label);
            }
        }

        HBox newMessage = new HBox();
        newMessage.getChildren().addAll(copiedNodes);
        newMessage.setAlignment(myMessageBoxTemp.getAlignment());
        newMessage.setPadding(myMessageBoxTemp.getPadding());
        newMessage.setPrefSize(myMessageBoxTemp.getPrefWidth(), myMessageBoxTemp.getPrefHeight());
        newMessage.setMaxSize(myMessageBoxTemp.getMaxWidth(), myMessageBoxTemp.getMaxHeight());
        newMessage.setMinSize(myMessageBoxTemp.getMinWidth(), myMessageBoxTemp.getMinHeight());
        newMessage.setSpacing(myMessageBoxTemp.getSpacing());

        chatPaneMy.getChildren().add(newMessage);

        return (Label) copiedNodes.get(labelIndex);
    }

    //todo -> make 2 chat panes, one for current profile and others for others

    @FXML
    void onSendMessage(ActionEvent event) throws IOException
    {
        Label label = addMessage();
        viewModel.onSendMessage(label.textProperty());
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
