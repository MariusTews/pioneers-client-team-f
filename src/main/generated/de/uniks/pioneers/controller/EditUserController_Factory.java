package de.uniks.pioneers.controller;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.App;
import de.uniks.pioneers.service.IDStorage;
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
public final class EditUserController_Factory implements Factory<EditUserController> {
  private final Provider<App> appProvider;

  private final Provider<LobbyController> lobbyControllerProvider;

  private final Provider<LoginController> loginControllerProvider;

  private final Provider<UserService> userServiceProvider;

  private final Provider<IDStorage> idStorageProvider;

  public EditUserController_Factory(Provider<App> appProvider,
      Provider<LobbyController> lobbyControllerProvider,
      Provider<LoginController> loginControllerProvider, Provider<UserService> userServiceProvider,
      Provider<IDStorage> idStorageProvider) {
    this.appProvider = appProvider;
    this.lobbyControllerProvider = lobbyControllerProvider;
    this.loginControllerProvider = loginControllerProvider;
    this.userServiceProvider = userServiceProvider;
    this.idStorageProvider = idStorageProvider;
  }

  @Override
  public EditUserController get() {
    return newInstance(appProvider.get(), lobbyControllerProvider, loginControllerProvider, userServiceProvider.get(), idStorageProvider.get());
  }

  public static EditUserController_Factory create(Provider<App> appProvider,
      Provider<LobbyController> lobbyControllerProvider,
      Provider<LoginController> loginControllerProvider, Provider<UserService> userServiceProvider,
      Provider<IDStorage> idStorageProvider) {
    return new EditUserController_Factory(appProvider, lobbyControllerProvider, loginControllerProvider, userServiceProvider, idStorageProvider);
  }

  public static EditUserController newInstance(App app, Provider<LobbyController> lobbyController,
      Provider<LoginController> loginController, UserService userService, IDStorage idStorage) {
    return new EditUserController(app, lobbyController, loginController, userService, idStorage);
  }
}
