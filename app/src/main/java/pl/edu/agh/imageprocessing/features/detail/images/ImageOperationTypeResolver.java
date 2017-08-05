package pl.edu.agh.imageprocessing.features.detail.images;

import android.util.Log;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.features.detail.images.operation.BasicOperation;
import pl.edu.agh.imageprocessing.features.detail.images.operation.BinarizationOperation;
import pl.edu.agh.imageprocessing.features.detail.images.operation.DilationOperation;

/**
 * Created by bwolcerz on 03.08.2017.
 */

public class ImageOperationTypeResolver {
    public static final String TAG=ImageOperationTypeResolver.class.getSimpleName();
    public BasicOperation resolveOperation(ImageOperationType type){
        Log.i(TAG, "resolveOperation: "+type.name());
        switch(type){
            case BINARIZATION:
                return new BinarizationOperation();
            case FILTER:
                break;
            case CONVOLUTION:
                break;
            case DILATION:
                return new DilationOperation();
            default:
                throw new AssertionError("resolver not provided for operation: "+type.name());
        }
        return null;
    }

}
