package pl.edu.agh.imageprocessing.features.detail.android.dialog;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.bytebuddy.utility.RandomString;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.features.detail.images.CapturePhotoUtils;

/**
 * Created by bwolcerz on 05.08.2017.
 */

public class EditPhotoDialog extends BottomSheetDialogFragment {
    public static final String TAG = EditPhotoDialog.class.getSimpleName();
    private static final String PHOTO_URI = "PHOTO_URI_KEY";
    private static final String RESOURCE_KEY = "RESOURCE_KEY";
    private static final String CAN_REMOVE_PHOTO = "CAN_REMOVE_PHOTO_KEY";
    private DialogListener listener;
    private Uri photoUri;
    private long resourceId;
    private Disposable disposable;

    public interface DialogListener {
        public void deleteOperation(long resourceId);

    }

    public EditPhotoDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditPhotoDialog newInstance(String title, Uri photoUri, long resId, Boolean canRemovePhoto) {
        EditPhotoDialog frag = new EditPhotoDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelable(PHOTO_URI, photoUri);
        args.putLong(RESOURCE_KEY, resId);
        args.putBoolean(CAN_REMOVE_PHOTO, canRemovePhoto);
        frag.setArguments(args);
        return frag;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_photo, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title", "Choose size");
        getDialog().setTitle(title);
        photoUri = getArguments().getParcelable(PHOTO_URI);
        resourceId = getArguments().getLong(RESOURCE_KEY);
        if (photoUri == null || resourceId == 0) {
            Log.e(TAG, "onViewCreated: empty PhotoUri");
            throw new AssertionError();
        }
        if (Boolean.TRUE.equals(getArguments().getBoolean(CAN_REMOVE_PHOTO, Boolean.FALSE))) {
            view.findViewById(R.id.delete_photo).setOnClickListener(l -> {
                {
                    listener.deleteOperation(resourceId);
                    dismiss();
                }
            });
        } else {
            view.findViewById(R.id.delete_photo).setEnabled(false);
        }
        view.findViewById(R.id.save_photo).setOnClickListener(l -> {
            {
                Observable.create((ObservableOnSubscribe<Boolean>) e -> {
                    e.onNext(exportPhoto(photoUri, RandomString.make(8)));
                    e.onComplete();
                }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(c -> {
                    if (Boolean.TRUE.equals(c)) {
                        Toast.makeText(getContext(), getString(R.string.photo_exported_msg), Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.photo_not_exported_msg), Toast.LENGTH_SHORT).show();
                    }
                });

//                final EditText txtUrl = new EditText(getActivity());

                // Set the default text to a link of the Queen
//                txtUrl.setHint(getString(R.string.export_photo_dialog_sample_name) + "_" + RandomString.make(RandomString.DEFAULT_LENGTH));
//                new AlertDialog.Builder(getActivity())
//                        .setTitle(getString(R.string.export_photo_dialog_title))
//                        .setMessage(getString(R.string.export_photo_dialog_msg))
//                        .setView(txtUrl)
//                        .setPositiveButton("Export", (dialog, whichButton) -> {
//                            String name = txtUrl.getText().toString();
//                            if (name.isEmpty()) {
//                                Toast.makeText(getContext(), "Name is empty", Toast.LENGTH_SHORT).show();
//                            } else {
//                                String fileName =name+ "_" + RandomString.make(RandomString.DEFAULT_LENGTH);
//                                exportPhoto(photoUri,name);
//                            }
//                        })
//                        .setNegativeButton("Cancel", (dialog, whichButton) -> {
//                        })
//                        .show();
            }
        });
        // Show soft keyboard automatically and request focus to field

    }

    private Boolean exportPhoto(Uri uri, String filename) {
        Bitmap bm = null;
        try {
            bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
        } catch (IOException e) {
            Log.e(TAG, "fromGallery: " + e.getMessage(), e);
            return false;
        }
        if (CapturePhotoUtils.insertImage(getActivity().getContentResolver(), bm, filename, getString(R.string.app_export_photo_desc)) != null) {
            return true;
        }
        return true;
    }
}
