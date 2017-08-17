package pl.edu.agh.imageprocessing.dagger;

import android.app.Activity;
import android.content.Context;

import dagger.Component;
import pl.edu.agh.imageprocessing.features.detail.images.FileTools;

/**
 * Created by bwolcerz on 31.07.2017.
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules=ActivityModule.class)
public interface ActivityComponent {
    Activity activity();
}
