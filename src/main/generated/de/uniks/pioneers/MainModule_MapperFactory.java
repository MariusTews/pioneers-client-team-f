package de.uniks.pioneers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class MainModule_MapperFactory implements Factory<ObjectMapper> {
  private final MainModule module;

  public MainModule_MapperFactory(MainModule module) {
    this.module = module;
  }

  @Override
  public ObjectMapper get() {
    return mapper(module);
  }

  public static MainModule_MapperFactory create(MainModule module) {
    return new MainModule_MapperFactory(module);
  }

  public static ObjectMapper mapper(MainModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.mapper());
  }
}
