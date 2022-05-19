package de.uniks.pioneers.view;

import de.uniks.pioneers.App;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class rememberMeTest extends ApplicationTest {

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
    public void rememberMeTest() {

    }

}
