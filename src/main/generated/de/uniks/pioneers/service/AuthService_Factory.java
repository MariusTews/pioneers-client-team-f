package de.uniks.pioneers.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.rest.AuthApiService;
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
public final class AuthService_Factory implements Factory<AuthService> {
  private final Provider<AuthApiService> authApiServiceProvider;

  private final Provider<TokenStorage> tokenStorageProvider;

  private final Provider<IDStorage> idStorageProvider;

  public AuthService_Factory(Provider<AuthApiService> authApiServiceProvider,
      Provider<TokenStorage> tokenStorageProvider, Provider<IDStorage> idStorageProvider) {
    this.authApiServiceProvider = authApiServiceProvider;
    this.tokenStorageProvider = tokenStorageProvider;
    this.idStorageProvider = idStorageProvider;
  }

  @Override
  public AuthService get() {
    return newInstance(authApiServiceProvider.get(), tokenStorageProvider.get(), idStorageProvider.get());
  }

  public static AuthService_Factory create(Provider<AuthApiService> authApiServiceProvider,
      Provider<TokenStorage> tokenStorageProvider, Provider<IDStorage> idStorageProvider) {
    return new AuthService_Factory(authApiServiceProvider, tokenStorageProvider, idStorageProvider);
  }

  public static AuthService newInstance(AuthApiService authApiService, TokenStorage tokenStorage,
      IDStorage idStorage) {
    return new AuthService(authApiService, tokenStorage, idStorage);
  }
}
