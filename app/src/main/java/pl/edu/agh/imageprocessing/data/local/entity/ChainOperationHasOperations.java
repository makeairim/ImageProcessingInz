package pl.edu.agh.imageprocessing.data.local.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bwolcerz on 22.07.2017.
 */
@Entity(tableName = "chain_has_operations")
public class ChainOperationHasOperations {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("chain_has_operations_id")
    private long id;

    @SerializedName("chain_id")
    private long chainId;

    @SerializedName("parent_operation_id")
    private long parentOperationId;

    @SerializedName("child_operation_id")
    private long childOperationId;

    public ChainOperationHasOperations() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChainId() {
        return chainId;
    }

    public void setChainId(long chainId) {
        this.chainId = chainId;
    }

    public long getParentOperationId() {
        return parentOperationId;
    }

    public void setParentOperationId(long parentOperationId) {
        this.parentOperationId = parentOperationId;
    }

    public long getChildOperationId() {
        return childOperationId;
    }

    public void setChildOperationId(long childOperationId) {
        this.childOperationId = childOperationId;
    }
}
