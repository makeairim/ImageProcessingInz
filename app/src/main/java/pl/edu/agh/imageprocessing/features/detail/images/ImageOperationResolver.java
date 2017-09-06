package pl.edu.agh.imageprocessing.features.detail.images;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Single;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.OperationStatus;
import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.ResourceDao;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;
import pl.edu.agh.imageprocessing.data.remote.OperationResourceAPIRepository;
import pl.edu.agh.imageprocessing.features.detail.images.operation.BasicOperation;
import pl.edu.agh.imageprocessing.features.detail.images.operation.BinarizationOperation;
import pl.edu.agh.imageprocessing.features.detail.images.operation.DilationOperation;
import pl.edu.agh.imageprocessing.features.detail.images.operation.ErosionOperation;
import pl.edu.agh.imageprocessing.features.detail.images.operation.FilterOperation;
import pl.edu.agh.imageprocessing.features.detail.images.operation.MeanFilterOperation;

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
    OperationDao operationDao;

    @Inject
    public ImageOperationResolver(Context context, FileTools fileTools, ResourceDao resourceDao, OperationResourceAPIRepository operationResourceAPIRepository, OperationDao operationDao) {
        this.fileTools = fileTools;
        this.context = context;
        this.resourceDao = resourceDao;
        this.operationResourceAPIRepository = operationResourceAPIRepository;
        this.operationDao = operationDao;
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
            case MEAN_FILTER:
                return new MeanFilterOperation(params,fileTools.getImageBitmap(context, params.getImageUri()));
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
            case MEAN_FILTER:
                result= mapMeanFilter(parameters);
                break;
            default:
                throw new AssertionError("resolver not provided for operation: " + type.name());
        }

        result.setImageUri(parameters.getImageUri());
        //result.setImageUri(Uri.parse(parameters.getBaseResource().getContent()));
        return result;
    }

    private ImageOperationParameter mapMeanFilter(ImageOperationResolverParameters parameters) {
        MeanFilterOperation.Parameters result=new MeanFilterOperation.Parameters();
        result.setSize(parameters.getMatrixWidth()); //todo set size from user ?
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
        Resource result = operationResourceAPIRepository.saveResource(ResourceType.IMAGE_FILE,
                fileUri.toString(), execute
                        .getParameter()
                        .getOperationId()).blockingSingle();
        operationDao.updateStatus(execute.getParameter().getOperationId(), OperationStatus.FINISHED);
        return result;
    }

}
