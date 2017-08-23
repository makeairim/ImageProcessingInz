package pl.edu.agh.imageprocessing.features.detail.android.databinding;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import pl.edu.agh.imageprocessing.R;

public final class ImageBindingAdapter {

    @BindingAdapter(value="bind:url")
    public static void loadImageUrl(ImageView view, String url) {
        if (url != null && !url.equals(""))
            Glide.with(view.getContext())
                    .load(Uri.parse(url)).apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                    .into(view);
    }

}
