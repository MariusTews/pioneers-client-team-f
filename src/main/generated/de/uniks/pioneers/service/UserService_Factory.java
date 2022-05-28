package de.uniks.pioneers.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.rest.UserApiService;
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
public final class UserService_Factory implements Factory<UserService> {
  private final Provider<UserApiService> userApiServiceProvider;

  public UserService_Factory(Provider<UserApiService> userApiServiceProvider) {
    this.userApiServiceProvider = userApiServiceProvider;
  }

  @Override
  public UserService get() {
    return newInstance(userApiServiceProvider.get());
  }

  public static UserService_Factory create(Provider<UserApiService> userApiServiceProvider) {
    return new UserService_Factory(userApiServiceProvider);
  }

  public static UserService newInstance(UserApiService userApiService) {
    return new UserService(userApiService);
  }
}
