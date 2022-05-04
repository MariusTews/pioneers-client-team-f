package de.uniks.pioneers.view;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.LobbyController;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

public class LobbyViewTest extends ApplicationTest {

    private Stage stage;
    private App app;

    @Override
    public void start(Stage stage) {
        // start application
        this.stage = stage;
        this.app = new App();
        this.app.start(stage);
    }

    @Test
    public void testViewParameters() {
        //opens the Lobby to test the View without the need to Login
        app.show(new LobbyController());

        Button rules = lookup("#rulesButton").query();
        Button logout = lookup("#logoutButton").query();
        Button editUser = lookup("#editUserButton").query();
        Button createGame = lookup("#createGameButton").query();
        Button send = lookup("#sendButton").query();

        Label welcomeLabel = lookup("#userWelcomeLabel").query();

        TextField chatMessage = lookup("#chatMessageField").query();

        ListView usersList = lookup("#userListView").query();
        ListView gamesList = lookup("#gameListView").query();

        Assertions.assertThat(rules.getText()).isEqualTo("Rules");
        Assertions.assertThat(logout.getText()).isEqualTo("Logout");
        Assertions.assertThat(editUser.getText()).isEqualTo("Edit User");
        Assertions.assertThat(createGame.getText()).isEqualTo("Create Game");
        Assertions.assertThat(send.getText()).isEqualTo("send");

        Assertions.assertThat(welcomeLabel.getText()).isEqualTo("Nice to see you again, username!");

        Assertions.assertThat(chatMessage.getText()).isEqualTo("");

        clickOn(chatMessage);
        write("test");
        Assertions.assertThat(chatMessage.getText()).isEqualTo("test");

        Assertions.assertThat(usersList.getItems().isEmpty()).isTrue();
        Assertions.assertThat(gamesList.getItems().isEmpty()).isTrue();
    }

    @Test
    public void testSendButton() {
        //opens the Lobby to test the View without the need to Login
        app.show(new LobbyController());

        Button send = lookup("#sendButton").query();
        TextField chatMessage = lookup("#chatMessageField").query();

        TabPane tabPane = lookup("#tabPane").query();
        Tab allTab = tabPane.getTabs().get(0);
        VBox chat = ((VBox) ((ScrollPane) allTab.getContent()).getContent());


        Assertions.assertThat(chat.getChildren().isEmpty()).isTrue();
        clickOn(chatMessage);
        write("test");

        clickOn(send);
        Assertions.assertThat(chat.getChildren().size()).isEqualTo(1);
        Label message = (Label) chat.getChildren().get(0);
        Assertions.assertThat(chatMessage.getText()).isEqualTo("");
        Assertions.assertThat(message.getText()).isEqualTo("username: test");
    }

    @Test
    public void testSendviaEnter() {
        //opens the Lobby to test the View without the need to Login
        app.show(new LobbyController());

        TextField chatMessage = lookup("#chatMessageField").query();

        TabPane tabPane = lookup("#tabPane").query();
        Tab allTab = tabPane.getTabs().get(0);
        VBox chat = ((VBox) ((ScrollPane) allTab.getContent()).getContent());

        Assertions.assertThat(chat.getChildren().isEmpty()).isTrue();
        clickOn(chatMessage);
        write("test");

        press(KeyCode.ENTER);
        Assertions.assertThat(chat.getChildren().size()).isEqualTo(1);
        Label message = (Label) chat.getChildren().get(0);
        Assertions.assertThat(chatMessage.getText()).isEqualTo("");
        Assertions.assertThat(message.getText()).isEqualTo("username: test");
    }
}
