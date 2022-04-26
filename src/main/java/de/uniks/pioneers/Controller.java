package de.uniks.pioneers;

import javafx.scene.Parent;

public interface Controller {
    void init();
    void destroy();
    Parent render();
}
