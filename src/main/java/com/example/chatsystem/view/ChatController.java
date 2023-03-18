package com.example.chatsystem.view;

import com.example.chatsystem.model.Message;
import com.example.chatsystem.viewmodel.ChatViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ChatController implements Controller, PropertyChangeListener
{
    @FXML
    private VBox chatPane;
    @FXML
    private Button sendButton, usersButton;
    @FXML
    private TextArea textField;
    @FXML
    private VBox messageMy, messageOthers;
    @FXML
    private ScrollPane scrollPane;

    private ViewHandler viewHandler;
    private ChatViewModel viewModel;
    private Region root;
    private Image profileImage;

    @Override
    public void init(ViewHandler viewHandler, ViewModel viewModel, Region root)
    {
        this.viewHandler = viewHandler;
        this.viewModel = (ChatViewModel) viewModel;
        this.root = root;
        this.viewModel.bindTextFieldProperty(textField.textProperty());
        this.viewModel.addPropertyChangeListener("new message", this);
        this.messageMy.setManaged(false);
        this.messageOthers.setManaged(false);
        this.scrollPane.vvalueProperty().bind(chatPane.heightProperty());
        profileImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/chatsystem/images/windturbine.jpg")));
        this.viewModel.loadMessages();

        textField.lengthProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.intValue() >= 2000)
            {
                textField.setStyle("-fx-text-fill: red");
                textField.setText("Stop");
                textField.setStyle("-fx-text-fill: white");
            }
        });
    }

    Label addMessage(VBox template)
    {
        int labelIndex = -1;
        ArrayList<Node> copiedNodes = new ArrayList<>();

        HBox message = (HBox) template.getChildren().get(0);

        for (Node node: message.getChildren())
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
                label.setMaxSize(chatPane.getWidth() / 2, chatPane.getHeight() / 2);

                copiedNodes.add(label);
                labelIndex = copiedNodes.indexOf(label);
            }
        }

        HBox newMessage = new HBox();
        newMessage.getChildren().addAll(copiedNodes);
        newMessage.setAlignment(message.getAlignment());
        newMessage.setPadding(message.getPadding());
        newMessage.setPrefSize(message.getPrefWidth(), message.getPrefHeight());
        newMessage.setMaxSize(message.getMaxWidth(), message.getMaxHeight());
        newMessage.setMinSize(message.getMinWidth(), message.getMinHeight());
        newMessage.setSpacing(message.getSpacing());

        VBox vBox = new VBox();
        vBox.getChildren().add(newMessage);
        vBox.setAlignment(template.getAlignment());
        vBox.setPadding(template.getPadding());
        vBox.setPrefSize(template.getPrefWidth(), template.getPrefHeight());
        vBox.setMaxSize(template.getMaxWidth(), template.getMaxHeight());
        vBox.setMinSize(template.getMinWidth(), template.getMinHeight());
        chatPane.getChildren().add(vBox);

        return (Label) copiedNodes.get(labelIndex);
    }

    //todo -> make 2 chat panes, one for current profile and others for others

    @FXML
    void onSendMessage() throws IOException
    {
        String t = textField.getText();
        if(t == null || t.isEmpty() || t.matches("^(\n)+$"))
            return;
        Label label = addMessage(messageMy);
        viewModel.onSendMessage(label.textProperty());
    }

    @FXML
    void onUsers()
    {

    }

    public Region getRoot()
    {
        return root;
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if(evt.getPropertyName().equals("new message"))
        {
            Platform.runLater(() -> addMessage(messageOthers).setText(((Message)evt.getNewValue()).getMessage()));
        }
    }

    @FXML
    void onEnter(KeyEvent event) throws IOException
    {
        if(event.getCode().equals(KeyCode.ENTER) && event.isShiftDown())
        {
            textField.setText(textField.getText() + "\n");
            textField.positionCaret(textField.getLength());
        }
        else if(event.getCode().equals(KeyCode.ENTER))
        {
            onSendMessage();
        }
    }

    // todo -> open extra info about user when press on image
    //      -> think about other GUI bugs
    //      -> login
    //      -> database
}
