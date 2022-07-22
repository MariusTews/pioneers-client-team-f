package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.Template.MapTemplate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Objects;

public class MapTemplateSubcontroller implements Controller{
    private final MapTemplate template;
    private final boolean ownMap;
    private final String createdBy;
    private Image leftActionImage;
    private Image rightActionImage;
    @FXML public Label nameLabel;
    @FXML public Label createdByLabel;
    @FXML public Label votesLabel;
    @FXML public Button showButton;
    @FXML public ImageView selectedImageView;
    @FXML public ImageView leftActionImageView;
    @FXML public ImageView rightActionImageView;

    public MapTemplateSubcontroller(MapTemplate template, boolean ownMap, String createdBy) {
        this.template = template;
        this.ownMap = ownMap;
        this.createdBy = createdBy;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {
        leftActionImage = null;
        rightActionImage = null;
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
        createdByLabel.setText("| " + createdBy);
        votesLabel.setText("| " + template.votes());

        if (ownMap) {
            leftActionImage = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/editIcon.png")).toString());
            rightActionImage = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/deleteIcon.png")).toString());
        }
        else {
            leftActionImage = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/thumbsUpIcon.png")).toString());
            rightActionImage = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/thumbsDownIcon.png")).toString());
        }

        leftActionImageView.setImage(leftActionImage);
        rightActionImageView.setImage(rightActionImage);

        if (template._id().equals("")) {
            ((HBox) parent).getChildren().clear();
        }

        return parent;
    }

    public void onLeftActionImagePressed() {
        if (ownMap) {
            //edit map
        }
        else {
            //up-vote map
        }
    }

    public void onRightActionImagePressed() {
        if (ownMap) {
            //delete map
        }
        else {
            //down-vote map
        }
    }
}
