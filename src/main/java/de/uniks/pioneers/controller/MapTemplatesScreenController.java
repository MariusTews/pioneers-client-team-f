package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.template.MapTemplate;
import de.uniks.pioneers.websocket.EventListener;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

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
    private final HashMap<String, Boolean> sortOrderFlags = new HashMap<>();
    private Polygon currentSortArrow;
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
        // false means sort in natural order, true means sort in reverse order
        sortOrderFlags.put("name", false);
        sortOrderFlags.put("createdBy", false);
        sortOrderFlags.put("votes", false);
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

        selectButton.setDisable(true);

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
            updateCurrentSort();
        } else if (event.event().endsWith(UPDATED)) {
            MapTemplateSubController controller = mapTemplateSubCons.get(template._id());
            controller.setTemplate(template);
            controller.updateContent();
            updateCurrentSort();
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
                if (selectButton.isDisable()) {
                    selectButton.setDisable(false);
                }
            }
        }
    }

    public void onBackButtonPressed() {
        final CreateGameController controller = createGameController.get();
        this.app.show(controller);
    }

    public void onCreateButtonPressed() {
        //TODO
    }

    public void onSelectButtonPressed() {
        final CreateGameController controller = createGameController.get();
        MapTemplate selectedTemplate = selectedMapTemplateSubController.getTemplate();
        controller.setMapTemplate(selectedTemplate.name(), selectedTemplate._id());
        this.app.show(controller);
    }

    public void onSortArrowPressed(MouseEvent mouseEvent) {
        Polygon source = ((Polygon) mouseEvent.getSource());
        sort(source.getId().replace("Arrow", ""));
        source.setRotate((source.getRotate() + 180) % 360);
        if (!(source.equals(currentSortArrow))) {
            if (currentSortArrow != null) {
                currentSortArrow.setFill(Color.web("#d5dfe8"));
            }
            source.setFill(Color.web("#ffffff"));
            currentSortArrow = source;
        }
    }

    private void sort(String sortBy) {
        List<String> ownMapTemplateIds = new ArrayList<>(mapTemplateSubCons.keySet().stream().filter(id -> mapTemplateSubCons.get(id).isOwnMap()).toList());
        List<String> otherMapTemplateIds = new ArrayList<>(mapTemplateSubCons.keySet().stream().filter(id -> !(mapTemplateSubCons.get(id).isOwnMap())).toList());
        ObservableList<Parent> ownMapTemplates = sortMapTemplates(ownMapTemplateIds, sortBy);
        ObservableList<Parent> otherMapTemplates = sortMapTemplates(otherMapTemplateIds, sortBy);
        mapTemplates.clear();
        mapTemplates.addAll(ownMapTemplates);
        addEmptyLine();
        mapTemplates.addAll(otherMapTemplates);
        mapTemplatesListView.refresh();
        sortOrderFlags.put(sortBy, !sortOrderFlags.get(sortBy));
    }

    private ObservableList<Parent> sortMapTemplates(List<String> mapTemplateIds, String sortBy) {
        HashMap<String, List<String>> valuesToIds = new HashMap<>();
        for (String id : mapTemplateIds) {
            MapTemplateSubController controller = mapTemplateSubCons.get(id);
            String value = controller.getSortValue(sortBy);
            if (valuesToIds.containsKey(value)) {
                valuesToIds.get(value).add(id);
            } else {
                List<String> idList = new ArrayList<>();
                idList.add(id);
                valuesToIds.put(value, idList);
            }
        }

        List<String> sortedValues;

        if (sortBy.equals("votes")) {
            // first create an integer stream, then sort it, then convert it back to string stream and to list
            sortedValues = new ArrayList<>(valuesToIds.keySet().stream().map(Integer::parseInt).sorted().map(String::valueOf).toList());
        } else {
            sortedValues = new ArrayList<>(valuesToIds.keySet().stream().sorted().toList());
        }
        if (sortOrderFlags.get(sortBy)) {
            Collections.reverse(sortedValues);
        }

        ObservableList<Parent> _mapTemplates = FXCollections.observableArrayList();
        for (String value : sortedValues) {
            for (String id : valuesToIds.get(value)) {
                _mapTemplates.add(mapTemplateSubCons.get(id).getParent());
            }
        }

        return _mapTemplates;
    }

    private void updateCurrentSort() {
        if (currentSortArrow != null) {
            String currentSortBy = currentSortArrow.getId().replace("Arrow", "");
            /* invert sort order here, otherwise it will be inverted when calling sort, which is not wanted */
            sortOrderFlags.put(currentSortBy, !sortOrderFlags.get(currentSortBy));
            sort(currentSortBy);
        }
    }

    public HashMap<String, MapTemplateSubController> getMapTemplateSubCons() {
        return mapTemplateSubCons;
    }

    public ObservableList<Parent> getMapTemplates() {
        return mapTemplates;
    }

}
