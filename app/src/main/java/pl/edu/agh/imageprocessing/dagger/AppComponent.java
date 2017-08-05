package pl.edu.agh.imageprocessing.dagger;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Provides;
import pl.edu.agh.imageprocessing.app.ImageProcessingApplication;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Created by bwolcerz on 20.07.2017.
 */

@Singleton
@Component(modules = {
        AppModule.class,ImageModule.class,
        AndroidInjectionModule.class,
        ActivityBuilderModule.class})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }
    void inject(ImageProcessingApplication app);


}
