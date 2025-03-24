package org.example.demo;

import javafx.embed.swing.SwingNode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.swing.*;

public class mapStackPane extends StackPane {
    private mainMap mainMap;
    public mapStackPane() {
        super();
        final SwingNode swingNode = new SwingNode();
        mainMap map = new mainMap();
        SwingUtilities.invokeLater(() -> {
            JPanel panel = map; // Utilise le JPanel de ton programme
            swingNode.setContent(panel);
        });
        VBox.setVgrow(swingNode, javafx.scene.layout.Priority.ALWAYS);

        this.getChildren().add(swingNode);
    }
}
