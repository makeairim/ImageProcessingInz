package pl.edu.agh.imageprocessing.data.local.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bwolcerz on 22.07.2017.
 */

@Entity(tableName = "Operation")
public class Operation {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("operation_id")
    private long id;

    //TODO mapping, many to many
    @SerializedName("operation_chain_id")
    private long operationChainId;

    @SerializedName("creation_date")
    private String creationDate;

    //TODO store enums like intdef and write type Convertet ?  or store enum like string
    @SerializedName("operation_type")
    private String operationType;

    @SerializedName("photo_path")
    private String photoPath;

    public Operation() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOperationChainId() {
        return operationChainId;
    }

    public void setOperationChainId(long operationChainId) {
        this.operationChainId = operationChainId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "id=" + id +
                ", operationChainId=" + operationChainId +
                ", creationDate='" + creationDate + '\'' +
                ", operationType='" + operationType + '\'' +
                ", photoPath='" + photoPath + '\'' +
                '}';
    }
}
