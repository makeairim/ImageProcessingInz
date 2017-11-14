package pl.edu.agh.imageprocessing.features.detail.android.dialog;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vansuita.pickimage.dialog.PickImageDialog;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;


public class ImageArgumentChooseCustomDialog extends BottomSheetDialogFragment {
    public static final String TAG = ImageArgumentChooseCustomDialog.class.getSimpleName();
    private static final String OPERATION_KEY = "operationResource";
    private static final String OPERATION_TYPE_KEY = "operationTypeKey";
    private DialogListener listener;
    private Resource firstArgument;
    private Resource secondArgument;

    @Inject
    PickImageDialog pickImageDialog;
    private ImageView secondPhotoIV;
    private Function<Consumer<Uri>, Boolean> pickPhoto;
    private TextView argument1Desc;
    private TextView argument2Desc;
    private ImageOperationType imageOperationType;
    private ImageView firstPhotoIV;

    public interface DialogListener {
        public void call(ArrayList<Resource> arguments);
    }

    public ImageArgumentChooseCustomDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ImageArgumentChooseCustomDialog newInstance(String title, ImageOperationType imageOperationType, Resource operationResource) {
        ImageArgumentChooseCustomDialog frag = new ImageArgumentChooseCustomDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        if (operationResource != null) {
            args.putParcelable(OPERATION_KEY, operationResource);
        }
        args.putString(OPERATION_TYPE_KEY, imageOperationType.name());
        frag.setArguments(args);
        return frag;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    public void setPickPhoto(Function<Consumer<Uri>, Boolean> callable) {
        this.pickPhoto = callable;
    }

    private final Consumer<Uri> loadPhoto = uri -> {
        secondArgument = new Resource.Builder().content(uri.toString()).build();
        loadPreviewsFromArguments(firstArgument, secondArgument, getView());
    };
    private final View.OnClickListener choosePhotoListener = l -> {
        try {
            pickPhoto.apply(loadPhoto);
        } catch (Exception e) {
            Log.e(TAG, "onViewCreated: " + e.getMessage(), e);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_image_argument_choose_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Choose size");
        getDialog().setTitle(title);
        imageOperationType = ImageOperationType.valueOf(getArguments().getString(OPERATION_TYPE_KEY));
        firstArgument = getArguments().getParcelable(OPERATION_KEY);
        this.firstPhotoIV = view.findViewById(R.id.iv_preview_arg_1);
        this.secondPhotoIV = view.findViewById(R.id.iv_preview_arg_2);
        this.argument1Desc = view.findViewById(R.id.tv_argument1_desc);
        this.argument2Desc = view.findViewById(R.id.tv_argument2_desc);

        loadPreviewsFromArguments(firstArgument, secondArgument, view);
        view.findViewById(R.id.iv_swap_arguments).setOnClickListener(l -> {
            swapArgument();
            loadPreviewsFromArguments(firstArgument, secondArgument, view);
        });
        // Show soft keyboard automatically and request focus to field


        view.findViewById(R.id.accept_params).setOnClickListener(view1 -> {
            //listener.call();
            if (firstArgument != null && firstArgument.getContent() != null && secondArgument != null && secondArgument.getContent() != null) {
                ArrayList<Resource> result = new ArrayList<>();
                if (firstArgument.getOperationId() == null) {
                    firstArgument.setType(ResourceType.ARGUMENT_IMAGE_FILE);
                    secondArgument.setType(ResourceType.ARGUMENT_OPERATION_RESOURCE);
                    secondArgument.setContent(secondArgument.getOperationId().toString());
                } else {
                    firstArgument.setContent(firstArgument.getOperationId().toString());
                    firstArgument.setType(ResourceType.ARGUMENT_OPERATION_RESOURCE);
                    secondArgument.setType(ResourceType.ARGUMENT_IMAGE_FILE);
                }
                result.add(firstArgument);
                result.add(secondArgument);
                listener.call(result);
                dismiss();
            } else {
                Toast.makeText(getActivity().getBaseContext(), "Arguments not set", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void loadPreviewsFromArguments(Resource firstArgument, Resource secondArgument, View view) {
        if (firstArgument != null && firstArgument.getOperationId() != null) {
            firstPhotoIV.setOnClickListener(null);
            secondPhotoIV.setOnClickListener(choosePhotoListener);
        } else {
            secondPhotoIV.setOnClickListener(null);
            firstPhotoIV.setOnClickListener(choosePhotoListener);
        }
        if (firstArgument != null && firstArgument.getOperationId() != null) {
            argument1Desc.setText(imageOperationType.getTitle());
            argument2Desc.setText(getString(R.string.input_from_user_desc));

        } else {
            argument1Desc.setText(getString(R.string.input_from_user_desc));
            argument2Desc.setText(imageOperationType.getTitle());
        }
        String uriPath = "";
        if (firstArgument != null && firstArgument.getContent() != null && !firstArgument.getContent().isEmpty()) {
            uriPath = firstArgument.getContent();
        }
        Glide.with(firstPhotoIV.getContext())
                .load(Uri.parse(uriPath)).apply(RequestOptions.placeholderOf(R.drawable.ic_add_a_photo_black_24dp)).apply(RequestOptions.centerCropTransform())
                .into(firstPhotoIV);
        uriPath = "";
        if (secondArgument != null && secondArgument.getContent() != null && !secondArgument.getContent().isEmpty()) {
            uriPath = secondArgument.getContent();
        }
        Glide.with(secondPhotoIV.getContext())
                .load(Uri.parse(uriPath)).apply(RequestOptions.placeholderOf(R.drawable.ic_add_a_photo_black_24dp)).apply(RequestOptions.centerCropTransform())
                .into(secondPhotoIV);
    }

    public void swapArgument() {
        Resource resourceTmp = firstArgument;
        firstArgument = secondArgument;
        secondArgument = resourceTmp;
    }
}
