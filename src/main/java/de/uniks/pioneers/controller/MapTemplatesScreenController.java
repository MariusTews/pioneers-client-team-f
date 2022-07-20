package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.io.IOException;

public class MapTemplatesScreenController implements Controller{
    @FXML public ImageView nameArrow;
    @FXML public ImageView createdByArrow;
    @FXML public ImageView votesArrow;
    @FXML public Label selectedLabel;
    @FXML public ListView mapTemplatesListView;
    @FXML public Button backButton;
    @FXML public Button createButton;
    @FXML public Button selectButton;

    @Inject
    public MapTemplatesScreenController() {

    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/MapTemplatesScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return parent;
    }

    public void onBackButtonPressed(ActionEvent actionEvent) {

    }

    public void onCreateButtonPressed(ActionEvent actionEvent) {

    }

    public void onSelectButtonPressed(ActionEvent actionEvent) {

    }
}
