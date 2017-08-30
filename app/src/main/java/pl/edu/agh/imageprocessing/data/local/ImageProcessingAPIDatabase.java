package pl.edu.agh.imageprocessing.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import pl.edu.agh.imageprocessing.data.local.converter.Converters;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResourceDao;
import pl.edu.agh.imageprocessing.data.local.dao.ResourceDao;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;

/**
 * Created by bwolcerz on 24.07.2017.
 */

@Database(entities = {Operation.class, Resource.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class ImageProcessingAPIDatabase extends RoomDatabase{
    public abstract OperationDao operationDao();
    public abstract ResourceDao resourceDao();
    public abstract OperationWithChainAndResourceDao operationWithChainAndResourceDao();

}
