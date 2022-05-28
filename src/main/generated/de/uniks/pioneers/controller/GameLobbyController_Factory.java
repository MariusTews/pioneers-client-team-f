package de.uniks.pioneers.controller;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.App;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.service.GameIDStorage;
import de.uniks.pioneers.service.GameService;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.MemberIDStorage;
import de.uniks.pioneers.service.MemberService;
import de.uniks.pioneers.service.MessageService;
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
public final class GameLobbyController_Factory implements Factory<GameLobbyController> {
  private final Provider<App> appProvider;

  private final Provider<MemberService> memberServiceProvider;

  private final Provider<UserService> userServiceProvider;

  private final Provider<MessageService> messageServiceProvider;

  private final Provider<GameService> gameServiceProvider;

  private final Provider<LobbyController> lobbyControllerProvider;

  private final Provider<EventListener> eventListenerProvider;

  private final Provider<IDStorage> idStorageProvider;

  private final Provider<GameIDStorage> gameIDStorageProvider;

  private final Provider<MemberIDStorage> memberIDStorageProvider;

  public GameLobbyController_Factory(Provider<App> appProvider,
      Provider<MemberService> memberServiceProvider, Provider<UserService> userServiceProvider,
      Provider<MessageService> messageServiceProvider, Provider<GameService> gameServiceProvider,
      Provider<LobbyController> lobbyControllerProvider,
      Provider<EventListener> eventListenerProvider, Provider<IDStorage> idStorageProvider,
      Provider<GameIDStorage> gameIDStorageProvider,
      Provider<MemberIDStorage> memberIDStorageProvider) {
    this.appProvider = appProvider;
    this.memberServiceProvider = memberServiceProvider;
    this.userServiceProvider = userServiceProvider;
    this.messageServiceProvider = messageServiceProvider;
    this.gameServiceProvider = gameServiceProvider;
    this.lobbyControllerProvider = lobbyControllerProvider;
    this.eventListenerProvider = eventListenerProvider;
    this.idStorageProvider = idStorageProvider;
    this.gameIDStorageProvider = gameIDStorageProvider;
    this.memberIDStorageProvider = memberIDStorageProvider;
  }

  @Override
  public GameLobbyController get() {
    return newInstance(appProvider.get(), memberServiceProvider.get(), userServiceProvider.get(), messageServiceProvider.get(), gameServiceProvider.get(), lobbyControllerProvider, eventListenerProvider.get(), idStorageProvider.get(), gameIDStorageProvider.get(), memberIDStorageProvider.get());
  }

  public static GameLobbyController_Factory create(Provider<App> appProvider,
      Provider<MemberService> memberServiceProvider, Provider<UserService> userServiceProvider,
      Provider<MessageService> messageServiceProvider, Provider<GameService> gameServiceProvider,
      Provider<LobbyController> lobbyControllerProvider,
      Provider<EventListener> eventListenerProvider, Provider<IDStorage> idStorageProvider,
      Provider<GameIDStorage> gameIDStorageProvider,
      Provider<MemberIDStorage> memberIDStorageProvider) {
    return new GameLobbyController_Factory(appProvider, memberServiceProvider, userServiceProvider, messageServiceProvider, gameServiceProvider, lobbyControllerProvider, eventListenerProvider, idStorageProvider, gameIDStorageProvider, memberIDStorageProvider);
  }

  public static GameLobbyController newInstance(App app, MemberService memberService,
      UserService userService, MessageService messageService, GameService gameService,
      Provider<LobbyController> lobbyController, EventListener eventListener, IDStorage idStorage,
      GameIDStorage gameIDStorage, MemberIDStorage memberIDStorage) {
    return new GameLobbyController(app, memberService, userService, messageService, gameService, lobbyController, eventListener, idStorage, gameIDStorage, memberIDStorage);
  }
}
