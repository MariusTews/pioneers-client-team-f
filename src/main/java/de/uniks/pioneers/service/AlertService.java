package de.uniks.pioneers.service;

import de.uniks.pioneers.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

import javax.inject.Inject;
import java.util.Objects;

public class AlertService {

    @Inject
    public AlertService() {

    }

    public void showAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, text);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
        alert.showAndWait();
    }
}
