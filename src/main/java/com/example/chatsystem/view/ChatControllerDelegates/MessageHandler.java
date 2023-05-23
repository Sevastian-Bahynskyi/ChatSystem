package com.example.chatsystem.view.ChatControllerDelegates;

import com.example.chatsystem.model.Message;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.HashMap;

public class MessageHandler
{
    private ChatController c;

    public MessageHandler(ChatController chatController)
    {
        this.c = chatController;
    }

    protected void addMessage(VBox template, Image image, Message message, boolean isNeedAnimation)
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

        var messageUI = c.generateTemplate(template, image, message.getMessage(), imageRadius, fontSize);


        vBox.getChildren().add(messageUI);
        vBox.setAlignment(template.getAlignment());
        vBox.setPadding(template.getPadding());
        vBox.setPrefSize(template.getPrefWidth(), template.getPrefHeight());
        vBox.setMaxSize(template.getMaxWidth(), template.getMaxHeight());
        vBox.setMinSize(template.getMinWidth(), template.getMinHeight());

        int transitionValue = 100;

        if(!template.equals(c.messageMyTemplate))
        {
            transitionValue = -transitionValue;
        }

        if(isNeedAnimation)
            vBox.setTranslateX(transitionValue);
        TranslateTransition animation = new TranslateTransition(Duration.seconds(1), vBox);

        animation.setByX(-transitionValue);
        c.chatPane.getChildren().add(vBox);
        if((c.viewModel.isMyMessage(message) || c.viewModel.isModerator(message.getChannelId())) && !message.getMessage().equals("deleted message"))
        {
            HashMap<String, Runnable> options = new HashMap<>();

            messageUI.setOnMouseClicked(event ->
            {
                if (event.getButton() == MouseButton.SECONDARY)
                {
                    c.indexOfMessageToChange = c.chatPane.getChildren().indexOf(vBox);
                    c.showContextMenu(options, event.getScreenX(), event.getScreenY());
                }
            });

            if(!c.viewModel.isModerator(message.getChannelId()))
                options.put("Edit", () -> editMessage(message.getMessage()));

            options.put("Delete", () -> c.deleteMessage(vBox));
        }
        if(isNeedAnimation)
            animation.play();
    }

    protected void editMessage(String message)
    {
        c.isEditMessage = true;
        c.textField.requestFocus();
        c.textField.setText(message);
        c.textField.positionCaret(c.textField.getLength());
    }

    protected void deleteMessage(VBox vBox)
    {
        int index = c.chatPane.getChildren().indexOf(vBox);
        c.editMessageInUI(vBox, "deleted message");
        new Thread(() -> c.viewModel.deleteMessage(index)).start();
    }
}
