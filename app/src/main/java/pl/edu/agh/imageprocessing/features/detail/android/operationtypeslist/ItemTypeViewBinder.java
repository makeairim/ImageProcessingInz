package pl.edu.agh.imageprocessing.features.detail.android.operationtypeslist;

import android.view.View;
import android.widget.TextView;

import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.features.detail.home.OperationHomeListCallback;
import pl.edu.agh.imageprocessing.features.detail.home.OperationInfoCallback;
import tellh.com.stickyheaderview_rv.adapter.StickyHeaderViewAdapter;
import tellh.com.stickyheaderview_rv.adapter.ViewBinder;

/**
 * Created by bwolcerz on 03.09.2017.
 */

public class ItemTypeViewBinder extends ViewBinder<GroupOperationModel, ItemTypeViewBinder.ViewHolder> {
    private final OperationHomeListCallback operationChoosecallback;
    private final OperationInfoCallback operationInformationCallback;

    public ItemTypeViewBinder(OperationHomeListCallback operationChoosecallback, OperationInfoCallback operationInformationCallback) {
        super();
        this.operationChoosecallback = operationChoosecallback;
        this.operationInformationCallback=operationInformationCallback;
    }

    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }
    @Override
    public void bindView(StickyHeaderViewAdapter adapter, ViewHolder holder, int position, GroupOperationModel entity) {
        holder.tvDesc.setText(entity.getType().getTitle());
        holder.tvDesc.setOnClickListener(view -> operationChoosecallback.onImageOperationClicked(entity.getType()));
        holder.ivInfo.setOnClickListener(view-> operationInformationCallback.operationInfoClicked(entity.getType()));
    }
    @Override
    public int getItemLayoutId(StickyHeaderViewAdapter adapter) {
        return R.layout.item_image_operation_type_list;
    }
    static class ViewHolder extends ViewBinder.ViewHolder {
        private final View ivInfo;
        private  final TextView tvDesc;
        public ViewHolder(View rootView) {
            super(rootView);
            this.tvDesc = rootView.findViewById(R.id.tv_desc);
            this.ivInfo=rootView.findViewById(R.id.iv_info);
        }
    }
}
