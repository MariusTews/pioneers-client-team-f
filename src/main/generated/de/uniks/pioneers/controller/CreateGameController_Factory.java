package de.uniks.pioneers.controller;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.App;
import de.uniks.pioneers.service.GameService;
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
public final class CreateGameController_Factory implements Factory<CreateGameController> {
  private final Provider<App> appProvider;

  private final Provider<GameService> gameServiceProvider;

  private final Provider<LobbyController> lobbyControllerProvider;

  private final Provider<GameLobbyController> gameLobbyControllerProvider;

  public CreateGameController_Factory(Provider<App> appProvider,
      Provider<GameService> gameServiceProvider, Provider<LobbyController> lobbyControllerProvider,
      Provider<GameLobbyController> gameLobbyControllerProvider) {
    this.appProvider = appProvider;
    this.gameServiceProvider = gameServiceProvider;
    this.lobbyControllerProvider = lobbyControllerProvider;
    this.gameLobbyControllerProvider = gameLobbyControllerProvider;
  }

  @Override
  public CreateGameController get() {
    return newInstance(appProvider.get(), gameServiceProvider.get(), lobbyControllerProvider, gameLobbyControllerProvider);
  }

  public static CreateGameController_Factory create(Provider<App> appProvider,
      Provider<GameService> gameServiceProvider, Provider<LobbyController> lobbyControllerProvider,
      Provider<GameLobbyController> gameLobbyControllerProvider) {
    return new CreateGameController_Factory(appProvider, gameServiceProvider, lobbyControllerProvider, gameLobbyControllerProvider);
  }

  public static CreateGameController newInstance(App app, GameService gameService,
      Provider<LobbyController> lobbyController,
      Provider<GameLobbyController> gameLobbyController) {
    return new CreateGameController(app, gameService, lobbyController, gameLobbyController);
  }
}
