package pl.edu.agh.imageprocessing.data.local.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import pl.edu.agh.imageprocessing.data.local.OperationStatus;

/**
 * Created by bwolcerz on 22.07.2017.
 */

@Entity(tableName = "operation")
public class Operation implements Parcelable{
    @PrimaryKey(autoGenerate = true)
    @SerializedName("operation_id")
    private long id;

    @SerializedName("parent_operation_id")
    private Long parentOperationId;
    @SerializedName("next_operation_id")
    private Long nextOperationId;

    @SerializedName("creation_date")
    private Date creationDate;

    @SerializedName("operation_type")
    private String operationType;

    @SerializedName("status")
    private OperationStatus status;
    @SerializedName("parameters")
    private String object;

    public Operation() {
    }

    private Operation(Builder builder) {
        setId(builder.id);
        setParentOperationId(builder.parentOperationId);
        setNextOperationId(builder.nextOperationId);
        setCreationDate(builder.creationDate);
        setOperationType(builder.operationType);
        setStatus(builder.status);
        setObject(builder.object);
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
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

    public OperationStatus getStatus() {
        return status;
    }

    public void setStatus(OperationStatus status) {
        this.status = status;
    }

    public static final class Builder {
        private long id;
        private Long parentOperationId;
        private Long nextOperationId;
        private Date creationDate;
        private String operationType;
        private OperationStatus status;
        private String object;

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

        public Builder status(OperationStatus val) {
            status = val;
            return this;
        }

        public Builder object(String val) {
            object = val;
            return this;
        }

        public Operation build() {
            return new Operation(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeValue(this.parentOperationId);
        dest.writeValue(this.nextOperationId);
        dest.writeLong(this.creationDate != null ? this.creationDate.getTime() : -1);
        dest.writeString(this.operationType);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeString(this.object);
    }

    protected Operation(Parcel in) {
        this.id = in.readLong();
        this.parentOperationId = (Long) in.readValue(Long.class.getClassLoader());
        this.nextOperationId = (Long) in.readValue(Long.class.getClassLoader());
        long tmpCreationDate = in.readLong();
        this.creationDate = tmpCreationDate == -1 ? null : new Date(tmpCreationDate);
        this.operationType = in.readString();
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : OperationStatus.values()[tmpStatus];
        this.object = in.readString();
    }

    public static final Creator<Operation> CREATOR = new Creator<Operation>() {
        @Override
        public Operation createFromParcel(Parcel source) {
            return new Operation(source);
        }

        @Override
        public Operation[] newArray(int size) {
            return new Operation[size];
        }
    };
}
