package com.example.chatsystem.view;

import com.example.chatsystem.model.Message;
import com.example.chatsystem.viewmodel.ChatViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
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
import java.util.List;
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
        profileImage = this.viewModel.getUserImage();
        this.viewModel.loadMessages();
        this.textField.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 8000 ? change : null));

        this.chatPane.heightProperty().addListener((observable, oldValue, newValue) ->
        {
            scrollPane.setVvalue(chatPane.getHeight());
        }); // scroll to the bottom of scrollpane whenever chatpane height is changed

        scrollPane.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            double width = scrollPane.getContent().getBoundsInLocal().getWidth();
            double vValue = scrollPane.getVvalue();

            scrollPane.setVvalue(vValue - deltaY / width);
        }); // allows to scroll with the mouse when scrollpane is focused
    }

    Label addMessage(VBox template)
    {
        Label rememberLabel = null;
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
            } else if (node instanceof VBox) {
                VBox newVbox = new VBox();
                Label templateLabel = (Label) ((VBox) node).getChildren().get(0);
                Label label = new Label(templateLabel.getText());

                label.setFont(templateLabel.getFont());
                label.setTextFill((templateLabel).getTextFill());
                label.setWrapText((templateLabel).isWrapText());
                label.setMaxWidth(chatPane.getWidth() / 2);

                newVbox.getChildren().add(label);
                newVbox.setAlignment(((VBox) node).getAlignment());
                newVbox.setPrefSize(((VBox) node).getPrefWidth(), ((VBox) node).getPrefHeight());

                copiedNodes.add(newVbox);
                rememberLabel = label;
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

        return rememberLabel;
    }


    @FXML
    void onSendMessage() throws IOException
    {
        String t = textField.getText();
        if(t == null || t.isEmpty() || t.matches("^(\n)+$")) // doesn't allow to send messages that consist of '\n' chars
            return;
        Label label = addMessage(messageMy);
        viewModel.onSendMessage(label.textProperty());
        scrollPane.requestFocus();
    }

    @FXML
    void onUsers()
    {

    }

    public Region getRoot()
    {
        return root;
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

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if(evt.getPropertyName().equals("new message"))
        {
            var propertyEvent = (List<Object>) evt.getNewValue();
            Message message = (Message) propertyEvent.get(0);
            boolean isMessageOfTheUser = (boolean) propertyEvent.get(1);
            Platform.runLater(() ->
            {
                if(isMessageOfTheUser)
                    addMessage(messageMy).setText(message.getMessage());
                else
                    addMessage(messageOthers).setText(message.getMessage());
            });
        }
    }

    // todo
    //      -> set image in register window
    //      -> open extra info about user when press on image
    //      -> think about other GUI bugs
    //      -> second button implementation
    //      -> registration
    //      -> database
    //      -> how should I check if user is registered or not?
    //      Should I search for people with the  same username and password, or only username, or ID?
}
