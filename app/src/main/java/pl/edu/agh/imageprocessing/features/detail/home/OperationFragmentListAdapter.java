package pl.edu.agh.imageprocessing.features.detail.home;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResource;
import pl.edu.agh.imageprocessing.databinding.ItemImageOperationListBinding;
import pl.edu.agh.imageprocessing.features.detail.android.BaseAdapter;
import pl.edu.agh.imageprocessing.features.detail.android.event.ExpandedOperationId;


public class OperationFragmentListAdapter extends BaseAdapter<OperationFragmentListAdapter.OperationViewHolder, OperationWithChainAndResource> {


    private final OperationFragmentListCallback operationsListCallback;
    private List<OperationWithChainAndResource> operationWithChainAndResources;

    public OperationFragmentListAdapter(@NonNull OperationFragmentListCallback operationsListCallback) {
        operationWithChainAndResources = new ArrayList<>();
        this.operationsListCallback = operationsListCallback;
    }

    @Override
    public void setData(List<OperationWithChainAndResource> operationWithChainAndResourceList) {
        this.operationWithChainAndResources = operationWithChainAndResourceList;
        notifyDataSetChanged();
    }

    @Override
    public OperationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return OperationViewHolder.create(LayoutInflater.from(viewGroup.getContext()), viewGroup, operationsListCallback);
    }

    @Override
    public void onBindViewHolder(OperationViewHolder viewHolder, int i) {
        if (null != operationWithChainAndResources && !operationWithChainAndResources.isEmpty() && null != operationWithChainAndResources.get(i)) {
            OperationWithChainAndResource operationWithChainAndResource = operationWithChainAndResources.get(i);
            if (null != operationWithChainAndResource)
                viewHolder.onBind(operationWithChainAndResource);
        }
    }

    @Override
    public int getItemCount() {
        if (null != operationWithChainAndResources && !operationWithChainAndResources.isEmpty() && null != operationWithChainAndResources.get(0)) {
            return operationWithChainAndResources.size();
        } else {
            return 0;
        }
    }

    static class OperationViewHolder extends RecyclerView.ViewHolder {

        private final ItemImageOperationListBinding binding;

        public static OperationViewHolder create(LayoutInflater inflater, ViewGroup parent, OperationFragmentListCallback callback) {
            ItemImageOperationListBinding itemOperationListBinding = ItemImageOperationListBinding.inflate(inflater, parent, false);
            return new OperationViewHolder(itemOperationListBinding, callback);
        }


        public OperationViewHolder(ItemImageOperationListBinding binding, OperationFragmentListCallback callback) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v ->
                    callback.onImageOperationClicked(binding.getResource(), null)); //todo binding.imageViewCover
        }

        public void onBind(OperationWithChainAndResource operationWithChainAndResource) {
//            binding.setResource(operationWithChainAndResource);
            binding.tvHeader.setText(operationWithChainAndResource.getOperation().getOperationType());
            binding.wrapper.setOnClickListener(view -> {
                if (binding.expandLayout.isCollapsed()) {
                    EventBus.getDefault().post(new ExpandedOperationId(operationWithChainAndResource.getOperation().getId()));
                    binding.expandLayout.expand();
                }
                else{
                    EventBus.getDefault().post(new ExpandedOperationId(null));
                    binding.expandLayout.collapse();
                }
            });
            binding.tvDescription.setText(DateUtils.formatDateTime(binding.getRoot().getContext(), operationWithChainAndResource.getOperation().getCreationDate().getTime(), DateUtils.FORMAT_SHOW_DATE| DateUtils.FORMAT_SHOW_TIME));
            if(operationWithChainAndResource.getImageFile()!=null && !operationWithChainAndResource.getImageFile().isEmpty()) {
                binding.ivPreview.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                Glide.with(binding.ivPreview.getContext())
                        .load(Uri.parse(operationWithChainAndResource.getImageFile())).apply(RequestOptions.placeholderOf(R.drawable.placeholder)).apply(RequestOptions.centerCropTransform())
                        .into(binding.ivPreview);
                Glide.with(binding.ivPhoto.getContext())
                        .load(Uri.parse(operationWithChainAndResource.getImageFile())).apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                        .into(binding.ivPhoto);
            }else{
                binding.ivPreview.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
            }
//            binding.executePendingBindings();
        }
    }

}
