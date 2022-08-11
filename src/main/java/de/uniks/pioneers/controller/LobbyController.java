package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.computation.LobbyTabsAndMessage;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.*;
import de.uniks.pioneers.util.ResourceManager;
import de.uniks.pioneers.websocket.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import kong.unirest.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static de.uniks.pioneers.Constants.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class LobbyController implements Controller {

	private final ObservableList<User> users = FXCollections.observableArrayList();

	private final ObservableList<User> friendsUserList = FXCollections.observableArrayList();

	private final ObservableList<Member> members = FXCollections.observableArrayList();
	private final ObservableList<Game> games = FXCollections.observableArrayList();
	private final ObservableList<Group> groups = FXCollections.observableArrayList();
	private final ObservableList<Message> messages = FXCollections.observableArrayList();

	//message for Lobby
	private final ObservableList<Message> lobby_messages = FXCollections.observableArrayList();

	private final List<String> deletedMessages = new ArrayList<>();
	private final List<String> deletedAllMessages = new ArrayList<>();
	private final HashMap<String, UserListSubController> userSubCons = new HashMap<>();
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
	@FXML
	public Button rejoinButton;

	private final App app;
	private final IDStorage idStorage;

	private final GameStorage gameStorage;

	private final UserService userService;
	private final GameService gameService;
	private final GroupService groupService;
	private final MessageService messageService;
	private final MemberService memberService;
	private final EventListener eventListener;
	private final AlertService alertService;
	private final UserStorage userStorage;
	private final Provider<LoginController> loginController;
	private final Provider<RulesScreenController> rulesScreenController;
	private final Provider<CreateGameController> createGameController;

	private final Provider<GameLobbyController> gameLobbyController;
	private final Provider<EditUserController> editUserController;
	private final Provider<GameScreenController> gameScreenController;
	public Button achievementsButton;

	private final Provider<AchievementsScreenController> achievementsScreenController;

	private final CompositeDisposable disposable = new CompositeDisposable();
	private Disposable tabDisposable;
	private DirectChatStorage currentDirectStorage;

	private final LobbyTabsAndMessage lb;

	private final ScheduledExecutorService scheduler =
			Executors.newScheduledThreadPool(1);

	private final Thread renderAvatarsThread = new Thread(() -> {
		List<UserListSubController> userSubConsCopy = new ArrayList<>(userSubCons.values());
		for (UserListSubController controller : userSubConsCopy) {
			if (Thread.currentThread().isInterrupted()) {
				break;
			}
			controller.setAvatar();
		}
	});

	@Inject
	public LobbyController(App app,
						   IDStorage idStorage,
						   GameStorage gameStorage, RefreshTokenStorage refreshTokenStorage, UserService userService,
						   GameService gameService,
						   GroupService groupService,
						   MessageService messageService,
						   AuthService authService,
						   MemberService memberService,
						   EventListener eventListener,
						   AlertService alertService,
						   UserStorage userStorage,
						   Provider<LoginController> loginController,
						   Provider<RulesScreenController> rulesScreenController,
						   Provider<CreateGameController> createGameController,
						   Provider<GameLobbyController> gameLobbyController,
						   Provider<EditUserController> editUserController,
						   Provider<GameScreenController> gameScreenController,
						   Provider<AchievementsScreenController> achievementsScreenController,
						   PioneersService pioneersService) {

		this.app = app;
		this.idStorage = idStorage;
		this.gameStorage = gameStorage;
		this.userService = userService;
		this.gameService = gameService;
		this.groupService = groupService;
		this.messageService = messageService;
		this.memberService = memberService;
		this.eventListener = eventListener;
		this.alertService = alertService;
		this.userStorage = userStorage;
		this.loginController = loginController;
		this.rulesScreenController = rulesScreenController;
		this.createGameController = createGameController;
		this.gameLobbyController = gameLobbyController;
		this.editUserController = editUserController;
		this.gameScreenController = gameScreenController;
		this.achievementsScreenController = achievementsScreenController;

		this.lb = new LobbyTabsAndMessage(this.app, this.gameStorage, this.gameService, this.memberService, this.idStorage, authService, this.messageService, pioneersService, this.userService, refreshTokenStorage);
	}

	@Override
	public void init() {
		//refreshed Token and runs for an hour
		if (this.gameStorage.getId() == null) {
			//call this method every 30 minutes to refresh refreshToken and ActiveToken
			lb.beepForAnHour(scheduler);
		}

		//set game id to enable rejoin if it was saved in the config file before
		JSONObject loadConfig = ResourceManager.loadConfig();
		if (loadConfig.has(JSON_GAME_ID)) {
			gameStorage.setId((String) loadConfig.get(JSON_GAME_ID));
		}

		if (this.gameStorage.getId() != null) {
			memberService.getAllGameMembers(this.gameStorage.getId())
					.observeOn(FX_SCHEDULER).subscribe(this.members::setAll);
		}

		gameService.findAllGames().observeOn(FX_SCHEDULER).subscribe(this::loadGames);
		userService.findAllUsers().observeOn(FX_SCHEDULER).subscribe(this::loadUsers);
		groupService.getAll().observeOn(FX_SCHEDULER).subscribe(this::loadGroups);

		disposable.add(eventListener.listen("users.*.*", User.class).observeOn(FX_SCHEDULER).subscribe(this::handleUserEvents));
		disposable.add(eventListener.listen("games.*.*", Game.class).observeOn(FX_SCHEDULER).subscribe(this::handleGameEvents));
		disposable.add(eventListener.listen("group.*.*", Group.class).observeOn(FX_SCHEDULER).subscribe(this::handleGroupEvents));

		//listen to messages on lobby on Global channel
		disposable.add(eventListener
				.listen("global." + LOBBY_ID + ".messages.*.*", Message.class)
				.observeOn(FX_SCHEDULER)
				.subscribe(this::handleAllTabMessages));

		//refreshed Token and runs for an hour
		if (this.gameStorage.getId() == null) {
			//call this method every 30 minutes to refresh refreshToken and ActiveToken
			lb.beepForAnHour(scheduler);
		}

		this.app.getStage().setOnCloseRequest(e -> {
			actionOnclose();
			e.consume();
		});
	}

	@Override
	public void destroy() {
		this.userSubCons.values().forEach(UserListSubController::destroy);
		this.gameSubCons.forEach(GameListSubController::destroy);
		this.userSubCons.clear();
		this.gameSubCons.clear();
		this.directChatStorages.clear();
		renderAvatarsThread.interrupt();
		disposable.clear();
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
		// create instance of LobbyTabs andMessage
		lb.rejoinButton(rejoinButton);

		this.users.addListener((ListChangeListener<? super User>) this::onUsersChanged);
		this.friendsUserList.addListener((ListChangeListener<? super User>) this::onUsersChanged);


		this.games.addListener((ListChangeListener<? super Game>) c -> ((VBox) this.gamesScrollPane.getContent())
				.getChildren().setAll(c.getList().stream().sorted(gameComparator).map(this::renderGame).toList()));

		this.tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
				handleTabSwitching(oldValue, newValue));
		tabPane.tabClosingPolicyProperty().set(TabPane.TabClosingPolicy.SELECTED_TAB);

		return parent;
	}

	//takes action when the application is forcefully closed such as logging out
	private void actionOnclose() {
		userService.statusUpdate(idStorage.getID(), "offline")
				.observeOn(FX_SCHEDULER)
				.subscribe(er -> System.exit(0));
	}

	private void handleTabSwitching(Tab oldValue, Tab newValue) {

		for (DirectChatStorage directChatStorage : directChatStorages) {
			if (directChatStorage.getTab() != null && directChatStorage.getTab().equals(oldValue) && tabDisposable != null) {
				this.currentDirectStorage = null;
				tabDisposable.dispose();
			}
		}
		for (DirectChatStorage directChatStorage : directChatStorages) {
			if (directChatStorage.getTab() != null && directChatStorage.getTab().equals(newValue)) {
				this.currentDirectStorage = directChatStorage;
				this.loadMessages(currentDirectStorage.getGroupId(), currentDirectStorage.getTab());
				tabDisposable = eventListener.listen("groups." + directChatStorage.getGroupId() + ".messages.*.*", Message.class).observeOn(FX_SCHEDULER).subscribe(messageEvent -> {
					if (messageEvent.event().endsWith(CREATED)) {
						this.messages.add(messageEvent.data());
						renderSingleMessage(directChatStorage.getGroupId(), directChatStorage.getTab(), messageEvent.data());
					} else if (messageEvent.event().endsWith(DELETED)) {
						this.deletedMessages.add(messageEvent.data()._id());
						loadMessages(directChatStorage.getGroupId(), newValue);
					}
				});
			}
		}
	}

	public void rulesButtonPressed() {
		final RulesScreenController controller = rulesScreenController.get();
		app.show(controller);
	}

	public void logoutButtonPressed() {
		logout();
	}

	public void logout() {
		lb.logout(loginController);

	}

	public void sendButtonPressed() {
		checkMessageField();
	}

	public void editButtonPressed() {
		final EditUserController controller = editUserController.get();
		app.show(controller);
	}

	public void createGameButtonPressed() {
		//makes sure if user in game or not , and depending on that allows user to create the game
		lb.createGame(createGameController);

	}

	public void enterKeyPressed(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			checkMessageField();
		}
	}

	private void checkMessageField() {
		lb.checkMessageField(chatMessageField, currentDirectStorage);
	}

	private Node renderUser(User user) {
		if (user._id().equals(this.idStorage.getID())) {
			this.userWelcomeLabel.setText(WELCOME + user.name() + "!");
		}
		if (this.userSubCons.containsKey(user._id())) {
			return userSubCons.get(user._id()).getParent();
		}
		UserListSubController userCon = new UserListSubController(this, user, idStorage);
		userSubCons.put(user._id(), userCon);
		return userCon.render();
	}

	private Node renderGame(Game game) {
		for (GameListSubController subCon : this.gameSubCons) {
			if (subCon.getId().equals(game._id())) {
				return subCon.getParent();
			}
		}
		GameListSubController gameCon = new GameListSubController(game, this);
		gameSubCons.add(gameCon);
		return gameCon.render();
	}

	private void handleUserEvents(Event<User> userEvent) {
		final User user = userEvent.data();

		if (userEvent.event().endsWith(CREATED)) {
			this.users.add(user);
			userSubCons.get(user._id()).setAvatar();
		} else if (userEvent.event().endsWith(DELETED)) {
			removeUserSubCon(user);
			this.users.removeIf(u -> u._id().equals(user._id()));
			this.friendsUserList.removeIf(u -> u._id().equals(user._id()));
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

			if (userSubCons.containsKey(user._id())) {
				userSubCons.get(user._id()).setAvatar();
			}

			//check for new/removed friends
			if (user._id().equals(idStorage.getID())) {
				//new friend
				if (friendsUserList.size() < user.friends().size()) {
					for (Iterator<User> iterator = users.iterator(); iterator.hasNext(); ) {
						User newFriend = iterator.next();
						if (!friendsUserList.contains(newFriend) && user.friends().contains(newFriend._id())) {
							iterator.remove();
							friendsUserList.add(newFriend);
						}
					}
				}
				//removed friend
				else if (friendsUserList.size() > user.friends().size()) {
					for (Iterator<User> iterator = friendsUserList.iterator(); iterator.hasNext(); ) {
						User removedFriend = iterator.next();
						if (!user.friends().contains(removedFriend._id())) {
							iterator.remove();
							users.add(removedFriend);
						}
					}
				}
			}
		}
	}

	private void removeUserSubCon(User updatedUser) {
		this.userSubCons.remove(updatedUser._id());
	}

	private void handleGameEvents(Event<Game> gameEvent) {
		final Game game = gameEvent.data();

		if (gameEvent.event().endsWith(CREATED)) {
			this.games.add(game);
		} else if (gameEvent.event().endsWith(DELETED)) {
			removeGameSubCon(game);
			this.games.removeIf(u -> u._id().equals(game._id()));
		} else if (gameEvent.event().endsWith(UPDATED)) {
			for (Game updatedGame : this.games) {
				if (updatedGame._id().equals(game._id())) {
					removeGameSubCon(updatedGame);
					this.games.set(this.games.indexOf(updatedGame), game);
					break;
				}
			}
		}
	}

	private void removeGameSubCon(Game updatedGame) {
		for (GameListSubController subCon : this.gameSubCons) {
			if (subCon.getId().equals(updatedGame._id())) {
				this.gameSubCons.remove(subCon);
				break;
			}
		}
	}

	// Handle group events, so the users do not end up in different groups when opening the direct chat
	private void handleGroupEvents(Event<Group> groupEvent) {
		final Group group = groupEvent.data();

		if (groupEvent.event().endsWith(CREATED)) {
			this.groups.add(group);
		} else if (groupEvent.event().endsWith(DELETED)) {
			this.groups.removeIf(u -> u._id().equals(group._id()));
		} else if (groupEvent.event().endsWith(UPDATED)) {
			for (Group updatedGroup : this.groups) {
				if (updatedGroup._id().equals(group._id())) {
					this.groups.set(this.groups.indexOf(updatedGroup), group);
					break;
				}
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

				if (storage != null && storage.getTab() != null && storage.getTab().equals(tab)) {
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
					User storageUser = storage.getUser();
					if (storage.getGroupId().equals(group._id()) && storageUser._id().equals(user._id())) {
						storage.setTab(tab);
						loadMessages(storage.getGroupId(), storage.getTab());
						return;
					}
				}
				addToDirectChatStorage(group._id(), user, tab);
				loadMessages(group._id(), tab);
				return;
			}
		}
		String userId = this.idStorage.getID();

		List<String> toAdd = new ArrayList<>();
		toAdd.add(userId);
		toAdd.add(user._id());

		this.groupService.createGroup(toAdd).observeOn(FX_SCHEDULER).subscribe(group -> {
			addToDirectChatStorage(group._id(), user, tab);
			loadMessages(group._id(), tab);
			this.groups.add(group);
		});
	}

	private void loadUsers(List<User> users) {
		List<String> friends = new ArrayList<>();
		for (User user : users) {
			memberHash.put(user._id(), user);
			if (user._id().equals(idStorage.getID())) {
				friends = user.friends();
			}
		}
		this.friendsUserList.addListener((ListChangeListener<? super User>) this::onUsersChanged);

		List<User> onlineFriends = new ArrayList<>();
		List<User> offlineFriends = new ArrayList<>();
		List<User> onlineUser = new ArrayList<>();
		List<User> offlineUser = new ArrayList<>();
		for (User user : users) {
			if (friends != null && friends.contains(user._id())) {
				if (user.status().equals("online")) {
					onlineFriends.add(user);
				} else {
					offlineFriends.add(user);
				}
			} else {
				if (user.status().equals("online")) {
					onlineUser.add(user);
				} else {
					offlineUser.add(user);
				}
			}
		}

		this.friendsUserList.addAll(onlineFriends);
		this.friendsUserList.addAll(offlineFriends);
		this.users.addAll(onlineUser);
		this.users.addAll(offlineUser);


		renderAvatarsThread.start();

		//get all messages from the user that are in lobby
		messageService
				.getAllMessages(GLOBAL, LOBBY_ID)
				.observeOn(FX_SCHEDULER)
				.subscribe(col -> {
					this.lobby_messages.setAll(col);
					for (Message message : lobby_messages) {
						if (!this.deletedAllMessages.contains(message._id())) {
							renderSingleMessage(null, allTab, message);
						}
					}
				});
	}

	private void loadGames(List<Game> games) {
		List<Game> accessible = games.stream().filter(game -> !(game.started())).toList();
		List<Game> notAccessible = games.stream().filter(Game::started).toList();

		this.games.addAll(accessible);
		this.games.addAll(notAccessible);
	}

	private void loadGroups(List<Group> groups) {
		this.groups.addAll(groups);
	}


	private void loadMessages(String groupId, Tab tab) {
		lb.loadMessages(allTab, tab, groupId, lobby_messages,
				deletedAllMessages, deletedMessages, messages, memberHash);
	}

	private void addToDirectChatStorage(String groupId, User user, Tab tab) {
		this.directChatStorages.add(lb.addToDirectChatStorage(groupId, user, tab));
	}

	private void handleAllTabMessages(Event<Message> event) {
		final Message message = event.data();
		if (event.event().endsWith(CREATED)) {
			this.lobby_messages.add(message);
			renderSingleMessage(null, allTab, message);
		} else if (event.event().endsWith(DELETED)) {
			this.deletedAllMessages.add(message._id());
			this.lobby_messages.removeIf(m -> m._id().equals(message._id()));
			this.loadMessages(null, allTab);
		}
	}

	private void renderSingleMessage(String groupID, Tab tab, Message message) {
		lb.renderSingleMessage(groupID, tab, message, memberHash);
	}

	public void joinGame(Game game) {
		//allows to join a game, if the user does not belong to another game
		//otherwise user cannot join the game
		lb.joinGame(game, gameLobbyController);
	}

	//reactivate for the possibility of joining the game
	public void onRejoin() {
		lb.onJoin(members, gameScreenController, userStorage);
	}

	private void onUsersChanged(ListChangeListener.Change<? extends User> c) {
		//clear scroll pane
		((VBox) this.userScrollPane.getContent()).getChildren().clear();

		//add header for friends
		this.createHeader("Friends:");

		//add friends
		((VBox) this.userScrollPane.getContent()).getChildren().addAll(friendsUserList.stream().sorted(userComparator).map(this::renderUser).toList());

		//add header for rest of Users
		this.createHeader("All Users:");

		//add rest of users
		((VBox) this.userScrollPane.getContent()).getChildren().addAll(users.stream().sorted(userComparator).map(this::renderUser).toList());
	}

	private void createHeader(String headerText) {
		Label header = new Label(headerText);
		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER);
		header.setStyle("-fx-font-size: 20");
		hBox.getChildren().add(header);
		((VBox) this.userScrollPane.getContent()).getChildren().add(hBox);
	}

	public void showFriendsMenu(User user) {
		boolean addFriend = alertService.showFriendsMenu("Do you want to add " + user.name() + " as a friend?");
		if (addFriend) {
			List<String> updatedFriends = new ArrayList<>();
			for (User oldFriends : friendsUserList) {
				updatedFriends.add(oldFriends._id());
			}
			updatedFriends.add(user._id());
			userService.userUpdate(idStorage.getID(), null, null, updatedFriends, null, null).blockingFirst();
		}
	}

	public void showRemoveFriendMenu(User user) {
		boolean removeFriend = alertService.showFriendsMenu("Do you want to remove " + user.name() + " as a friend?");
		if (removeFriend) {
			List<String> updatedFriends = new ArrayList<>();
			for (User oldFriends : friendsUserList) {
				if (!Objects.equals(oldFriends._id(), user._id())) {
					updatedFriends.add(oldFriends._id());
				}
			}
			userService.userUpdate(idStorage.getID(), null, null, updatedFriends, null, null).blockingFirst();
		}

	}

	public boolean isNotAFriend(User user) {
		return !friendsUserList.contains(user);
	}

	public void OnAchievementsPressed() {
		this.app.show(achievementsScreenController.get());
	}
}
