package de.uniks.pioneers.controller;

import de.uniks.pioneers.Template.MapTemplate;
import javafx.scene.Parent;

public class MapTemplateSubcontroller implements Controller{
    private final MapTemplate template;

    public MapTemplateSubcontroller(MapTemplate template) {
        this.template = template;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        return null;
    }
}
