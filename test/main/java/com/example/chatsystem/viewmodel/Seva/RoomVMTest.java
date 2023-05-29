package main.java.com.example.chatsystem.viewmodel.Seva;

import com.example.chatsystem.model.Data;
import com.example.chatsystem.model.Model;
import com.example.chatsystem.view.AddRoomController;
import com.example.chatsystem.view.EditRoomController;
import com.example.chatsystem.viewmodel.RoomViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class RoomVMTest
{
    private RoomViewModel viewModel;
    private EditRoomController editRoomController;
    private AddRoomController addRoomController;
    private StringProperty nameField;
    private StringProperty codeField;
    private StringProperty errorField;
    private Model modelMock;
    @BeforeEach void setUp()
    {
        modelMock = Mockito.mock(Model.class);
        viewModel = new RoomViewModel(modelMock);
        editRoomController = Mockito.mock(EditRoomController.class);
        addRoomController = Mockito.mock(AddRoomController.class);
        nameField = new SimpleStringProperty("roomName");
        codeField = new SimpleStringProperty("roomCode");
        errorField = new SimpleStringProperty("");
        errorField.setValue("");
        viewModel.bindNameField(nameField);
        viewModel.bindCodeField(codeField);
        viewModel.bindErrorField(errorField);
        viewModel.setImageUrl(Data.getDefaultImageUrl());
        assertEquals(viewModel.getImageUrl(), Data.getDefaultImageUrl());
    }

    @Test void onCreateRoom_call_add_room_in_model()
    {
        viewModel.onCreateRoom();
    }

    @Test void onEditRoom_call_edit_room_in_model()
    {
        viewModel.onEditRoom();
    }

    @Test void onEditRoom_throws_exception()
    {
        nameField.setValue("1");
        viewModel.onEditRoom();
        assertNull(errorField.getValue());
        errorField.setValue("");
    }

    @Test void onCreateRoom_throws_exception()
    {
        nameField.setValue("1");
        viewModel.onCreateRoom();
        assertNull(errorField.getValue());
        errorField.setValue("");
    }

    @Test void generateCode_should_have_8_min_size()
    {
        viewModel.generateCode();
        assertTrue(codeField.getValue().length() >= 8);
    }
}
