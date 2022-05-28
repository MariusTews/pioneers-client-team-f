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
public final class IDStorage_Factory implements Factory<IDStorage> {
  @Override
  public IDStorage get() {
    return newInstance();
  }

  public static IDStorage_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static IDStorage newInstance() {
    return new IDStorage();
  }

  private static final class InstanceHolder {
    private static final IDStorage_Factory INSTANCE = new IDStorage_Factory();
  }
}
