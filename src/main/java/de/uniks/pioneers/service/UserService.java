package de.uniks.pioneers.service;

import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class UserService {

    private final UserApiService userApiService;

    @Inject
    public UserService(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    public void register(String username, String avatar, String password) {
    }

    public Observable<List<User>> findAllUsers() {
        return this.userApiService.findAllUsers();
    }
}
