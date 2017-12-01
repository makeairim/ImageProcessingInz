package pl.edu.agh.imageprocessing.features.detail.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.veinhorn.scrollgalleryview.loader.MediaLoader;

import java.util.concurrent.Callable;

import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.dagger.GlideApp;

/**
 * Created by bwolcerz on 15.11.2017.
 */

public class GlideImageLoader implements MediaLoader {
    public static final String TAG = GlideImageLoader.class.getSimpleName();
    private Uri url;

    public interface ImageChooseCallback {
        void onClick(Uri url);
    }

    public GlideImageLoader(Uri url) {
        this.url = url;
    }

    @Override
    public boolean isImage() {
        return true;
    }

    @Override
    public void loadMedia(Context context, final ImageView imageView, final MediaLoader.SuccessCallback callback) {
        GlideApp.with(context).applyDefaultRequestOptions(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)).applyDefaultRequestOptions(RequestOptions.encodeFormatOf(Bitmap.CompressFormat.PNG))
                .applyDefaultRequestOptions(RequestOptions.skipMemoryCacheOf(true))
                .applyDefaultRequestOptions(RequestOptions.downsampleOf(DownsampleStrategy.AT_MOST))
                .applyDefaultRequestOptions(RequestOptions.overrideOf(288,352))
                .load(url).apply(RequestOptions.placeholderOf(R.drawable.placeholder)).apply(RequestOptions.centerCropTransform()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                try {
                    new ImageCallback(callback).call();
                } catch (Exception e) {
                    Log.e(TAG, "onResourceReady: " + e.getMessage(), e);
                }
                return false;
            }
        }).into(imageView);

    }

    @Override
    public void loadThumbnail(Context context, ImageView thumbnailView, MediaLoader.SuccessCallback callback) {
        thumbnailView.setTag(null);
//        GlideApp.with(context).applyDefaultRequestOptions(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
//                .load(url).apply(RequestOptions.placeholderOf(R.drawable.placeholder)).apply(RequestOptions.centerInsideTransform()).apply(RequestOptions.priorityOf(Priority.LOW))
//                .apply(RequestOptions.overrideOf(25,25)).listener(new RequestListener<Drawable>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                try {
//                    new ImageCallback(callback).call();
//                } catch (Exception e) {
//                    Log.e(TAG, "onResourceReady: " + e.getMessage(), e);
//                }
//                return false;
//            }
//        }).into(thumbnailView);
    }

    private static class ImageCallback implements Callable {

        private final MediaLoader.SuccessCallback callback;

        public ImageCallback(SuccessCallback callback) {
            this.callback = callback;
        }

        @Override
        public Object call() throws Exception {
            callback.onSuccess();
            return null;
        }
    }
}
