


package pl.edu.agh.imageprocessing.features.detail.viemodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.util.ArrayMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.edu.agh.imageprocessing.dagger.ViewModelSubComponent;

@Singleton
public class ImageProcessingViewModelFactory implements ViewModelProvider.Factory {

//    private final HashMap<Class, Callable<? extends ViewModel>> creators;
    private HomeViewModel homeViewModel;
    private ImageOperationViewModel imageOperationViewModel;
    @Inject
    public ImageProcessingViewModelFactory(HomeViewModel model,ImageOperationViewModel imageOperationViewModel) {
//        creators = new HashMap<>();
//        creators.put(HomeViewModel.class, );
        this.homeViewModel=model;
        this.imageOperationViewModel=imageOperationViewModel;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) homeViewModel;
        }
        if (modelClass.isAssignableFrom(ImageOperationViewModel.class)) {
            return (T) imageOperationViewModel;
        }
        throw new IllegalArgumentException("Unknown class name");
//        Callable<? extends ViewModel> creator = creators.get(modelClass);
//        if (creator == null) {
//            for (Map.Entry<Class, Callable<? extends ViewModel>> entry : creators.entrySet()) {
//                if (modelClass.isAssignableFrom(entry.getKey())) {
//                    creator = entry.getValue();
//                    break;
//                }
//            }
//        }
//        if (creator == null) {
//            throw new IllegalArgumentException("unknown model class " + modelClass);
//        }
//        try {
//            return (T) creator.call();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }
}
