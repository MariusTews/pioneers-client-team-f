package de.uniks.pioneers.service;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.GameLobbyController;
import de.uniks.pioneers.controller.GameScreenController;
import de.uniks.pioneers.controller.LoginController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.util.JsonUtil;
import de.uniks.pioneers.util.ResourceManager;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static de.uniks.pioneers.Constants.*;
import static java.util.concurrent.TimeUnit.SECONDS;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class LobbyComService {

    private final App app;
    private final GameStorage gameStorage;
    private final GameService gameService;
    private final MemberService memberService;
    private final IDStorage idStorage;
    private final AuthService authService;
    private final MessageService messageService;
    private final PioneersService pioneersService;
    private final UserService userService;
    private final RefreshTokenStorage refreshTokenStorage;

    public LobbyComService(App app, GameStorage gameStorage, GameService gameService, MemberService memberService, IDStorage idStorage, AuthService authService, MessageService messageService, PioneersService pioneersService, UserService userService, RefreshTokenStorage refreshTokenStorage) {

        this.app = app;
        this.gameStorage = gameStorage;
        this.gameService = gameService;
        this.memberService = memberService;
        this.idStorage = idStorage;
        this.authService = authService;
        this.messageService = messageService;
        this.pioneersService = pioneersService;
        this.userService = userService;
        this.refreshTokenStorage = refreshTokenStorage;
    }


    public void logout(Provider<LoginController> loginController) {
        userService.statusUpdate(idStorage.getID(), "offline")
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        result -> authService.logout()
                                .subscribeOn(FX_SCHEDULER)
                                .subscribe(
                                        onSuccess -> {
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

    public void checkMessageField(TextField chatMessageField, DirectChatStorage currentDirectStorage) {
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

    public void renderSingleMessage(String groupID, Tab tab, Message message, HashMap<String, User> memberHash) {
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
        initRightClick(label, message._id(), message.sender(), groupID);
        label.setText(memberHash.get(message.sender()).name() + ": " + message.body());
        box.getChildren().add(label);

        ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().add(box);

        ((ScrollPane) tab.getContent()).vvalueProperty().bind(((VBox) ((ScrollPane) tab.getContent()).getContent()).heightProperty());
    }

    private void initRightClick(Label label, String messageId, String sender, String groupId) {
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

    public void joinGame(Game game, Provider<GameLobbyController> gameLobbyController) {
        if (gameStorage.getId() != null) {
            memberService.getAllGameMembers(gameStorage.getId()).observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                        boolean trace = true;
                        for (Member member : result) {
                            if (member.userId().equals(idStorage.getID())) {
                                Alert alert = new Alert(Alert.AlertType.ERROR, "You can't join a game while \nbeing part of another game");
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
                            joinMessage(game, gameLobbyController);
                        }
                    });
        } else {
            joinMessage(game, gameLobbyController);
        }
    }

    private void joinMessage(Game game, Provider<GameLobbyController> gameLobbyController) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter the password");
        dialog.setHeaderText("password");
        // Change style of password input dialog
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
        dialog.showAndWait()
                .ifPresent(password -> memberService.join(game._id(), password)
                        .observeOn(FX_SCHEDULER)
                        .doOnError(error -> {
                            if ("HTTP 403 ".equals(error.getMessage())) {
                                Alert alert = new Alert(Alert.AlertType.ERROR, "wrong password");
                                // Change style of error alert
                                DialogPane errorPane = alert.getDialogPane();
                                errorPane.getStylesheets().add(Objects.requireNonNull(Main.class.
                                        getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                                alert.showAndWait();
                            }
                        })
                        .subscribe(result -> app.show(gameLobbyController.get()), onError -> {
                        }));
    }

    public void onJoin(ObservableList<Member> members, Provider<GameScreenController> gameScreenController, UserStorage userStorage) {
        //set game options (if app was closed before, they are now longer saved)
        Game game = gameService.findOneGame(gameStorage.getId()).blockingFirst();
        gameStorage.setGameOptions(game.settings());

        List<User> allUsers = userService.findAllUsers().blockingFirst();
        List<User> usersFromGame = new ArrayList<>();
        for (User user : allUsers) {
            for (Member member : members) {
                if (user._id().equals(member.userId())) {
                    usersFromGame.add(user);
                }
            }
        }
        userStorage.setUserList(usersFromGame);

        boolean changeToPlayer = false;
        for (Member m : members) {
            if (m.gameId().equals(gameStorage.getId()) && m.userId().equals(idStorage.getID())
                    && m.spectator()) {
                GameScreenController controller = gameScreenController.get();
                controller.setRejoin(true);
                app.show(controller);
                changeToPlayer = true;
                break;
            }
        }
        if (!changeToPlayer) {
            pioneersService.updatePlayer(gameStorage.getId(), idStorage.getID(), true)
                    .observeOn(FX_SCHEDULER)
                    .subscribe();
            GameScreenController controller = gameScreenController.get();
            controller.setRejoin(true);
            app.show(controller);
        }
    }

    public void loadMessages(Tab allTab, Tab tab, String groupId, ObservableList<Message> lobby_messages,
                             List<String> deletedAllMessages, List<String> deletedMessages, ObservableList<Message> messages,
                             HashMap<String, User> memberHash) {
        if (tab.equals(allTab)) {
            messageService.getAllMessages(GLOBAL, LOBBY_ID).observeOn(FX_SCHEDULER).subscribe(m -> {
                lobby_messages.clear();
                lobby_messages.addAll(m);
                ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().clear();

                for (Message message : lobby_messages) {
                    if (!deletedAllMessages.contains(message._id())) {
                        renderSingleMessage(null, allTab, message, memberHash);
                    }
                }
            });
        } else {
            messageService.getAllMessages(GROUPS, groupId).observeOn(FX_SCHEDULER).subscribe(m -> {
                messages.clear();
                messages.addAll(m);
                ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().clear();

                for (Message message : messages) {
                    if (!deletedMessages.contains(message._id())) {
                        renderSingleMessage(groupId, tab, message, memberHash);
                    }
                }
            });
        }
    }

    public void rejoinButton(Button rejoinButton) {
        //make the rejoin button visible
        //based upon if a user is in game or not
        if (gameStorage.getId() != null) {
            memberService.getAllGameMembers(gameStorage.getId()).observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                                boolean trace = true;
                                for (Member member : result) {
                                    if (member.userId().equals(idStorage.getID())) {
                                        rejoinButton.disableProperty().set(false);
                                        trace = false;
                                        break;
                                    }
                                }
                                if (trace) {
                                    rejoinButton.disableProperty().set(true);
                                    gameStorage.setId(null);
                                    ResourceManager.saveConfig(JsonUtil.removeGameIdFromConfig());
                                }
                            }, error -> {
                            }
                    );
        } else {
            rejoinButton.disableProperty().set(true);
            gameStorage.setId(null);
            ResourceManager.saveConfig(JsonUtil.removeGameIdFromConfig());
        }
    }

    public void beepForAnHour(ScheduledExecutorService scheduler) {
        String refreshToken = refreshTokenStorage.getRefreshToken();
        final Runnable beeper = () -> authService.refreshToken(refreshToken).
                observeOn(FX_SCHEDULER).subscribe();

        final ScheduledFuture<?> beeperHandle =
                scheduler.scheduleAtFixedRate(beeper, 10, 30 * 60, SECONDS);
        scheduler.schedule(() -> beeperHandle.cancel(true), 60 * 60, SECONDS);
    }

    public DirectChatStorage addToDirectChatStorage(String groupId, User user, Tab tab) {
        DirectChatStorage directChatStorage = new DirectChatStorage();
        directChatStorage.setGroupId(groupId);
        directChatStorage.setUser(user);
        directChatStorage.setTab(tab);
        return directChatStorage;
    }
}
