package pl.edu.agh.imageprocessing.dagger;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.view.View;

import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.enums.EPickType;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Core;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.edu.agh.imageprocessing.data.local.ImageProcessingAPIDatabase;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResourceDao;
import pl.edu.agh.imageprocessing.data.local.dao.ResourceDao;
import pl.edu.agh.imageprocessing.features.detail.viemodel.ImageProcessingViewModelFactory;

/**
 * Created by bwolcerz on 20.07.2017.
 */

@Module
public class AppModule {


    @Provides
    @Singleton
    View.OnKeyListener getIt() {
        return (view, i, keyEvent) -> {
            return true;
        };
    }

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
    OperationWithChainAndResourceDao provideOperationWithChainAndResourceDao(ImageProcessingAPIDatabase imageProcessingAPIDatabase) {
        return imageProcessingAPIDatabase.operationWithChainAndResourceDao();
    }

    @Provides
    @Singleton
    ResourceDao provideResourceFileDao(ImageProcessingAPIDatabase imageProcessingAPIDatabase) {
        return imageProcessingAPIDatabase.resourceDao();
    }

    @Provides
    @Singleton
    PickSetup providePickPhotoSetup() {
        return new PickSetup().setTitle("title").setMaxSize(500).setPickTypes(EPickType.GALLERY, EPickType.CAMERA).setSystemDialog(false);
    }

    @Provides
    ViewModelProvider.Factory provideViewModelFactory(ImageProcessingViewModelFactory factory) {
        return factory;
    }

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

    @Provides
    @Singleton
    public BaseLoaderCallback provideOpenCvManagerCallBack(Application application) {
        return new BaseLoaderCallback(application) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
    }
//    @Provides
//    Context provideContext(){
//        return mApplication;
//    }

}
