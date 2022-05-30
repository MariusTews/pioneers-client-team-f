package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Group;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.*;

public class LobbyController implements Controller {

	private final ObservableList<User> users = FXCollections.observableArrayList();
	private final ObservableList<Game> games = FXCollections.observableArrayList();
	private final ObservableList<Group> groups = FXCollections.observableArrayList();
	private final ObservableList<Message> messages = FXCollections.observableArrayList();

	//message for Lobby
	private final ObservableList<Message> lobby_messages = FXCollections.observableArrayList();

	private final List<String> lobby_deletedMessages = new ArrayList<>();
	private final List<String> deletedMessages = new ArrayList<>();
	private final List<UserListSubController> userSubCons = new ArrayList<>();
	private final List<GameListSubController> gameSubCons = new ArrayList<>();
	private final List<DirectChatStorage> directChatStorages = new ArrayList<>();
	//store id and user
	private final HashMap<String, User> memberHash = new HashMap<>();

	@FXML
	public ScrollPane gamesScrollPane;
	@FXML
	public ScrollPane userScrollPane;
	@FXML
	public Button rulesButton;
	@FXML
	public Label userWelcomeLabel;
	@FXML
	public Button logoutButton;
	@FXML
	public TabPane tabPane;
	@FXML
	public Tab allTab;
	@FXML
	public TextField chatMessageField;
	@FXML
	public Button sendButton;
	@FXML
	public Button editUserButton;
	@FXML
	public Button createGameButton;

	private final App app;
	private final IDStorage idStorage;

	private final UserService userService;
	private final GameService gameService;
	private final GroupService groupService;
	private final MessageService messageService;
	private final AuthService authService;
	private final MemberService memberService;
	private final EventListener eventListener;
	private final Provider<LoginController> loginController;
	private final Provider<RulesScreenController> rulesScreenController;
	private final Provider<CreateGameController> createGameController;
	private final Provider<EditUserController> editUserController;
	private final Provider<GameLobbyController> gameLobbyController;

	private final CompositeDisposable disposable = new CompositeDisposable();
	private Disposable tabDisposable;
	private DirectChatStorage currentDirectStorage;

	String ownUsername = "";
	String ownAvatar = null;

	@Inject
	public LobbyController(App app,
						   IDStorage idStorage,
						   UserService userService,
						   GameService gameService,
						   GroupService groupService,
						   MessageService messageService,
						   AuthService authService,
						   MemberService memberService,
						   EventListener eventListener,
						   Provider<LoginController> loginController,
						   Provider<RulesScreenController> rulesScreenController,
						   Provider<CreateGameController> createGameController,
						   Provider<EditUserController> editUserController,
						   Provider<GameLobbyController> gameLobbyController) {

		this.app = app;
		this.idStorage = idStorage;
		this.userService = userService;
		this.gameService = gameService;
		this.groupService = groupService;
		this.messageService = messageService;
		this.authService = authService;
		this.memberService = memberService;
		this.eventListener = eventListener;
		this.loginController = loginController;
		this.rulesScreenController = rulesScreenController;
		this.createGameController = createGameController;
		this.editUserController = editUserController;
		this.gameLobbyController = gameLobbyController;
	}

	@Override
	public void init() {
		gameService.findAllGames().observeOn(FX_SCHEDULER).subscribe(this::loadGames);
		userService.findAllUsers().observeOn(FX_SCHEDULER).subscribe(this::loadUsers);
		groupService.getAll().observeOn(FX_SCHEDULER).subscribe(this::loadGroups);

		disposable.add(eventListener.listen("users.*.*", User.class).observeOn(FX_SCHEDULER).subscribe(this::handleUserEvents));
		disposable.add(eventListener.listen("games.*.*", Game.class).observeOn(FX_SCHEDULER).subscribe(this::handleGameEvents));

		//listen to messages on lobby on Global channel
		disposable.add(eventListener
				.listen("global." + LOBBY_ID + ".messages.*.*", Message.class)
				.observeOn(FX_SCHEDULER)
				.subscribe(event -> {
					final Message message = event.data();
					if (event.event().endsWith(CREATED)) {
						renderMessage(message, true);
					} else if (event.event().endsWith(DELETED)) {
						renderMessage(message, false);
					}
				}));


	}

