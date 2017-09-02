package pl.edu.agh.imageprocessing.features.detail.home;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.databinding.ItemRootOperationBinding;
import pl.edu.agh.imageprocessing.features.detail.android.BaseAdapter;


public class ListOperationFragmentListAdapter extends BaseAdapter<ListOperationFragmentListAdapter.ListOperationViewHolder, Operation> {


    private final ListOperationFragmentListCallback operationsListCallback;
    private final ObtainImageFileForOperationCallback obtainImageFileForOperationCallback;
    private final RegisterDisposableCallback registerDisposableCallback;
    private List<Operation> operations;


    public ListOperationFragmentListAdapter(@NonNull ListOperationFragmentListCallback listOperationFragmentListCallback,
                                            ObtainImageFileForOperationCallback obtainImageFileForOperationCallback, RegisterDisposableCallback registerDisposableCallback
    ) {
        operations = new ArrayList<>();
        this.operationsListCallback = listOperationFragmentListCallback;
        this.obtainImageFileForOperationCallback = obtainImageFileForOperationCallback;
        this.registerDisposableCallback = registerDisposableCallback;
    }

    @Override
    public void setData(List<Operation> operations) {
        this.operations = operations;
        notifyDataSetChanged();
    }

    @Override
    public ListOperationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return ListOperationViewHolder.create(LayoutInflater.from(viewGroup.getContext()), viewGroup, operationsListCallback,
                obtainImageFileForOperationCallback, registerDisposableCallback);
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
        private final ObtainImageFileForOperationCallback obtainResourceCallback;
        private final RegisterDisposableCallback registerDisposableCallback;

        public static ListOperationViewHolder create(LayoutInflater inflater, ViewGroup parent,
                                                     ListOperationFragmentListCallback callback,
                                                     ObtainImageFileForOperationCallback obtainImageFileForOperationCallback,
                                                     RegisterDisposableCallback registerDisposableCallback) {
            ItemRootOperationBinding itemRootOperationBinding = ItemRootOperationBinding.inflate(inflater, parent, false);
            return new ListOperationViewHolder(itemRootOperationBinding,
                    callback, obtainImageFileForOperationCallback,
                    registerDisposableCallback);
        }


        public ListOperationViewHolder(ItemRootOperationBinding binding, ListOperationFragmentListCallback callback,
                                       ObtainImageFileForOperationCallback obtainImageFileForOperationCallback,
                                       RegisterDisposableCallback registerDisposableCallback) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(view -> {
                callback.onImageOperationClicked(binding.getOperation(), null); //todo binding.imageViewCover
            });
            this.obtainResourceCallback = obtainImageFileForOperationCallback;
            this.registerDisposableCallback = registerDisposableCallback;
        }

        public void onBind(Operation operation) {
            binding.setOperation(operation);
            binding.tvHeader.setText(DateUtils.formatDateTime(binding.getRoot().getContext(), operation.getCreationDate().getTime(), DateUtils.FORMAT_ABBREV_ALL));
            binding.tvAdditional.setText(DateUtils.formatDateTime(binding.getRoot().getContext(), operation.getCreationDate().getTime(),DateUtils.FORMAT_SHOW_TIME|DateUtils.FORMAT_NO_NOON));
            registerDisposableCallback.registerDisposableCallback(obtainResourceCallback.obtainOperationResourceImageFile(operation.getId()).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(resource ->
                            Glide.with(binding.ivPreview.getContext()).load(Uri.parse(resource.getContent()))
                                    .apply(RequestOptions.centerCropTransform()).into(binding.ivPreview)
                    ));

            //todo comment ? tasks done ?
//            binding.executePendingBindings();
        }
    }

}
