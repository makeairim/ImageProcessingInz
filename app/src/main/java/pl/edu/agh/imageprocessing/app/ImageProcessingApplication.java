package pl.edu.agh.imageprocessing.app;

import android.app.Activity;
import android.app.Application;
import android.arch.persistence.room.RoomDatabase;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;

import javax.inject.Inject;


import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import pl.edu.agh.imageprocessing.dagger.DaggerAppComponent;
import pl.edu.agh.imageprocessing.data.local.ImageProcessingAPIDatabase;
import pl.edu.agh.imageprocessing.features.detail.android.event.TriggerServiceWorkEvent;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationService;

/**
 * Created by bwolcerz on 20.07.2017.
 */

public class ImageProcessingApplication extends Application implements HasActivityInjector {
    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingInjector;
    @Inject
    BaseLoaderCallback baseLoaderCallback;
    @Inject
    ImageProcessingAPIDatabase imageProcessingAPIDatabase;
    /** Messenger for communicating with the service. */
    Messenger mService = null;
    /** Flag indicating whether we have called bind on the service. */
    boolean mBound;
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }
    };

    public ImageProcessingAPIDatabase getImageProcessingAPIDatabase() {
        return imageProcessingAPIDatabase;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeComponent();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0,this ,
                baseLoaderCallback);
        Intent intent = new Intent(this, ImageOperationService.class);
        startService(intent);
        EventBus.getDefault().register(this);
    }

    private void initializeComponent() {
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);
    }

    public void bindService(){
        bindService(new Intent(this, ImageOperationService.class), mConnection,
                Context.BIND_AUTO_CREATE);

    }

    public void unbindService(){
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityDispatchingInjector;
    }

    @Subscribe
    public void triggerServiceWork(TriggerServiceWorkEvent event) {
        if (!mBound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, ImageOperationService.MSG_CHECK_NEW_OPERATION, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
