module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.json;
    requires jxmapviewer2;
    requires mapsforge.map;
    requires jsr305;
    requires com.google.auth.oauth2;
    requires google.cloud.firestore;
    requires google.cloud.core;
    requires com.google.auth;
    requires com.google.api.apicommon;
    requires javafx.graphics;

    opens org.example.demo to javafx.fxml;
    exports org.example.demo;
}