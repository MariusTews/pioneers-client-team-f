package de.uniks.pioneers;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.rest.AuthApiService;
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
public final class MainModule_AuthApiServiceFactory implements Factory<AuthApiService> {
  private final MainModule module;

  private final Provider<Retrofit> retrofitProvider;

  public MainModule_AuthApiServiceFactory(MainModule module, Provider<Retrofit> retrofitProvider) {
    this.module = module;
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public AuthApiService get() {
    return authApiService(module, retrofitProvider.get());
  }

  public static MainModule_AuthApiServiceFactory create(MainModule module,
      Provider<Retrofit> retrofitProvider) {
    return new MainModule_AuthApiServiceFactory(module, retrofitProvider);
  }

  public static AuthApiService authApiService(MainModule instance, Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(instance.authApiService(retrofit));
  }
}
