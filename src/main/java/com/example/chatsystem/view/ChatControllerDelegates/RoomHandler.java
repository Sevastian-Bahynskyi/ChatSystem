package com.example.chatsystem.view.ChatControllerDelegates;

import com.example.chatsystem.model.Room;
import com.example.chatsystem.view.WINDOW;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;

import java.util.HashMap;
import java.util.Optional;

public class RoomHandler
{
    private ChatController c;


    public RoomHandler(ChatController chatController)
    {
        this.c = chatController;
    }
    protected void addRoom(Room room)
    {
        addRoom(c.roomList.getChildren().size(), room);
    }

    protected void addRoom(int index, Room room)
    {
        Circle circle = new Circle(30);
        Label label = new Label(room.getName());
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14");
        Popup popup = new Popup();


        VBox vbox = new VBox(label);
        vbox.setStyle("-fx-background-color: #4C956C;");
        vbox.setPadding(new Insets(0, 10, 0,10));
        popup.getContent().add(vbox);

        HBox imageRoomContainer = new HBox(circle);
        imageRoomContainer.setMaxWidth(Double.MAX_VALUE);
        imageRoomContainer.setAlignment(Pos.CENTER);


        c.roomList.getChildren().add(index, imageRoomContainer);
        if(c.roomList.getChildren().size() == 1)
        {
            c.currentRoom = imageRoomContainer;
            imageRoomContainer.getStyleClass().add("room-selected");
        }

        circle.setOnMouseEntered(event -> {
            popup.show(imageRoomContainer.getScene().getWindow(), event.getScreenX() + 10, event.getScreenY() + 10);
        });

        circle.setOnMouseClicked(event -> {
            int indexX = c.roomList.getChildren().indexOf(imageRoomContainer);
            if(!c.viewModel.loadChannelsByRoomIndex(indexX))
                return;

            if (c.currentRoom != null)
                c.currentRoom.getStyleClass().remove(c.currentRoom.getStyleClass().size() - 1);
            c.currentRoom = imageRoomContainer;
            imageRoomContainer.getStyleClass().add("room-selected");

            if(event.getButton() == MouseButton.SECONDARY)
            {
                if(c.viewModel.isModeratorInRoom(room.getId()))
                {
                    HashMap<String, Runnable> options = new HashMap<>();
                    options.put("Edit", () -> {
                        c.viewHandler.openParallelView(WINDOW.EDIT_ROOM);
                    });
                    c.showContextMenu(options, event.getScreenX(), event.getScreenY());
                }
            }
        });


        circle.setOnMouseExited(event -> {
            popup.hide();
        });

        if(room.getImage() != null)
            circle.setFill(new ImagePattern(room.getImage()));

    }

    protected void joinRoom(Room room)
    {
        String dialogColor = "yellow";
        TextInputDialog dialog = new TextInputDialog();
        var dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + dialogColor);

        dialog.setTitle("Input Dialog");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter the room code:");

        // Show the dialog and wait for user input
        Optional<String> result = dialog.showAndWait();

        // Process the user input
        result.ifPresent(code -> {
            Alert confirmationDialog;
            if(!code.equals(room.getCode()))
            {
                confirmationDialog = new Alert(Alert.AlertType.WARNING);
                confirmationDialog.setTitle("Oops");
                confirmationDialog.setHeaderText(null);
                confirmationDialog.setContentText("The code that was entered is incorrect!");
            }
            else
            {
                confirmationDialog = new Alert(Alert.AlertType.INFORMATION);
                confirmationDialog.setTitle("Welcome");
                confirmationDialog.setHeaderText(null);
                confirmationDialog.setContentText("You successfully joined the room: " + room.getName());
                c.viewModel.joinRoom(room);
                HBox hbox = (HBox) c.roomList.getChildren().get(c.viewModel.getRoomIndex(room.getId()));
                Circle circle = ((Circle) hbox.getChildren().get(0));
                MouseEvent clickEvent = new MouseEvent(
                        MouseEvent.MOUSE_CLICKED,
                        0, 0, 0, 0,
                        MouseButton.PRIMARY, 1,
                        false, false, false, false,
                        true, false, false, false,
                        true, true, null
                );
                circle.fireEvent(clickEvent);
            }
            confirmationDialog.getDialogPane().setStyle("-fx-background-color: " + dialogColor);
            confirmationDialog.showAndWait();
        });
    }
}
