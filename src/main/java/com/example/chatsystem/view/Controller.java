package com.example.chatsystem.view;

import com.example.chatsystem.viewmodel.ViewModel;
import javafx.scene.layout.Region;

import java.io.IOException;

public interface Controller
{
    Region getRoot();
    void init(ViewHandler viewHandler, ViewModel viewModel, Region root) throws IOException;
}
