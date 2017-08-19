package pl.edu.agh.imageprocessing.data.remote;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.ImageProcessingAPIDatabase;
import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.ResourceDao;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;

/**
 * Created by bwolcerz on 24.07.2017.
 */

public class OperationResourceAPIRepository {
    private final ImageProcessingAPIDatabase imageProcessingAPIDatabase;
    private final OperationDao operationDao;
    private final ResourceDao resourceDao;

    @Inject
    public OperationResourceAPIRepository(ImageProcessingAPIDatabase imageProcessingAPIDatabase, OperationDao operationDao, ResourceDao resourceDao) {
        this.imageProcessingAPIDatabase = imageProcessingAPIDatabase;
        this.operationDao = operationDao;
        this.resourceDao = resourceDao;
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

    public void chainOperations(Operation parent, Operation child) {
        parent.setNextOperationId(child.getId());
        child.setParentOperationId(parent.getParentOperationId() != null ? parent.getParentOperationId() : parent.getId());
        operationDao.update(parent);
        operationDao.update(child);
    }

    public Operation createOperation(){
        return new Operation.Builder().creationDate(new Date(System.currentTimeMillis())).build();
    }

}
