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

import java.io.IOException;
import java.util.Objects;

public class MapTemplateSubController implements Controller {
    private MapTemplate template;
    private final boolean ownMap;
    private final String createdBy;
    private Image leftActionImage;
    private Image rightActionImage;
    private Image selectedMapIcon;
    @FXML
    public Label nameLabel;
    @FXML
    public Label createdByLabel;
    @FXML
    public Label votesLabel;
    @FXML
    public Button showButton;
    @FXML
    public ImageView selectedImageView;
    @FXML
    public ImageView leftActionImageView;
    @FXML
    public ImageView rightActionImageView;

    public MapTemplateSubController(MapTemplate template, boolean ownMap, String createdBy) {
        this.template = template;
        this.ownMap = ownMap;
        this.createdBy = createdBy;
    }

    @Override
    public void init() {
        selectedMapIcon = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/selectedMapIcon.png")).toString());
    }

    @Override
    public void destroy() {
        leftActionImage = null;
        rightActionImage = null;
        selectedMapIcon = null;
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

        updateContent();

        if (ownMap) {
            leftActionImage = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/editIcon.png")).toString());
            rightActionImage = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/deleteIcon.png")).toString());
        } else {
            leftActionImage = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/thumbsUpIcon.png")).toString());
            rightActionImage = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/thumbsDownIcon.png")).toString());
        }

        leftActionImageView.setImage(leftActionImage);
        rightActionImageView.setImage(rightActionImage);

        parent.setId(template._id());

        return parent;
    }

    public void updateContent() {
        nameLabel.setText(template.name());
        createdByLabel.setText("| " + createdBy);
        if ((int) template.votes() > 0) {
            votesLabel.setText("| +" + template.votes());
        } else if ((int) template.votes() == 0) {
            votesLabel.setText("|  " + template.votes());
        } else {
            votesLabel.setText("| " + template.votes());
        }
    }

    public void onLeftActionImagePressed() {
        if (ownMap) {
            //edit map
            //TODO
        } else {
            //up-vote map
            //TODO
        }
    }

    public void onRightActionImagePressed() {
        if (ownMap) {
            //delete map
            //TODO
        } else {
            //down-vote map
            //TODO
        }
    }

    public void selectItem() {
        selectedImageView.setImage(selectedMapIcon);
    }

    public void unselectItem() {
        selectedImageView.setImage(null);
    }

    public MapTemplate getTemplate() {
        return template;
    }

    public void setTemplate(MapTemplate template) {
        this.template = template;
    }
}
