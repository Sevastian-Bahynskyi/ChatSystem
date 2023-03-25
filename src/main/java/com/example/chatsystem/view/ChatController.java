package com.example.chatsystem.view;

import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.User;
import com.example.chatsystem.viewmodel.ChatViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatController implements Controller, PropertyChangeListener
{
    @FXML
    private VBox chatPane, mainPane;
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
    private ObjectProperty<Image> profileImage = new SimpleObjectProperty<>();

    @Override
    public void init(ViewHandler viewHandler, ViewModel viewModel, Region root)
    {
        this.viewHandler = viewHandler;
        this.viewModel = (ChatViewModel) viewModel;
        this.root = root;
        this.viewModel.bindTextFieldProperty(textField.textProperty());
        this.viewModel.bindUserImage(profileImage);
        this.viewModel.addPropertyChangeListener("new message", this);
        this.messageMy.setManaged(false);
        this.messageOthers.setManaged(false);
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

    void addMessage(VBox template, Image image, String message)
    {
        VBox vBox = new VBox();
        vBox.getChildren().add(generateTemplate(template, image, message));
        vBox.setAlignment(template.getAlignment());
        vBox.setPadding(template.getPadding());
        vBox.setPrefSize(template.getPrefWidth(), template.getPrefHeight());
        vBox.setMaxSize(template.getMaxWidth(), template.getMaxHeight());
        vBox.setMinSize(template.getMinWidth(), template.getMinHeight());

        int transitionValue;
        if(template.equals(messageMy))
        {
            transitionValue = 100;
        }
        else transitionValue = -100;

        vBox.setTranslateX(transitionValue);
        TranslateTransition animation = new TranslateTransition(Duration.seconds(1), vBox);

        animation.setByX(-transitionValue);
        chatPane.getChildren().add(vBox);
        animation.play();
    }


    private HBox generateTemplate(VBox template, Image circleImage, String labelText)
    {
        HBox message = (HBox) template.getChildren().get(0);
        ArrayList<Node> copiedNodes = new ArrayList<>();


        for (Node node: message.getChildren())
        {
            if(node instanceof Circle)
            {
                Circle circle = new Circle();
                circle.setFill(new ImagePattern(circleImage));
                circle.setRadius(((Circle) node).getRadius());

                copiedNodes.add(circle);
            } else if (node instanceof VBox) {
                VBox newVbox = new VBox();
                Label templateLabel = (Label) ((VBox) node).getChildren().get(0);
                Label label = new Label(labelText);

                label.setFont(templateLabel.getFont());
                label.setTextFill((templateLabel).getTextFill());
                label.setWrapText((templateLabel).isWrapText());
                label.setMaxWidth(chatPane.getWidth() / 2);

                newVbox.getChildren().add(label);
                newVbox.setAlignment(((VBox) node).getAlignment());
                newVbox.setPrefSize(((VBox) node).getPrefWidth(), ((VBox) node).getPrefHeight());

                copiedNodes.add(newVbox);
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

        return newMessage;
    }

    @FXML
    void onSendMessage() throws IOException
    {
        String t = textField.getText();
        if(t == null || t.isEmpty() || t.matches("^(\n)+$")) // doesn't allow to send messages that consist of '\n' chars
            return;
        viewModel.onSendMessage();
        addMessage(messageMy, profileImage.get(), t);
        scrollPane.requestFocus();
    }



    @FXML
    void onUsers() throws IOException
    {
        VBox vBox = new VBox();
        ArrayList<Node> children = new ArrayList<>();
        for (User user:viewModel.getUsers())
        {
            children.add(generateTemplate(messageOthers, user.getImage(), user.getUsername()));
            System.out.println(user.getImageUrl());
        }
        vBox.getChildren().addAll(children);
        vBox.setStyle("-fx-background-color: #0C281E");
        Dialog<VBox> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(vBox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
        dialog.show();
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
            textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
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
                profileImage.set(message.getUser().getImage());
                System.out.println("Image url that controller received: " + message.getUser().getImageUrl());
                if(isMessageOfTheUser)
                    addMessage(messageMy, profileImage.get(), message.getMessage());
                else
                {
                    addMessage(messageOthers, profileImage.get(), message.getMessage());
                }
            });
        }
    }

    // todo
    //      -> database
    //      -> check what the problem with image is, when user press user list
}
