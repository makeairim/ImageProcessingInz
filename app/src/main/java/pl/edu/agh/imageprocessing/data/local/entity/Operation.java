package pl.edu.agh.imageprocessing.data.local.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by bwolcerz on 22.07.2017.
 */

@Entity(tableName = "operation")
public class Operation {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("operation_id")
    private long id;

    //TODO mapping, many to many
    @SerializedName("parent_operation_id")
    private Long parentOperationId;
    @SerializedName("next_operation_id")
    private Long nextOperationId;

    @SerializedName("creation_date")
    private Date creationDate;

    //TODO store enums like intdef and write type Convertet ?  or store enum like string
    @SerializedName("operation_type")
    private String operationType;



    public Operation() {
    }

    private Operation(Builder builder) {
        setId(builder.id);
        setParentOperationId(builder.parentOperationId);
        setNextOperationId(builder.nextOperationId);
        setCreationDate(builder.creationDate);
        setOperationType(builder.operationType);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getParentOperationId() {
        return parentOperationId;
    }

    public void setParentOperationId(Long parentOperationId) {
        this.parentOperationId = parentOperationId;
    }

    public Long getNextOperationId() {
        return nextOperationId;
    }

    public void setNextOperationId(Long nextOperationId) {
        this.nextOperationId = nextOperationId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public static final class Builder {
        private long id;
        private Long parentOperationId;
        private Long nextOperationId;
        private Date creationDate;
        private String operationType;

        public Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder parentOperationId(Long val) {
            parentOperationId = val;
            return this;
        }

        public Builder nextOperationId(Long val) {
            nextOperationId = val;
            return this;
        }

        public Builder creationDate(Date val) {
            creationDate = val;
            return this;
        }

        public Builder operationType(String val) {
            operationType = val;
            return this;
        }

        public Operation build() {
            return new Operation(this);
        }
    }
}
