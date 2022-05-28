package de.uniks.pioneers;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.service.TokenStorage;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
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
public final class MainModule_ClientFactory implements Factory<OkHttpClient> {
  private final Provider<TokenStorage> tokenStorageProvider;

  public MainModule_ClientFactory(Provider<TokenStorage> tokenStorageProvider) {
    this.tokenStorageProvider = tokenStorageProvider;
  }

  @Override
  public OkHttpClient get() {
    return client(tokenStorageProvider.get());
  }

  public static MainModule_ClientFactory create(Provider<TokenStorage> tokenStorageProvider) {
    return new MainModule_ClientFactory(tokenStorageProvider);
  }

  public static OkHttpClient client(TokenStorage tokenStorage) {
    return Preconditions.checkNotNullFromProvides(MainModule.client(tokenStorage));
  }
}
