package com.example.chatsystem.view.setimage;

import com.example.chatsystem.view.ChatController;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class GetImageAsFile
{
    public static String getImage(Window window) throws Exception
    {
        AtomicReference<String> imageURL = new AtomicReference<>();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        File recordsDir = new File(System.getProperty("user.home") + "/Pictures");
        fileChooser.setInitialDirectory(recordsDir);
        File selectedImage = fileChooser.showOpenDialog(window);
        if(selectedImage == null)
            return null;
        Path sourcePath = selectedImage.toPath();
        AtomicReference<Path> destinationPath = new AtomicReference<>(Paths.get("src/main/resources/com/example/chatsystem/images/" + selectedImage.getName()));


            try
            {
                Files.copy(sourcePath, destinationPath.get(), StandardCopyOption.REPLACE_EXISTING);
                destinationPath.set(Paths.get("target/classes/com/example/chatsystem/images/" + selectedImage.getName()));
                Files.copy(sourcePath, destinationPath.get(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e)
            {
                throw new RuntimeException("Image hasn't been chosen.");
            }
            imageURL.set("/com/example/chatsystem/images/" + selectedImage.getName());

        return imageURL.get();
    }
}
