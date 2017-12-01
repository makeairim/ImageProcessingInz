package pl.edu.agh.imageprocessing.data.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Maybe;
import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;

/**
 * Created by bwolcerz on 24.07.2017.
 */
@Dao
public interface ResourceDao {
    @Query("SELECT * FROM Resource")
    List<Resource> all();

    @Query("SELECT * FROM Resource WHERE id = :id")
    Maybe<Resource> get(long id);

    @Query("SELECT * FROM Resource WHERE operationId = :operationId AND type = :resourceType")
    Maybe<Resource> getByOperationAndType(long operationId, ResourceType resourceType);

    @Query("SELECT * FROM Resource WHERE operationId = :operationId AND type IN(:types)")
    List<Resource> getByOperationidAndTypes(long operationId, ResourceType[] types);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(Resource resource);

    @Update
    int update(Resource resource);

    @Delete
    int delete(Resource resource);

}
