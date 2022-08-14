package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.DevelopmentCard;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.io.IOException;

import static de.uniks.pioneers.Constants.KNIGHT;

public class OpponentSubController implements Controller {

    private final int maxVictoryPoints;

    @FXML
    public VBox singleOpponentView;
    @FXML
    public ImageView avatarImageView;
    @FXML
    public Label usernameLabel;
    @FXML
    public Label victoryPointsLabel;
    @FXML
    public ImageView earthCactusImage;
    @FXML
    public ImageView marsBarImage;
    @FXML
    public ImageView moonRockImage;
    @FXML
    public ImageView neptunCrystalsImage;
    @FXML
    public ImageView venusGrainImage;
    @FXML
    public Label totalResourcesLabel;
    @FXML
    public Label opponentFleetLabel;
    @FXML
    public ImageView opponentLargestFleetIcon;
    @FXML
    public ImageView opponentLongestRoadIcon;
    private final Player opponent;
    private final User opponentAsUser;
    public Label developmentCardsLabel;

    private String userId;
    private Parent parent;

    @Inject
    public OpponentSubController(Player player, User user, int maxVictoryPoints) {
        // Opponent as player needed for victory points and color
        this.opponent = player;
        // Opponents as user needed for username and avatar
        this.opponentAsUser = user;
        this.maxVictoryPoints = maxVictoryPoints;

    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/OpponentSubView.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Add victory points, add colored username and avatar
        if (this.opponent != null) {
            this.userId = this.opponent.userId();
            if (this.opponentAsUser == null) {
                this.usernameLabel.setText("UNKNOWN");
            } else {
                this.usernameLabel.setText(this.opponentAsUser.name());
            }
            this.usernameLabel.setTextFill(Color.web(this.opponent.color()));
            if (this.opponentAsUser != null && this.opponentAsUser.avatar() != null) {
                this.avatarImageView.setImage(new Image(this.opponentAsUser.avatar()));
            }
            // Set the current victory points and total amount of resources
            this.victoryPointsLabel.setText("VP: " + this.opponent.victoryPoints() + "/" + maxVictoryPoints);
            this.totalResourcesLabel.setText("Total resources: " + opponent.resources().get("unknown"));
            if (opponent.developmentCards() != null) {
                this.developmentCardsLabel.setText("Development cards: " + opponent.developmentCards().size());
                int knight = 0;

                for (DevelopmentCard dc : opponent.developmentCards()) {
                    if (dc.type().equals(KNIGHT) && dc.revealed()) {
                        knight += 1;
                    }
                }
                this.opponentFleetLabel.setText(":" + knight);
            } else {
                this.developmentCardsLabel.setText("Development cards: 0");
                this.opponentFleetLabel.setText(": 0");
            }


            if (opponent.hasLargestArmy()) {
                this.opponentLargestFleetIcon.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/largestFleetIcon.png"))));
            }
            if (opponent.hasLongestRoad()) {
                this.opponentLongestRoadIcon.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/longestRoadIcon.png"))));
            }
        }

        // Add information when hovering over items
        Tooltip.install(earthCactusImage, new Tooltip("Earth cactus"));
        Tooltip.install(marsBarImage, new Tooltip("Mars bar"));
        Tooltip.install(moonRockImage, new Tooltip("Moon rock"));
        Tooltip.install(neptunCrystalsImage, new Tooltip("Neptune crystals"));
        Tooltip.install(venusGrainImage, new Tooltip("Venus grain"));


        this.parent = parent;
        return parent;
    }


    public String getId() {
        return userId;
    }

    public Parent getParent() {
        return parent;
    }
}
