package pl.edu.agh.imageprocessing.features.detail.images;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.dao.ResourceDao;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;
import pl.edu.agh.imageprocessing.data.remote.OperationResourceAPIRepository;
import pl.edu.agh.imageprocessing.features.detail.images.operation.BasicOperation;
import pl.edu.agh.imageprocessing.features.detail.images.operation.BinarizationOperation;
import pl.edu.agh.imageprocessing.features.detail.images.operation.DilationOperation;
import pl.edu.agh.imageprocessing.features.detail.images.operation.ErosionOperation;
import pl.edu.agh.imageprocessing.features.detail.images.operation.FilterOperation;
import pl.edu.agh.imageprocessing.features.detail.viemodel.ImageOperationViewModel;

/**
 * Created by bwolcerz on 03.08.2017.
 */

public class ImageOperationResolver {
    public static final String TAG = ImageOperationResolver.class.getSimpleName();
    @Inject
    FileTools fileTools;
    @Inject
    Context context;
    @Inject
    ResourceDao resourceDao;
    @Inject
    OperationResourceAPIRepository operationResourceAPIRepository;

    @Inject
    public ImageOperationResolver(Context context, FileTools fileTools, ResourceDao resourceDao, OperationResourceAPIRepository operationResourceAPIRepository) {
        this.fileTools = fileTools;
        this.context = context;
        this.resourceDao = resourceDao;
        this.operationResourceAPIRepository = operationResourceAPIRepository;
    }

    public BasicOperation resolveOperation(ImageOperationType type, ImageOperationResolverParameters parameters, long processingOperationId) throws IOException {
        Log.i(TAG, "resolveOperation: " + type.name());
        ImageOperationParameter params = imageOperationParameterResolver(type, parameters);
        params.setOperationId(processingOperationId);
        switch (type) {
            case BINARIZATION:
                return new BinarizationOperation(params, fileTools.getImageBitmap(context, params.getImageUri()));
            case DILATION:
                return new DilationOperation(params, fileTools.getImageBitmap(context, params.getImageUri()));
            case EROSION:
                return new ErosionOperation(params, fileTools.getImageBitmap(context, params.getImageUri()));
            case FILTER:
                return new FilterOperation(params, fileTools.getImageBitmap(context, params.getImageUri()));
            default:
                throw new AssertionError("resolver not provided for operation: " + type.name());
        }
    }

    private ImageOperationParameter imageOperationParameterResolver(ImageOperationType type, ImageOperationResolverParameters parameters) throws IOException {
        ImageOperationParameter result = null;
        switch (type) {
            case BINARIZATION:
                result = mapBinarizationParameter(parameters);
                break;
            case DILATION:
                result = mapDilationParameter(parameters);
                break;
            case EROSION:
                result = mapErosionParameter(parameters);
                break;
            case FILTER:
                result = mapFilterParameter(parameters);
                break;
            default:
                throw new AssertionError("resolver not provided for operation: " + type.name());
        }
//        List<Resource> resources = resourceDao.getByOperationAndType(parameters.getOperationId(), ResourceType.IMAGE_FILE.name());
//        if (resources.size() != 1) { //todo to delete
//            Log.e(TAG, "imageOperationParameterResolver: " + "Could not be more than 1 or less than 0: " + resources.size());
//            throw new AssertionError("Could not be more than 1 or less than 0: " + resources.size());
//        }
        result.setImageUri(parameters.getImageUri());
        //result.setImageUri(Uri.parse(parameters.getBaseResource().getContent()));
        return result;
    }

    private ImageOperationParameter mapFilterParameter(ImageOperationResolverParameters parameters) {
        FilterOperation.Parameters result = new FilterOperation.Parameters();
        result.setHeight(parameters.getMatrixHeight());
        result.setWidth(parameters.getMatrixWidth());
        result.setMatrix(parameters.getMatrix());
        return result;
    }

    private ImageOperationParameter mapErosionParameter(ImageOperationResolverParameters parameters) {
        ErosionOperation.Parameters result;
        result = new ErosionOperation.Parameters();
        result.setStructElementHeight(parameters.getMorphologyElementType());
        result.setStructElementHeight(parameters.getMorphologyHeight());
        result.setStructElementWidth(parameters.getMorphologyWidth());
        return result;
    }

    private BinarizationOperation.Parameters mapBinarizationParameter(ImageOperationResolverParameters parameters) {
        BinarizationOperation.Parameters result;
        result = new BinarizationOperation.Parameters();
        result.setThreshold(parameters.getThreshold());
        return result;
    }

    private DilationOperation.Parameters mapDilationParameter(ImageOperationResolverParameters parameters) {
        DilationOperation.Parameters result;
        result = new DilationOperation.Parameters();
        result.setStructElementHeight(parameters.getMorphologyElementType());
        result.setStructElementHeight(parameters.getMorphologyHeight());
        result.setStructElementWidth(parameters.getMorphologyWidth());
        return result;
    }

    public Resource processResult(BasicOperation execute) {
        Uri fileUri = fileTools.saveFile(execute.getBitmap());
        List<Resource> resId = resourceDao.getByOperationAndType(execute.getParameter().getOperationId(), ResourceType.IMAGE_FILE.name());
        if (resId.size() != 0) {
            if (resId.size() > 1) throw new AssertionError();
            resId.get(0).setContent(fileUri.toString());
            resourceDao.update(resId.get(0));
            return resId.get(0);
        }
        return operationResourceAPIRepository.saveResource(ResourceType.IMAGE_FILE,
                fileUri.toString(), execute
                .getParameter()
                .getOperationId()).blockingSingle();
//        resourceDao.save(new Resource.Builder()
//                .operationId(execute
//                        .getParameter()
//                        .getOperationId())
//                .type(ResourceType.IMAGE_FILE_RESULT.name())
//                .creationDate(new Date(System.currentTimeMillis()))
//                .content(fileUri.toString()).build());
    }

}
