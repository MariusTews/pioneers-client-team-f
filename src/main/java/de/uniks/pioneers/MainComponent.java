package de.uniks.pioneers;

import dagger.BindsInstance;
import dagger.Component;
import de.uniks.pioneers.controller.LoginController;
import de.uniks.pioneers.controller.MapEditorController;

import javax.inject.Singleton;

@Component(modules = MainModule.class)
@Singleton
public interface MainComponent {
    //LoginController loginController();
    MapEditorController mapEditorController();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder mainapp(App app);

        MainComponent build();
    }
}
