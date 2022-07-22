package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Template.MapTemplate;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.MapsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class MapTemplatesScreenController implements Controller{
    private final App app;
    private final Provider<CreateGameController> createGameController;
    private final MapsService mapsService;
    private final IDStorage idStorage;
    @FXML public ImageView nameArrow;
    @FXML public ImageView createdByArrow;
    @FXML public ImageView votesArrow;
    @FXML public Label selectedLabel;
    @FXML public Button backButton;
    @FXML public Button createButton;
    @FXML public Button selectButton;
    @FXML public ListView<Parent> mapTemplatesListView;
    private final ObservableList<Parent> mapTemplates = FXCollections.observableArrayList();
    private final List<MapTemplateSubcontroller> mapTemplateSubCons = new ArrayList<>();

    @Inject
    public MapTemplatesScreenController(App app, Provider<CreateGameController> createGameController, MapsService mapsService, IDStorage idStorage) {
        this.app = app;
        this.createGameController = createGameController;
        this.mapsService = mapsService;
        this.idStorage = idStorage;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {
        for (MapTemplateSubcontroller controller : mapTemplateSubCons) {
            controller.destroy();
        }
        mapTemplateSubCons.clear();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
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

        mapTemplatesListView.setItems(mapTemplates);

        mapsService
                .findAllMaps()
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        maps -> {
                            List<MapTemplate> ownMaps = new ArrayList<>(maps.stream().filter(mapTemplate -> idStorage.getID().equals(mapTemplate.createdBy())).toList());
                            List<MapTemplate> otherMaps = new ArrayList<>(maps.stream().filter(mapTemplate -> !(idStorage.getID().equals(mapTemplate.createdBy()))).toList());
                            maps.clear();
                            maps.addAll(ownMaps);
                            maps.addAll(otherMaps);

                            for (MapTemplate template : maps) {
                                boolean ownMap = idStorage.getID().equals(template.createdBy());
                                MapTemplateSubcontroller controller = new MapTemplateSubcontroller(template, ownMap);
                                mapTemplateSubCons.add(controller);
                                mapTemplates.add(controller.render());
                            }
                        }
                );

        return parent;
    }

    public void onBackButtonPressed() {
        final CreateGameController controller = createGameController.get();
        this.app.show(controller);
    }

    public void onCreateButtonPressed() {

    }

    public void onSelectButtonPressed() {

    }
}
