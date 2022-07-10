package de.uniks.pioneers.view;

import de.uniks.pioneers.App;
import de.uniks.pioneers.websocket.EventListener;
import de.uniks.pioneers.controller.LobbyController;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.*;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.MAX_MEMBERS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LobbyViewTest extends ApplicationTest {

	@Mock
	UserService userService;

	@Mock
	MemberService memberService;

	@Mock
	MessageService messageService;

	@Mock
	GameService gameService;

	@Mock
	GroupService groupService;

	@Mock
	EventListener eventListener;

	@Spy
	GameStorage gameStorage;

	@Spy
	IDStorage idStorage;

	@InjectMocks
	App app;

	@InjectMocks
	LobbyController lobbyController;

	@Override
	public void start(Stage stage) {
		when(idStorage.getID()).thenReturn("4");
		when(gameStorage.getId()).thenReturn("");
		when(gameService.findAllGames()).thenReturn(Observable.just(List.of(new Game("1", "1", "627cf3c93496bc00158f3859", "testGame", "1", 2, false, new GameSettings(2, 10)),
				new Game("1", "1", "14", "testGame", "3", MAX_MEMBERS, false, new GameSettings(2, 10)),
				new Game("1", "1", "13", "testGame", "2", 2, true, new GameSettings(2, 10)))));
		when(userService.findAllUsers()).thenReturn(Observable.just(List.of(new User("1234", "12345", "1", "test", "online", null, new ArrayList<>()),
				new User("1234", "12345", "4", "testus", "online", null, new ArrayList<>()),
				new User("1234", "12345", "3", "testtest", "offline", null, new ArrayList<>()))));
		when(memberService.getAllGameMembers("")).thenReturn(Observable.empty());
		when(groupService.getAll()).thenReturn(Observable.just(List.of(new Group("1", "1", "627cf3c93496bc00158f3859", null, List.of("1", "4")))));
		when(eventListener.listen(any(), any())).thenReturn(Observable.empty());
		when(messageService.getAllMessages("global", "627cf3c93496bc00158f3859")).thenReturn(Observable.just(List.of(new Message("1", "1", "5", "1", "Test Message"), new Message("1", "1", "5", "4", "Test Message2"), new Message("1", "1", "5", "1", "Test Message3"))));
		when(messageService.getAllMessages("groups", "627cf3c93496bc00158f3859")).thenReturn(Observable.just(List.of(new Message("1", "1", "5", "1", "Test Message"), new Message("1", "1", "5", "4", "Test Message2"), new Message("1", "1", "5", "1", "Test Message3"))));

		final App app = new App(null);
		app.start(stage);
		app.show(lobbyController);
	}

	@Test
	public void testViewParameters() {
		// Assert that all initial elements are displayed
		Button rules = lookup("#rulesButton").query();
		Button logout = lookup("#logoutButton").query();
		Button editUser = lookup("#editUserButton").query();
		Button createGame = lookup("#createGameButton").query();
		Button send = lookup("#sendButton").query();

		Label welcomeLabel = lookup("#userWelcomeLabel").query();

		TextField chatMessage = lookup("#chatMessageField").query();

		Assertions.assertThat(rules.getText()).isEqualTo("Rules");
		Assertions.assertThat(logout.getText()).isEqualTo("Logout");
		Assertions.assertThat(editUser.getText()).isEqualTo("Edit User");
		Assertions.assertThat(createGame.getText()).isEqualTo("Create Game");
		Assertions.assertThat(send.getText()).isEqualTo("send");

		Assertions.assertThat(welcomeLabel.getText()).isEqualTo("Nice to see you again, testus!");

		Assertions.assertThat(chatMessage.getText()).isEqualTo("");

		// Assert that a message can be written
		clickOn(chatMessage);
		write("test");
		Assertions.assertThat(chatMessage.getText()).isEqualTo("test");

		// Assert that the elements of every user in the userlist are placed for offline and online
		ScrollPane scrollPane = lookup("#userScrollPane").query();
		VBox vBox = (VBox) scrollPane.getContent();

		Assertions.assertThat(vBox.getChildren().size()).isEqualTo(3);

		HBox hBox = (HBox) vBox.getChildren().get(0);

		ImageView imageView = (ImageView) hBox.getChildren().get(0);
		Circle circle = (Circle) hBox.getChildren().get(1);
		Label label = (Label) hBox.getChildren().get(2);
		Button chat = (Button) hBox.getChildren().get(3);

		Assertions.assertThat(imageView.getImage()).isNull();
		Assertions.assertThat(circle.getFill()).isEqualTo(Color.GREEN);
		Assertions.assertThat(label.getText()).isEqualTo("test");
		// There is an icon for the chat button, therefore no text
		Assertions.assertThat(chat.getText()).isEqualTo("");

		HBox hBox2 = (HBox) vBox.getChildren().get(2);

		ImageView imageView2 = (ImageView) hBox2.getChildren().get(0);
		Circle circle2 = (Circle) hBox2.getChildren().get(1);
		Label label2 = (Label) hBox2.getChildren().get(2);

		Assertions.assertThat(imageView2.getImage()).isNull();
		Assertions.assertThat(circle2.getFill()).isEqualTo(Color.RED);
		Assertions.assertThat(label2.getText()).isEqualTo("testtest");

		// Assert that all elements of a game in the gamelist are displayed
		ScrollPane scrollPane2 = lookup("#gamesScrollPane").query();
		VBox vBox2 = (VBox) scrollPane2.getContent();

		Assertions.assertThat(vBox2.getChildren().size()).isEqualTo(3);

		// Assert that the first game of the list is accessible and right amount of members is displayed
		HBox hBox3 = (HBox) vBox2.getChildren().get(0);
		Assertions.assertThat(hBox3.getChildren().size()).isEqualTo(2);

		Label label3 = (Label) hBox3.getChildren().get(0);
		Button join = (Button) hBox3.getChildren().get(1);

		Assertions.assertThat(label3.getText()).isEqualTo("testGame (2/6)");
		Assertions.assertThat(join.getText()).isEqualTo("join");

		// Assert that the third game of the list has no "join" button, because max. members are reached
		HBox hBox4 = (HBox) vBox2.getChildren().get(1);
		Assertions.assertThat(hBox4.getChildren().size()).isEqualTo(1);

		Label label4 = (Label) hBox4.getChildren().get(0);
		Assertions.assertThat(label4.getText()).isEqualTo("testGame (" + MAX_MEMBERS + "/6)");

		// Assert that the second game of the list has no "join" button, because started = true
		HBox hBox5 = (HBox) vBox2.getChildren().get(2);
		Assertions.assertThat(hBox5.getChildren().size()).isEqualTo(1);

		Label label5 = (Label) hBox5.getChildren().get(0);
		Assertions.assertThat(label5.getText()).isEqualTo("testGame (2/6)");

		// Assert that the text messages are displayed correctly
		clickOn(chat);

		TabPane tabPane = lookup("#tabPane").query();
		Assertions.assertThat(tabPane.getTabs().size()).isEqualTo(2);

		Tab chatTab = tabPane.getTabs().get(1);

		ScrollPane chatScrollPane = (ScrollPane) chatTab.getContent();
		VBox chatVBox = (VBox) chatScrollPane.getContent();

		Assertions.assertThat(chatVBox.getChildren().size()).isEqualTo(3);

		HBox firstMessage = (HBox) chatVBox.getChildren().get(0);
		HBox secondMessage = (HBox) chatVBox.getChildren().get(1);
		HBox thirdMessage = (HBox) chatVBox.getChildren().get(2);

		Label firstChatMessage = (Label) firstMessage.getChildren().get(1);
		Label secondChatMessage = (Label) secondMessage.getChildren().get(1);
		Label thirdChatMessage = (Label) thirdMessage.getChildren().get(1);

		Assertions.assertThat(firstChatMessage.getText()).isEqualTo("test: Test Message");
		Assertions.assertThat(secondChatMessage.getText()).isEqualTo("testus: Test Message2");
		Assertions.assertThat(thirdChatMessage.getText()).isEqualTo("test: Test Message3");
	}
}
