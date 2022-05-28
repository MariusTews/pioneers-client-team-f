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
public final class SignUpController_Factory implements Factory<SignUpController> {
  private final Provider<App> appProvider;

  private final Provider<LoginController> loginControllerProvider;

  private final Provider<AuthService> authServiceProvider;

  private final Provider<UserService> userServiceProvider;

  public SignUpController_Factory(Provider<App> appProvider,
      Provider<LoginController> loginControllerProvider, Provider<AuthService> authServiceProvider,
      Provider<UserService> userServiceProvider) {
    this.appProvider = appProvider;
    this.loginControllerProvider = loginControllerProvider;
    this.authServiceProvider = authServiceProvider;
    this.userServiceProvider = userServiceProvider;
  }

  @Override
  public SignUpController get() {
    return newInstance(appProvider.get(), loginControllerProvider, authServiceProvider.get(), userServiceProvider.get());
  }

  public static SignUpController_Factory create(Provider<App> appProvider,
      Provider<LoginController> loginControllerProvider, Provider<AuthService> authServiceProvider,
      Provider<UserService> userServiceProvider) {
    return new SignUpController_Factory(appProvider, loginControllerProvider, authServiceProvider, userServiceProvider);
  }

  public static SignUpController newInstance(App app, Provider<LoginController> loginController,
      AuthService authService, UserService userService) {
    return new SignUpController(app, loginController, authService, userService);
  }
}
