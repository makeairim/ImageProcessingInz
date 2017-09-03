package pl.edu.agh.imageprocessing.features.detail.android.recyclerview;

import android.view.View;
import android.widget.TextView;

import pl.edu.agh.imageprocessing.R;
import tellh.com.stickyheaderview_rv.adapter.StickyHeaderViewAdapter;
import tellh.com.stickyheaderview_rv.adapter.ViewBinder;

/**
 * Created by bwolcerz on 03.09.2017.
 */

public class ItemTypeViewBinder extends ViewBinder<GroupOperationModel, ItemTypeViewBinder.ViewHolder> {
    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }
    @Override
    public void bindView(StickyHeaderViewAdapter adapter, ViewHolder holder, int position, GroupOperationModel entity) {
        holder.tv_desc.setText(entity.getType().getTitle());
    }
    @Override
    public int getItemLayoutId(StickyHeaderViewAdapter adapter) {
        return R.layout.item_image_operation_type_list;
    }
    static class ViewHolder extends ViewBinder.ViewHolder {
        TextView tv_desc;
        public ViewHolder(View rootView) {
            super(rootView);
            this.tv_desc = (TextView) rootView.findViewById(R.id.tv_desc);

        }
    }
}
