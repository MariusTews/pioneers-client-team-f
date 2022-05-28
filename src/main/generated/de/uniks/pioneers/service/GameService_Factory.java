package de.uniks.pioneers.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.rest.GamesApiService;
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
public final class GameService_Factory implements Factory<GameService> {
  private final Provider<GamesApiService> gamesApiServiceProvider;

  private final Provider<GameIDStorage> gameIDStorageProvider;

  private final Provider<MemberIDStorage> memberIDStorageProvider;

  public GameService_Factory(Provider<GamesApiService> gamesApiServiceProvider,
      Provider<GameIDStorage> gameIDStorageProvider,
      Provider<MemberIDStorage> memberIDStorageProvider) {
    this.gamesApiServiceProvider = gamesApiServiceProvider;
    this.gameIDStorageProvider = gameIDStorageProvider;
    this.memberIDStorageProvider = memberIDStorageProvider;
  }

  @Override
  public GameService get() {
    return newInstance(gamesApiServiceProvider.get(), gameIDStorageProvider.get(), memberIDStorageProvider.get());
  }

  public static GameService_Factory create(Provider<GamesApiService> gamesApiServiceProvider,
      Provider<GameIDStorage> gameIDStorageProvider,
      Provider<MemberIDStorage> memberIDStorageProvider) {
    return new GameService_Factory(gamesApiServiceProvider, gameIDStorageProvider, memberIDStorageProvider);
  }

  public static GameService newInstance(GamesApiService gamesApiService,
      GameIDStorage gameIDStorage, MemberIDStorage memberIDStorage) {
    return new GameService(gamesApiService, gameIDStorage, memberIDStorage);
  }
}
