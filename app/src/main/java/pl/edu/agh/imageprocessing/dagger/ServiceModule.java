package pl.edu.agh.imageprocessing.dagger;

import android.app.Service;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bwolcerz on 28.08.2017.
 */
@Module
public class ServiceModule {
    private Service service;

    public ServiceModule(Service service) {
        this.service = service;
    }

    @Provides
    @Singleton
    Service provideService() {
        return service;
    }
}
