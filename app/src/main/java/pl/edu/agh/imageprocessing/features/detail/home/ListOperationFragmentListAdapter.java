package pl.edu.agh.imageprocessing.features.detail.home;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResource;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.databinding.ItemImageOperationListBinding;
import pl.edu.agh.imageprocessing.databinding.ItemRootOperationBinding;
import pl.edu.agh.imageprocessing.databinding.ListOperationsViewBinding;
import pl.edu.agh.imageprocessing.features.detail.android.BaseAdapter;
import pl.edu.agh.imageprocessing.features.detail.android.event.ExpandedOperationId;


public class ListOperationFragmentListAdapter extends BaseAdapter<ListOperationFragmentListAdapter.ListOperationViewHolder, Operation> {


    private final ListOperationFragmentListCallback operationsListCallback;
    private List<Operation> operations;

    public ListOperationFragmentListAdapter(@NonNull ListOperationFragmentListCallback listOperationFragmentListCallback) {
        operations = new ArrayList<>();
        this.operationsListCallback=listOperationFragmentListCallback;
    }

    @Override
    public void setData(List<Operation> operations) {
        this.operations=operations;
        notifyDataSetChanged();
    }

    @Override
    public ListOperationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return ListOperationViewHolder.create(LayoutInflater.from(viewGroup.getContext()), viewGroup, operationsListCallback);
    }

    @Override
    public void onBindViewHolder(ListOperationViewHolder viewHolder, int i) {
        if (null != operations && !operations.isEmpty() && null != operations.get(i)) {
            Operation operation = operations.get(i);
            if (null != operation)
                viewHolder.onBind(operation);
        }
    }

    @Override
    public int getItemCount() {
        if (null != operations && !operations.isEmpty() && null != operations.get(0)) {
            return operations.size();
        } else {
            return 0;
        }
    }

    static class ListOperationViewHolder extends RecyclerView.ViewHolder {

        private final ItemRootOperationBinding binding;

        public static ListOperationViewHolder create(LayoutInflater inflater, ViewGroup parent, ListOperationFragmentListCallback callback) {
            ItemRootOperationBinding itemRootOperationBinding = ItemRootOperationBinding.inflate(inflater, parent, false);
            return new ListOperationViewHolder(itemRootOperationBinding, callback);
        }


        public ListOperationViewHolder(ItemRootOperationBinding binding, ListOperationFragmentListCallback callback) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(view -> {
                callback.onImageOperationClicked(binding.getOperation(), null); //todo binding.imageViewCover
            });

        }

        public void onBind(Operation operation) {
            binding.setOperation(operation);
            binding.textview.setText(DateUtils.formatDateTime(binding.getRoot().getContext(),operation.getCreationDate().getTime(),DateUtils.FORMAT_ABBREV_ALL));


//            binding.executePendingBindings();
        }
    }

}
