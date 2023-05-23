module com.example.chatsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires remoteobserver;
    requires fontawesomefx;
    requires java.rmi;
    requires com.google.gson;
    requires java.sql;
    requires com.zaxxer.hikari;
  requires org.postgresql.jdbc;

    opens com.example.chatsystem to javafx.fxml;
    opens com.example.chatsystem.view to javafx.fxml;
    opens com.example.chatsystem.images to com.google.gson;
    opens com.example.chatsystem.model to com.google.gson;
    exports com.example.chatsystem.model to java.rmi;
    exports com.example.chatsystem.server.shared to java.rmi;
    exports com.example.chatsystem.server.client to java.rmi;
    exports com.example.chatsystem;
    exports com.example.chatsystem.view;
    exports com.example.chatsystem.viewmodel;
    exports com.example.chatsystem.view.ChatControllerDelegates;
    opens com.example.chatsystem.view.ChatControllerDelegates to javafx.fxml;
}