package pl.edu.agh.imageprocessing.data.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import javax.annotation.Resource;

import pl.edu.agh.imageprocessing.data.local.entity.Chain;
import pl.edu.agh.imageprocessing.data.local.entity.ResourceFile;

/**
 * Created by bwolcerz on 24.07.2017.
 */
@Dao
public interface ResourceDao {
    @Query("SELECT * FROM ResourceFile")
    LiveData<List<ResourceFile>> loadFiles();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveResourceFile(ResourceFile resourceFile);

    
}
