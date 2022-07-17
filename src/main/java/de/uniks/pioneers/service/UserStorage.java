package de.uniks.pioneers.service;

import de.uniks.pioneers.model.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class UserStorage {
    private List<User> userList = new ArrayList<>();

    @Inject
    public UserStorage() {

    }

    public List<User> getUserList() {
        return this.userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
