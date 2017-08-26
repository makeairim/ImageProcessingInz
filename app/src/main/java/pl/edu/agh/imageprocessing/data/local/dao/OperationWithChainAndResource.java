package pl.edu.agh.imageprocessing.data.local.dao;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;

/**
 * Created by bwolcerz on 19.08.2017.
 */
@BindingMethods({
@BindingMethod(type = ImageView.class,
        attribute = "android:url",
        method = "getImageFile")})
public class OperationWithChainAndResource implements Parcelable {
    @Embedded
    private Operation operation;

    @Relation(parentColumn = "id", entityColumn = "operationId", entity = Resource.class)
    private List<Resource> resource;

    public OperationWithChainAndResource() {
    }

    private OperationWithChainAndResource(Builder builder) {
        setOperation(builder.operation);
        setResource(builder.resource);
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public List<Resource> getResource() {
        return resource;
    }

    public void setResource(List<Resource> resource) {
        this.resource = resource;
    }


    public static final class Builder {
        private Operation operation;
        private List<Resource> resource;

        public Builder() {
        }

        public Builder operation(Operation val) {
            operation = val;
            return this;
        }

        public Builder resource(List<Resource> val) {
            resource = val;
            return this;
        }

        public OperationWithChainAndResource build() {
            return new OperationWithChainAndResource(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.operation, flags);
        dest.writeTypedList(this.resource);
    }

    protected OperationWithChainAndResource(Parcel in) {
        this.operation = in.readParcelable(Operation.class.getClassLoader());
        this.resource = in.createTypedArrayList(Resource.CREATOR);
    }

    public static final Creator<OperationWithChainAndResource> CREATOR = new Creator<OperationWithChainAndResource>() {
        @Override
        public OperationWithChainAndResource createFromParcel(Parcel source) {
            return new OperationWithChainAndResource(source);
        }

        @Override
        public OperationWithChainAndResource[] newArray(int size) {
            return new OperationWithChainAndResource[size];
        }
    };

    public String getImageFile() {
        if (resource != null && resource.size() > 0) {
            for (int i = 0; i < resource.size(); i++) {
                if (ResourceType.IMAGE_FILE.equals(ResourceType.valueOf(resource.get(i).getType()))) {
                    return resource.get(0).getContent();
                }
            }
        }
        return null;
    }
}
