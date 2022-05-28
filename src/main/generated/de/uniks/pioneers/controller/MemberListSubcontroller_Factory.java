package de.uniks.pioneers.controller;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.service.UserService;
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
public final class MemberListSubcontroller_Factory implements Factory<MemberListSubcontroller> {
  private final Provider<App> appProvider;

  private final Provider<Member> memberProvider;

  private final Provider<UserService> userServiceProvider;

  public MemberListSubcontroller_Factory(Provider<App> appProvider, Provider<Member> memberProvider,
      Provider<UserService> userServiceProvider) {
    this.appProvider = appProvider;
    this.memberProvider = memberProvider;
    this.userServiceProvider = userServiceProvider;
  }

  @Override
  public MemberListSubcontroller get() {
    return newInstance(appProvider.get(), memberProvider.get(), userServiceProvider.get());
  }

  public static MemberListSubcontroller_Factory create(Provider<App> appProvider,
      Provider<Member> memberProvider, Provider<UserService> userServiceProvider) {
    return new MemberListSubcontroller_Factory(appProvider, memberProvider, userServiceProvider);
  }

  public static MemberListSubcontroller newInstance(App app, Member member,
      UserService userService) {
    return new MemberListSubcontroller(app, member, userService);
  }
}
