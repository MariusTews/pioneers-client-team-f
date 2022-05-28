package de.uniks.pioneers.controller;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.App;
import de.uniks.pioneers.service.AuthService;
import de.uniks.pioneers.service.UserService;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class LoginController_Factory implements Factory<LoginController> {
  private final Provider<App> appProvider;

  private final Provider<AuthService> authServiceProvider;

  private final Provider<UserService> userServiceProvider;

  private final Provider<SignUpController> signUpControllerProvider;

  private final Provider<LobbyController> lobbyControllerProvider;

  public LoginController_Factory(Provider<App> appProvider,
      Provider<AuthService> authServiceProvider, Provider<UserService> userServiceProvider,
      Provider<SignUpController> signUpControllerProvider,
      Provider<LobbyController> lobbyControllerProvider) {
    this.appProvider = appProvider;
    this.authServiceProvider = authServiceProvider;
    this.userServiceProvider = userServiceProvider;
    this.signUpControllerProvider = signUpControllerProvider;
    this.lobbyControllerProvider = lobbyControllerProvider;
  }

  @Override
  public LoginController get() {
    return newInstance(appProvider.get(), authServiceProvider.get(), userServiceProvider.get(), signUpControllerProvider, lobbyControllerProvider);
  }

  public static LoginController_Factory create(Provider<App> appProvider,
      Provider<AuthService> authServiceProvider, Provider<UserService> userServiceProvider,
      Provider<SignUpController> signUpControllerProvider,
      Provider<LobbyController> lobbyControllerProvider) {
    return new LoginController_Factory(appProvider, authServiceProvider, userServiceProvider, signUpControllerProvider, lobbyControllerProvider);
  }

  public static LoginController newInstance(App app, AuthService authService,
      UserService userService, Provider<SignUpController> signUpController,
      Provider<LobbyController> lobbyController) {
    return new LoginController(app, authService, userService, signUpController, lobbyController);
  }
}
