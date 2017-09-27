package pl.edu.agh.imageprocessing.features.detail.android.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import pl.edu.agh.imageprocessing.R;

/**
 * Created by bwolcerz on 05.08.2017.
 */

public class CannyEdgeCustomDialog extends BottomSheetDialogFragment {
    public static final String TAG = CannyEdgeCustomDialog.class.getSimpleName();
    private EditText mSupressedPointThresholdEditText;
    private EditText mStrongPointThresholdEditText;
    private DialogListener listener;

    public interface DialogListener {
        public void call(int supressedPointThreshold,int strongPointThreshold);

    }

    public CannyEdgeCustomDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CannyEdgeCustomDialog newInstance(String title,int defaultValueSupressedPointThreshold,int defaultValueStrongPointThreshold) {
        CannyEdgeCustomDialog frag = new CannyEdgeCustomDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("supressedThresholdDefault",defaultValueSupressedPointThreshold);
        args.putInt("strongThresholdDefault",defaultValueStrongPointThreshold);
        frag.setArguments(args);
        return frag;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_canny_edge, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mSupressedPointThresholdEditText = view.findViewById(R.id.et_supressed_point_threshold);
        mStrongPointThresholdEditText = view.findViewById(R.id.et_strong_point_threshold);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Define threshold values");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field


        view.findViewById(R.id.accept_params).setOnClickListener(view1 -> {
            try {
                int supressedPointThreshold = Integer.parseInt(mSupressedPointThresholdEditText.getText().toString());
                int strongPointThreshold = Integer.parseInt(mStrongPointThresholdEditText.getText().toString());
                listener.call(supressedPointThreshold, strongPointThreshold);
                dismiss();
            } catch (NumberFormatException e) {
                Log.i(TAG, "onViewCreated: " + e.getMessage(), e);
                Toast.makeText(getActivity().getBaseContext(), "Incorrect value format", Toast.LENGTH_LONG).show();
            }
        });

    }
}
