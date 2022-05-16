package de.uniks.pioneers.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MemberIDStorage {

    private String id;

    @Inject
    public MemberIDStorage() {

    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
