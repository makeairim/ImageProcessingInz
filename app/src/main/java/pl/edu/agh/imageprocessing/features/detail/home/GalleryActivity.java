package pl.edu.agh.imageprocessing.features.detail.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.veinhorn.scrollgalleryview.MediaInfo;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;

import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.features.detail.android.event.PhotoEvent;
import pl.edu.agh.imageprocessing.features.detail.images.GlideImageLoader;

/**
 * Created by bwolcerz on 15.11.2017.
 */

public class GalleryActivity extends FragmentActivity {
    public static final String FINISHED_ACTION = "pl.edu.agh.imageprocessing.features.detail.home.FINISHED_ACTION";
    public static final String DISPOSED_ACTION = "pl.edu.agh.imageprocessing.features.detail.home.DISPOSED_ACTION";
    public static final String EXTRA_URIS = "extraUris";

    public static final String TAG = GalleryActivity.class.getSimpleName();
    public static final int REQUEST_CODE = 101;
    public static final String PHOTO_URI_KEY = "PHOTO_URI_KEY";
    public static final String PHOTOS_KEY = "PHOTOS_KEY";
    public static final String HIDE_ACCEPT_BTN = "HIDE_ACCEPT_BTN";

    private ScrollGalleryView scrollGalleryView;
    public static final String PHOTO_EVENT_TYPE = "PHOTO_EVENT_KEY";
    private final BroadcastReceiver disposedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            GalleryActivity.this.finish();
        }
    };
    private ArrayList<Uri> photoUris = new ArrayList<>();
    private boolean hideAcceptBtn;

    private void onResult(Uri photUri, PhotoEvent photoEvent) {
        Intent intent = new Intent(FINISHED_ACTION);
        intent.putExtra(PHOTO_URI_KEY, photUri);
        intent.putExtra(PHOTO_EVENT_TYPE, photoEvent.name());
        setResult(RESULT_OK, intent);
        this.sendBroadcast(intent);
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.disposedReceiver);
    }

    private void onExportPhoto() {
        Log.i(TAG, "onExportPhoto: " + scrollGalleryView.getCurrentItem());
        Uri currentImage = photoUris.get(scrollGalleryView.getCurrentItem());
        onResult(currentImage, PhotoEvent.EXPORT);
    }

    private void onAcceptPhoto() {
        Log.i(TAG, "onAcceptPhoto: " + scrollGalleryView.getCurrentItem());
        Uri currentImage = photoUris.get(scrollGalleryView.getCurrentItem());
        onResult(currentImage, PhotoEvent.ACCEPT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        this.registerReceiver(this.disposedReceiver, new IntentFilter(DISPOSED_ACTION));
        ArrayList<Parcelable> data = getIntent().getParcelableArrayListExtra(PHOTOS_KEY);
        hideAcceptBtn = getIntent().getBooleanExtra(HIDE_ACCEPT_BTN, Boolean.FALSE);
        for (Parcelable p : data) {
            photoUris.add((Uri) p);
        }
        List<MediaInfo> infos = new ArrayList<>(photoUris.size());
        for (Uri url : photoUris)
            infos.add(MediaInfo.mediaLoader(new GlideImageLoader(url)));
        scrollGalleryView = findViewById(R.id.scroll_gallery_view);
        scrollGalleryView
                .hideThumbnails(true)
                .setThumbnailSize(1)
                .setZoom(true)
                .setFragmentManager(getSupportFragmentManager())
                .addMedia(infos);
        scrollGalleryView.setClickable(true);
        scrollGalleryView.setOnClickListener(l ->
        {
            Log.i(TAG, "onCreate: " + scrollGalleryView.getCurrentItem());
        });
        if (hideAcceptBtn) {
            findViewById(R.id.fb_btn_accept).setVisibility(View.GONE);
        } else {
            findViewById(R.id.fb_btn_accept).setOnClickListener(l -> onAcceptPhoto());
        }
        findViewById(R.id.fb_btn_export).setOnClickListener(l -> onExportPhoto());
    }

    private Bitmap toBitmap(int image) {
        return ((BitmapDrawable) getResources().getDrawable(image)).getBitmap();
    }

    public static class Response {
        Uri uri;
        PhotoEvent photoEvent;

        public Response(Uri uri, PhotoEvent photoEvent) {
            this.uri = uri;
            this.photoEvent = photoEvent;
        }

        public PhotoEvent getPhotoEvent() {
            return photoEvent;
        }

        public Uri getUri() {
            return uri;
        }
    }
}
