package de.uniks.pioneers.service;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Player;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

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

    public boolean showFriendsMenu(String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, text);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    //Gives alert based on what card have been received
    public void alertForEachCard(Player player, Player p) {
        if (player.developmentCards().get(player.developmentCards().size() - 1).type().equals("victory-point")
                && player.developmentCards().size() != p.developmentCards().size()) {
            showAlert("Congratulations, you acquired Victory point card");
        } else if (player.developmentCards().get(player.developmentCards().size() - 1).type().equals("knight")
                && player.developmentCards().size() != p.developmentCards().size()) {
            showAlert("Congratulations, you acquired Knight card");
        } else if (player.developmentCards().get(player.developmentCards().size() - 1).type().equals("road-building")
                && player.developmentCards().size() != p.developmentCards().size()) {

            showAlert("Congratulations, you acquired Road-building card");
        } else if (player.developmentCards().get(player.developmentCards().size() - 1).type().equals("year-of-plenty")
                && player.developmentCards().size() != p.developmentCards().size()) {
            showAlert("Congratulations, you acquired Year-of-plenty card");
        } else if (player.developmentCards().get(player.developmentCards().size() - 1).type().equals("monopoly")
                && player.developmentCards().size() != p.developmentCards().size()) {
            showAlert("Congratulations, you acquired Monopoly card");
        }
    }
}
