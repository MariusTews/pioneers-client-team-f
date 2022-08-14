package de.uniks.pioneers;

import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.assertions.api.Assertions.assertThat;


public class AppTest extends ApplicationTest {


    @Override
    public void start(Stage stage) {
        final App app = new App(null);
        MainComponent testComponent = DaggerTestComponent.builder().mainapp(app).build();
        app.setTest(true);
        app.start(stage);
        app.show(testComponent.loginController());
    }

    @Test
    public void criticalPath() {

        //test SignUp
        write("\t\t\t");
        type(KeyCode.SPACE);
        write("Alice\t");
        write("00000000\t");
        write("00000000\t");


        //test back button in SignUp
        type(KeyCode.SPACE);
        WaitForAsyncUtils.waitForFxEvents();
        type(KeyCode.ENTER);
        type(KeyCode.SPACE);
        write("\t\t\t");
        type(KeyCode.SPACE);
        write("\t\t\t");
        type(KeyCode.SPACE);

        //test Login
        write("test\t");
        write("00000000");
        type(KeyCode.ENTER);

        //test RulesScreen
        type(KeyCode.SPACE);
        type(KeyCode.SPACE);

        type(KeyCode.DOWN);
        type(KeyCode.SPACE);
        //test EditUser
        write("\tAlice123\t");
        write("123456789\t");
        write("123456789\t\t\t");
        type(KeyCode.SPACE);

        write("\t\t\t\t\t\t\t");
        type(KeyCode.SPACE);

        //test AchievementsScreen
        write("\t");
        type(KeyCode.SPACE);

        write("\t\t\t\t\t\t\t\t");
        type(KeyCode.SPACE);

        //test CreateGameScreen
        write("testGame\t");
        write("12\t");
        type(KeyCode.SPACE);
        write("\t\t");
        for (int i = 0; i < 6; i++) {
            type(KeyCode.SPACE);
        }

        //test back button game lobby
        WaitForAsyncUtils.waitForFxEvents();
        write("\t\t");
        type(KeyCode.SPACE);
        write("\t\t\t\t\t\t\t\t");
        type(KeyCode.SPACE);

        //test maps screen
        WaitForAsyncUtils.waitForFxEvents();
        write("\t\t");
        type(KeyCode.SPACE);
        write("\t\t\t\t\t");
        type(KeyCode.SPACE);
        write("\t\t\t");
        type(KeyCode.SPACE);
        clickOn("#mapSizePlusButton");
        clickOn("#mapSizeMinusButton");
        clickOn("#mapSizePlusButton");
        clickOn("#x0y0z0_tileButton");
        clickOn("random");
        clickOn("moon");
        clickOn("#x1yM1z0_harborButton");
        clickOn("#x1yM1z0_cancelButton");
        clickOn("#x0y0z0_cancelButton");
        clickOn("#x0y0z0_tileButton");
        clickOn("#x0yM1z1_tileButton");
        clickOn("random");
        clickOn("moon");
        clickOn("#x0yM1z1_numberField");
        write("8");
        clickOn("#xM1y0z1_harborButton");
        clickOn("Top_right");
        clickOn("Middle_right");
        clickOn("3:1");
        clickOn("mars_bar");
        clickOn("#nameTextField");
        write("mapname");
        clickOn("#saveButton");
        write("\t");
        type(KeyCode.SPACE);
        write("\t\t\t\t\t");
        type(KeyCode.SPACE);
        doubleClickOn("#nameLabel");
        write("\t\t\t");
        type(KeyCode.SPACE);


        //test CreateGameScreen
        write("testGame\t");
        write("12\t");
        type(KeyCode.SPACE);
        write("\t\t");
        for (int i = 0; i < 6; i++) {
            type(KeyCode.SPACE);
        }
        write("\t\t\t\t");
        type(KeyCode.SPACE);


        //test gameLobby
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Choose color");
        clickOn("BEIGE");
        clickOn("#spectatorCheckBox");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#spectatorCheckBox");
        ComboBox<Label> colorPicker = lookup("#colorPicker").query();
        colorPicker.setSelectionModel(null);
        clickOn("Start");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(5000);

        //test ingameScreen
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Leave");
        clickOn("Rejoin");
        WaitForAsyncUtils.waitForFxEvents();
        // founding-settlement-1
        clickOn("#x0y0z0_6");
        //founding-road-1
        clickOn("#x0y0z0_7");
        //founding-settlement-2
        clickOn("#x0y0z0_0");
        //founding-road-2
        clickOn("#x1y0zM1_7");
        //roll dice
        clickOn("#roll");
        //buy dev card
        clickOn("#developmentBuyIdButton");
        type(KeyCode.ENTER);
        //clickOn("OK");
        //trade with bank
        clickOn("#tradingFoldOutId");
        clickOn("#giveCactusPlusButton");
        clickOn("#giveCactusPlusButton");
        clickOn("#giveCactusPlusButton");
        clickOn("#giveCactusPlusButton");
        clickOn("#receiveMoonPlusButton");
        clickOn("#offerBank");
        //trade with player
        clickOn("#giveCactusPlusButton");
        clickOn("#receiveMoonPlusButton");
        clickOn("#offerPlayer");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#tradePartnerLabel");
        clickOn("#tradeButton");
        //finish turn
        clickOn("#finishTurnButton");
        clickOn("#acceptOfferButton");
        clickOn("#roll");
        //discard resources
        clickOn("#increment_lumber");
        clickOn("#increment_lumber");
        clickOn("#increment_lumber");
        clickOn("#increment_lumber");
        clickOn("#increment_lumber");
        clickOn("#increment_brick");
        clickOn("#increment_brick");
        clickOn("#increment_brick");
        clickOn("#increment_brick");
        clickOn("#increment_brick");
        clickOn("#increment_ore");
        clickOn("#increment_ore");
        clickOn("#increment_ore");
        clickOn("#increment_ore");
        clickOn("#increment_ore");
        clickOn("#increment_wool");
        clickOn("#increment_wool");
        clickOn("#increment_wool");
        clickOn("#increment_wool");
        clickOn("#increment_wool");
        clickOn("#increment_grain");
        clickOn("#increment_grain");
        clickOn("#increment_grain");
        clickOn("#increment_grain");
        clickOn("#increment_grain");
        clickOn("#discardButton");
        type(KeyCode.ENTER);

        //rob (coordinates, because on the tile is also a number label -> can't click on the tile directly with the id
        clickOn(new Point2D(670, 303));

        //play devCard
        clickOn("#devCardsPane");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#devCardBuildRoad");
        //build road
        clickOn("#x1yM1z0_11");
        clickOn("#x2yM1zM1_7");

        //build settlement
        clickOn("#sett");
        clickOn("#x2yM1zM1_6");
        //build city
        clickOn("#city");
        clickOn("#x2yM1zM1_6");
        WaitForAsyncUtils.waitForFxEvents();
        //back to lobbyScreen
        clickOn("#closeGameButtonId");
        //back to loginScreen
        clickOn("#logoutButton");
        WaitForAsyncUtils.waitForFxEvents();
        //screenAssert login
        Button loginButton = lookup("#loginButton").query();
        assertThat(loginButton).isVisible();

    }
}
