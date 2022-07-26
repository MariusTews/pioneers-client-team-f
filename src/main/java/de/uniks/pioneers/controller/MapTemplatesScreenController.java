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
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
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
    private final HashMap<String, MapTemplateSubcontroller> mapTemplateSubCons = new HashMap<>();
    private final HashMap<String, String> userNames = new HashMap<>();
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
    }

    @Override
    public void destroy() {
        for (MapTemplateSubcontroller controller : mapTemplateSubCons.values()) {
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
                            maps.clear();
                            maps.addAll(ownMaps);
                            //add empty template to create distance between own and other maps
                            maps.add(new MapTemplate(null, null, "", null, null, null, null, null, null));
                            maps.addAll(otherMaps);

                            for (MapTemplate template : maps) {
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
                    //the id "" is only used for the empty line hbox between own and other players' maps
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
            MapTemplateSubcontroller controller = mapTemplateSubCons.get(template._id());
            controller.setTemplate(template);
            controller.updateContent();
        } else if (event.event().endsWith(DELETED)) {
            MapTemplateSubcontroller controller = mapTemplateSubCons.get(template._id());
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
        MapTemplateSubcontroller controller = new MapTemplateSubcontroller(template, ownMap, userName);
        mapTemplateSubCons.put(template._id(), controller);
        //if position is -1 then the item should be added at the end of the list
        if (position == -1) {
            mapTemplates.add(controller.render());
        } else {
            mapTemplates.add(position, controller.render());
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
        //TODO
    }

    public HashMap<String, MapTemplateSubcontroller> getMapTemplateSubCons() {
        return mapTemplateSubCons;
    }

    public ObservableList<Parent> getMapTemplates() {
        return mapTemplates;
    }
}
