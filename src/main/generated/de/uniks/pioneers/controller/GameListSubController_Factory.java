package de.uniks.pioneers.controller;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Game;
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
public final class GameListSubController_Factory implements Factory<GameListSubController> {
  private final Provider<App> appProvider;

  private final Provider<Game> gameProvider;

  private final Provider<LobbyController> lobbyControllerProvider;

  public GameListSubController_Factory(Provider<App> appProvider, Provider<Game> gameProvider,
      Provider<LobbyController> lobbyControllerProvider) {
    this.appProvider = appProvider;
    this.gameProvider = gameProvider;
    this.lobbyControllerProvider = lobbyControllerProvider;
  }

  @Override
  public GameListSubController get() {
    return newInstance(appProvider.get(), gameProvider.get(), lobbyControllerProvider.get());
  }

  public static GameListSubController_Factory create(Provider<App> appProvider,
      Provider<Game> gameProvider, Provider<LobbyController> lobbyControllerProvider) {
    return new GameListSubController_Factory(appProvider, gameProvider, lobbyControllerProvider);
  }

  public static GameListSubController newInstance(App app, Game game,
      LobbyController lobbyController) {
    return new GameListSubController(app, game, lobbyController);
  }
}
