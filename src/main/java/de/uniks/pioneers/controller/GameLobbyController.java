package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.*;
import de.uniks.pioneers.websocket.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.uniks.pioneers.Constants.*;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class GameLobbyController implements Controller {

	private final ObservableList<Member> members = FXCollections.observableArrayList();

	private final ObservableList<Member> spectatorMember = FXCollections.observableArrayList();

	private final ObservableList<User> playerList = FXCollections.observableArrayList();

	@FXML
	public Label idTitleLabel;
	@FXML
	public Button idLeaveButton;
	@FXML
	public ScrollPane idUserInLobby;
	@FXML
	public Button idReadyButton;
	@FXML
	public Button idStartGameButton;
	@FXML
	public VBox idUserList;
	@FXML
	public VBox idChatContainer;
	@SuppressWarnings("CanBeFinal")
	@FXML
	public ComboBox<Label> colorPicker = new ComboBox<>();
	@FXML
	public Label settingsLabel;
	@FXML
	public ScrollPane idUserInLobby1;
	@FXML
	public Label spectatorId;
	@FXML
	public CheckBox checkBoxId;

	private final App app;
	private final MemberService memberService;
	private final UserService userService;
	private final MessageService messageService;
	private final GameService gameService;
	private final Provider<LobbyController> lobbyController;
	private final Provider<GameScreenController> gameScreenController;
	//Button for spectator
	public VBox spectatorIds;
	//player Numbers Label
	public Label playersNumberId;
	//player LabelID
	public Label spectatorLabelId;

	private MessageViewSubController messageViewSubController;
	private MemberListSubcontroller memberListSubcontroller;

	private MemberListSubcontroller memberListSpectatorSubcontroller;
	private final EventListener eventListener;
	private final GameStorage gameStorage;
	private final MemberIDStorage memberIDStorage;
	private final IDStorage idStorage;
	private final CompositeDisposable disposable = new CompositeDisposable();

	private Game game;

	@Inject
	public GameLobbyController(App app,
							   MemberService memberService,
							   UserService userService,
							   MessageService messageService,
							   GameService gameService,
							   Provider<LobbyController> lobbyController,
							   Provider<GameScreenController> gameScreenController,
							   EventListener eventListener,
							   IDStorage idStorage,
							   GameStorage gameStorage,
							   MemberIDStorage memberIDStorage) {
		this.app = app;
		this.memberService = memberService;
		this.userService = userService;
		this.messageService = messageService;
		this.gameService = gameService;
		this.lobbyController = lobbyController;
		this.gameScreenController = gameScreenController;
		this.eventListener = eventListener;
		this.gameStorage = gameStorage;
		this.memberIDStorage = memberIDStorage;
		this.idStorage = idStorage;
	}

	@Override
	public void init() {

		// get all game members
		memberService
				.getAllGameMembers(this.gameStorage.getId())
				.observeOn(FX_SCHEDULER)
				.subscribe(col -> {
					this.members.setAll(col);
					int ready = 0;
					for (Member member : members) {
						if (member.ready()) {
							ready++;
						}
					}
					if (ready >= 2) {
						idStartGameButton.disableProperty().set(false);
					}
					this.userService.findAllUsers().
							observeOn(FX_SCHEDULER)
							.subscribe(event -> {
								for (User user : event) {
									for (Member member : members) {
										if (user._id().equals(member.userId())) {
											this.playerList.add(user);
										}
									}
								}

							});
				});

		// listen to members
		disposable.add(eventListener
				.listen("games." + this.gameStorage.getId() + ".members.*.*", Member.class)
				.observeOn(FX_SCHEDULER)
				.subscribe(event -> {
					final Member member = event.data();
					if (event.event().endsWith(CREATED)) {

						if (!members.contains(member)) {
							members.add(member);
							userService.findOne(member.userId())
									.observeOn(FX_SCHEDULER)
									.subscribe(this.playerList::add);
						}

					} else if (event.event().endsWith(DELETED)) {
						if (members.contains(member)) {
							members.remove(member);
						} else {
							spectatorMember.remove(member);
						}
						if (member.userId().equals(idStorage.getID())) {
							app.show(lobbyController.get());
						}

					} else if (event.event().endsWith(UPDATED)) {
						for (Member updatedMember : this.members) {
							if (updatedMember.userId().equals(member.userId()) && member.spectator()) {
								spectatorMember.add(member);
								members.remove(updatedMember);
								this.playersNumberId.setText("Players " + members.size() + "/6");
								break;
							} else if (updatedMember.userId().equals(member.userId())) {
								this.members.set(this.members.indexOf(updatedMember), member);
								break;
							}
						}
						for (Member upSpectatorMember : this.spectatorMember) {
							if (upSpectatorMember.userId().equals(member.userId()) && !member.spectator()) {
								spectatorMember.remove(upSpectatorMember);
								members.add(member);
								this.playersNumberId.setText("Players " + members.size() + "/6");
								break;
							}
						}
						int readyMembers = 0;
						for (Member members : this.members) {
							if (members.ready()) {
								readyMembers += 1;
							}
						}
						for (Member member1 : this.spectatorMember) {
							if (member1.ready()) {
								readyMembers += 1;
							}
						}
						this.idStartGameButton.disableProperty().set(readyMembers < 1 || readyMembers != members.size()
								+ spectatorMember.size());// || members.size() == 0);

						//checks if combobox has been clicked,if not
						//then automatically picks color for the user.
						if(colorPicker.getSelectionModel().isEmpty()){
							if(readyMembers == members.size() + spectatorMember.size()){
								giveYourselfColor();
							}
						}

						this.idUserList.getChildren().clear();
						this.idUserList.getChildren().setAll(members.stream().map(this::renderMember).toList());
						this.spectatorIds.getChildren().clear();
						this.spectatorIds.getChildren().setAll(spectatorMember.stream().map(this::renderSpectatorMember).toList());
					}
				}));

		//listen to the game
		disposable.add(eventListener
				.listen("games." + this.gameStorage.getId() + ".*.*", Message.class)
				.observeOn(FX_SCHEDULER)
				.subscribe(event -> {
					if (event.event().endsWith("state" + CREATED)) {
						final GameScreenController controller = gameScreenController.get();
						this.app.show(controller);
					}
				}));

		// initialize sub-controller, so the disposable in sub-controller listens to incoming/outgoing messages
		this.messageViewSubController = new MessageViewSubController(eventListener, gameStorage,
				userService, messageService, memberIDStorage, memberService);
		messageViewSubController.init();

		//action when the screen is closed
		this.app.getStage().setOnCloseRequest(e -> {
				gameService
						.findOneGame(gameStorage.getId())
						.observeOn(FX_SCHEDULER)
						.subscribe(result -> {
							actionOnCloseScreen(result);
						});
				e.consume();
		});

	}

	@Override
	public void destroy() {
		// destroy sub controller, otherwise the messages are displayed twice in-game, because the game controller
		// creates a new messageViewSubController
		if (this.messageViewSubController != null) {
			this.messageViewSubController.destroy();
		}
		disposable.dispose();
	}


	@Override
	public Parent render() {
		// load UI elements
		final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/GameLobbyScreen.fxml"));
		loader.setControllerFactory(c -> this);
		final Parent parent;

		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		gameService
				.findOneGame(this.gameStorage.getId())
				.observeOn(FX_SCHEDULER)
				.subscribe(result -> {
					this.game = result;
					int victoryPoints = result.settings().victoryPoints();
					int mapRadius = result.settings().mapRadius();
					this.gameStorage.setSize(mapRadius);
					this.gameStorage.setVictoryPoints(victoryPoints);
					this.settingsLabel.setText("Settings: Map Size = " + mapRadius + "  Required VP = " + victoryPoints);
					this.idTitleLabel.setText("Welcome to " + this.game.name());
				});

		// load game members

		this.idUserList.getChildren().setAll(members.stream().map(this::renderMember).toList());
		playerList.addListener((ListChangeListener<? super User>) c -> this.idUserList.getChildren().setAll(members.stream().map(this::renderMember).toList()));

		addColorOnComboBox(colorPicker);

		this.spectatorIds.getChildren().setAll(spectatorMember.stream().map(this::renderSpectatorMember).toList());
		playerList.addListener((ListChangeListener<? super User>) c -> this.spectatorIds.getChildren().setAll(spectatorMember.stream().map(this::renderSpectatorMember).toList()));

		// disable start button when entering game lobby
		idStartGameButton.disableProperty().set(true);

		// show chat and load the messages
		idChatContainer.getChildren().setAll(messageViewSubController.render());

		return parent;
	}

	//This makes sure the user is offline
	// and is not  part of the game anymore.
	private void actionOnCloseScreen(Game result) {
		if ((int) result.members() == 1 || result.owner().equals(idStorage.getID())) {
			gameService
					.deleteGame(gameStorage.getId())
					.observeOn(FX_SCHEDULER)
					.subscribe(e -> {
					});
		} else {
			memberService
					.leave(gameStorage.getId(), idStorage.getID())
					.observeOn(FX_SCHEDULER)
					.subscribe();
		}

		userService.statusUpdate(this.idStorage.getID(), "offline").
				observeOn(FX_SCHEDULER)
				.subscribe(e -> {
					System.exit(0);
				});
	}

	public void leave() {
		gameService
				.findOneGame(gameStorage.getId())
				.observeOn(FX_SCHEDULER)
				.subscribe(result -> {
					if ((int) result.members() == 1 || result.owner().equals(idStorage.getID())) {
						gameService
								.deleteGame(gameStorage.getId())
								.observeOn(FX_SCHEDULER)
								.subscribe(onSuccess -> app.show(lobbyController.get()), onError -> {
								});
					} else {
						memberService
								.leave(gameStorage.getId(), idStorage.getID())
								.observeOn(FX_SCHEDULER)
								.subscribe(onSuccess -> app.show(lobbyController.get()), onError -> {
								});
					}
				});
	}

	public void ready() {
		for (Member member : this.members) {
			if (member.userId().equals(idStorage.getID())) {
				if (member.ready()) {
					memberService.statusUpdate(gameStorage.getId(), idStorage.getID(), false, member.color(), member.spectator()).subscribe();
					this.idReadyButton.setText("Ready");
				} else {
					memberService.statusUpdate(gameStorage.getId(), idStorage.getID(), true, member.color(), member.spectator()).subscribe();
					this.idReadyButton.setText("Not Ready");
				}
			}
		}
	}

	public void startGame() {
		//this makes sure the game can only be started
		//with allowed maximum numbers
		if(members.size() > 6){
			Alert alert = new Alert(Alert.AlertType.INFORMATION, "Maximum allowed players is "+ MAX_MEMBERS);
			// Set alert stylesheet
			DialogPane dialogPane = alert.getDialogPane();
			dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
					.getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
			alert.showAndWait();

		}else {
			gameService.updateGame(gameStorage.getId(), null, null, this.idStorage.getID(), true, game.settings().mapRadius(), game.settings().victoryPoints())
					.observeOn(FX_SCHEDULER)
					.doOnError(error -> {
						if ("HTTP 403 ".equals(error.getMessage())) {
							Alert alert = new Alert(Alert.AlertType.INFORMATION, "only the owner can start the game!");
							// Set alert stylesheet
							DialogPane dialogPane = alert.getDialogPane();
							dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
									.getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
							alert.showAndWait();
						}
					})
					.subscribe(onSuccess -> {
						final GameScreenController controller = gameScreenController.get();
						this.app.show(controller);

					}, onError -> {
					});
		}
	}

	private void giveYourselfColor() {
		ColorController controller = new ColorController();
		List<Label> createdColors = controller.getColor();

		List<String> allColors = createdColors(createdColors);

		//remove color from allColors if it belongs to a member
		for (Member member : members) {
			if (member.color() != null && !member.spectator()) {
				allColors.remove(member.color());
			}
		}

		//give color to member that do not have colors
		for (Member member : members) {
			if ((member.color() == null || member.color().equals("#000000"))
					&& member.userId().equals(this.idStorage.getID()) && !member.spectator()) {
				memberService.statusUpdate(member.gameId(), member.userId(), member.ready(), allColors.get(0), member.spectator())
						.subscribe();
				allColors.remove(0);
			}
		}
	}

	//change String into hashcode for colors
	private List<String> createdColors(List<Label> createdColors) {
		List<String> colorNames = new ArrayList<>();
		for (Label label : createdColors) {
			String colorInString = "#" + Color.web(label.getText().toLowerCase()).toString().substring(2, 8);
			colorNames.add(colorInString);
		}

		return colorNames;
	}

	private Node renderMember(Member member) {
		//sets the size of player
		//This flag will make sure,
		//Null Pointer exception will not be thrown
		//,if there are more Players
		boolean ch = false;
		this.playersNumberId.setText("Players " + members.size() + "/6");
		for (User user : playerList) {
			if (user._id().equals(member.userId()) && !member.spectator()) {
				ch = true;
				this.memberListSubcontroller = new MemberListSubcontroller(member, user);
				break;
			}
		}
		if(!ch){
			this.memberListSubcontroller = new MemberListSubcontroller(null,null);
		}
		return memberListSubcontroller.render();
	}

	//render spectators
	private Node renderSpectatorMember(Member member) {
		for (User user : playerList) {
			if (user._id().equals(member.userId()) && member.spectator()) {
				this.memberListSpectatorSubcontroller = new MemberListSubcontroller(member, user);
				break;
			}
		}
		return memberListSpectatorSubcontroller.render();
	}

	private void addColorOnComboBox(ComboBox<Label> comboBox) {

		ObservableList<Label> items = FXCollections.observableArrayList(color());
		comboBox.getItems().addAll(items);
		comboBox.getSelectionModel().clearSelection(0);
		comboBox.setVisibleRowCount(300);
		//This makes sure the color are presented in the strings and
		//border will be shown on Labels
		comboBox.setCellFactory(listView -> new ListCell<>() {
			public void updateItem(Label label, boolean empty) {
				super.updateItem(label, empty);
				if (label != null) {
					if (label.getText().equals("Select Color")) {
						setText(null);
					}
					setText(label.getText());
					setTextFill(label.getTextFill());
					setMinWidth(label.getMinWidth());
				} else {
					setText(null);

				}
			}
		});
	}

	//List of colors
	private List<Label> color() {
		final ColorController controller = new ColorController();
		return controller.getColor();
	}

	//color event, if color is picked then send color
	public void colorPicked() {
		Label label = colorPicker.getSelectionModel().getSelectedItem();

		Color c = Color.web(label.getText().toLowerCase());
		String pickedColor = "#" + c.toString().substring(2, 8);

		boolean chose = true;
		boolean ready = false;
		boolean spectator = false;
		List<Member> memberList = memberService.getAllGameMembers(gameStorage.getId()).blockingFirst();
		for (Member member : memberList) {
			if (member.color() != null && member.color().equals(pickedColor)) {
				chose = false;
			}
			if (member.userId().equals(idStorage.getID())) {
				ready = member.ready();
				spectator = member.spectator();
			}
		}
		if (chose) {
			memberService.statusUpdate(gameStorage.getId(), idStorage.getID(), ready, pickedColor, spectator).
					observeOn(FX_SCHEDULER).subscribe();
		}
	}

	//changes between Spectator and Player
	public void onCheckBox() {
		//get all the members that are currently in the game
		List<Member> memberList = memberService.getAllGameMembers(gameStorage.getId()).blockingFirst();
		boolean ready = false;
		boolean spectator = false;
		for (Member member : memberList) {
			if (member.userId().equals(idStorage.getID())) {
				ready = member.ready();
				spectator = member.spectator();
			}
		}

		if (!spectator) {
			//makes ready button invisible
			this.idReadyButton.setText("Not Ready");
			this.idReadyButton.setDisable(true);

			this.colorPicker.setDisable(true);
			memberService.statusUpdate(gameStorage.getId(), idStorage.getID(), true, "#000000", true)
					.subscribe();
		} else {
			//makes ready button visible
			this.idReadyButton.setText("Ready");
			this.idReadyButton.setDisable(false);
			this.colorPicker.setDisable(false);
			memberService.statusUpdate(gameStorage.getId(), idStorage.getID(), !ready, null, false)
					.subscribe();
		}
	}
}
