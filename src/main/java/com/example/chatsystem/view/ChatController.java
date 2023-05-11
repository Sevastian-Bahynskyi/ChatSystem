package com.example.chatsystem.view;

import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.User;
import com.example.chatsystem.viewmodel.ChatViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChatController implements Controller, PropertyChangeListener
{
    @FXML
    private VBox chatPane, mainPane, userListPane, channelListPane;
    @FXML
    private HBox parent;
    @FXML
    private Button sendButton, usersButton;
    @FXML
    private TextArea textField;
    @FXML
    private Label channelNameTemplate;
    @FXML
    private VBox messageMyTemplate, messageOthersTemplate;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField newChannelField;



    private ViewHandler viewHandler;
    private ChatViewModel viewModel;
    private Region root;
    private ObjectProperty<Image> profileImage = new SimpleObjectProperty<>();



    private int indexOfUserListAsChild;
    private double rememberParentWidth;


    @Override
    public void init(ViewHandler viewHandler, ViewModel viewModel, Region root) throws IOException
    {
        this.viewHandler = viewHandler;
        this.viewModel = (ChatViewModel) viewModel;
        this.root = root;
        this.viewModel.bindTextFieldProperty(textField.textProperty());
        this.viewModel.bindUserImage(profileImage);
        this.viewModel.addPropertyChangeListener(this);
        this.viewHandler.addPropertyChangeListener(this);
        this.messageMyTemplate.setManaged(false);
        this.messageOthersTemplate.setManaged(false);
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

        indexOfUserListAsChild = parent.getChildren().indexOf(userListPane);
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
        if(template.equals(messageMyTemplate))
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
        newMessage.setStyle(":hover");


        return newMessage;
    }

    private HBox generateTemplate(VBox template, Image circleImage, String labelText, double circleRadius, double fontSize)
    {
        HBox message = (HBox) template.getChildren().get(0);
        ArrayList<Node> copiedNodes = new ArrayList<>();


        for (Node node: message.getChildren())
        {
            if(node instanceof Circle)
            {
                Circle circle = new Circle();
                circle.setFill(new ImagePattern(circleImage));
                circle.setRadius(circleRadius);

                copiedNodes.add(circle);
            } else if (node instanceof VBox) {
                VBox newVbox = new VBox();
                Label templateLabel = (Label) ((VBox) node).getChildren().get(0);
                Label label = new Label(labelText);


                label.setFont(new Font(templateLabel.getFont().getName(), fontSize));
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
        addMessage(messageMyTemplate, profileImage.get(), t);
        scrollPane.requestFocus();
    }



    @FXML
    void onUsers() throws IOException
    {
        var stage = viewHandler.getStage(parent);
        var borderSize = (stage.getWidth() - stage.getScene().getWidth()) / 2;


        if (parent.getChildren().contains(userListPane)) {
            parent.getChildren().remove(userListPane);
            var newWidth = stage.getWidth() - userListPane.getWidth() - borderSize * 2 - userListPane.getPadding().getLeft() - userListPane.getPadding().getRight();

            parent.setPrefWidth(newWidth);
            stage.setWidth(newWidth);
        } else {
            parent.getChildren().add(indexOfUserListAsChild, userListPane);

            var newWidth = stage.getWidth() + userListPane.getWidth() + borderSize * 2 + userListPane.getPadding().getLeft() + userListPane.getPadding().getRight();

            parent.setPrefWidth(newWidth);
            stage.setWidth(newWidth);
        }

    }


    private void loadUsersToUserListPane(Collection<User> users) throws IOException
    {
        ArrayList<Node> children = new ArrayList<>();
        for (User user:users)
        {
            children.add(generateTemplate(messageOthersTemplate, user.getImage(), user.getUsername(), 20, 14));
        }
        userListPane.getChildren().setAll(children);
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
            if(textField.isFocused())
            {
                textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
                onSendMessage();
            }
            else if(newChannelField.isFocused())
            {
                Label label = new Label();
                label.setFont(channelNameTemplate.getFont());
                label.setTextFill(channelNameTemplate.getTextFill());
                label.setText(newChannelField.getText());

                channelListPane.getChildren().add(0, label);
                newChannelField.setVisible(false);
                newChannelField.clear();
            }
        }
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        switch (evt.getPropertyName())
        {
            case "new message" -> {
                var propertyEvent = (List<Object>) evt.getNewValue();
                Message message = (Message) propertyEvent.get(0);
                boolean isMessageOfTheUser = (boolean) propertyEvent.get(1);

                Platform.runLater(() ->
                {
                    profileImage.set(message.getUser().getImage());
                    System.out.println("Image url that controller received: " + message.getUser().getImageUrl());
                    if(isMessageOfTheUser)
                        addMessage(messageMyTemplate, profileImage.get(), message.getMessage());
                    else
                    {
                        addMessage(messageOthersTemplate, profileImage.get(), message.getMessage());
                    }
                });
            }
            case "load user list" -> {
                try
                {
                    loadUsersToUserListPane(viewModel.getUsers());
                    onUsers();
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
            case "update user list" -> {
                try
                {
                    loadUsersToUserListPane(((List<User>) evt.getNewValue()));
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @FXML
    void onAddChannel()
    {
        newChannelField.setVisible(true);
        newChannelField.requestFocus();
    }



    // todo
    //      -> bug, user list is not being updated
    //      -> check what the problem with image is, when user press user list
}
