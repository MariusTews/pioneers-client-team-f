package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Vote;
import de.uniks.pioneers.service.MapsService;
import de.uniks.pioneers.template.MapTemplate;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.inject.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class MapTemplateSubController implements Controller {
    private final App app;
    private MapTemplate template;
    private final boolean ownMap;
    private final String createdBy;
    private final String userId;
    private final MapTemplatesScreenController parentController;
    private MapsService mapsService;
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
    private Pane mainPane;
    private final IntegerProperty voted = new SimpleIntegerProperty();
    private final ChangeListener<? super Number> voteListener = this::onVoteScoreChanged;
    private final ColorAdjust darkColorAdjust = new ColorAdjust();
    private final ColorAdjust brightColorAdjust = new ColorAdjust();
    private HashMap<String, String> userNames;

    public MapTemplateSubController(App app, MapTemplate template, boolean ownMap, String createdBy, MapsService mapsService, MapTemplatesScreenController mapTemplatesScreenControllerString, String userId, MapTemplatesScreenController parentController) {
        this.app = app;
        this.template = template;
        this.ownMap = ownMap;
        this.createdBy = createdBy;
        this.userId = userId;
        this.parentController = parentController;
        this.mapTemplatesScreenController = mapTemplatesScreenController;
    }

    @Override
    public void init() {
        selectedMapIcon = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/selectedMapIcon.png")).toString());
        darkColorAdjust.setBrightness(-0.3);
        brightColorAdjust.setBrightness(0.3);
        mapsService = parentController.getMapsService();
    }

    @Override
    public void destroy() {
        leftActionImage = null;
        rightActionImage = null;
        selectedMapIcon = null;
        votesLabel.setOnMouseClicked(null);
        if (!ownMap) {
            voted.removeListener(voteListener);
        }
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

        if (!ownMap) {
            voted.addListener(voteListener);
        }

        mainPane = parentController.getMainPane();
        userNames = parentController.getUserNames();
        votesLabel.setOnMouseEntered(event -> ((Node) event.getSource()).setEffect(new Glow(0.7)));
        votesLabel.setOnMouseExited(event -> ((Node) event.getSource()).setEffect(null));
        votesLabel.setOnMouseClicked(this::showVotes);

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
            // edit map
            //TODO
        } else {
            updateVote(1);
        }
    }

    public void onRightActionImagePressed() {
        if (ownMap) {
            openDeleteDialog();
        } else {
            updateVote(-1);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void updateVote(int score) {
        if (voted.get() == score) {
            // delete vote
            this.mapsService.deleteVote(template._id(), userId).observeOn(FX_SCHEDULER).subscribe(
                    vote -> voted.set(0)
            );
        } else {
            // vote map according to score
            this.mapsService.voteMap(template._id(), score).observeOn(FX_SCHEDULER).subscribe(
                    vote -> voted.set(score)
            );
        }
    }

    private void onVoteScoreChanged(ObservableValue<? extends Number> observableValue, Number oldScore, Number newScore) {
        switch (newScore.intValue()) {
            case -1 -> {
                leftActionImageView.setDisable(true);
                leftActionImageView.setEffect(darkColorAdjust);
                rightActionImageView.setEffect(brightColorAdjust);
            }
            case 0 -> {
                leftActionImageView.setDisable(false);
                leftActionImageView.setEffect(null);
                rightActionImageView.setDisable(false);
                rightActionImageView.setEffect(null);
            }
            case 1 -> {
                rightActionImageView.setDisable(true);
                rightActionImageView.setEffect(darkColorAdjust);
                leftActionImageView.setEffect(brightColorAdjust);
            }
        }
    }

    private void showVotes(MouseEvent mouseEvent) {
        votesLabel.setEffect(null);

        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: #666666; -fx-border-color: grey; -fx-border-width: 3px");
        pane.setLayoutX(325);
        pane.setLayoutY(25);

        Image upVoteImage = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/thumbsUpIcon.png")).toString());
        ImageView upVoteImageView = new ImageView(upVoteImage);
        upVoteImageView.setFitHeight(25);
        upVoteImageView.setFitWidth(25);
        upVoteImageView.setTranslateX(35);
        upVoteImageView.setTranslateY(5);

        Image downVoteImage = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/thumbsDownIcon.png")).toString());
        ImageView downVoteImageView = new ImageView(downVoteImage);
        downVoteImageView.setFitHeight(25);
        downVoteImageView.setFitWidth(25);
        downVoteImageView.setTranslateX(185);
        downVoteImageView.setTranslateY(5);

        Button closeButton = new Button("X");
        closeButton.setTranslateX(260);
        closeButton.setOnAction(event -> mainPane.getChildren().remove(pane));

        HBox upperHBox = new HBox(6);
        upperHBox.setPrefHeight(28);
        upperHBox.getChildren().addAll(upVoteImageView, downVoteImageView, closeButton);

        ScrollPane upVoteScrollPane = new ScrollPane();
        upVoteScrollPane.setPrefWidth(180);
        upVoteScrollPane.setPrefHeight(300);
        upVoteScrollPane.setPadding(new Insets(0, 0, 0, 6));
        upVoteScrollPane.setStyle("-fx-background-color: transparent");

        ScrollPane downVoteScrollPane = new ScrollPane();
        downVoteScrollPane.setPrefWidth(180);
        downVoteScrollPane.setPrefHeight(300);
        downVoteScrollPane.setPadding(new Insets(0, 0, 0, 6));
        downVoteScrollPane.setStyle("-fx-background-color: transparent");

        VBox upVoteVBox = new VBox();
        upVoteVBox.setPadding(new Insets(6, 0, 0, 6));

        VBox downVoteVBox = new VBox();
        downVoteVBox.setPadding(new Insets(6, 0, 0, 6));

        upVoteScrollPane.setContent(upVoteVBox);
        downVoteScrollPane.setContent(downVoteVBox);

        HBox lowerHBox = new HBox();
        lowerHBox.getChildren().addAll(upVoteScrollPane, downVoteScrollPane);

        List<Vote> votes = this.mapsService.findVotesByMapId(template._id()).blockingFirst();
        for (Vote vote : votes) {
            HBox item = new HBox();
            Label nameLabel = new Label(userNames.getOrDefault(vote.userId(), ""));
            item.getChildren().add(nameLabel);
            if (vote.score().equals(1)) {
                upVoteVBox.getChildren().add(item);
            } else {
                downVoteVBox.getChildren().add(item);
            }
        }

        VBox vBox = new VBox();
        vBox.setPrefWidth(360);
        vBox.setPrefHeight(328);
        vBox.getChildren().addAll(upperHBox, lowerHBox);

        pane.getChildren().add(vBox);
        mainPane.getChildren().add(pane);
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

    public void setVoted(int score) {
        this.voted.set(score);
    }
}
