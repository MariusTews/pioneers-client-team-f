package de.uniks.pioneers.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class GIDStorage_Factory implements Factory<GIDStorage> {
  @Override
  public GIDStorage get() {
    return newInstance();
  }

  public static GIDStorage_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GIDStorage newInstance() {
    return new GIDStorage();
  }

  private static final class InstanceHolder {
    private static final GIDStorage_Factory INSTANCE = new GIDStorage_Factory();
  }
}