	//render messages for all lobby
	private void renderMessage(Message message, boolean render) {
		((VBox) ((ScrollPane) allTab.getContent()).getContent()).getChildren().clear();

		if (render) {
			this.lobby_messages.add(message);
		} else {
			this.lobby_deletedMessages.add(message._id());
		}

		if (!lobby_messages.isEmpty()) {
			for (Message m : lobby_messages) {
				HBox box = new HBox(3);
				ImageView imageView = new ImageView();
				imageView.setFitWidth(20);
				imageView.setFitHeight(20);
				if (this.memberHash.get(m.sender()).avatar() != null) {
					imageView.setImage(new Image(this.memberHash.get(m.sender()).avatar()));
				}
				box.getChildren().add(imageView);
				if (this.lobby_deletedMessages.contains(m._id())) {
					Label label = new Label(this.memberHash.get(m.sender()).name() + ": - message deleted - ");
					label.setFont(Font.font("Italic"));
					box.getChildren().add(label);
					//this.idMessageView.getChildren().add(box);
					((VBox) ((ScrollPane) allTab.getContent()).getContent()).getChildren().add(box);
				} else {
					System.out.println("3");
					Label label = new Label();
					label.setMinWidth(100);
					this.initRightClickForAllMessages(label, m._id(), m.sender());
					label.setText(memberHash.get(m.sender()).name() + ": " + m.body());
					box.getChildren().add(label);
					((VBox) ((ScrollPane) allTab.getContent()).getContent()).getChildren().add(box);

				}
			}
		}
		//this helps the messages to be scrolled down
		((ScrollPane)allTab.getContent()).vvalueProperty().bind(((VBox)((ScrollPane) allTab.getContent()).getContent()).heightProperty());

	}


	@Override
	public void destroy() {
		this.userSubCons.forEach(UserListSubController::destroy);
		this.gameSubCons.forEach(GameListSubController::destroy);
		this.userSubCons.clear();
		this.gameSubCons.clear();
		this.directChatStorages.clear();

		disposable.dispose();
	}

