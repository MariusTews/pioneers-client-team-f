package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class UserService {

    private final UserApiService userApiService;

    @Inject
    public UserService(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    public void register(String username, String avatar, String password) {
    }

    public Observable<List<User>> findAll() {
        return this.userApiService.findAllUsers();
    }
}
