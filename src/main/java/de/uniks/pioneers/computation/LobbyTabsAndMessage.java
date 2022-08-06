package de.uniks.pioneers.computation;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.CreateGameController;
import de.uniks.pioneers.controller.LoginController;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.*;
import de.uniks.pioneers.util.JsonUtil;
import de.uniks.pioneers.util.ResourceManager;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Provider;

import java.util.HashMap;
import java.util.Objects;

import static de.uniks.pioneers.Constants.*;
import static de.uniks.pioneers.Constants.LOBBY_ID;

@SuppressWarnings("ALL")
public class LobbyTabsAndMessage {

    public LobbyTabsAndMessage(){

    }


    public void logout(UserService userService, IDStorage idStorage, AuthService authService, Provider<LoginController> loginController, App app) {
        userService.statusUpdate(idStorage.getID(), "offline")
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        result -> authService.logout()
                                .subscribeOn(FX_SCHEDULER)
                                .subscribe(
                                        onSuccess -> {
                                            System.out.println("hallos");
                                            ResourceManager.saveConfig(JsonUtil.createDefaultConfig());
                                            app.show(loginController.get());
                                        },
                                        onError -> {
                                        }
                                ),
                        error -> {
                        }
                );
    }

    public void createGame(GameStorage gameStorage, MemberService memberService, IDStorage idStorage, Provider<CreateGameController> createGameController, App app) {
        if (gameStorage.getId() != null) {
            memberService.getAllGameMembers(gameStorage.getId()).observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                        boolean trace = true;
                        for (Member member : result) {
                            if (member.userId().equals(idStorage.getID())) {
                                Alert alert = new Alert(Alert.AlertType.ERROR, "You cannot create Game while being part of another Game");
                                // Change style of error alert
                                DialogPane dialogPane = alert.getDialogPane();
                                dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                                        .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                                alert.showAndWait();
                                trace = false;
                                break;
                            }
                        }
                        if (trace) {
                            final CreateGameController controller = createGameController.get();
                            app.show(controller);
                        }
                    });
        } else {
            final CreateGameController controller = createGameController.get();
            app.show(controller);
        }
    }

    public void checkMessageField(TextField chatMessageField, DirectChatStorage currentDirectStorage, MessageService messageService) {
        if (!chatMessageField.getText().isEmpty()) {
            if (currentDirectStorage != null) {
                messageService.send(GROUPS, currentDirectStorage.getGroupId(), chatMessageField.getText())
                        .observeOn(FX_SCHEDULER)
                        .subscribe(result -> chatMessageField.setText(""));
            } else {
                messageService.send(GLOBAL, LOBBY_ID, chatMessageField.getText())
                        .observeOn(FX_SCHEDULER)
                        .subscribe();
                chatMessageField.clear();
            }
        }
    }

    public void renderSingleMessage(String groupID, Tab tab, Message message, HashMap<String, User> memberHash, IDStorage idStorage, MessageService messageService) {
        HBox box = new HBox(3);
        Label label = new Label();
        ImageView imageView = new ImageView();
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        if (memberHash.get(message.sender()).avatar() != null) {
            imageView.setImage(new Image(memberHash.get(message.sender()).avatar()));
        }
        box.getChildren().add(imageView);
        label.setMinWidth(100);
        initRightClick(label, message._id(), message.sender(), groupID, idStorage,messageService);
        label.setText(memberHash.get(message.sender()).name() + ": " + message.body());
        box.getChildren().add(label);

        ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().add(box);

        ((ScrollPane) tab.getContent()).vvalueProperty().bind(((VBox) ((ScrollPane) tab.getContent()).getContent()).heightProperty());
    }

    private void initRightClick(Label label, String messageId, String sender, String groupId, IDStorage idStorage, MessageService messageService) {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem menuItem = new MenuItem("delete");

        contextMenu.getItems().add(menuItem);
        label.setOnMouseEntered(event -> label.setStyle("-fx-background-color: LIGHTGREY"));
        label.setOnMouseExited(event -> label.setStyle("-fx-background-color: TRANSPARENT"));
        label.setContextMenu(contextMenu);

        menuItem.setOnAction(event -> {
            if (sender.equals(idStorage.getID())) {
                if (groupId != null) {
                    messageService
                            .delete(GROUPS, groupId, messageId)
                            .observeOn(FX_SCHEDULER)
                            .subscribe();
                } else {
                    messageService
                            .delete(GLOBAL, LOBBY_ID, messageId)
                            .observeOn(FX_SCHEDULER)
                            .subscribe();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Deleting other members messages is not possible.");
                // set style of warning
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                        .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                alert.showAndWait();
            }
        });
    }
}
