package de.uniks.pioneers;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.rest.UserApiService;
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
public final class MainModule_UserApiServiceFactory implements Factory<UserApiService> {
  private final MainModule module;

  private final Provider<Retrofit> retrofitProvider;

  public MainModule_UserApiServiceFactory(MainModule module, Provider<Retrofit> retrofitProvider) {
    this.module = module;
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public UserApiService get() {
    return userApiService(module, retrofitProvider.get());
  }

  public static MainModule_UserApiServiceFactory create(MainModule module,
      Provider<Retrofit> retrofitProvider) {
    return new MainModule_UserApiServiceFactory(module, retrofitProvider);
  }

  public static UserApiService userApiService(MainModule instance, Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(instance.userApiService(retrofit));
  }
}
