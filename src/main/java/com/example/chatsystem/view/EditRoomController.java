package com.example.chatsystem.view;

import com.example.chatsystem.viewmodel.ViewModel;
import javafx.scene.layout.Region;

import java.io.IOException;

public class EditRoomController extends AddRoomController
{
    @Override
    public void init(ViewHandler viewHandler, ViewModel viewModel, Region root) throws IOException
    {
        super.init(viewHandler, viewModel, root);
    }

    @Override
    protected void onCreateRoom()
    {
        viewModel.onEditRoom();
        viewHandler.closeWindow(getRoot());
    }
}
