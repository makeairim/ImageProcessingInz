package pl.edu.agh.imageprocessing.databinding;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;



import java.util.List;

import pl.edu.agh.imageprocessing.data.remote.Resource;
import pl.edu.agh.imageprocessing.features.detail.android.BaseAdapter;


/**
 * Created by Anil on 6/7/2017.
 */

public final class ListBindingAdapter {
    @BindingAdapter(value = "resource")
    public static void setResource(RecyclerView recyclerView, Resource resource){
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if(adapter == null)
            return;

        if(resource == null || resource.data == null)
            return;

        if(adapter instanceof BaseAdapter){
            ((BaseAdapter)adapter).setData((List) resource.data);
        }
    }
}
