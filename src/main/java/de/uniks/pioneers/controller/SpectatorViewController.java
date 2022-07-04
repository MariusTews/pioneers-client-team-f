package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.io.IOException;

public class SpectatorViewController implements Controller{

    private final User user;
    @FXML
    public Label spectatorNameId;


    @Inject
    public SpectatorViewController(User user) {
        this.user = user;
    }

    @Override
    public void init() {


    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/SpectatorView.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        this.spectatorNameId.setText(user.name());

        return parent;
    }
}
