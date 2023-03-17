module com.example.chatsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;


    opens com.example.chatsystem to javafx.fxml;
    opens com.example.chatsystem.view to javafx.fxml;
    exports com.example.chatsystem;
    exports com.example.chatsystem.view;
}