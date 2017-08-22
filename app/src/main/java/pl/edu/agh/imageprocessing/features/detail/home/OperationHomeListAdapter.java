package pl.edu.agh.imageprocessing.features.detail.home;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.imageprocessing.data.ImageOperationType;

import pl.edu.agh.imageprocessing.databinding.ItemImageOperationTypeListBinding;
import pl.edu.agh.imageprocessing.features.detail.android.BaseAdapter;


public class OperationHomeListAdapter extends BaseAdapter<OperationHomeListAdapter.OperationViewHolder, ImageOperationType> {

    private List<ImageOperationType> imageOperationTypes;

    private final OperationHomeListCallback operationsListCallback;

    public OperationHomeListAdapter(@NonNull OperationHomeListCallback operationsListCallback) {
        imageOperationTypes = new ArrayList<>();
        this.operationsListCallback = operationsListCallback;
    }

    @Override
    public void setData(List<ImageOperationType> articleEntities) {
        this.imageOperationTypes = articleEntities;
        notifyDataSetChanged();
    }

    @Override
    public OperationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return OperationViewHolder.create(LayoutInflater.from(viewGroup.getContext()), viewGroup, operationsListCallback);
    }

    @Override
    public void onBindViewHolder(OperationViewHolder viewHolder, int i) {
        if (null != imageOperationTypes && imageOperationTypes.size() > 0 && null != imageOperationTypes.get(i)) {
            ImageOperationType operationType = imageOperationTypes.get(i);
            if (null != operationType)
                viewHolder.onBind(operationType);
        }
    }

    @Override
    public int getItemCount() {
        if (null != imageOperationTypes && imageOperationTypes.size() > 0 && null != imageOperationTypes.get(0)) {
            return imageOperationTypes.size();
        } else {
            return 0;
        }
    }

    static class OperationViewHolder extends RecyclerView.ViewHolder {

        public static OperationViewHolder create(LayoutInflater inflater, ViewGroup parent, OperationHomeListCallback callback) {
            ItemImageOperationTypeListBinding itemOperationListBinding = ItemImageOperationTypeListBinding.inflate(inflater, parent, false);
            return new OperationViewHolder(itemOperationListBinding, callback);
        }

        ItemImageOperationTypeListBinding binding;

        public OperationViewHolder(ItemImageOperationTypeListBinding binding, OperationHomeListCallback callback) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v ->
                    callback.onImageOperationClicked(binding.getOperation(), null)); //todo binding.imageViewCover
        }

        public void onBind(ImageOperationType imageOperationType) {
            binding.setOperation(imageOperationType);
            binding.executePendingBindings();
        }
    }
}
