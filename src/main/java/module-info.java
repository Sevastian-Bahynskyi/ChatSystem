module com.example.chatsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires com.google.gson;
    requires java.sql;


    opens com.example.chatsystem to javafx.fxml;
    opens com.example.chatsystem.view to javafx.fxml;
    opens com.example.chatsystem.images to com.google.gson;
    opens com.example.chatsystem.model to com.google.gson;
    exports com.example.chatsystem;
    exports com.example.chatsystem.view;
}