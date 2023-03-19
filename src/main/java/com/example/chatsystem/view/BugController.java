package com.example.chatsystem.view;

import com.example.chatsystem.viewmodel.BugViewModel;
import com.example.chatsystem.viewmodel.ViewModel;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import java.util.Objects;

public class BugController implements Controller
{
    private ViewHandler viewHandler;
    private BugViewModel viewModel;
    private Region root;
    @FXML
    private ImageView deadImage;

    @Override
    public void init(ViewHandler viewHandler, ViewModel viewModel, Region root)
    {
        this.viewHandler = viewHandler;
        this.viewModel = (BugViewModel) viewModel;
        this.root = root;
        this.deadImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/chatsystem/images/dead_screen.png"))));
    }
    @Override
    public Region getRoot()
    {
        return root;
    }
}
