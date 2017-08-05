package pl.edu.agh.imageprocessing.dagger;

import pl.edu.agh.imageprocessing.BaseActivity;
import pl.edu.agh.imageprocessing.features.detail.home.HomeActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by bwolcerz on 20.07.2017.
 */

@Module
public abstract class ActivityBuilderModule {
    @ContributesAndroidInjector
    abstract BaseActivity baseActivity();
    @ContributesAndroidInjector
    abstract HomeActivity homeActivity();
}
