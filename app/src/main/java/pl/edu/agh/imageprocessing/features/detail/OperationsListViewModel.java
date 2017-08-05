package pl.edu.agh.imageprocessing.features.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.bumptech.glide.load.engine.Resource;

import java.util.List;

import javax.inject.Inject;

import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.remote.ImageProcessingAPIRepository;

/**
 * Created by bwolcerz on 24.07.2017.
 */

public class OperationsListViewModel extends ViewModel {
    private ImageProcessingAPIRepository imageProcessingAPIRepository;
    @Inject
    public OperationsListViewModel(ImageProcessingAPIRepository imageProcessingAPIRepository) {
        this.imageProcessingAPIRepository = imageProcessingAPIRepository;
    }
    public LiveData<List<Operation>> getOperations(){
        LiveData<List<Operation>> operations = imageProcessingAPIRepository.getOperations();
        return operations;
    }
}
