package de.uniks.pioneers.controller;


import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.IDStorage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.inject.Inject;
import java.io.IOException;

public class UserListSubController implements Controller {
    @FXML
    public ImageView userImageView;
    @FXML
    public Circle userStatusCircle;
    @FXML
    public Label userNameLabel;
    @FXML
    public Button chatButton;
    private final LobbyController lobbyController;
    private final User user;
    private final IDStorage idStorage;

    private Parent parent;

    private String id;

    @Inject
    public UserListSubController(LobbyController lobbyController, User user, IDStorage idStorage) {

        this.lobbyController = lobbyController;
        this.user = user;
        this.idStorage = idStorage;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/UserListSubView.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (this.user != null) {
            this.userNameLabel.setText(this.user.name());
            this.id = user._id();

            if (this.user.status().equals("online")) {
                this.userStatusCircle.setFill(Color.GREEN);
                if (this.user.avatar() != null) {
                    this.userImageView.setImage(new Image(this.user.avatar()));
                }
            } else if (this.user.status().equals("offline")) {
                this.userStatusCircle.setFill(Color.RED);
            }

            if (this.user.status().equals("offline") || user._id().equals(idStorage.getID())) {
                HBox box = (HBox) this.chatButton.getParent();
                box.getChildren().removeIf(node -> node.equals(chatButton));
            }
        }

        this.parent = parent;
        return parent;
    }

    public void chatButtonPressed() {
        this.lobbyController.openDirectChat(this.user);
    }

    public Parent getParent() {
        return parent;
    }

    public String getId() {
        return id;
    }
}
