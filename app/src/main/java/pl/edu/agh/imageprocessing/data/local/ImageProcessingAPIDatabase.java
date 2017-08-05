package pl.edu.agh.imageprocessing.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import pl.edu.agh.imageprocessing.data.local.dao.ChainDao;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.ResourceDao;
import pl.edu.agh.imageprocessing.data.local.entity.ChainOperationHasOperations;
import pl.edu.agh.imageprocessing.data.local.entity.Chain;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.local.entity.ResourceFile;

/**
 * Created by bwolcerz on 24.07.2017.
 */

@Database(entities = {Operation.class,Chain.class, ChainOperationHasOperations.class, ResourceFile.class}, version = 1)
public abstract class ImageProcessingAPIDatabase extends RoomDatabase{
    public abstract OperationDao operationDao();
    public abstract ChainDao operationChainDao();
    public abstract ResourceDao resourceDao();

}
