package de.uniks.pioneers.view;

import de.uniks.pioneers.App;
import de.uniks.pioneers.websocket.EventListener;
import de.uniks.pioneers.controller.GameLobbyController;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.*;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameLobbyViewTest extends ApplicationTest {
	@Mock
	MemberService memberService;
	@Mock
	UserService userService;
	@Mock
	MessageService messageService;
	@Mock
	GameService gameService;
	@Mock
	EventListener eventListener;
	@Spy
	GameStorage gameStorage;

	@Mock
	App app;

	@InjectMocks
	GameLobbyController gameLobbyController;

	@Override
	public void start(Stage stage) {
		Member m1 = new Member("0", "0", "g1", "u1", false, "#0000FF", false);
		Member m2 = new Member("1", "1", "g1", "u2", false, "#0000FF", false);

		User u1 = new User("1234", "12345", "u1", "a", "on", null, new ArrayList<>());
		User u2 = new User("1234", "12345", "u2", "b", "on", null, new ArrayList<>());

		Message x1 = new Message("2022-07-14T22:29:30.318Z", "0", "me1", "u1", "test1");
		Message x2 = new Message("2022-07-14T22:29:32.318Z", "1", "me2", "u2", "test2");

		Game g = new Game("0", "0", "g1", "g", "u1", 2, true, new GameSettings(2, 10));

		when(gameStorage.getId()).thenReturn("g1");
		when(memberService.getAllGameMembers(any())).thenReturn(Observable.just(m1, m2).buffer(2));
		when(userService.findAllUsers()).thenReturn(Observable.just(u1, u2).buffer(2));
		when(eventListener.listen(any(), any())).thenReturn(Observable.empty());
		when(gameService.findOneGame(any())).thenReturn(Observable.just(g));
		when(messageService.getAllMessages(any(), any())).thenReturn(Observable.just(x1, x2).buffer(2));
		when(app.getStage()).thenReturn(new Stage());

		App app = new App(gameLobbyController);
		app.start(stage);
	}

	@Test
	public void testGameLobbyUIElement() {
		// buttons
		// The message sub view is a VBox with all the chat elements in it which is loaded into the chat container
		VBox vBox = lookup("#idChatContainer").query();
		HBox hBox = (HBox) ((VBox) vBox.getChildren().get(0)).getChildren().get(1);

		Button leaveButton = lookup("#idLeaveButton").query();
		Button sendButton = (Button) hBox.getChildren().get(1);
		Button readyButton = lookup("#idReadyButton").query();
		Button startGameButton = lookup("#idStartGameButton").query();

		// label
		Label titleLabel = lookup("#idTitleLabel").query();

		// text field is part of the message sub view (see above)
		TextField messageField = (TextField) hBox.getChildren().get(0);

		// assertions buttons
		Assertions.assertEquals(leaveButton.getText(), "Leave");
		Assertions.assertEquals(sendButton.getText(), "Send");
		Assertions.assertEquals(readyButton.getText(), "Ready");
		Assertions.assertEquals(startGameButton.getText(), "Start");

		// assertion title
		Assertions.assertEquals(titleLabel.getText(), "Welcome to g");

		// assertion message field
		clickOn(messageField);
		write("something");
		Assertions.assertEquals(messageField.getText(), "something");
	}

	@Test
	public void renderMessageTest() {
		FlowPane box = lookup("#idMessageView").query();
		HBox messages = (HBox) box.getChildren().get(0);
		Label label = (Label) messages.getChildren().get(1);

		Assertions.assertEquals("a:", label.getText());
		Assertions.assertEquals(Color.BLUE, label.getTextFill());

		verify(messageService).getAllMessages("games", "g1");

	}

 	@Override
	public void stop() throws Exception {
		super.stop();
		gameLobbyController =null;
	}
}
