package pl.edu.agh.imageprocessing.data.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;

/**
 * Created by bwolcerz on 19.08.2017.
 */
@Dao
public interface OperationWithChainAndResourceDao {

    @Query("SELECT * FROM Operation WHERE id =:operation_id")
    public List<OperationWithChainAndResource> loadOperation(int operation_id);

    @Query("SELECT * FROM Operation ORDER BY creationDate DESC")
    public List<OperationWithChainAndResource> loadOperationSortedByDate();

    @Query("SELECT * FROM Operation op WHERE op.operationType =:type " +
            "AND op.parentOperationId Is NULL AND op.nextOperationId is NULL")
    Flowable<List<OperationWithChainAndResource>> getUnchainedOperationsByType(String type);
    @Query("SELECT * FROM Operation op WHERE op.parentOperationId =:parentId  OR op.id=:parentId" +
            " ORDER BY op.id")
    List<OperationWithChainAndResource> getChainOperationsSortedAsc(long parentId);

}
