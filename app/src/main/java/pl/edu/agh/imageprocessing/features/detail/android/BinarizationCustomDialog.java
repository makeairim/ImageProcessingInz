package pl.edu.agh.imageprocessing.features.detail.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import android.widget.Toast;

import com.projects.alshell.android.SeekBarValueChangedListener;
import com.projects.alshell.android.TerminalSeekBar;

import java.util.ArrayList;
import java.util.List;

import ir.hamsaa.RtlMaterialSpinner;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.app.constants.AppConstants;

/**
 * Created by bwolcerz on 05.08.2017.
 */

public class BinarizationCustomDialog extends BottomSheetDialogFragment {

    private RtlMaterialSpinner spinner;
    private DialogListener listener;
    private EditText mThresholdEditText;
    private TerminalSeekBar mThresholdBar;

    public interface DialogListener{
        public void call(int threshold);

    }
    public BinarizationCustomDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static BinarizationCustomDialog newInstance(String title) {
        BinarizationCustomDialog frag = new BinarizationCustomDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_binarization, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mThresholdEditText = view.findViewById(R.id.txt_threshold);
        mThresholdBar = view.findViewById(R.id.seekbar);
        mThresholdBar.setMaxValue(AppConstants.MAX_ADAPTIVE_THRESHOLD);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Set threshold");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        mThresholdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String text = editable.toString();
                if( !text.isEmpty() ){
                    int threshold = Integer.parseInt(text);
                    mThresholdBar.setProgress(threshold);
                }
            }
        });
//        mThresholdEditText.setOnFocusChangeListener((view1, b) -> {
//            if(!b){
//                if(!mThresholdEditText.getText().toString().isEmpty())
//                mThresholdBar.setProgress(Integer.valueOf(mThresholdEditText.getText().toString()));
//            }
//        });
        mThresholdBar.setSeekBarValueChangedListener((i, b) -> {
            if (b) {
                mThresholdEditText.setText(i + "");
            }
                }
        );

        view.findViewById(R.id.accept_params).setOnClickListener(view1 -> {
            try {
                int threshold = Integer.parseInt(mThresholdEditText.getText().toString());
                listener.call(threshold);
                dismiss();
            }catch(NumberFormatException e){
                Toast.makeText(getActivity().getBaseContext(),"Incorrect format of threshold",Toast.LENGTH_LONG).show();
            }
        });

    }
}
