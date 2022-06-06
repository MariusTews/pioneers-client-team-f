package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.io.IOException;

public class OpponentSubController implements Controller {

    @FXML
    VBox singleOpponentView;
    @FXML
    ImageView avatarImageView;
    @FXML
    Label usernameLabel;
    // TODO: in the second release victory points are not given, have to calculate them
    @FXML
    Label victoryPointsLabel;
    // TODO: Amount of opponent's resources is not displayed, add mouse hover for resource names
    //  and add images of resources
    @FXML
    HBox resourcesView;
    private final Member opponent;
    private final User opponentAsUser;
    private String userId;
    private Parent parent;

    @Inject
    public OpponentSubController(Member member, User user) {
        // Opponent as player needed for victory points and color
        this.opponent = member;

        // Opponents as user needed for username and avatar
        this.opponentAsUser = user;
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

        if (this.opponent != null) {
            this.userId = this.opponent.userId();
            this.usernameLabel.setText(this.opponentAsUser.name());
            this.usernameLabel.setTextFill(Color.web(this.opponent.color()));
            if (this.opponentAsUser.avatar() != null) {
                this.avatarImageView.setImage(new Image(this.opponentAsUser.avatar()));
            }
            this.victoryPointsLabel.setText("UP: " + " /10");
        }

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
