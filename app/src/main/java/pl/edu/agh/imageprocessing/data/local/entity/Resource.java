package pl.edu.agh.imageprocessing.data.local.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

/**
 * Created by bwolcerz on 25.07.2017.
 */
@Entity(tableName = "resource")
public class Resource implements Parcelable{
    @PrimaryKey(autoGenerate = true)
    @SerializedName("resource_id")
    private long id;

    @SerializedName("operation_id")
    private Long operationId;

    @SerializedName("creation_date")
    private Date creationDate;

    @SerializedName("content")
    private String content;

    @SerializedName("type")
    private String type;

    public Resource() {
    }

    private Resource(Builder builder) {
        setId(builder.id);
        setOperationId(builder.operationId);
        setCreationDate(builder.creationDate);
        setContent(builder.content);
        setType(builder.type);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public static final class Builder {
        private long id;
        private Long operationId;
        private Date creationDate;
        private String content;
        private String type;

        public Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder operationId(Long val) {
            operationId = val;
            return this;
        }

        public Builder creationDate(Date val) {
            creationDate = val;
            return this;
        }

        public Builder content(String val) {
            content = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Resource build() {
            return new Resource(this);
        }
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", operationId=" + operationId +
                ", creationDate=" + creationDate +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeValue(this.operationId);
        dest.writeLong(this.creationDate != null ? this.creationDate.getTime() : -1);
        dest.writeString(this.content);
        dest.writeString(this.type);
    }

    protected Resource(Parcel in) {
        this.id = in.readLong();
        this.operationId = (Long) in.readValue(Long.class.getClassLoader());
        long tmpCreationDate = in.readLong();
        this.creationDate = tmpCreationDate == -1 ? null : new Date(tmpCreationDate);
        this.content = in.readString();
        this.type = in.readString();
    }

    public static final Creator<Resource> CREATOR = new Creator<Resource>() {
        @Override
        public Resource createFromParcel(Parcel source) {
            return new Resource(source);
        }

        @Override
        public Resource[] newArray(int size) {
            return new Resource[size];
        }
    };
}
