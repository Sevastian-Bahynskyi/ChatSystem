package com.example.chatsystem.view.ChatControllerDelegates;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChannelHandler
{
    private ChatController c;


    public ChannelHandler(ChatController chatController)
    {
        this.c = chatController;
    }

    protected void addChannel(int index, String channelName)
    {
        Map<String, Runnable> options = new HashMap<>();


        Label label = new Label();
        label.setPadding(new Insets(10));
        label.setMaxWidth(Double.MAX_VALUE);
        label.setText(channelName);
        label.getStyleClass().set(0, "channel-selected");



        options.put("Edit", this::editChannel);
        options.put("Delete", this::deleteChannel);
        if(index == -1)
            c.channelListPane.getChildren().add(label);
        else c.channelListPane.getChildren().add(index, label);
        int indexOfChannel = c.indexOfChannelToChange;


        label.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY)
            {
                if(!c.viewModel.amIModerator())
                    return;

                c.indexOfChannelToChange = c.channelListPane.getChildren().indexOf(label);
                System.out.println("Index of channel: " + c.indexOfChannelToChange);

                c.showContextMenu(options, event.getScreenX(), event.getScreenY());
            }
            else if(event.getButton() == MouseButton.PRIMARY)
            {
                c.onChannelClick(event);

                c.viewModel.loadMessagesByChannelIndex(c.channelListPane.getChildren().indexOf(label));
            }
        });
        if (c.selectedChannel != null) {
            c.selectedChannel.getStyleClass().set(0, "channel");
        }
        c.selectedChannel = label;

    }

    protected void deleteChannel()
    {
        c.viewModel.deleteChannel(c.indexOfChannelToChange);

    }


    protected void editChannel()
    {
        c.isEditChannel = true;
        c.newChannelField.setVisible(true);
        c.newChannelField.requestFocus();
        c.newChannelField.setText(c.viewModel.getChannelByIndex(c.indexOfChannelToChange).getName());
        c.newChannelField.positionCaret(c.newChannelField.getLength());
    }
}
