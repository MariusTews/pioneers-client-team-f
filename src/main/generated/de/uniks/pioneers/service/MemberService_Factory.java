package de.uniks.pioneers.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.rest.GameMembersApiService;
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
public final class MemberService_Factory implements Factory<MemberService> {
  private final Provider<GameMembersApiService> gameMembersApiServiceProvider;

  private final Provider<GameIDStorage> gameIDStorageProvider;

  private final Provider<MemberIDStorage> memberIDStorageProvider;

  public MemberService_Factory(Provider<GameMembersApiService> gameMembersApiServiceProvider,
      Provider<GameIDStorage> gameIDStorageProvider,
      Provider<MemberIDStorage> memberIDStorageProvider) {
    this.gameMembersApiServiceProvider = gameMembersApiServiceProvider;
    this.gameIDStorageProvider = gameIDStorageProvider;
    this.memberIDStorageProvider = memberIDStorageProvider;
  }

  @Override
  public MemberService get() {
    return newInstance(gameMembersApiServiceProvider.get(), gameIDStorageProvider.get(), memberIDStorageProvider.get());
  }

  public static MemberService_Factory create(
      Provider<GameMembersApiService> gameMembersApiServiceProvider,
      Provider<GameIDStorage> gameIDStorageProvider,
      Provider<MemberIDStorage> memberIDStorageProvider) {
    return new MemberService_Factory(gameMembersApiServiceProvider, gameIDStorageProvider, memberIDStorageProvider);
  }

  public static MemberService newInstance(GameMembersApiService gameMembersApiService,
      GameIDStorage gameIDStorage, MemberIDStorage memberIDStorage) {
    return new MemberService(gameMembersApiService, gameIDStorage, memberIDStorage);
  }
}
