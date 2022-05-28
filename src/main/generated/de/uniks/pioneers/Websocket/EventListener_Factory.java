package de.uniks.pioneers.Websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.service.TokenStorage;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class EventListener_Factory implements Factory<EventListener> {
  private final Provider<TokenStorage> tokenStorageProvider;

  private final Provider<ObjectMapper> mapperProvider;

  public EventListener_Factory(Provider<TokenStorage> tokenStorageProvider,
      Provider<ObjectMapper> mapperProvider) {
    this.tokenStorageProvider = tokenStorageProvider;
    this.mapperProvider = mapperProvider;
  }

  @Override
  public EventListener get() {
    return newInstance(tokenStorageProvider.get(), mapperProvider.get());
  }

  public static EventListener_Factory create(Provider<TokenStorage> tokenStorageProvider,
      Provider<ObjectMapper> mapperProvider) {
    return new EventListener_Factory(tokenStorageProvider, mapperProvider);
  }

  public static EventListener newInstance(TokenStorage tokenStorage, ObjectMapper mapper) {
    return new EventListener(tokenStorage, mapper);
  }
}
