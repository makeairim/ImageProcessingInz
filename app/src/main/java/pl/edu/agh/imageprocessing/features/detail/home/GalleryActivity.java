package pl.edu.agh.imageprocessing.features.detail.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.veinhorn.scrollgalleryview.MediaInfo;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.features.detail.android.event.PhotoEvent;
import pl.edu.agh.imageprocessing.features.detail.images.GlideImageLoader;

/**
 * Created by bwolcerz on 15.11.2017.
 */

public class GalleryActivity extends FragmentActivity {
    public static final String TAG = GalleryActivity.class.getSimpleName();
    private static final ArrayList<String> images = new ArrayList<>(Arrays.asList(
            "http://img1.goodfon.ru/original/1920x1080/d/f5/aircraft-jet-su-47-berkut.jpg",
            "http://www.dishmodels.ru/picture/glr/13/13312/g13312_7657277.jpg",
            "http://img2.goodfon.ru/original/1920x1080/b/c9/su-47-berkut-c-37-firkin.jpg"
    ));
    private static final String movieUrl = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4";
    public static final int REQUEST_CODE = 101;
    public static final String PHOTO_URI_KEY = "PHOTO_URI_KEY";

    private ScrollGalleryView scrollGalleryView;
    public static final String PHOTO_EVENT_TYPE = "PHOTO_EVENT_KEY";

    private void onExportPhoto() {
        Log.i(TAG, "onExportPhoto: " + scrollGalleryView.getCurrentItem());
        String currentImage = images.get(scrollGalleryView.getCurrentItem());
        Intent data = new Intent();
        data.putExtra(PHOTO_URI_KEY, currentImage);
        data.putExtra(PHOTO_EVENT_TYPE, PhotoEvent.EXPORT.name());
        setResult(RESULT_OK, data);
    }

    private void onAcceptPhoto() {
        Log.i(TAG, "onAcceptPhoto: " + scrollGalleryView.getCurrentItem());
        String currentImage = images.get(scrollGalleryView.getCurrentItem());
        Intent data = new Intent();
        data.putExtra(PHOTO_URI_KEY, currentImage);
        data.putExtra(PHOTO_EVENT_TYPE, PhotoEvent.ACCEPT.name());
        setResult(RESULT_OK, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        List<MediaInfo> infos = new ArrayList<>(images.size());

        for (String url : images)
            infos.add(MediaInfo.mediaLoader(new GlideImageLoader(url)));
        scrollGalleryView = findViewById(R.id.scroll_gallery_view);
        scrollGalleryView
                .setThumbnailSize(100)
                .setZoom(true)
                .setFragmentManager(getSupportFragmentManager())
                .addMedia(infos);
        scrollGalleryView.setClickable(true);
        scrollGalleryView.setOnClickListener(l -> {
            Log.i(TAG, "onCreate: " + scrollGalleryView.getCurrentItem());
        });
        findViewById(R.id.fb_btn_accept).setOnClickListener(l -> onAcceptPhoto());
        findViewById(R.id.fb_btn_export).setOnClickListener(l -> onExportPhoto());
    }

    private Bitmap toBitmap(int image) {
        return ((BitmapDrawable) getResources().getDrawable(image)).getBitmap();
    }
}
