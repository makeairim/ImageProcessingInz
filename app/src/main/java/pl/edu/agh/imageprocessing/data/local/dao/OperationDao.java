package pl.edu.agh.imageprocessing.data.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;

/**
 * Created by bwolcerz on 24.07.2017.
 */
@Dao
public interface OperationDao {

    @Query("SELECT * FROM Operation")
    List<Operation> all();

    @Query("SELECT * FROM Operation WHERE parentOperationId is NULL")
    public List<Operation> chainRoots();

    @Query("SELECT * FROM Operation WHERE parentOperationId=:parentId")
    public List<Operation> chainByRoot(long parentId);

    @Query("SELECT * FROM Operation WHERE id = :id")
    public Flowable<Operation> get(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(Operation operation);

    @Delete
    int delete(Operation operation);

    @Update
    int update(Operation operation);

//    @Query("DELETE FROM Operation WHERE operationType =:type " +
//            "AND parentId Is NULL AND nextOperationId is NULL")
//    int deleteNotAssignedOperationsByType(String type);
//
//    @Query("SELECT * FROM Operation WHERE operationType =:type " +
//            "AND parentId Is NULL AND nextOperationId is NULL")
//    Flowable<Operation> getUnchainedOperationsByType(String type);
}
