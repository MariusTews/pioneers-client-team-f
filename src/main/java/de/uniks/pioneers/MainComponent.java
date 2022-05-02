package de.uniks.pioneers;

import dagger.BindsInstance;
import dagger.Component;
import de.uniks.pioneers.controller.LoginController;

import javax.inject.Singleton;

@Component(modules = MainModule.class)
@Singleton
public interface MainComponent {
    LoginController loginController();

    @Component.Builder
    interface Builder
    {
        @BindsInstance
        Builder mainapp(App app);
        MainComponent build();
    }
}
