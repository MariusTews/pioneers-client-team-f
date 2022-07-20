package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
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
import javax.inject.Provider;
import java.io.IOException;

public class MapTemplatesScreenController implements Controller{
    private final App app;
    private final Provider<CreateGameController> createGameController;
    @FXML public ImageView nameArrow;
    @FXML public ImageView createdByArrow;
    @FXML public ImageView votesArrow;
    @FXML public Label selectedLabel;
    @FXML public ListView mapTemplatesListView;
    @FXML public Button backButton;
    @FXML public Button createButton;
    @FXML public Button selectButton;

    @Inject
    public MapTemplatesScreenController(App app, Provider<CreateGameController> createGameController) {
        this.app = app;
        this.createGameController = createGameController;
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

    public void onBackButtonPressed() {
        final CreateGameController controller = createGameController.get();
        this.app.show(controller);
    }

    public void onCreateButtonPressed(ActionEvent actionEvent) {

    }

    public void onSelectButtonPressed(ActionEvent actionEvent) {

    }
}
