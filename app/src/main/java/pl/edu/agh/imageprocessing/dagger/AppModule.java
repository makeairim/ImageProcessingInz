package pl.edu.agh.imageprocessing.dagger;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.enums.EPickType;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.engine.OpenCVEngineInterface;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.edu.agh.imageprocessing.data.local.ImageProcessingAPIDatabase;
import pl.edu.agh.imageprocessing.data.local.dao.ChainDao;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.ResourceDao;
import pl.edu.agh.imageprocessing.features.detail.viemodel.ImageProcessingViewModelFactory;

/**
 * Created by bwolcerz on 20.07.2017.
 */

@Module
public class AppModule {


    @Provides
    @Singleton
    View.OnKeyListener getIt(){
     return (view, i, keyEvent) -> {return true;};}

    @Provides
    @Singleton
    ImageProcessingAPIDatabase provideNewsAPIDatabase(Application application) {
        return Room.databaseBuilder(application, ImageProcessingAPIDatabase.class, "imageprocessingapi.db").build();
    }
    @Provides
    @Singleton
    OperationDao provideOperationDao(ImageProcessingAPIDatabase imageProcessingAPIDatabase) {
        return imageProcessingAPIDatabase.operationDao();
    }
    @Provides
    @Singleton
    ChainDao provideOperationChainDao(ImageProcessingAPIDatabase imageProcessingAPIDatabase) {
        return imageProcessingAPIDatabase.operationChainDao();
    }
    @Provides
    @Singleton
    ResourceDao provideResourceFileDao(ImageProcessingAPIDatabase imageProcessingAPIDatabase) {
        return imageProcessingAPIDatabase.resourceDao();
    }
    @Provides
    @Singleton
    PickSetup providePickPhotoSetup(){
        return new PickSetup().setTitle("title").setMaxSize(500).setPickTypes(EPickType.GALLERY,EPickType.CAMERA).setSystemDialog(false);
    }

    @Provides
    ViewModelProvider.Factory provideViewModelFactory(ImageProcessingViewModelFactory factory){
        return factory;
    }

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }
    @Provides
    @Singleton
    public BaseLoaderCallback provideOpenCvManagerCallBack(Application application){
        return new BaseLoaderCallback(application) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                        //DO YOUR WORK/STUFF HERE
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
    }
    @Provides
    @Singleton
    JobManager provideJobManager(){
        Configuration.Builder builder = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";
                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }

                    @Override
                    public void v(String text, Object... args) {

                    }
                })
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120);//wait 2 minute
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.scheduler(FrameworkJobSchedulerService.createSchedulerFor(this,
                    MyJobService.class), true);
        } else {
            int enableGcm = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
            if (enableGcm == ConnectionResult.SUCCESS) {
                builder.scheduler(GcmJobSchedulerService.createSchedulerFor(this,
                        MyGcmJobService.class), true);
            }
        }
        jobManager = new JobManager(builder.build());
    }
//    @Provides
//    Context provideContext(){
//        return mApplication;
//    }

}
