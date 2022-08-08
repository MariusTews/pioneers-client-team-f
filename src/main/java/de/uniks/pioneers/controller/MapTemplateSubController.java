package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.template.MapTemplate;
import de.uniks.pioneers.service.MapsService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Provider;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class MapTemplateSubController implements Controller {
    private final App app;
    private MapTemplate template;
    private final boolean ownMap;
    private final String createdBy;
    private final MapsService mapsService;
    private MapTemplatesScreenController mapTemplatesScreenController;
    private Provider<MapTemplateViewController> mapTemplateViewController;
    private Image leftActionImage;
    private Image rightActionImage;
    private Image selectedMapIcon;
    private Parent parent;
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

    public MapTemplateSubController(App app, MapTemplate template, boolean ownMap, String createdBy, MapsService mapsService, MapTemplatesScreenController mapTemplatesScreenController) {
        this.app = app;
        this.template = template;
        this.ownMap = ownMap;
        this.createdBy = createdBy;
        this.mapsService = mapsService;
        this.mapTemplatesScreenController = mapTemplatesScreenController;
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
        this.parent = parent;

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
            openDeleteDialog();
        } else {
            //down-vote map
            //TODO
        }
    }

    private void openDeleteDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        // Change style of alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
        alert.setTitle("Deleting Map Template");
        alert.setContentText("Are you sure you want to delete " + template.name() + "?");
        alert.getButtonTypes().set(0, ButtonType.YES);
        alert.getButtonTypes().set(1, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            //delete map
            this.mapsService.deleteMapTemplate(this.template._id()).observeOn(FX_SCHEDULER).subscribe();
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

    public String getSortValue(String sortBy) {
        String value = "";
        switch (sortBy) {
            case "name" -> value = template.name();
            case "createdBy" -> value = createdBy;
            case "votes" -> value = String.valueOf(template.votes());
        }
        return value;
    }

    public boolean isOwnMap() {
        return ownMap;
    }

    public Parent getParent() {
        return parent;
    }

    public void setTemplate(MapTemplate template) {
        this.template = template;
    }

	public void onShowClicked() {
        final MapTemplateViewController controller = new MapTemplateViewController(this.app, this.template, this.mapTemplatesScreenController);
        app.show(controller);
	}
}
