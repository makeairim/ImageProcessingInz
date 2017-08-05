package pl.edu.agh.imageprocessing.dagger;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bwolcerz on 31.07.2017.
 */

@Module
public class ActivityModule {
    private Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    Activity provideActivity(){
        return activity;
    }
}
