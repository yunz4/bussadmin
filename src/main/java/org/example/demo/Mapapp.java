package org.example.demo;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Mapapp extends Application {

    private JXMapViewer mapViewer;
    private List<GeoPosition> waypoints = new ArrayList<>();
    private GeoPosition firstMarker, secondMarker;
    private boolean isFirstMarkerActive = false, isSecondMarkerActive = false;
    private JTextField routeTextField;
    GeoPosition destination = null;
    GeoPosition source = null;
    private itineraire itineraire ;
    private Integer numpa = 0;
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 10px; -fx-background-color: #ECECEC;");
        itineraire = new itineraire();
        // Create a SwingNode to display JXMapViewer inside JavaFX
        final SwingNode swingNode = new SwingNode();
        mainMap map = new mainMap();
        SwingUtilities.invokeLater(() -> {
            JPanel panel = map; // Utilise le JPanel de ton programme
            swingNode.setContent(panel);
        });
        VBox.setVgrow(swingNode, javafx.scene.layout.Priority.ALWAYS);

        StackPane pane = new StackPane();
        pane.getChildren().add(swingNode);
        primaryStage.setScene(new Scene(pane, 600, 500));
        primaryStage.show();

        // Text field to enter route name
        TextField routeNameField = new TextField();
        routeNameField.setPromptText("Enter route name...");

        // Buttons to add markers
        Button addFirstMarkerButton = new Button("Set First Marker");
        Button addSecondMarkerButton = new Button("Set Second Marker");
        Button clearButton = new Button("Clear Markers");
        Button addPointButton = new Button("Ajouter point d'arret");
        // Button Actions


        addFirstMarkerButton.setOnAction(e -> {
            source =  map.markSource();
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Source");
            dialog.setHeaderText(null);
            dialog.setContentText("Source: ");
            dialog.setGraphic(null);
            ButtonType customOkButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
            ButtonType customCancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().setAll(customOkButton, customCancelButton);
            Optional<String> src = dialog.showAndWait();
            src.ifPresent(s -> {
                itineraire.setSource(new Pair<>(s, source));
            });
        });

        addSecondMarkerButton.setOnAction(e -> {
            destination = map.markDestination();
            if(destination != null) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Destination");
                dialog.setHeaderText(null);
                dialog.setContentText("Destination: ");
                dialog.setGraphic(null);
                ButtonType customOkButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
                ButtonType customCancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().setAll(customOkButton, customCancelButton);
                dialog.showAndWait();
                Optional<String> dest = dialog.showAndWait();
                dest.ifPresent(s -> {
                    itineraire.setDestination(new Pair<>(s, destination));
                });
            }
        });

        clearButton.setOnAction(e -> {
            map.clearMarkers();
        });


            addPointButton.setOnAction(e -> {
                if(destination != null && source != null) {
                    numpa++;
                    System.out.println(numpa);
                    GeoPosition pointArret = map.ajouterPointArret();
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Point arret");
                    dialog.setHeaderText(null);
                    dialog.setContentText("Point arret: : ");
                    dialog.setGraphic(null);
                    ButtonType ajouter = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
                    ButtonType annuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
                    dialog.getDialogPane().getButtonTypes().setAll(ajouter, annuler);
                    dialog.showAndWait();
                    Optional<String> rslt = dialog.showAndWait();
                    rslt.ifPresent(s -> {
                        itineraire.addPointArret(numpa, new Pair<>(s, pointArret));
                    });
                }
            });

        // Add components to VBox
        root.getChildren().addAll(routeNameField, addFirstMarkerButton, addSecondMarkerButton, clearButton ,addPointButton,swingNode);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("JavaFX & JXMapViewer Map");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

