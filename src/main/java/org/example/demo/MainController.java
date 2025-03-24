package org.example.demo;

import com.google.cloud.firestore.Firestore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.demo.FirestoreService;

import java.io.IOException;

public class MainController {

    @FXML
    private TextField Destname;
    @FXML
    private TextField originename;
    @FXML
    private TextField routeid_field;
    @FXML
    private VBox mainLayout; // Inject the main layout container
    @FXML
    private StackPane mapContainer;

    private Firestore firestore;

    @FXML
    private AnchorPane mainContent; // Panneau principal pour charger le contenu dynamique

    public void initialize() throws Exception {
        FirestoreService firestoreService = new FirestoreService();
        firestore = firestoreService.getFirestore();
    }

    @FXML
    private void goToUserPage() {
        loadPage("users.fxml");
    }

    @FXML
    private void goToRoutePage() {
        loadPage("map_view.fxml");
    }

    // Méthode pour charger une page dans le panneau principal
    private void loadPage(String fxmlFile) {
        try {
            // Utilisez getClass().getResource() pour charger le fichier FXML
            Parent page = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
            mainContent.getChildren().setAll(page); // Charger la page dans le panneau principal
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la page : " + fxmlFile);
        }
    }

    @FXML
    private void goAjouterRoute(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/map_view.fxml"));
        Parent root = loader.load();
        MapController controller = loader.getController();
        controller.setFirestore(firestore);

        Scene currentScene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) currentScene.getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void goBackToHome(ActionEvent event) {
        loadPage("home.fxml", (Node) event.getSource());
    }

    @FXML
    private void goBackToMain(ActionEvent event) {
        loadPage("Main.fxml", (Node) event.getSource());
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

    public void setFirestore(Firestore firestore) {
        this.firestore = firestore;
    }
}