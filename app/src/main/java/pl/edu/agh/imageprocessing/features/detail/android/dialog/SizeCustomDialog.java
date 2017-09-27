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

public class SizeCustomDialog extends BottomSheetDialogFragment {
    public static final String TAG=SizeCustomDialog.class.getSimpleName();
    private EditText mSizeEditText;
    private DialogListener listener;

    public interface DialogListener{
        public void call(int sizeXY);

    }
    public SizeCustomDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SizeCustomDialog newInstance(String title) {
        SizeCustomDialog frag = new SizeCustomDialog();
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
        return inflater.inflate(R.layout.dialog_size_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mSizeEditText = view.findViewById(R.id.txt_size);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Choose size");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field


        view.findViewById(R.id.accept_params).setOnClickListener(view1 -> {
            try {
                int size = Integer.parseInt(mSizeEditText.getText().toString());
                listener.call(size);
                dismiss();
            }catch(NumberFormatException e){
                Log.i(TAG, "onViewCreated: "+e.getMessage(),e);
                Toast.makeText(getActivity().getBaseContext(),"Incorrect format of size",Toast.LENGTH_LONG).show();
            }
        });

    }
}
