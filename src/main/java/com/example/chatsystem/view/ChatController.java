package com.example.chatsystem.view;

import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.Room;
import com.example.chatsystem.model.UserInterface;
import com.example.chatsystem.viewmodel.ChatViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.*;

public class ChatController implements Controller, PropertyChangeListener
{
    @FXML
    private VBox chatPane, mainPane, userListPane, channelListPane, roomList;
    @FXML
    private HBox parent;
    @FXML
    private TextArea textField;
    @FXML
    private VBox messageMyTemplate, messageOthersTemplate;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField newChannelField;



    private ViewHandler viewHandler;
    private ChatViewModel viewModel;
    private Region root;
    private final ObjectProperty<Image> profileImage = new SimpleObjectProperty<>();
    private Label selectedChannel;



    private int indexOfUserListAsChild;


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

        newChannelField.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            if(!newValue)
                newChannelField.setVisible(false);
        });

        indexOfUserListAsChild = parent.getChildren().indexOf(userListPane);
        addChannel("default");
    }

    void addMessage(VBox template, Image image, String message)
    {
        double imageRadius = 25;
        double fontSize = 16;

        VBox vBox = new VBox();

        vBox.getChildren().add(generateTemplate(template, image, message, imageRadius, fontSize));
        vBox.setAlignment(template.getAlignment());
        vBox.setPadding(template.getPadding());
        vBox.setPrefSize(template.getPrefWidth(), template.getPrefHeight());
        vBox.setMaxSize(template.getMaxWidth(), template.getMaxHeight());
        vBox.setMinSize(template.getMinWidth(), template.getMinHeight());

        int transitionValue = 100;

        if(!template.equals(messageMyTemplate))
        {
            transitionValue = -transitionValue;
        }

        vBox.setTranslateX(transitionValue);
        TranslateTransition animation = new TranslateTransition(Duration.seconds(1), vBox);

        animation.setByX(-transitionValue);
        chatPane.getChildren().add(vBox);
        animation.play();
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

    // takes map of choices and functions that choice should provide
    private void showContextMenu(Map<String, Runnable> options, double screenX, double screenY)
    {
        ContextMenu popup = new ContextMenu();

        // Add menu items to the popup for each item in the list
        for (var item : options.keySet()) {
            MenuItem menuItem = new MenuItem(item);
            menuItem.setOnAction(event -> options.get(item).run());
            popup.getItems().add(menuItem);
        }


        parent.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (popup.isShowing()) {
                popup.hide();
            }
        });
        popup.show(parent, screenX, screenY);
    }


    private void loadUsersToUserListPane(Collection<UserInterface> users) throws IOException
    {
        ArrayList<Node> children = new ArrayList<>();


        HashMap<String, Runnable> options = new HashMap<>();
        options.put("Edit", () -> viewModel.editUser());
        options.put("Delete", () -> viewModel.deleteUser());


        for (UserInterface user:users)
        {
            VBox newUser = new VBox();
            newUser.setPrefSize(messageOthersTemplate.getPrefWidth(), messageOthersTemplate.getPrefHeight());
            newUser.setMaxSize(messageOthersTemplate.getMaxWidth(), messageOthersTemplate.getMaxHeight());
            newUser.setMinSize(messageOthersTemplate.getMinWidth(), messageOthersTemplate.getMinHeight());
            newUser.setPadding(newUser.getPadding());
            newUser.getStyleClass().add("message-template");
            newUser.getChildren().add(generateTemplate(messageOthersTemplate, user.getImage(), user.getUsername(), 20, 16));
            newUser.setOnMouseClicked(event ->
            {
                showContextMenu(options, event.getScreenX(), event.getScreenY());
            });


            children.add(newUser);
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
                if(newChannelField.getText().isEmpty() || newChannelField.getText().matches("^(\n)+$"))
                {
                    return;
                }
                else {
                    for (Node node:channelListPane.getChildren())
                    {
                        Label label = (Label) node;
                        if(label.getText().equals(newChannelField.getText()))
                        {
                            return;
                        }
                    }

                    // if nothing was entered or entered value already exists in channel list not add a new channel
                }
                addChannel(newChannelField.getText());

                newChannelField.setVisible(false);
                newChannelField.clear();
            }
        }
    }

    private void addChannel(String channelName)
    {
        Label label = new Label();
        label.setPadding(new Insets(10));
        label.setMaxWidth(Double.MAX_VALUE);
        label.setText(channelName);
        label.getStyleClass().set(0, "channel-selected");
        label.setOnMouseClicked(this::onChannelClick);
        if (selectedChannel != null) {
            selectedChannel.getStyleClass().set(0, "channel");
        }
        selectedChannel = label;

        channelListPane.getChildren().add(0, label);
    }

    @FXML
    private void onChannelClick(MouseEvent event) {
        // Get the clicked label
        Label clickedChannel = (Label) event.getSource();

        // Update the styles of the selected and clicked labels
        if (selectedChannel != null) {
            selectedChannel.getStyleClass().set(0, "channel");
        }
        clickedChannel.getStyleClass().set(0, "channel-selected");

        // Update the selected label
        selectedChannel = clickedChannel;
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
                    loadUsersToUserListPane(((List<UserInterface>) evt.getNewValue()));
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }

            case "room added" -> {
                Room room = (Room) evt.getNewValue();
                System.out.println(room);
                System.out.println(room.getImage());
                Circle circle = new Circle(30);
                Label label = new Label(room.getName());
                label.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16");
                Popup popup = new Popup();

// set the content of the popup
                VBox vbox = new VBox(label);
                vbox.setStyle("-fx-background-color: #4C956C;");
                vbox.setPadding(new Insets(0, 10, 0,10));
                popup.getContent().add(vbox);

// show the popup when the user hovers over the circle
                circle.setOnMouseEntered(event -> {
                    popup.show(circle.getScene().getWindow(), event.getScreenX() + 10, event.getScreenY() + 10);
                });

// hide the popup when the user moves the mouse away from the circle
                circle.setOnMouseExited(event -> {
                    popup.hide();
                });

                if(room.getImage() != null)
                    circle.setFill(new ImagePattern(room.getImage()));
                else {

                }
                roomList.getChildren().add(circle);
            }
        }
    }


    @FXML
    private void onAddChannel()
    {
        newChannelField.setVisible(true);
        newChannelField.requestFocus();
    }

    @FXML
    private void onAddRoom(MouseEvent event)
    {
        viewHandler.openParallelView(WINDOW.ADD_ROOM);
    }



    // todo
    //      -> bug, user list is not being updated
    //      -> check what the problem with image is, when user press user list
}
