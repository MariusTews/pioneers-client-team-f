package de.uniks.pioneers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.internal.DaggerGenerated;
import dagger.internal.DelegateFactory;
import dagger.internal.DoubleCheck;
import dagger.internal.InstanceFactory;
import dagger.internal.Preconditions;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.Websocket.EventListener_Factory;
import de.uniks.pioneers.controller.CreateGameController;
import de.uniks.pioneers.controller.CreateGameController_Factory;
import de.uniks.pioneers.controller.EditUserController;
import de.uniks.pioneers.controller.EditUserController_Factory;
import de.uniks.pioneers.controller.GameLobbyController;
import de.uniks.pioneers.controller.GameLobbyController_Factory;
import de.uniks.pioneers.controller.LobbyController;
import de.uniks.pioneers.controller.LobbyController_Factory;
import de.uniks.pioneers.controller.LoginController;
import de.uniks.pioneers.controller.LoginController_Factory;
import de.uniks.pioneers.controller.RulesScreenController;
import de.uniks.pioneers.controller.RulesScreenController_Factory;
import de.uniks.pioneers.controller.SignUpController;
import de.uniks.pioneers.controller.SignUpController_Factory;
import de.uniks.pioneers.rest.AuthApiService;
import de.uniks.pioneers.rest.GameMembersApiService;
import de.uniks.pioneers.rest.GamesApiService;
import de.uniks.pioneers.rest.GroupApiService;
import de.uniks.pioneers.rest.MessageApiService;
import de.uniks.pioneers.rest.UserApiService;
import de.uniks.pioneers.service.AuthService;
import de.uniks.pioneers.service.AuthService_Factory;
import de.uniks.pioneers.service.GameIDStorage;
import de.uniks.pioneers.service.GameIDStorage_Factory;
import de.uniks.pioneers.service.GameService;
import de.uniks.pioneers.service.GameService_Factory;
import de.uniks.pioneers.service.GroupService;
import de.uniks.pioneers.service.GroupService_Factory;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.IDStorage_Factory;
import de.uniks.pioneers.service.MemberIDStorage;
import de.uniks.pioneers.service.MemberIDStorage_Factory;
import de.uniks.pioneers.service.MemberService;
import de.uniks.pioneers.service.MemberService_Factory;
import de.uniks.pioneers.service.MessageService;
import de.uniks.pioneers.service.MessageService_Factory;
import de.uniks.pioneers.service.TokenStorage;
import de.uniks.pioneers.service.TokenStorage_Factory;
import de.uniks.pioneers.service.UserService;
import de.uniks.pioneers.service.UserService_Factory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class DaggerMainComponent {
  private DaggerMainComponent() {
  }

  public static MainComponent.Builder builder() {
    return new Builder();
  }

  private static final class Builder implements MainComponent.Builder {
    private App mainapp;

    @Override
    public Builder mainapp(App app) {
      this.mainapp = Preconditions.checkNotNull(app);
      return this;
    }

    @Override
    public MainComponent build() {
      Preconditions.checkBuilderRequirement(mainapp, App.class);
      return new MainComponentImpl(new MainModule(), mainapp);
    }
  }

  private static final class MainComponentImpl implements MainComponent {
    private final App mainapp;

    private final MainComponentImpl mainComponentImpl = this;

    private Provider<TokenStorage> tokenStorageProvider;

    private Provider<OkHttpClient> clientProvider;

    private Provider<ObjectMapper> mapperProvider;

    private Provider<Retrofit> retrofitProvider;

    private Provider<AuthApiService> authApiServiceProvider;

    private Provider<IDStorage> iDStorageProvider;

    private Provider<UserApiService> userApiServiceProvider;

    private Provider<App> mainappProvider;

    private Provider<AuthService> authServiceProvider;

    private Provider<UserService> userServiceProvider;

    private Provider<SignUpController> signUpControllerProvider;

    private Provider<GamesApiService> gamesApiServiceProvider;

    private Provider<GameIDStorage> gameIDStorageProvider;

    private Provider<MemberIDStorage> memberIDStorageProvider;

    private Provider<GameService> gameServiceProvider;

    private Provider<GroupApiService> groupApiServiceProvider;

    private Provider<GroupService> groupServiceProvider;

    private Provider<MessageApiService> messageApiServiceProvider;

    private Provider<MessageService> messageServiceProvider;

    private Provider<GameMembersApiService> gameMembersApiServiceProvider;

    private Provider<MemberService> memberServiceProvider;

    private Provider<EventListener> eventListenerProvider;

    private Provider<LoginController> loginControllerProvider;

    private Provider<LobbyController> lobbyControllerProvider;

    private Provider<RulesScreenController> rulesScreenControllerProvider;

    private Provider<GameLobbyController> gameLobbyControllerProvider;

    private Provider<CreateGameController> createGameControllerProvider;

    private Provider<EditUserController> editUserControllerProvider;

    private MainComponentImpl(MainModule mainModuleParam, App mainappParam) {
      this.mainapp = mainappParam;
      initialize(mainModuleParam, mainappParam);

    }

    private AuthService authService() {
      return new AuthService(authApiServiceProvider.get(), tokenStorageProvider.get(), iDStorageProvider.get());
    }

    private UserService userService() {
      return new UserService(userApiServiceProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final MainModule mainModuleParam, final App mainappParam) {
      this.tokenStorageProvider = DoubleCheck.provider(TokenStorage_Factory.create());
      this.clientProvider = DoubleCheck.provider(MainModule_ClientFactory.create(tokenStorageProvider));
      this.mapperProvider = DoubleCheck.provider(MainModule_MapperFactory.create(mainModuleParam));
      this.retrofitProvider = DoubleCheck.provider(MainModule_RetrofitFactory.create(mainModuleParam, clientProvider, mapperProvider));
      this.authApiServiceProvider = DoubleCheck.provider(MainModule_AuthApiServiceFactory.create(mainModuleParam, retrofitProvider));
      this.iDStorageProvider = DoubleCheck.provider(IDStorage_Factory.create());
      this.userApiServiceProvider = DoubleCheck.provider(MainModule_UserApiServiceFactory.create(mainModuleParam, retrofitProvider));
      this.mainappProvider = InstanceFactory.create(mainappParam);
      this.authServiceProvider = AuthService_Factory.create(authApiServiceProvider, tokenStorageProvider, iDStorageProvider);
      this.userServiceProvider = UserService_Factory.create(userApiServiceProvider);
      this.signUpControllerProvider = new DelegateFactory<>();
      this.gamesApiServiceProvider = DoubleCheck.provider(MainModule_GamesApiServiceFactory.create(mainModuleParam, retrofitProvider));
      this.gameIDStorageProvider = DoubleCheck.provider(GameIDStorage_Factory.create());
      this.memberIDStorageProvider = DoubleCheck.provider(MemberIDStorage_Factory.create());
      this.gameServiceProvider = GameService_Factory.create(gamesApiServiceProvider, gameIDStorageProvider, memberIDStorageProvider);
      this.groupApiServiceProvider = DoubleCheck.provider(MainModule_GroupApiServiceFactory.create(mainModuleParam, retrofitProvider));
      this.groupServiceProvider = GroupService_Factory.create(groupApiServiceProvider);
      this.messageApiServiceProvider = DoubleCheck.provider(MainModule_MessageApiServiceFactory.create(mainModuleParam, retrofitProvider));
      this.messageServiceProvider = MessageService_Factory.create(messageApiServiceProvider);
      this.gameMembersApiServiceProvider = DoubleCheck.provider(MainModule_GameMembersApiServiceFactory.create(mainModuleParam, retrofitProvider));
      this.memberServiceProvider = MemberService_Factory.create(gameMembersApiServiceProvider, gameIDStorageProvider, memberIDStorageProvider);
      this.eventListenerProvider = DoubleCheck.provider(EventListener_Factory.create(tokenStorageProvider, mapperProvider));
      this.loginControllerProvider = new DelegateFactory<>();
      this.lobbyControllerProvider = new DelegateFactory<>();
      this.rulesScreenControllerProvider = RulesScreenController_Factory.create(mainappProvider, lobbyControllerProvider);
      this.gameLobbyControllerProvider = GameLobbyController_Factory.create(mainappProvider, memberServiceProvider, userServiceProvider, messageServiceProvider, gameServiceProvider, lobbyControllerProvider, eventListenerProvider, iDStorageProvider, gameIDStorageProvider, memberIDStorageProvider);
      this.createGameControllerProvider = CreateGameController_Factory.create(mainappProvider, gameServiceProvider, lobbyControllerProvider, gameLobbyControllerProvider);
      this.editUserControllerProvider = EditUserController_Factory.create(mainappProvider, lobbyControllerProvider, loginControllerProvider, userServiceProvider, iDStorageProvider);
      DelegateFactory.setDelegate(lobbyControllerProvider, LobbyController_Factory.create(mainappProvider, iDStorageProvider, userServiceProvider, gameServiceProvider, groupServiceProvider, messageServiceProvider, authServiceProvider, memberServiceProvider, eventListenerProvider, loginControllerProvider, rulesScreenControllerProvider, createGameControllerProvider, editUserControllerProvider, gameLobbyControllerProvider));
      DelegateFactory.setDelegate(loginControllerProvider, LoginController_Factory.create(mainappProvider, authServiceProvider, userServiceProvider, signUpControllerProvider, lobbyControllerProvider));
      DelegateFactory.setDelegate(signUpControllerProvider, SignUpController_Factory.create(mainappProvider, loginControllerProvider, authServiceProvider, userServiceProvider));
    }

    @Override
    public LoginController loginController() {
      return new LoginController(mainapp, authService(), userService(), signUpControllerProvider, lobbyControllerProvider);
    }
  }
}
