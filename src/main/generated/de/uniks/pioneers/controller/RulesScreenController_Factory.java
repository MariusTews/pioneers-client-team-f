package de.uniks.pioneers.controller;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.App;
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
public final class RulesScreenController_Factory implements Factory<RulesScreenController> {
  private final Provider<App> appProvider;

  private final Provider<LobbyController> lobbyControllerProvider;

  public RulesScreenController_Factory(Provider<App> appProvider,
      Provider<LobbyController> lobbyControllerProvider) {
    this.appProvider = appProvider;
    this.lobbyControllerProvider = lobbyControllerProvider;
  }

  @Override
  public RulesScreenController get() {
    return newInstance(appProvider.get(), lobbyControllerProvider);
  }

  public static RulesScreenController_Factory create(Provider<App> appProvider,
      Provider<LobbyController> lobbyControllerProvider) {
    return new RulesScreenController_Factory(appProvider, lobbyControllerProvider);
  }

  public static RulesScreenController newInstance(App app,
      Provider<LobbyController> lobbyController) {
    return new RulesScreenController(app, lobbyController);
  }
}
