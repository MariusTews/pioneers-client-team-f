package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;

public class UserService {

    private final UserApiService userApiService;

    @Inject
    public UserService(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    public void register(String username, String avatar, String password) throws IOException {
        try {
            Response<User> response = userApiService.create(new CreateUserDto(username, avatar, password)).execute();
            final User user = response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
