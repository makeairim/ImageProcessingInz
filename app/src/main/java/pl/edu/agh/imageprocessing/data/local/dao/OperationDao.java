package pl.edu.agh.imageprocessing.data.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.media.VolumeShaper;

import java.util.List;

import pl.edu.agh.imageprocessing.data.local.entity.Operation;

/**
 * Created by bwolcerz on 24.07.2017.
 */
@Dao
public interface OperationDao {
    @Query("SELECT * FROM Operation")
    LiveData<List<Operation>> loadOperations();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveOperation(Operation operation);


}
