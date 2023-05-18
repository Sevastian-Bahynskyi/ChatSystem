package com.example.chatsystem.view;

import com.example.chatsystem.model.Channel;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class ChatController implements Controller, PropertyChangeListener
{

    @FXML
    private VBox chatPane, mainPane, userListPane, channelListPane, roomList, userPane;
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
    private int indexOfUserPaneAsChild;
    private boolean isEditChannel = false;
    private boolean isEditMessage = false;

    private int indexOfMessageToChange = -1;
    private int indexOfChannelToChange = -1;



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
        this.viewModel.loadEverything();
        this.messageMyTemplate.setManaged(false);
        this.messageOthersTemplate.setManaged(false);
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
        chatPane.getChildren().remove(messageMyTemplate);
        chatPane.getChildren().remove(messageOthersTemplate);

        indexOfUserPaneAsChild = parent.getChildren().indexOf(userPane);
    }

    void addMessage(VBox template, Image image, Message message, boolean isNeedAnimation)
    {
        double imageRadius = 25;
        double fontSize = 16;

        VBox vBox = new VBox();

        Label nameTimestampTemplate = (Label) template.getChildren().get(0);
        Label label_0 = new Label(message.getMetadata());

        label_0.setTextFill(Color.WHITE);
        label_0.setFont(new Font(nameTimestampTemplate.getFont().getName(), nameTimestampTemplate.getFont().getSize()));
        label_0.setWrapText((nameTimestampTemplate).isWrapText());
        label_0.setPadding(nameTimestampTemplate.getPadding());
        vBox.getChildren().add(label_0);

        var messageUI = generateTemplate(template, image, message.getMessage(), imageRadius, fontSize);


        vBox.getChildren().add(messageUI);
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

        if(isNeedAnimation)
            vBox.setTranslateX(transitionValue);
        TranslateTransition animation = new TranslateTransition(Duration.seconds(1), vBox);

        animation.setByX(-transitionValue);
        chatPane.getChildren().add(vBox);
        if((viewModel.isMyMessage(message) || viewModel.isModerator(message.getChannelId())) && !message.getMessage().equals("deleted message"))
        {
            HashMap<String, Runnable> options = new HashMap<>();

            messageUI.setOnMouseClicked(event ->
            {
                if (event.getButton() == MouseButton.SECONDARY)
                {
                    indexOfMessageToChange = chatPane.getChildren().indexOf(vBox);
                    showContextMenu(options, event.getScreenX(), event.getScreenY());
                }
            });

            if(!viewModel.isModerator(message.getChannelId()))
                options.put("Edit", () -> editMessage(message.getMessage()));

            options.put("Delete", () -> deleteMessage(vBox));
        }
        if(isNeedAnimation)
            animation.play();
    }

    private void editMessage(String message)
    {
        isEditMessage = true;
        textField.requestFocus();
        textField.setText(message);
        textField.positionCaret(textField.getLength());
    }

    private void editMessageInUI(VBox template, String messageToChange)
    {
        HBox message = (HBox) template.getChildren().get(1);


        for (Node node: message.getChildren())
        {
            if (node instanceof VBox) {
                Label templateLabel = (Label) ((VBox) node).getChildren().get(0);
                templateLabel.setText(messageToChange);

                if(messageToChange.equals("deleted message"))
                {
                    templateLabel.setTextFill(Color.RED);
                }
            }
        }
    }


    private void deleteMessage(VBox vBox)
    {
        int index = chatPane.getChildren().indexOf(vBox);
        editMessageInUI(vBox, "deleted message");
        new Thread(() -> this.viewModel.deleteMessage(index)).start();
    }

    private HBox generateTemplate(VBox template, Image circleImage, String labelText, double circleRadius, double fontSize)
    {
        HBox message = (HBox) template.getChildren().get(1);
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
                if(labelText.equals("deleted message"))
                    label.setTextFill(Color.RED);
                else
                {
                    label.setTextFill((templateLabel).getTextFill());
                }
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
        else if(t.equals("deleted message"))
        {
            textField.setText("");
            return;
        }

        if(isEditMessage)
        {
            isEditMessage = false;
            String temp = textField.getText();
            textField.clear();
            editMessageInUI((VBox) chatPane.getChildren().get(indexOfMessageToChange), temp);
            new Thread(() -> this.viewModel.editMessage(indexOfMessageToChange, temp)).start();
        }
        else
        {
            Message message = viewModel.onSendMessage();
            addMessage(messageMyTemplate, profileImage.get(), message, true);
            scrollPane.requestFocus();
        }
    }



    @FXML
    void onUsers() throws IOException
    {
        var stage = viewHandler.getStage(parent);
        var borderSize = (stage.getWidth() - stage.getScene().getWidth()) / 2;


        if (parent.getChildren().contains(userPane)) {
            parent.getChildren().remove(userPane);
            var newWidth = stage.getWidth() - userPane.getWidth() - borderSize * 2 - userPane.getPadding().getLeft() - userPane.getPadding().getRight();

            parent.setPrefWidth(newWidth);
            stage.setWidth(newWidth);
        } else {
            parent.getChildren().add(indexOfUserPaneAsChild, userPane);

            var newWidth = stage.getWidth() + userPane.getWidth() + borderSize * 2 + userPane.getPadding().getLeft() + userPane.getPadding().getRight();

            parent.setPrefWidth(newWidth);
            stage.setWidth(newWidth);
        }

    }


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
    void onEnter(KeyEvent event) throws IOException, SQLException
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
                String s = textField.getText();
                textField.setText(s.replace("\n", ""));
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
                }

                if(isEditChannel)
                {
                    isEditChannel = false;
                    viewModel.editChannel(viewModel.getChannelByIndex(indexOfChannelToChange).getName(), newChannelField.getText());
                }
                else {
                    this.viewModel.addChannel(newChannelField.getText());
                }

                newChannelField.setVisible(false);
                newChannelField.clear();
            }
        }
    }

    private void addChannel(String channelName) throws IOException
    {
        Map<String, Runnable> options = new HashMap<>();


        Label label = new Label();
        label.setPadding(new Insets(10));
        label.setMaxWidth(Double.MAX_VALUE);
        label.setText(channelName);
        label.getStyleClass().set(0, "channel-selected");



        options.put("Edit", this::editChannel);
        options.put("Delete", this::deleteChannel);

        channelListPane.getChildren().add(0, label);
        int indexOfChannel = channelListPane.getChildren().size() - 1;


        label.setOnMouseClicked(event -> {



            if(event.getButton() == MouseButton.SECONDARY)
            {
                indexOfChannelToChange = channelListPane.getChildren().size() - channelListPane.getChildren().indexOf(label) - 1;
                System.out.println("Index of channel: " + indexOfChannelToChange);

                showContextMenu(options, event.getScreenX(), event.getScreenY());
            }
            else if(event.getButton() == MouseButton.PRIMARY)
            {
                onChannelClick(event);

                try
                {
                    viewModel.loadMessages(indexOfChannel);
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        });
        if (selectedChannel != null) {
            selectedChannel.getStyleClass().set(0, "channel");
        }
        selectedChannel = label;

    }

    @FXML
    private void onChannelClick(MouseEvent event)
    {
        Label clickedChannel = (Label) event.getSource();


        if (selectedChannel != null) {
            selectedChannel.getStyleClass().set(0, "channel");
        }
        clickedChannel.getStyleClass().set(0, "channel-selected");

        selectedChannel = clickedChannel;
    }


    private void deleteChannel()
    {
        viewModel.deleteChannel(indexOfChannelToChange);
    }


    private void editChannel()
    {
        isEditChannel = true;
        newChannelField.setVisible(true);
        newChannelField.requestFocus();
        newChannelField.setText(viewModel.getChannelByIndex(indexOfChannelToChange).getName());
        newChannelField.positionCaret(newChannelField.getLength());
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
                    if(isMessageOfTheUser)
                        addMessage(messageMyTemplate, profileImage.get(), message, true);
                    else
                    {
                        addMessage(messageOthersTemplate, profileImage.get(), message, true);
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
                Circle circle = new Circle(30);
                Label label = new Label(room.getName());
                label.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16");
                Popup popup = new Popup();


                VBox vbox = new VBox(label);
                vbox.setStyle("-fx-background-color: #4C956C;");
                vbox.setPadding(new Insets(0, 10, 0,10));
                popup.getContent().add(vbox);


                circle.setOnMouseEntered(event -> {
                    popup.show(circle.getScene().getWindow(), event.getScreenX() + 10, event.getScreenY() + 10);
                });


                circle.setOnMouseExited(event -> {
                    popup.hide();
                });

                if(room.getImage() != null)
                    circle.setFill(new ImagePattern(room.getImage()));

                roomList.getChildren().add(circle);
            }

            case "reload messages" -> {
                ArrayList<Message> messages = (ArrayList<Message>) evt.getNewValue();
                chatPane.getChildren().clear();
                for (var message:messages)
                {
                    if(viewModel.isMyMessage(message))
                    {
                        addMessage(messageMyTemplate, message.getUser().getImage(), message, false);
                    }
                    else {
                        addMessage(messageOthersTemplate, message.getUser().getImage(), message, false);
                    }
                }
                System.out.println("done");
            }

            case "reload message" -> {
                List<Object> t = (List<Object>) evt.getNewValue();
                Message mes = (Message) t.get(0);
                int index = (int) t.get(1);

                editMessageInUI((VBox) chatPane.getChildren().get(index), mes.getMessage());
                System.out.println("done");
            }

            case "new channel" -> {
                Channel channel = (Channel) evt.getNewValue();
                try
                {
                    addChannel(channel.getName());
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }

            case "clear messages" -> {
                chatPane.getChildren().clear();
            }

            case "reload channel" -> {
                List<Object> t = (List<Object>) evt.getNewValue();
                Channel mes = (Channel) t.get(0);
                int index = (int) t.get(1);
                ((Label) channelListPane.getChildren().get(index)).setText(mes.getName());
            }

            case "load channels" -> {
                ArrayList<Channel> channels = (ArrayList<Channel>) evt.getNewValue();
                for (Channel ch:channels)
                {
                    try
                    {
                        addChannel(ch.getName());
                    } catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }

            case "delete channel" -> {
                int index = (int) evt.getNewValue();
                channelListPane.getChildren().remove(index);
                System.out.println("Bruhhhhhhhhhhhhhhhhhhh");
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
