package pl.edu.agh.imageprocessing.app;

import android.app.Activity;
import android.app.Application;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

import javax.inject.Inject;


import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import pl.edu.agh.imageprocessing.dagger.DaggerAppComponent;

/**
 * Created by bwolcerz on 20.07.2017.
 */

public class ImageProcessingApplication extends Application implements HasActivityInjector {
    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingInjector;
    @Inject
    BaseLoaderCallback baseLoaderCallback;
    @Override
    public void onCreate() {
        super.onCreate();
        initializeComponent();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0,this ,
                baseLoaderCallback);
    }
    private void initializeComponent() {
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityDispatchingInjector;
    }
}
