package pl.edu.agh.imageprocessing.data.local.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bwolcerz on 25.07.2017.
 */
@Entity(tableName = "ResourceFile")
public class ResourceFile {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("resource_id")
    private long id;

    @SerializedName("creation_date")
    private String creationDate;

    @SerializedName("file_uri")
    private String fileUri;

    public ResourceFile() {
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

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    @Override
    public String toString() {
        return "ResourceFile{" +
                "id=" + id +
                ", creationDate='" + creationDate + '\'' +
                ", fileUri='" + fileUri + '\'' +
                '}';
    }
}
