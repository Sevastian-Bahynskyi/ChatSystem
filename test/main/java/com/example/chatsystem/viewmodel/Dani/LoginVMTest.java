package main.java.com.example.chatsystem.viewmodel.Dani;

import com.example.chatsystem.model.Chatter;
import com.example.chatsystem.model.Message;
import com.example.chatsystem.model.Model;
import com.example.chatsystem.model.UserInterface;
import com.example.chatsystem.view.ChatControllerDelegates.ChatController;
import com.example.chatsystem.view.LoginController;
import com.example.chatsystem.viewmodel.LoginViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;

public class LoginVMTest {
    private Model modelMock;
    private LoginViewModel loginViewModel;
    private LoginController loginControllerMock;
    private UserInterface Dummy = new Chatter("111111","Blahaj","shorks123");
    private ArrayList<UserInterface> dummyList = new ArrayList<>();

    private ObjectProperty<Image> profileImage = new SimpleObjectProperty();
    private SimpleStringProperty textFieldProperty = new SimpleStringProperty();
    @BeforeEach
    void setUp() throws SQLException, IOException
    {
        dummyList.add(Dummy);
        modelMock  = Mockito.mock(Model.class);
        this.loginViewModel = new LoginViewModel(modelMock);
        Mockito.when(modelMock.getUserList()).thenReturn(dummyList);


        loginControllerMock = Mockito.mock(LoginController.class);

    }
    @Test
    public void testOnSuccess(){
        Assertions.assertTrue(loginViewModel.onLogin());
        loginViewModel.setImageUrl("/com/example/chatsystem/images/default_user_avatar.png");
        loginViewModel.bindId(textFieldProperty);
        loginViewModel.bindError(textFieldProperty);
        loginViewModel.bindPassword(textFieldProperty);
        loginViewModel.bindUsername(textFieldProperty);
        Assertions.assertTrue(loginViewModel.onRegister());

    }
    @Test
    public void exceptionTests(){

            //Mockito.when(modelMock.login(anyString(),anyString(),anyString())).thenThrow(RuntimeException.class);
            Mockito.doThrow(RuntimeException.class).when(modelMock).login(Mockito.<String>any(),Mockito.<String>any(),Mockito.<String>any());
            Assertions.assertFalse(loginViewModel.onLogin(),"Login not failed");
            Mockito.doThrow(RuntimeException.class).when(modelMock).register(Mockito.<String>any(),Mockito.<String>any(),Mockito.<String>any(),Mockito.<String>any());
            Assertions.assertFalse(loginViewModel.onRegister(),"Register not failed");

    }
}

