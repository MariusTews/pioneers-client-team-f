package de.uniks.pioneers.controller;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.App;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.IDStorage;
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
public final class UserListSubController_Factory implements Factory<UserListSubController> {
  private final Provider<App> appProvider;

  private final Provider<LobbyController> lobbyControllerProvider;

  private final Provider<User> userProvider;

  private final Provider<IDStorage> idStorageProvider;

  public UserListSubController_Factory(Provider<App> appProvider,
      Provider<LobbyController> lobbyControllerProvider, Provider<User> userProvider,
      Provider<IDStorage> idStorageProvider) {
    this.appProvider = appProvider;
    this.lobbyControllerProvider = lobbyControllerProvider;
    this.userProvider = userProvider;
    this.idStorageProvider = idStorageProvider;
  }

  @Override
  public UserListSubController get() {
    return newInstance(appProvider.get(), lobbyControllerProvider.get(), userProvider.get(), idStorageProvider.get());
  }

  public static UserListSubController_Factory create(Provider<App> appProvider,
      Provider<LobbyController> lobbyControllerProvider, Provider<User> userProvider,
      Provider<IDStorage> idStorageProvider) {
    return new UserListSubController_Factory(appProvider, lobbyControllerProvider, userProvider, idStorageProvider);
  }

  public static UserListSubController newInstance(App app, LobbyController lobbyController,
      User user, IDStorage idStorage) {
    return new UserListSubController(app, lobbyController, user, idStorage);
  }
}
