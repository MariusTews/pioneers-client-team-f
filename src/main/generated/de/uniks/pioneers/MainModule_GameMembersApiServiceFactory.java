package de.uniks.pioneers;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.rest.GameMembersApiService;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class MainModule_GameMembersApiServiceFactory implements Factory<GameMembersApiService> {
  private final MainModule module;

  private final Provider<Retrofit> retrofitProvider;

  public MainModule_GameMembersApiServiceFactory(MainModule module,
      Provider<Retrofit> retrofitProvider) {
    this.module = module;
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public GameMembersApiService get() {
    return gameMembersApiService(module, retrofitProvider.get());
  }

  public static MainModule_GameMembersApiServiceFactory create(MainModule module,
      Provider<Retrofit> retrofitProvider) {
    return new MainModule_GameMembersApiServiceFactory(module, retrofitProvider);
  }

  public static GameMembersApiService gameMembersApiService(MainModule instance,
      Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(instance.gameMembersApiService(retrofit));
  }
}
