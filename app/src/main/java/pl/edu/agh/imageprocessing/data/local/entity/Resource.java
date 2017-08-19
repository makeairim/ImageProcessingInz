package pl.edu.agh.imageprocessing.data.local.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by bwolcerz on 25.07.2017.
 */
@Entity(tableName = "resource")
public class Resource {
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
}
