package de.uniks.pioneers;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.rest.MessageApiService;
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
public final class MainModule_MessageApiServiceFactory implements Factory<MessageApiService> {
  private final MainModule module;

  private final Provider<Retrofit> retrofitProvider;

  public MainModule_MessageApiServiceFactory(MainModule module,
      Provider<Retrofit> retrofitProvider) {
    this.module = module;
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public MessageApiService get() {
    return messageApiService(module, retrofitProvider.get());
  }

  public static MainModule_MessageApiServiceFactory create(MainModule module,
      Provider<Retrofit> retrofitProvider) {
    return new MainModule_MessageApiServiceFactory(module, retrofitProvider);
  }

  public static MessageApiService messageApiService(MainModule instance, Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(instance.messageApiService(retrofit));
  }
}
