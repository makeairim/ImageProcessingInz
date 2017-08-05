package pl.edu.agh.imageprocessing.dagger;

import android.app.Activity;

import dagger.Component;

/**
 * Created by bwolcerz on 31.07.2017.
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules=ActivityModule.class)
public interface ActivityComponent {
    Activity activity();
}
