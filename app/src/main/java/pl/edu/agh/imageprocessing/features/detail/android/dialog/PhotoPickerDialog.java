package pl.edu.agh.imageprocessing.features.detail.android.dialog;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.marchinram.rxgallery.RxGallery;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.features.detail.android.event.PhotoEvent;
import pl.edu.agh.imageprocessing.features.detail.home.GalleryActivity;

/**
 * Created by bwolcerz on 05.08.2017.
 */

public class PhotoPickerDialog extends BottomSheetDialogFragment {
    public static final String TAG = PhotoPickerDialog.class.getSimpleName();
    private ImagePickerListener listener;
    public static final String PHOTO_KEY = "photos_key";
    private Uri[] photoUris;

    public PhotoPickerDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public interface ImagePickerListener {
        void call(Uri uri, PhotoEvent photoEvent);
    }

    public static PhotoPickerDialog newInstance(String title, List<Uri> photoGallery) {
        PhotoPickerDialog frag = new PhotoPickerDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelableArray(PHOTO_KEY, photoGallery.toArray(new Uri[0]));
        frag.setArguments(args);
        return frag;
    }

    public void setListener(ImagePickerListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_photo_picker, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        setRetainInstance(true);
        if (listener == null) {
            throw new AssertionError();
        }
        String title = getArguments().getString("title", "Set threshold");
        getDialog().setTitle(title);
        photoUris = (Uri[]) getArguments().getParcelableArray(PHOTO_KEY);
        view.findViewById(R.id.iv_gallery_phone).setOnClickListener(l -> RxGallery.gallery(getActivity(), false, RxGallery.MimeType.IMAGE).subscribe(c -> {
            listener.call(c.get(0), PhotoEvent.ACCEPT);
            dismiss();
        }));
        view.findViewById(R.id.iv_camera).setOnClickListener(l ->{
                Observable<Boolean> permissionObservable = Observable.just(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionObservable = new RxPermissions(getActivity()).request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA);
        }

        permissionObservable.flatMap(new Function<Boolean, ObservableSource<Uri>>() {
            @Override
            public ObservableSource<Uri> apply(@NonNull Boolean granted) throws Exception {
                if (!granted) {
                    return Observable.empty();
                }
                return RxGallery.photoCapture(getActivity()).toObservable();
            }
        }).subscribe(new Consumer<Uri>() {
            @Override
            public void accept(Uri uri) throws Exception {
                listener.call(uri, PhotoEvent.ACCEPT);
                dismiss();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        });
        view.findViewById(R.id.iv_gallery_app).setOnClickListener(l -> {
            if (photoUris != null && photoUris.length > 0) {
                registerReceiver().subscribe(c -> {
                    listener.call(c.getUri(), c.getPhotoEvent());
                    dismissAllowingStateLoss();
                });
            } else {
                Toast.makeText(getContext(), getString(R.string.no_photo_in_app_gallery), Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Maybe<GalleryActivity.Response> registerReceiver() {
        final Context appContext = getActivity().getApplicationContext();
        return Maybe.create(new MaybeOnSubscribe<GalleryActivity.Response>() {
            public void subscribe(@io.reactivex.annotations.NonNull final MaybeEmitter<GalleryActivity.Response> e) throws Exception {
                final BroadcastReceiver receiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        if (!e.isDisposed()) {
                            if (intent.hasExtra("extraErrorNoActivity")) {
                                e.onError(new ActivityNotFoundException("No activity found to handle request"));
                            } else if (intent.hasExtra("extraErrorSecurity")) {
                                e.onError((Throwable) intent.getSerializableExtra("extraErrorSecurity"));
                            } else if (intent.hasExtra(GalleryActivity.PHOTO_URI_KEY)) {
                                Uri photoUri = intent.getParcelableExtra(GalleryActivity.PHOTO_URI_KEY);
                                if (photoUri != null) {
                                    e.onSuccess(new GalleryActivity.Response(photoUri, PhotoEvent.valueOf(intent.getStringExtra(GalleryActivity.PHOTO_EVENT_TYPE))));
                                } else {
                                    e.onComplete();
                                }
                            }
                        }

                    }
                };
                IntentFilter intentFilter = new IntentFilter(GalleryActivity.FINISHED_ACTION);
                appContext.registerReceiver(receiver, intentFilter);
                e.setDisposable(new MainThreadDisposable() {
                    protected void onDispose() {
                        appContext.unregisterReceiver(receiver);
                        appContext.sendBroadcast(new Intent(GalleryActivity.DISPOSED_ACTION));
                    }
                });
                Intent intent = new Intent(appContext, GalleryActivity.class);
                intent.putParcelableArrayListExtra(GalleryActivity.PHOTOS_KEY, new ArrayList<>(Arrays.asList(photoUris)));
                getActivity().startActivity(intent);
            }
        });
    }
}
