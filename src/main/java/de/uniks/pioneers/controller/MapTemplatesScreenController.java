package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Template.MapTemplate;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.MapsService;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.*;

public class MapTemplatesScreenController implements Controller {
    private final App app;
    private final Provider<CreateGameController> createGameController;
    private final MapsService mapsService;
    private final UserService userService;
    private final IDStorage idStorage;
    private final EventListener eventListener;
    @FXML
    public ImageView nameArrow;
    @FXML
    public ImageView createdByArrow;
    @FXML
    public ImageView votesArrow;
    @FXML
    public Label selectedLabel;
    @FXML
    public Button backButton;
    @FXML
    public Button createButton;
    @FXML
    public Button selectButton;
    @FXML
    public ListView<Parent> mapTemplatesListView;
    private final ObservableList<Parent> mapTemplates = FXCollections.observableArrayList();
    private final HashMap<String, MapTemplateSubController> mapTemplateSubCons = new HashMap<>();
    private final HashMap<String, String> userNames = new HashMap<>();
    private final HashMap<String, Boolean> reverseSortFlags = new HashMap<>();
    private MapTemplateSubController selectedMapTemplateSubController;
    private CompositeDisposable disposable;

    @Inject
    public MapTemplatesScreenController(App app, Provider<CreateGameController> createGameController,
                                        MapsService mapsService, UserService userService, IDStorage idStorage,
                                        EventListener eventListener) {
        this.app = app;
        this.createGameController = createGameController;
        this.mapsService = mapsService;
        this.userService = userService;
        this.idStorage = idStorage;
        this.eventListener = eventListener;
    }

    @Override
    public void init() {
        disposable = new CompositeDisposable();
        disposable.add(eventListener.listen("maps.*.*", MapTemplate.class).observeOn(FX_SCHEDULER).subscribe(this::handleMapTemplateEvents));
        reverseSortFlags.put("name", false);
        reverseSortFlags.put("createdBy", false);
        reverseSortFlags.put("votes", false);
    }

    @Override
    public void destroy() {
        for (MapTemplateSubController controller : mapTemplateSubCons.values()) {
            controller.destroy();
        }
        mapTemplateSubCons.clear();
        if (disposable != null) {
            disposable.clear();
        }
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

        List<User> users = userService.findAllUsers().blockingFirst();

        for (User user : users) {
            userNames.put(user._id(), user.name());
        }

        mapsService
                .findAllMaps()
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        maps -> {
                            List<MapTemplate> ownMaps = new ArrayList<>(maps.stream().filter(mapTemplate -> idStorage.getID().equals(mapTemplate.createdBy())).toList());
                            List<MapTemplate> otherMaps = new ArrayList<>(maps.stream().filter(mapTemplate -> !(idStorage.getID().equals(mapTemplate.createdBy()))).toList());

                            for (MapTemplate template : ownMaps) {
                                addMapTemplateItem(template, -1);
                            }
                            addEmptyLine();
                            for (MapTemplate template : otherMaps) {
                                addMapTemplateItem(template, -1);
                            }
                        }
                );

