package pl.edu.agh.imageprocessing.data.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;

/**
 * Created by bwolcerz on 19.08.2017.
 */

@Dao
public interface OperationWithChainAndResourceDao {

    @Transaction
    @Query("SELECT * FROM Operation WHERE id =:operation_id")
    public List<OperationWithChainAndResource> loadOperation(int operation_id);

    @Transaction
    @Query("SELECT * FROM Operation ORDER BY creationDate DESC")
    public List<OperationWithChainAndResource> loadOperationSortedByDate();

    @Transaction
    @Query("SELECT * FROM Operation op WHERE op.operationType =:type " +
            "AND op.parentOperationId IS NULL AND op.nextOperationId is NULL")
    Flowable<List<OperationWithChainAndResource>> getUnchainedOperationsByType(ImageOperationType type);

    @Transaction
    @Query("SELECT * FROM Operation op WHERE op.parentOperationId =:parentId  OR op.id=:parentId" +
            " ORDER BY op.id")
    List<OperationWithChainAndResource> getChainOperationsSortedAsc(long parentId);

    @Transaction
    @Query("SELECT * FROM Operation op WHERE op.parentOperationId =:parentId  OR op.id=:parentId AND op.nextOperationId IS NULL")
    public Maybe<OperationWithChainAndResource> getLastOperationFromChain(long parentId);

}
