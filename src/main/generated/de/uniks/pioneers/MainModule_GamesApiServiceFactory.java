package de.uniks.pioneers;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.rest.GamesApiService;
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
public final class MainModule_GamesApiServiceFactory implements Factory<GamesApiService> {
  private final MainModule module;

  private final Provider<Retrofit> retrofitProvider;

  public MainModule_GamesApiServiceFactory(MainModule module, Provider<Retrofit> retrofitProvider) {
    this.module = module;
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public GamesApiService get() {
    return gamesApiService(module, retrofitProvider.get());
  }

  public static MainModule_GamesApiServiceFactory create(MainModule module,
      Provider<Retrofit> retrofitProvider) {
    return new MainModule_GamesApiServiceFactory(module, retrofitProvider);
  }

  public static GamesApiService gamesApiService(MainModule instance, Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(instance.gamesApiService(retrofit));
  }
}
