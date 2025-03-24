package org.example.demo;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserController {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> nom;
    @FXML
    private TableColumn<User, String> prenom;
    @FXML
    private TableColumn<User, String> email;
    @FXML
    private TableColumn<User, Integer> age;
    @FXML
    private TableColumn<User, String> sex;
    @FXML
    private TableColumn<User, Void> actionColumn; // Nouvelle colonne pour le bouton supprimer

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private Firestore firestore;

    public void initialize() throws Exception {
        FirestoreService firestoreService = new FirestoreService();
        firestore = firestoreService.getFirestore();

        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Empêcher certaines colonnes de devenir trop petites
        nom.setMinWidth(100);
        prenom.setMinWidth(100);
        email.setMinWidth(150);
        age.setMinWidth(50);
        sex.setMinWidth(50);
        actionColumn.setMinWidth(100);

        // Configurer les colonnes
        nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        age.setCellValueFactory(new PropertyValueFactory<>("age"));
        sex.setCellValueFactory(new PropertyValueFactory<>("sex"));

        // Charger les utilisateurs
        userList.addAll(getUsers());
        userTable.setItems(userList);

        // Ajouter les boutons de suppression
        addDeleteButtonToTable();
    }

    // Lire tous les utilisateurs depuis Firestore
    private List<User> getUsers() throws Exception {
        List<User> users = new ArrayList<>();
        try {


            ApiFuture<QuerySnapshot> future = firestore.collection("users").get();
            QuerySnapshot querySnapshot = future.get();

            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                String userId = document.getId();
                String nom = document.getString("nom");
                String prenom = document.getString("prenom");
                String email = document.getString("email");
                Number ageNumber;
                String ageString;
                int age;
                try {
                    ageNumber = document.getLong("age");
                    age = ageNumber == null ? 0 : ageNumber.intValue();
                }catch (Exception e) {
                    ageString = document.getString("age");
                    age = Integer.parseInt(ageString);
                }// Convertir en int en gérant les cas null
                String sex = document.getString("sex");
                users.add(new User(userId, nom, prenom, email, age, sex));

            }
        }catch (Exception e){
            e.printStackTrace();

        }
        return users;
    }

    @FXML
    private void deleteUser(User user) {
        try {
            if (user != null) {
                firestore.collection("users").document(user.getUserId()).delete().get();

                userList.remove(user);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }

    private void addDeleteButtonToTable() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Supprimer");

                    {
                        deleteButton.setOnAction((ActionEvent event) -> {
                            User user = getTableView().getItems().get(getIndex());
                            deleteUser(user);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }
    @FXML
    private void goBackToMain(ActionEvent event) {
        try {


            loadPage("Main.fxml", (Node) event.getSource());
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
            System.err.println("Erreur lors du chargement de la page : " + e.getMessage());
        }
    }


}
