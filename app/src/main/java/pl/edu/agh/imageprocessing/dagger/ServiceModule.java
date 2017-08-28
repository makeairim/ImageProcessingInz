package pl.edu.agh.imageprocessing.dagger;

import android.app.Service;

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
    Service provideService() {
        return service;
    }
}
