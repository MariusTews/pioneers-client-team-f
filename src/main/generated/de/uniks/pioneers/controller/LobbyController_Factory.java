package de.uniks.pioneers.controller;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.App;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.service.AuthService;
import de.uniks.pioneers.service.GameService;
import de.uniks.pioneers.service.GroupService;
import de.uniks.pioneers.service.IDStorage;
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
public final class LobbyController_Factory implements Factory<LobbyController> {
  private final Provider<App> appProvider;

  private final Provider<IDStorage> idStorageProvider;

  private final Provider<UserService> userServiceProvider;

  private final Provider<GameService> gameServiceProvider;

  private final Provider<GroupService> groupServiceProvider;

  private final Provider<MessageService> messageServiceProvider;

  private final Provider<AuthService> authServiceProvider;

  private final Provider<MemberService> memberServiceProvider;

  private final Provider<EventListener> eventListenerProvider;

  private final Provider<LoginController> loginControllerProvider;

  private final Provider<RulesScreenController> rulesScreenControllerProvider;

  private final Provider<CreateGameController> createGameControllerProvider;

  private final Provider<EditUserController> editUserControllerProvider;

  private final Provider<GameLobbyController> gameLobbyControllerProvider;

  public LobbyController_Factory(Provider<App> appProvider, Provider<IDStorage> idStorageProvider,
      Provider<UserService> userServiceProvider, Provider<GameService> gameServiceProvider,
      Provider<GroupService> groupServiceProvider, Provider<MessageService> messageServiceProvider,
      Provider<AuthService> authServiceProvider, Provider<MemberService> memberServiceProvider,
      Provider<EventListener> eventListenerProvider,
      Provider<LoginController> loginControllerProvider,
      Provider<RulesScreenController> rulesScreenControllerProvider,
      Provider<CreateGameController> createGameControllerProvider,
      Provider<EditUserController> editUserControllerProvider,
      Provider<GameLobbyController> gameLobbyControllerProvider) {
    this.appProvider = appProvider;
    this.idStorageProvider = idStorageProvider;
    this.userServiceProvider = userServiceProvider;
    this.gameServiceProvider = gameServiceProvider;
    this.groupServiceProvider = groupServiceProvider;
    this.messageServiceProvider = messageServiceProvider;
    this.authServiceProvider = authServiceProvider;
    this.memberServiceProvider = memberServiceProvider;
    this.eventListenerProvider = eventListenerProvider;
    this.loginControllerProvider = loginControllerProvider;
    this.rulesScreenControllerProvider = rulesScreenControllerProvider;
    this.createGameControllerProvider = createGameControllerProvider;
    this.editUserControllerProvider = editUserControllerProvider;
    this.gameLobbyControllerProvider = gameLobbyControllerProvider;
  }

  @Override
  public LobbyController get() {
    return newInstance(appProvider.get(), idStorageProvider.get(), userServiceProvider.get(), gameServiceProvider.get(), groupServiceProvider.get(), messageServiceProvider.get(), authServiceProvider.get(), memberServiceProvider.get(), eventListenerProvider.get(), loginControllerProvider, rulesScreenControllerProvider, createGameControllerProvider, editUserControllerProvider, gameLobbyControllerProvider);
  }

  public static LobbyController_Factory create(Provider<App> appProvider,
      Provider<IDStorage> idStorageProvider, Provider<UserService> userServiceProvider,
      Provider<GameService> gameServiceProvider, Provider<GroupService> groupServiceProvider,
      Provider<MessageService> messageServiceProvider, Provider<AuthService> authServiceProvider,
      Provider<MemberService> memberServiceProvider, Provider<EventListener> eventListenerProvider,
      Provider<LoginController> loginControllerProvider,
      Provider<RulesScreenController> rulesScreenControllerProvider,
      Provider<CreateGameController> createGameControllerProvider,
      Provider<EditUserController> editUserControllerProvider,
      Provider<GameLobbyController> gameLobbyControllerProvider) {
    return new LobbyController_Factory(appProvider, idStorageProvider, userServiceProvider, gameServiceProvider, groupServiceProvider, messageServiceProvider, authServiceProvider, memberServiceProvider, eventListenerProvider, loginControllerProvider, rulesScreenControllerProvider, createGameControllerProvider, editUserControllerProvider, gameLobbyControllerProvider);
  }

  public static LobbyController newInstance(App app, IDStorage idStorage, UserService userService,
      GameService gameService, GroupService groupService, MessageService messageService,
      AuthService authService, MemberService memberService, EventListener eventListener,
      Provider<LoginController> loginController,
      Provider<RulesScreenController> rulesScreenController,
      Provider<CreateGameController> createGameController,
      Provider<EditUserController> editUserController,
      Provider<GameLobbyController> gameLobbyController) {
    return new LobbyController(app, idStorage, userService, gameService, groupService, messageService, authService, memberService, eventListener, loginController, rulesScreenController, createGameController, editUserController, gameLobbyController);
  }
}
