package com.example.chatsystem.view.ChatControllerDelegates;

import com.example.chatsystem.model.Channel;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.Room;
import com.example.chatsystem.model.UserInterface;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.*;

public class UiHandler
{
    private ChatController c;


    public UiHandler(ChatController chatController)
    {
        this.c = chatController;
    }

    protected void editMessageInUI(VBox template, String messageToChange)
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


    protected HBox generateTemplate(VBox template, Image circleImage, String labelText, double circleRadius, double fontSize)
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
                label.setMaxWidth(c.chatPane.getWidth() / 2);

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

    protected void showContextMenu(Map<String, Runnable> options, double screenX, double screenY)
    {
        ContextMenu popup = new ContextMenu();

        // Add menu items to the popup for each item in the list
        for (var item : options.keySet()) {
            MenuItem menuItem = new MenuItem(item);
            menuItem.setOnAction(event -> options.get(item).run());
            popup.getItems().add(menuItem);
        }


        c.parent.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (popup.isShowing()) {
                popup.hide();
            }
        });
        popup.show(c.parent, screenX, screenY);
    }

    protected void loadUsersToUserListPane(Collection<UserInterface> users)
    {
        ArrayList<Node> children = new ArrayList<>();

        for (UserInterface user:users)
        {
            VBox newUser = new VBox();
            newUser.setPrefSize(c.messageOthersTemplate.getPrefWidth(), c.messageOthersTemplate.getPrefHeight());
            newUser.setMaxSize(c.messageOthersTemplate.getMaxWidth(), c.messageOthersTemplate.getMaxHeight());
            newUser.setMinSize(c.messageOthersTemplate.getMinWidth(), c.messageOthersTemplate.getMinHeight());
            newUser.setPadding(newUser.getPadding());
            newUser.getStyleClass().add("message-template");
            HBox userUi = generateTemplate(c.messageOthersTemplate, user.getImage(), user.getUsername(), 20, 16);

            if(user.isModerator())
            {
                var starIcon = new FontAwesomeIcon();
                starIcon.setIcon(FontAwesomeIcons.STAR);
                starIcon.setFill(Color.YELLOW);
                userUi.getChildren().add(starIcon);
            }
            newUser.getChildren().add(userUi);

            if(c.viewModel.amIModerator() && !user.isModerator()) {
                Dialog<String> dialog = new Dialog<>();
                ButtonType confirmButton = new ButtonType("Confirm");
                dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

                HashMap<String, Runnable> options = new HashMap<>();
                options.put("Ban", () -> {
                    dialog.setContentText("Do you confirm banning the user?");

                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == confirmButton) {
                            c.viewModel.banUser(user);
                            c.userListPane.getChildren().remove(newUser);
                        } else if (dialogButton == ButtonType.CANCEL) {
                            dialog.close();
                        }
                        return null;
                    });
                    dialog.showAndWait();

                });
                options.put("Make a moderator", () -> {
                    dialog.setContentText("Do you confirm making the user a moderator?");

                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == confirmButton) {
                            c.viewModel.makeModerator(user);
                            var starIcon = new FontAwesomeIcon();
                            starIcon.setIcon(FontAwesomeIcons.STAR);
                            starIcon.setFill(Color.YELLOW);
                            newUser.setOnMouseClicked(null);
                            userUi.getChildren().add(starIcon);
                        } else if (dialogButton == ButtonType.CANCEL) {
                            dialog.close();
                        }
                        return null;
                    });
                    dialog.showAndWait();
                });

                newUser.setOnMouseClicked(event ->
                {
                    showContextMenu(options, event.getScreenX(), event.getScreenY());
                });
            }


            children.add(newUser);
        }
        c.userListPane.getChildren().setAll(children);
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        switch (evt.getPropertyName())
        {
            case "new message" -> {
                var propertyEvent = (List<Object>) evt.getNewValue();
                Message message = (Message) propertyEvent.get(0);
                // if message author is not the same who logged it will be displayed on the right side of chat pane
                boolean isMessageOfTheUser = (boolean) propertyEvent.get(1);

                Platform.runLater(() ->
                {
                    c.profileImage.set(message.getUser().getImage());
                    if(isMessageOfTheUser)
                        c.addMessage(c.messageMyTemplate, c.profileImage.get(), message, true);
                    else
                    {
                        c.addMessage(c.messageOthersTemplate, c.profileImage.get(), message, true);
                    }
                });
            }
            case "load user list" -> {
                loadUsersToUserListPane(c.viewModel.getUsers());
                c.onUsers();
            }
            case "update user list" -> {
                loadUsersToUserListPane(((List<UserInterface>) evt.getNewValue()));
            }

            case "room added" -> {
                Room room = (Room) evt.getNewValue();
                c.addRoom(0, room, false);
            }

            case "reload messages" -> {
                ArrayList<Message> messages = (ArrayList<Message>) evt.getNewValue();
                c.chatPane.getChildren().clear();
                for (var message:messages)
                {
                    if(c.viewModel.isMyMessage(message))
                    {
                       c.addMessage(c.messageMyTemplate, message.getUser().getImage(), message, false);
                    }
                    else {
                        c.addMessage(c.messageOthersTemplate, message.getUser().getImage(), message, false);
                    }
                }
            }

            case "reload message" -> {
                List<Object> t = (List<Object>) evt.getNewValue();
                Message mes = (Message) t.get(0);
                int index = (int) t.get(1);

                editMessageInUI((VBox) c.chatPane.getChildren().get(index), mes.getMessage());
            }

            case "new channel" -> {
                Channel channel = (Channel) evt.getNewValue();
                c.addChannel(0, channel.getName());
            }

            case "clear messages" -> {
                c.chatPane.getChildren().clear();
            }

            case "reload channel" -> {
                List<Object> t = (List<Object>) evt.getNewValue();
                Channel mes = (Channel) t.get(0);
                int index = (int) t.get(1);
                ((Label) c.channelListPane.getChildren().get(index)).setText(mes.getName());
            }

            case "select channel" -> {
                int index = (int) evt.getNewValue();
                c.channelListPane.getChildren().get(index).fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
                        0, 0, 0, 0, MouseButton.PRIMARY, 1,
                        false, false, false, false, true, false, false, true, true, true, null));
            }

            case "load channels" -> {
                ArrayList<Channel> channels = (ArrayList<Channel>) evt.getNewValue();
                for (Channel ch:channels)
                {
                    c.addChannel(-1, ch.getName());
                }
            }

            case "load rooms" -> {
                ArrayList<Room> rooms = (ArrayList<Room>) evt.getNewValue();
                for (var r:rooms)
                {
                    c.addRoom(r);
                }
                c.roomList.getChildren().get(c.roomList.getChildren().size() - 1).fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
                        0, 0, 0, 0, MouseButton.PRIMARY, 1,
                        false, false, false, false, true, false, false, true, true, true, null));
            }

            case "select room" -> {
                c.roomList.getChildren().get((Integer) evt.getNewValue()).fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
                        0, 0, 0, 0, MouseButton.PRIMARY, 1,
                        false, false, false, false, true, false, false, true, true, true, null));
            }

            case "delete channel" -> {
                int index = (int) evt.getNewValue();
                c.channelListPane.getChildren().remove(index);
            }

            case "clear channels" -> {
                c.channelListPane.getChildren().clear();
            }

            case "join a room" -> {
                var data = ((List) evt.getNewValue());
                Room room = (Room) data.get(0);

                c.joinRoom(room);
            }

            case "reload room" -> {
                List<Object> t = (List<Object>) evt.getNewValue();
                Room room = (Room) t.get(0);
                int index = (int) t.get(1);
                c.addRoom(index, room, true);
            }
        }
    }
}
