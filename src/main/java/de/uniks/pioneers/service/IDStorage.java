package de.uniks.pioneers.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class IDStorage {

    private String ID;

    @Inject
    public IDStorage() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
