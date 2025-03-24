package org.example.demo;

import com.google.cloud.firestore.Firestore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.io.IOException;
import java.util.Optional;

public class MapController {

     // Field to enter route name
    @FXML
    private Button addFirstMarkerButton;  // Button to add the first marker
    @FXML
    private Button addSecondMarkerButton;  // Button to add the second marker
    @FXML
    private Button clearButton;  // Button to clear all markers
    @FXML
    private Button addPointButton;  // Button to add a point of interest (arret)
    @FXML
    private StackPane mapContainer;  // Container to hold the map
    private Firestore firestore;
    private itineraire itineraire;  // Itinerary object to store route data
    private mainMap map;  // Instance of the map
    private GeoPosition source, destination;  // Source and destination GeoPositions
    private int numpa = 0;  // Counter for stop points
    @FXML
    private TableView<itineraire> routeTable;  // Table to display route information
    @FXML
    private TableColumn<itineraire, String> sourceColumn;  // Column for Source
    @FXML
    private TableColumn<itineraire, String> destinationColumn;
    @FXML
    private VBox vbx;
    private ObservableList<itineraire> routeData;
    // Initialize the controller, setup map and buttons
    public void initialize() {
        // Initialize components
        itineraire = new itineraire();
        map = new mainMap();

        // Create a SwingNode to embed the Swing-based map
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            JPanel panel = map; // Getting map panel
            swingNode.setContent(panel);  // Setting the map in SwingNode
        });
        VBox.setVgrow(swingNode, javafx.scene.layout.Priority.ALWAYS);  // Allow the map to grow

        // Add the SwingNode (map) to the container
        mapContainer.getChildren().add(swingNode);

        // Set up event handlers for buttons
        addFirstMarkerButton.setOnAction(e -> setFirstMarker());
        addSecondMarkerButton.setOnAction(e -> setSecondMarker());
        clearButton.setOnAction(e -> clearMarkers());
        addPointButton.setOnAction(e -> addPointArret());
        // Initialize the ObservableList for the TableView
        routeData = FXCollections.observableArrayList();
        routeTable.setItems(routeData);

        // Set up columns in the TableView
    }

    private void setFirstMarker() {
        source = map.markSource();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Source");
        dialog.setContentText("Source: ");

        Optional<String> src = dialog.showAndWait();
        src.ifPresent(s ->
                setSource(s, source)
        );
    }
    public void setSource(String source, GeoPosition sourcePosition) {
        itineraire.setSource(new Pair<>(source, sourcePosition));
        sourceColumn.setCellValueFactory(cellData ->new SimpleStringProperty(source.toString()));
    }
    public void setDestination(String destination, GeoPosition destinationPosition) {
        itineraire.setDestination(new Pair<>(destination, destinationPosition));
        destinationColumn.setCellValueFactory(cellData ->new SimpleStringProperty(destination.toString()));
    }
    // Method to set the second marker (Destination)
    private void setSecondMarker() {
        destination = map.markDestination();  // Mark the destination on the map

        if (destination != null) {
            // Open a dialog for the user to enter the destination name
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Destination");
            dialog.setContentText("Destination: ");

            Optional<String> dest = dialog.showAndWait();
            dest.ifPresent(s -> setDestination(s,destination));  // Store the destination info
        }
    }
    public void setFirestore(Firestore firestore) {
        this.firestore = firestore;
    }

    // Method to add a point of interest (Point Arret)
    private void addPointArret() {
        if (destination != null && source != null) {
            numpa++;  // Increment the stop point counter

            // Mark the stop point on the map
            GeoPosition pointArret = map.ajouterPointArret();

            // Open a dialog for the user to enter the point name
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Point arret");
            dialog.setContentText("Point arret: ");

            Optional<String> rslt = dialog.showAndWait();
            rslt.ifPresent(s -> itineraire.addPointArret(numpa, new Pair<>(s, pointArret)));  // Store the stop point info
        }
    }

    // Method to clear all markers on the map
    private void clearMarkers() {
        map.clearMarkers();  // Clear the itinerary data
    }
    @FXML
    private void goBackToHome(ActionEvent event) {
        try {


            loadPage("home.fxml", (Node) event.getSource());
        } catch (Exception e) {
            System.err.println("Erreur lors de la fermeture de Firestore : " + e.getMessage());
        }
    }

    private void loadPage(String fxmlFile, Node node) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
            Parent root = loader.load();


            MainController controller = loader.getController();
            controller.setFirestore(firestore);

            Scene currentScene = node.getScene();

            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(fxmlFile.replace(".fxml", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void ajouterRoute(ActionEvent event) {

    }
}
