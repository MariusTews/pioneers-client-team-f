package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.Template.MapTemplate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class MapTemplateSubcontroller implements Controller{
    private final MapTemplate template;
    @FXML public Label nameLabel;
    @FXML public Label createdByLabel;
    @FXML public Label votesLabel;
    @FXML public Button showButton;
    @FXML public ImageView selectedImageView;
    @FXML public ImageView leftActionImageView;
    @FXML public ImageView rightActionImageView;

    public MapTemplateSubcontroller(MapTemplate template) {
        this.template = template;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/MapTemplateListItem.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        nameLabel.setText(template.name());
        createdByLabel.setText(template.createdBy());
        votesLabel.setText("" + template.votes());

        return parent;
    }
}