	@Override
	public Parent render() {
		final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/LobbyScreen.fxml"));
		loader.setControllerFactory(c -> this);
		final Parent parent;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		this.users.addListener((ListChangeListener<? super User>) c -> {
			((VBox) this.userScrollPane.getContent()).getChildren().setAll(c.getList().stream().sorted(userComparator).map(this::renderUser).toList());
		});

		this.games.addListener((ListChangeListener<? super Game>) c -> {
			((VBox) this.gamesScrollPane.getContent()).getChildren().setAll(c.getList().stream().sorted(gameComparator).map(this::renderGame).toList());
		});


		this.tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
					handleTabSwitching(oldValue, newValue));
		tabPane.tabClosingPolicyProperty().set(TabPane.TabClosingPolicy.SELECTED_TAB);
		return parent;
	}

	private void handleTabSwitching(Tab oldValue, Tab newValue) {

		for (DirectChatStorage directChatStorage : directChatStorages) {
			if (directChatStorage.getTab().equals(oldValue) && tabDisposable != null) {
				this.currentDirectStorage = null;
				tabDisposable.dispose();
			}
		}
		for (DirectChatStorage directChatStorage : directChatStorages) {
			if (directChatStorage.getTab().equals(newValue)) {
				this.currentDirectStorage = directChatStorage;
				User user = currentDirectStorage.getUser();
				this.loadDirectMessages(currentDirectStorage.getGroupId(), user, currentDirectStorage.getTab());
				tabDisposable = eventListener.listen("groups." + directChatStorage.getGroupId() + ".messages.*.*", Message.class).observeOn(FX_SCHEDULER).subscribe(messageEvent -> {
					if (messageEvent.event().endsWith(CREATED)) {
						this.messages.add(messageEvent.data());
						renderSingleMessage(directChatStorage.getUser(), directChatStorage.getGroupId(), directChatStorage.getTab(), messageEvent.data());
					} else if (messageEvent.event().endsWith(DELETED)) {
						this.deletedMessages.add(messageEvent.data()._id());
						loadDirectMessages(directChatStorage.getGroupId(), user, newValue);
					}
				});
			}
		}
	}

	public void rulesButtonPressed(ActionEvent ignoredEvent) {
		final RulesScreenController controller = rulesScreenController.get();
		app.show(controller);
	}

	public void logoutButtonPressed(ActionEvent ignoredEvent) {
		logout();
	}

	public void logout() {
		userService.statusUpdate(idStorage.getID(), "offline")
				.observeOn(FX_SCHEDULER)
				.subscribe();
		authService.logout()
				.subscribeOn(FX_SCHEDULER)
				.subscribe(onSuccess -> app.show(loginController.get()), onError -> {
				});
	}

	public void sendButtonPressed(ActionEvent ignoredEvent) {
		checkMessageField();
	}

	public void editButtonPressed(ActionEvent ignoredEvent) {
		final EditUserController controller = editUserController.get();
		app.show(controller);
	}

	public void createGameButtonPressed(ActionEvent ignoredEvent) {
		final CreateGameController controller = createGameController.get();
		app.show(controller);
	}

	public void enterKeyPressed(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			checkMessageField();
		}
	}

	private void checkMessageField() {
		if (!chatMessageField.getText().isEmpty()) {
			if (currentDirectStorage != null) {
				this.messageService.send(GROUPS, currentDirectStorage.getGroupId(), chatMessageField.getText())
						.observeOn(FX_SCHEDULER)
						.subscribe(result -> {
							this.chatMessageField.setText("");
						});
			} else{
				this.messageService.send(GLOBAL, LOBBY_ID, chatMessageField.getText())
						.observeOn(FX_SCHEDULER)
						.subscribe();
				this.chatMessageField.clear();
			}
		}
	}

	private Node renderUser(User user) {
		if (user._id().equals(this.idStorage.getID())) {
			if (user.avatar() != null) {
				this.ownAvatar = user.avatar();
			}
			this.ownUsername = user.name();
			this.userWelcomeLabel.setText(WELCOME + user.name() + "!");
		}
		for (UserListSubController subCon : this.userSubCons) {
			if (subCon.getId().equals(user._id())) {
				return subCon.getParent();
			}
		}
		UserListSubController userCon = new UserListSubController(this.app, this, user, idStorage);
		userSubCons.add(userCon);
		return userCon.render();
	}

	private Node renderGame(Game game) {
		for (GameListSubController subCon : this.gameSubCons) {
			if (subCon.getId().equals(game._id())) {
				return subCon.getParent();
			}
		}
		GameListSubController gameCon = new GameListSubController(this.app, game, this);
		gameSubCons.add(gameCon);
		return gameCon.render();
	}

	private void handleUserEvents(Event<User> userEvent) {
		final User user = userEvent.data();

		if (userEvent.event().endsWith(CREATED)) {
			this.users.add(user);
		} else if (userEvent.event().endsWith(DELETED)) {
			removeUserSubCon(user);
			this.users.removeIf(u -> u._id().equals(user._id()));
		} else if (userEvent.event().endsWith(UPDATED)) {

			for (DirectChatStorage directChatStorage : directChatStorages) {
				if (directChatStorage.getUser()._id().equals(user._id())) {

					directChatStorage.setUser(user);
				}
			}
			for (User updatedUser : this.users) {
				if (updatedUser._id().equals(user._id())) {
					removeUserSubCon(user);
					this.users.set(this.users.indexOf(updatedUser), user);
					break;
				}
			}
		}
	}

	private void removeUserSubCon(User updatedUser) {
		for (UserListSubController subCon : this.userSubCons) {
			if (subCon.getId().equals(updatedUser._id())) {
				this.userSubCons.remove(subCon);
				break;
			}
		}
	}

	private void handleGameEvents(Event<Game> gameEvent) {
		final Game game = gameEvent.data();

		if (gameEvent.event().endsWith(CREATED)) {
			this.games.add(game);
		} else if (gameEvent.event().endsWith(DELETED)) {
			removeGameSubcon(game);
			this.games.removeIf(u -> u._id().equals(game._id()));
		} else if (gameEvent.event().endsWith(UPDATED)) {
			for (Game updatedGame : this.games) {
				if (updatedGame._id().equals(game._id())) {
					removeGameSubcon(updatedGame);
					this.games.set(this.games.indexOf(updatedGame), game);
					break;
				}
			}
		}
	}

	private void removeGameSubcon(Game updatedGame) {
		for (GameListSubController subCon : this.gameSubCons) {
			if (subCon.getId().equals(updatedGame._id())) {
				this.gameSubCons.remove(subCon);
				break;
			}
		}
	}

	public void openDirectChat(User user) {

		List<Tab> tabs = this.tabPane.getTabs();

		for (Tab tab : tabs) {
			if (tab.getText().equals(DirectMessage + user.name())) {
				checkGroups(user, tab);
				return;
			}
		}

		Tab tab = new Tab();
		tab.setText(DirectMessage + user.name());
		tab.setClosable(true);
		tab.setOnClosed(event -> {
			for (DirectChatStorage storage : this.directChatStorages) {

				if (storage != null && storage.getTab().equals(tab)) {
					storage.setTab(null);
				}
			}
		});
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(new VBox(3));
		tab.setContent(scrollPane);

		checkGroups(user, tab);
		this.tabPane.getTabs().add(tab);
	}

	private void checkGroups(User user, Tab tab) {

		for (Group group : this.groups) {
			if (group.members().size() == 2 && group.members().contains(user._id()) && group.members().contains(this.idStorage.getID())) {
				for (DirectChatStorage storage : directChatStorages) {
					User stoageUser = storage.getUser();
					if (storage.getGroupId().equals(group._id()) && stoageUser._id().equals(user._id())) {
						storage.setTab(tab);
						loadDirectMessages(storage.getGroupId(), stoageUser, storage.getTab());
						return;
					}
				}
				addToDirectChatStorage(group._id(), user, tab);
				loadDirectMessages(group._id(), user, tab);
				return;
			}
		}
		String userId = this.idStorage.getID();

		List<String> toAdd = new ArrayList<>();
		toAdd.add(userId);
		toAdd.add(user._id());

		this.groupService.createGroup(toAdd).observeOn(FX_SCHEDULER).subscribe(group -> {
			addToDirectChatStorage(group._id(), user, tab);
			loadDirectMessages(group._id(), user, tab);
			this.groups.add(group);
		});
	}

	private void loadUsers(List<User> users) {

		for (User user : users) {
			memberHash.put(user._id(),user);
			if (user._id().equals(this.idStorage.getID())) {
				this.ownUsername = user.name();
			}
		}

		//get all messages from the user that are in lobby
		messageService
				.getAllMessages(GLOBAL, LOBBY_ID)
				.observeOn(FX_SCHEDULER)
				.subscribe(col -> {
					this.lobby_messages.setAll(col);
					this.initAllMessages();
				});



		List<User> online = users.stream().filter(user -> user.status().equals("online")).toList();
		List<User> offline = users.stream().filter(user -> user.status().equals("offline")).toList();
		this.users.addAll(online);
		this.users.addAll(offline);
	}

	private void loadGames(List<Game> games) {
		List<Game> accessible = games.stream().filter(game -> (!game.started() && (int) game.members() < MAX_MEMBERS)).toList();
		List<Game> notAccessible = games.stream().filter(game -> (game.started() || game.members().equals(MAX_MEMBERS))).toList();

		this.games.addAll(accessible);
		this.games.addAll(notAccessible);
	}

	//load alll messages
	private void initAllMessages() {
		for (Message m : this.lobby_messages) {
			HBox box = new HBox(3);
			Label label = new Label();
			ImageView imageView = new ImageView();
			imageView.setFitWidth(20);
			imageView.setFitHeight(20);
			if (this.memberHash.get(m.sender()).avatar() != null) {
				imageView.setImage(new Image(this.memberHash.get(m.sender()).avatar()));
			}
			box.getChildren().add(imageView);
			label.setMinWidth(100);
			this.initRightClickForAllMessages(label, m._id(), m.sender());
			label.setText(memberHash.get(m.sender()).name() + ": " + m.body());
			box.getChildren().add(label);
			//this attaches messages to alltab
			((VBox) ((ScrollPane) allTab.getContent()).getContent()).getChildren().add(box);

		}
	}

	//this delete messages on right click or gives warning
	// if the code doesnot belong to the user
	private void initRightClickForAllMessages(Label label, String messageId, String sender) {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem menuItem = new MenuItem("delete");

		contextMenu.getItems().add(menuItem);

		label.setOnMouseEntered(event -> {
			label.setStyle("-fx-background-color: LIGHTGREY");
		});
		label.setOnMouseExited(event -> {
			label.setStyle("-fx-background-color: DEFAULT");
		});
		label.setContextMenu(contextMenu);

		menuItem.setOnAction(event -> {
			if (sender.equals(this.idStorage.getID())) {
				messageService
						.delete(GLOBAL, LOBBY_ID, messageId)
						.observeOn(FX_SCHEDULER)
						.subscribe();
				((VBox) ((ScrollPane) allTab.getContent()).getContent()).getChildren().remove(label);
			} else {
				new Alert(Alert.AlertType.WARNING, "Deleting other members messages is not possible.")
						.showAndWait();
			}
		});


	}

	private void loadGroups(List<Group> groups) {
		//627xx3c93496bc00158f3859
		this.groups.addAll(groups);
	}


	private void loadDirectMessages(String groupId, User user, Tab tab) {
		this.messageService.getAllMessages(GROUPS, groupId).observeOn(FX_SCHEDULER).subscribe(messages -> {
			this.messages.clear();
			this.messages.addAll(messages);
			((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().clear();

			for (Message message : this.messages) {
				if (!this.deletedMessages.contains(message._id())) {
					renderSingleMessage(user, groupId, tab, message);
				}
			}
		});
	}

	private void addToDirectChatStorage(String groupId, User user, Tab tab) {
		DirectChatStorage directChatStorage = new DirectChatStorage();
		directChatStorage.setGroupId(groupId);
		directChatStorage.setUser(user);
		directChatStorage.setTab(tab);
		this.directChatStorages.add(directChatStorage);
	}

	private void renderSingleMessage(User user, String groupID, Tab tab, Message message) {
		HBox box = new HBox(3);
		ImageView imageView = new ImageView();
		imageView.setFitWidth(20);
		imageView.setFitHeight(20);
		Label label = new Label();
		initRightClick(label, message._id(), message.sender(), groupID);
		if (message.sender().equals(idStorage.getID())) {
			if (this.ownAvatar != null) {
				imageView.setImage(new Image(this.ownAvatar));
			}
			box.getChildren().add(imageView);

			label.setText(ownUsername + ": " + message.body());
			box.getChildren().add(label);
			((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().add(box);
		} else if (message.sender().equals(user._id())) {
			if (user.avatar() != null) {
				imageView.setImage(new Image(user.avatar()));
			}
			box.getChildren().add(imageView);

			label.setText(user.name() + ": " + message.body());
			box.getChildren().add(label);
			((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().add(box);
		}
	}

	public void joinGame(Game game) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Enter the password");
		dialog.setHeaderText("password");
		dialog.showAndWait()
				.ifPresent(password -> {
					this.memberService.join(idStorage.getID(), game._id(), password)
							.observeOn(FX_SCHEDULER)
							.doOnError(error -> {
								if ("HTTP 401 ".equals(error.getMessage())) {
									new Alert(Alert.AlertType.ERROR, "wrong password")
											.showAndWait();
								}

							})
							.subscribe(result -> app.show(gameLobbyController.get()), onError -> {
							});
				});
	}

	private void initRightClick(Label label, String messageId, String sender, String groupId) {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem menuItem = new MenuItem("delete");

		contextMenu.getItems().add(menuItem);
		label.setOnMouseEntered(event -> {
			label.setStyle("-fx-background-color: LIGHTGREY");
		});
		label.setOnMouseExited(event -> {
			label.setStyle("-fx-background-color: DEFAULT");
		});
		label.setContextMenu(contextMenu);

		menuItem.setOnAction(event -> {
			if (sender.equals(this.idStorage.getID())) {
				messageService
						.delete(GROUPS, groupId, messageId)
						.observeOn(FX_SCHEDULER)
						.subscribe();
			} else {
				new Alert(Alert.AlertType.WARNING, "Deleting other members messages is not possible.")
						.showAndWait();
			}
		});
	}
}
