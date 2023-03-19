package com.example.chatsystem.view;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ImageLoader {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public void loadImage(String imageUrl, Consumer<Image> imageConsumer) {
        Task<Image> task = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                return new Image(imageUrl);
            }

            @Override
            protected void succeeded() {
                Image image = getValue();
                imageConsumer.accept(image);
            }
        };

        executor.submit(task);
    }
}
