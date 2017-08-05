package pl.edu.agh.imageprocessing.data.remote;

import android.arch.lifecycle.LiveData;
import android.net.Uri;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.ImageProcessingAPIDatabase;
import pl.edu.agh.imageprocessing.data.local.dao.ChainDao;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.ResourceDao;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.local.entity.Chain;
import pl.edu.agh.imageprocessing.data.local.entity.ResourceFile;

/**
 * Created by bwolcerz on 24.07.2017.
 */

public class ImageProcessingAPIRepository {
    private final ImageProcessingAPIDatabase imageProcessingAPIDatabase;
    private final OperationDao operationDao;
    private final ChainDao chainDao;
    private final ResourceDao resourceDao;
    @Inject
    public ImageProcessingAPIRepository(ImageProcessingAPIDatabase imageProcessingAPIDatabase, OperationDao operationDao, ChainDao chainDao, ResourceDao resourceDao) {
        this.imageProcessingAPIDatabase = imageProcessingAPIDatabase;
        this.operationDao = operationDao;
        this.chainDao = chainDao;
        this.resourceDao = resourceDao;
    }
//    public LiveData<Resource<List<Operation>>> loadOperationChains(){
//        return new Resource<List<Operation>>(new );
//    }
    public LiveData<List<Operation>> getOperations(){
        return operationDao.loadOperations();
    }
    public LiveData<List<Chain>> getOperationChains(){
        return chainDao.loadOperationChains();
    }
    public boolean saveResource(Uri uri){
        ResourceFile resource = new ResourceFile();
        resource.setCreationDate(String.valueOf(System.currentTimeMillis()));
        resource.setFileUri(uri.toString());
        resourceDao.saveResourceFile(resource);
        return true;
    }
    public Observable<Resource<List<ImageOperationType>>> getImageOperationTypes(){
        ImageOperationType type1=ImageOperationType.DILATION;
        ImageOperationType type2=ImageOperationType.BINARIZATION;

        LinkedList<ImageOperationType> types = new LinkedList<>();
        types.add(type1);
        types.add(type2);
        Resource<List<ImageOperationType>> res1 = new Resource<>(types);
        return Observable.just(res1);
    }

}
