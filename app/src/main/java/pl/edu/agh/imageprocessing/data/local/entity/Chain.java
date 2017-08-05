package pl.edu.agh.imageprocessing.data.local.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by bwolcerz on 22.07.2017.
 */

@Entity(tableName = "Chain")
public class Chain {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("chain_id")
    private long id;

    @SerializedName("creation_date")
    private String creationDate;

    @SerializedName("chain_name")
    private String chainName;

    public Chain() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }
}
