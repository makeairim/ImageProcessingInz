package pl.edu.agh.imageprocessing.dagger;

import android.app.Activity;
import android.app.Application;
import android.app.Service;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import pl.edu.agh.imageprocessing.app.ImageProcessingApplication;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationService;

/**
 * Created by bwolcerz on 31.07.2017.
 */

@Singleton
@Component(modules = {ServiceModule.class,AppModule.class,ImageModule.class})
public interface ServiceComponent {
    void inject(ImageOperationService service);
}


