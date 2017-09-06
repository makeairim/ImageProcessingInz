package pl.edu.agh.imageprocessing.features.detail.android.operationtypeslist;

import android.view.View;
import android.widget.TextView;

import pl.edu.agh.imageprocessing.R;
import tellh.com.stickyheaderview_rv.adapter.StickyHeaderViewAdapter;
import tellh.com.stickyheaderview_rv.adapter.ViewBinder;

/**
 * Created by bwolcerz on 03.09.2017.
 */

public class ItemHeaderViewBinder extends ViewBinder<ItemHeader, ItemHeaderViewBinder.ViewHolder> {
    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }
    @Override
    public void bindView(StickyHeaderViewAdapter adapter, ViewHolder holder, int position, ItemHeader entity) {
        holder.tvPrefix.setText(entity.getPrefix());
    }
    @Override
    public int getItemLayoutId(StickyHeaderViewAdapter adapter) {
        return R.layout.item_expandable_group_header;
    }
    static class ViewHolder extends ViewBinder.ViewHolder {
        TextView tvPrefix;
        public ViewHolder(View rootView) {
            super(rootView);
            this.tvPrefix = (TextView) rootView.findViewById(R.id.tv_header);
        }
    }
}
