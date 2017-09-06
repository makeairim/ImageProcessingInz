package pl.edu.agh.imageprocessing.features.detail.android.operationtypeslist;

import pl.edu.agh.imageprocessing.R;
import tellh.com.stickyheaderview_rv.adapter.DataBean;
import tellh.com.stickyheaderview_rv.adapter.StickyHeaderViewAdapter;

/**
 * Created by bwolcerz on 03.09.2017.
 */
public class ItemHeader extends DataBean {
    private String prefix;

    public ItemHeader(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public int getItemLayoutId(StickyHeaderViewAdapter adapter) {
        return R.layout.item_expandable_group_header;
    }
    @Override
    public boolean shouldSticky() {
        return true;
    }
}