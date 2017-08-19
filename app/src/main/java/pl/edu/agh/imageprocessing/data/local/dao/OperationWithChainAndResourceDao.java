package pl.edu.agh.imageprocessing.data.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by bwolcerz on 19.08.2017.
 */
@Dao
public interface OperationWithChainAndResourceDao {

    @Query("SELECT * FROM Operation WHERE id =:operation_id")
    public List<OperationWithChainAndResource> loadOperation(int operation_id);

    @Query("SELECT * FROM Operation ORDER BY creationDate DESC")
    public List<OperationWithChainAndResource> loadOperationSortedByDate();

}
