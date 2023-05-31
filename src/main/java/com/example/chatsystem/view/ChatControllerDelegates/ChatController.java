package com.example.chatsystem.view.ChatControllerDelegates;

import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.Room;
import com.example.chatsystem.model.UserInterface;
import com.example.chatsystem.view.Controller;
import com.example.chatsystem.view.ViewHandler;
import com.example.chatsystem.view.WINDOW;
import com.example.chatsystem.viewmodel.ChatViewModelDelegates.ChatViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;


public class ChatController implements Controller, PropertyChangeListener
{

    @FXML
    protected VBox chatPane, mainPane, userListPane, channelListPane, roomList, userPane;
    @FXML
    protected HBox parent;
    @FXML
    protected TextArea textField;
    @FXML
    protected VBox messageMyTemplate, messageOthersTemplate;
    @FXML
    protected ScrollPane scrollPane;
    @FXML
    protected TextField newChannelField;


    protected MessageHandler messageHandler;
    protected UiHandler uiHandler;
    protected ChannelHandler channelHandler;
    protected RoomHandler roomHandler;


    protected ViewHandler viewHandler;
    protected ChatViewModel viewModel;

    protected Region root;
    protected final ObjectProperty<Image> profileImage = new SimpleObjectProperty<>();
    protected Label selectedChannel;
    protected HBox currentRoom;
    protected int indexOfUserPaneAsChild;
    protected boolean isEditChannel = false;
    protected boolean isEditMessage = false;

    protected int indexOfMessageToChange = -1;
    protected int indexOfChannelToChange = -1;



    @Override
    public void init(ViewHandler viewHandler, ViewModel viewModel, Region root) throws IOException
    {
        this.viewHandler = viewHandler;
        this.viewModel = (ChatViewModel) viewModel;
        this.messageHandler = new MessageHandler(this);
        this.channelHandler = new ChannelHandler(this);
        this.uiHandler = new UiHandler(this);
        this.roomHandler = new RoomHandler(this);
        this.root = root;
        this.viewModel.bindTextFieldProperty(textField.textProperty());
        this.viewModel.bindUserImage(profileImage);
        this.viewModel.addPropertyChangeListener(this);
        this.viewHandler.addPropertyChangeListener(this);
        this.viewModel.loadEverything();
        this.messageMyTemplate.setManaged(false);
        this.messageOthersTemplate.setManaged(false);
        this.textField.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 3000 ? change : null));

        this.chatPane.heightProperty().addListener((observable, oldValue, newValue) ->
        {
            scrollPane.setVvalue(chatPane.getHeight());
        });

        scrollPane.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            double width = scrollPane.getContent().getBoundsInLocal().getWidth();
            double vValue = scrollPane.getVvalue();

            scrollPane.setVvalue(vValue - deltaY / width);
        });

        newChannelField.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            if(!newValue)
                newChannelField.setVisible(false);
        });
        chatPane.getChildren().remove(messageMyTemplate);
        chatPane.getChildren().remove(messageOthersTemplate);

        indexOfUserPaneAsChild = parent.getChildren().indexOf(userPane);
    }

    protected void addMessage(VBox template, Image image, Message message, boolean isNeedAnimation)
    {
        messageHandler.addMessage(template, image, message, isNeedAnimation);
    }

    protected void editMessage(String message)
    {
        messageHandler.editMessage(message);
    }

    protected void deleteMessage(VBox vBox)
    {
        messageHandler.deleteMessage(vBox);
    }

    protected void editMessageInUI(VBox template, String messageToChange)
    {
        uiHandler.editMessageInUI(template, messageToChange);
    }

    protected HBox generateTemplate(VBox template, Image circleImage, String labelText, double circleRadius, double fontSize)
    {
        return uiHandler.generateTemplate(template, circleImage, labelText, circleRadius, fontSize);
    }

    protected void showContextMenu(Map<String, Runnable> options, double screenX, double screenY)
    {
        uiHandler.showContextMenu(options, screenX, screenY);
    }

    protected void loadUsersToUserListPane(Collection<UserInterface> users) throws IOException
    {
        uiHandler.loadUsersToUserListPane(users);
    }

    public Region getRoot()
    {
        return root;
    }

    @FXML
    protected void onLeaveRoom()
    {
        if(!viewModel.amIModerator())
        {
            viewModel.leaveRoom();
            MouseEvent clickEvent = new MouseEvent(
                    MouseEvent.MOUSE_CLICKED,
                    0, 0, 0, 0,
                    MouseButton.PRIMARY, 1,
                    false, false, false, false,
                    true, false, false, false,
                    true, true, null
            );
            HBox temp = (HBox) roomList.getChildren().get(0);
            temp.getChildren().get(0).fireEvent(clickEvent);
        }
    }

    @FXML
    protected void onSendMessage() throws IOException
    {
        String t = textField.getText();
        // doesn't allow to send messages that consist of whitespace chars
        if(t == null || t.isEmpty() || (t.trim().isEmpty()))
        {
            textField.clear();
            return;
        } // if message is deleted
        else if(t.equals("deleted message"))
        {
            textField.setText("");
            return;
        }

        if(isEditMessage) // if edit message was selected before
        {
            isEditMessage = false;
            String temp = textField.getText();
            textField.clear();
            // change GUI
            editMessageInUI((VBox) chatPane.getChildren().get(indexOfMessageToChange), temp);
            // edit message in database and notify other client about the change
            new Thread(() -> this.viewModel.editMessage(indexOfMessageToChange, temp)).start();
        }
        else
        {
            Message message = viewModel.onSendMessage();
            // creates message in database and notifies all the clients to add the message in UI
            addMessage(messageMyTemplate, profileImage.get(), message, true);
        }
        scrollPane.requestFocus();
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

    @FXML
    void onUsers()
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

    @FXML
    protected void onChannelClick(MouseEvent event)
    {
        Label clickedChannel = (Label) event.getSource();


        if (selectedChannel != null) {
            selectedChannel.getStyleClass().set(0, "channel");
        }
        clickedChannel.getStyleClass().set(0, "channel-selected");

        selectedChannel = clickedChannel;
    }

    @FXML
    protected void onAddChannel()
    {
        if(!viewModel.amIModerator())
            return;
        newChannelField.setVisible(true);
        newChannelField.requestFocus();
    }

    @FXML
    protected void onAddRoom(MouseEvent event)
    {
        viewHandler.openParallelView(WINDOW.ADD_ROOM);
    }

    public void addChannel(int index, String channelName)
    {
        channelHandler.addChannel(index, channelName);
    }

    public void deleteChannel()
    {
        channelHandler.deleteChannel();
    }

    public void editChannel()
    {
        channelHandler.editChannel();
    }

    public void addRoom(Room room)
    {
        roomHandler.addRoom(room);
    }

    public void addRoom(int index, Room room, boolean needToReplace)
    {
        roomHandler.addRoom(index, room, needToReplace);
    }

    public void joinRoom(Room room)
    {
        roomHandler.joinRoom(room);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        Platform.runLater(() -> {
            uiHandler.propertyChange(evt);
        });

    }
}
