package de.uniks.pioneers.view;

import de.uniks.pioneers.App;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;

class RulesScreenViewTest extends ApplicationTest {
    private Stage stage;
    private App app;

    @Override
    public void start(Stage stage) {
        // start application
        this.stage = stage;
        this.app = new App();
        this.app.start(stage);
    }

}
