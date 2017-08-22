package pl.edu.agh.imageprocessing.data.remote;

import android.net.Uri;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.ImageProcessingAPIDatabase;
import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResource;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResourceDao;
import pl.edu.agh.imageprocessing.data.local.dao.ResourceDao;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.features.detail.images.FileTools;

/**
 * Created by bwolcerz on 24.07.2017.
 */

public class OperationResourceAPIRepository {
    private final ImageProcessingAPIDatabase imageProcessingAPIDatabase;
    private final OperationDao operationDao;
    private final ResourceDao resourceDao;
    private final OperationWithChainAndResourceDao operationWithChainAndResourceDao;
    private final FileTools fileTools;
    @Inject
    public OperationResourceAPIRepository(ImageProcessingAPIDatabase imageProcessingAPIDatabase, OperationDao operationDao, ResourceDao resourceDao, OperationWithChainAndResourceDao operationWithChainAndResourceDao, FileTools fileTools) {
        this.imageProcessingAPIDatabase = imageProcessingAPIDatabase;
        this.operationDao = operationDao;
        this.resourceDao = resourceDao;
        this.operationWithChainAndResourceDao = operationWithChainAndResourceDao;
        this.fileTools = fileTools;
    }

    public List<Operation> getOperations() {
        return operationDao.all();
    }

    public Observable<pl.edu.agh.imageprocessing.data.local.entity.Resource> saveResource(ResourceType type, String content, Long operationId) {
        pl.edu.agh.imageprocessing.data.local.entity.Resource res = new pl.edu.agh.imageprocessing.data.local.entity.Resource.Builder()
                .content(content)
                .type(type.name())
                .creationDate(new Date(System.currentTimeMillis()))
                .operationId(operationId)
                .build();
        res.setId(resourceDao.save(res));
        return Observable.just(res);
    }

    public Observable<pl.edu.agh.imageprocessing.data.remote.Resource> getImageOperationTypes() {
        pl.edu.agh.imageprocessing.data.remote.Resource res1 = new pl.edu.agh.imageprocessing.data.remote.Resource(Arrays.asList(ImageOperationType.values()));
        return Observable.just(res1);
    }

    public boolean chainOperations(Operation parent, Operation child) {
        parent.setNextOperationId(child.getId());
        child.setParentOperationId(parent.getParentOperationId() != null ? parent.getParentOperationId() : parent.getId());
        operationDao.update(parent);
        operationDao.update(child);
        return true;
    }

    public Operation createOperation(){
        return new Operation.Builder().creationDate(new Date(System.currentTimeMillis())).build();
    }

    public Disposable deleteUnchainedOperations(){
        return operationWithChainAndResourceDao.getUnchainedOperationsByType(ImageOperationType.BINARIZATION.name())
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation()).subscribe(operationWithChainAndResources -> {
            for (OperationWithChainAndResource operationWithChainAndResource : operationWithChainAndResources) {
                for (pl.edu.agh.imageprocessing.data.local.entity.Resource resource : operationWithChainAndResource.getResource()) {
                    fileTools.deleteFile(Uri.parse(resource.getContent()));
                    resourceDao.delete(resource);
                }
                operationDao.delete(operationWithChainAndResource.getOperation());
            }
        });
    }
}
