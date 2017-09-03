package pl.edu.agh.imageprocessing.features.detail.android.recyclerview;

import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import tellh.com.stickyheaderview_rv.adapter.DataBean;
import tellh.com.stickyheaderview_rv.adapter.StickyHeaderViewAdapter;

/**
 * Created by bwolcerz on 03.09.2017.
 */

public class GroupOperationModel extends DataBean{
    private ImageOperationType type;
    private boolean shouldSticky;

    public GroupOperationModel(ImageOperationType type) {
        this.type = type;
    }

    public ImageOperationType getType() {
        return type;
    }

    public void setType(ImageOperationType type) {
        this.type = type;
    }
    @Override
    public int getItemLayoutId(StickyHeaderViewAdapter adapter) {
        return R.layout.item_image_operation_type_list;
    }
    public void setShouldSticky(boolean shouldSticky) {
        this.shouldSticky = shouldSticky;
    }
    // Decide whether the item view should be suspended on the top.
    @Override
    public boolean shouldSticky() {
        return shouldSticky;
    }
}
