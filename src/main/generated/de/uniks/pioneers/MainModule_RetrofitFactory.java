package de.uniks.pioneers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;
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
public final class MainModule_RetrofitFactory implements Factory<Retrofit> {
  private final MainModule module;

  private final Provider<OkHttpClient> clientProvider;

  private final Provider<ObjectMapper> mapperProvider;

  public MainModule_RetrofitFactory(MainModule module, Provider<OkHttpClient> clientProvider,
      Provider<ObjectMapper> mapperProvider) {
    this.module = module;
    this.clientProvider = clientProvider;
    this.mapperProvider = mapperProvider;
  }

  @Override
  public Retrofit get() {
    return retrofit(module, clientProvider.get(), mapperProvider.get());
  }

  public static MainModule_RetrofitFactory create(MainModule module,
      Provider<OkHttpClient> clientProvider, Provider<ObjectMapper> mapperProvider) {
    return new MainModule_RetrofitFactory(module, clientProvider, mapperProvider);
  }

  public static Retrofit retrofit(MainModule instance, OkHttpClient client, ObjectMapper mapper) {
    return Preconditions.checkNotNullFromProvides(instance.retrofit(client, mapper));
  }
}