        return parent;
    }

    private void handleMapTemplateEvents(Event<MapTemplate> event) {
        MapTemplate template = event.data();
        if (event.event().endsWith(CREATED)) {
            if (idStorage.getID().equals(template.createdBy())) {
                int position = 0;
                for (Parent mapTemplate : mapTemplates) {
                    //the id "" is only used for the empty line between own and other players' maps
                    if (mapTemplate.getId().equals("")) {
                        break;
                    }
                    position++;
                }
                addMapTemplateItem(template, position);
            } else {
                addMapTemplateItem(template, -1);
            }
        } else if (event.event().endsWith(UPDATED)) {
            MapTemplateSubController controller = mapTemplateSubCons.get(template._id());
            controller.setTemplate(template);
            controller.updateContent();
        } else if (event.event().endsWith(DELETED)) {
            MapTemplateSubController controller = mapTemplateSubCons.get(template._id());
            controller.destroy();
            mapTemplateSubCons.remove(template._id());
            for (Parent mapTemplateItem : mapTemplates) {
                if (mapTemplateItem.getId().equals(template._id())) {
                    mapTemplates.remove(mapTemplateItem);
                    break;
                }
            }
        }
    }

    private void addMapTemplateItem(MapTemplate template, int position) {
        boolean ownMap = idStorage.getID().equals(template.createdBy());
        String userName = userNames.getOrDefault(template.createdBy(), "");
        MapTemplateSubController controller = new MapTemplateSubController(template, ownMap, userName);
        mapTemplateSubCons.put(template._id(), controller);
        controller.init();
        Parent item = controller.render();
        item.setOnMouseClicked(this::selectMapTemplateItem);
        //if position is -1 then the item should be added at the end of the list
        if (position == -1) {
            mapTemplates.add(item);
        } else {
            mapTemplates.add(position, item);
        }
    }

    private void addEmptyLine() {
        HBox emptyLine = new HBox();
        emptyLine.setPrefWidth(720);
        emptyLine.setPrefHeight(25);
        emptyLine.setId("");
        mapTemplates.add(emptyLine);
    }

    public void onBackButtonPressed() {
        final CreateGameController controller = createGameController.get();
        this.app.show(controller);
    }

    public void onCreateButtonPressed() {
        //TODO
        sortByName();
    }

    public void onSelectButtonPressed() {
        final CreateGameController controller = createGameController.get();
        MapTemplate selectedTemplate = selectedMapTemplateSubController.getTemplate();
        controller.setMapTemplate(selectedTemplate.name(), selectedTemplate._id());
        this.app.show(controller);
    }

    private void selectMapTemplateItem(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                if (selectedMapTemplateSubController != null) {
                    selectedMapTemplateSubController.unselectItem();
                }
                String mapTemplateId = ((Node) mouseEvent.getSource()).getId();
                selectedMapTemplateSubController = mapTemplateSubCons.get(mapTemplateId);
                selectedMapTemplateSubController.selectItem();
                selectedLabel.setText("Selected: " + selectedMapTemplateSubController.getTemplate().name());
            }
        }
    }

    private void sortByName() {
        HashMap<String, List<String>> namesToIdsOwnMaps = new HashMap<>();
        HashMap<String, List<String>> namesToIdsOtherMaps = new HashMap<>();
        for (String id : mapTemplateSubCons.keySet()) {
            MapTemplateSubController controller = mapTemplateSubCons.get(id);
            String name = controller.getName();
            if (controller.isOwnMap()) {
                if (namesToIdsOwnMaps.containsKey(name)) {
                    namesToIdsOwnMaps.get(name).add(id);
                } else {
                    List<String> idList = new ArrayList<>();
                    idList.add(id);
                    namesToIdsOwnMaps.put(name, idList);
                }
            }
            else {
                if (namesToIdsOtherMaps.containsKey(name)) {
                    namesToIdsOtherMaps.get(name).add(id);
                } else {
                    List<String> idList = new ArrayList<>();
                    idList.add(id);
                    namesToIdsOtherMaps.put(name, idList);
                }
            }
        }

        List<String> sortedOwnNames = new ArrayList<>(namesToIdsOwnMaps.keySet().stream().sorted().toList());
        if (reverseSortFlags.get("name")) {
            Collections.reverse(sortedOwnNames);
        }

        List<String> sortedOtherNames = new ArrayList<>(namesToIdsOtherMaps.keySet().stream().sorted().toList());
        if (reverseSortFlags.get("name")) {
            Collections.reverse(sortedOtherNames);
        }

        ObservableList<Parent> tempOwnMaps = FXCollections.observableArrayList();
        for (String name : sortedOwnNames) {
            for (String id : namesToIdsOwnMaps.get(name)) {
                tempOwnMaps.add(mapTemplateSubCons.get(id).getParent());
            }
        }

        ObservableList<Parent> tempOtherMaps = FXCollections.observableArrayList();
        for (String name : sortedOtherNames) {
            for (String id : namesToIdsOtherMaps.get(name)) {
                tempOtherMaps.add(mapTemplateSubCons.get(id).getParent());
            }
        }

        mapTemplates.clear();
        mapTemplates.addAll(tempOwnMaps);
        addEmptyLine();
        mapTemplates.addAll(tempOtherMaps);
        mapTemplatesListView.refresh();
        reverseSortFlags.put("name", !reverseSortFlags.get("name"));
    }

    public HashMap<String, MapTemplateSubController> getMapTemplateSubCons() {
        return mapTemplateSubCons;
    }

    public ObservableList<Parent> getMapTemplates() {
        return mapTemplates;
    }
}
