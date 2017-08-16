package pl.edu.agh.imageprocessing.features.detail.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.hamsaa.RtlMaterialSpinner;
import pl.edu.agh.imageprocessing.R;

/**
 * Created by bwolcerz on 05.08.2017.
 */

public class DilationErosionCustomDialog extends BottomSheetDialogFragment {

    private EditText mEditText;
    private RtlMaterialSpinner spinner;
    private EditText mWidthEditText;
    private EditText mHeightEditText;
    private DialogListener listener;

    public interface DialogListener{
        public void call(int width,int height,String elementType);

    }
    public DilationErosionCustomDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static DilationErosionCustomDialog newInstance(String title, List<String> structuringElementTypes) {
        DilationErosionCustomDialog frag = new DilationErosionCustomDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putStringArrayList("items", new ArrayList<>(structuringElementTypes));
        frag.setArguments(args);
        return frag;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_morph_element, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mWidthEditText = view.findViewById(R.id.txt_width);
        mHeightEditText = view.findViewById(R.id.txt_height);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Define structuring element");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field

        ArrayList<String> ITEMS = getArguments().getStringArrayList("items");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setHint("Structuring element type");
        view.findViewById(R.id.accept_params).setOnClickListener(view1 -> {
            try {
                int width = Integer.parseInt(mWidthEditText.getText().toString());
                int height = Integer.parseInt(mHeightEditText.getText().toString());
                String operation=spinner.getItemAtPosition(spinner.getSelectedItemPosition()-1).toString();
                listener.call(width,height,operation);
                dismiss();
            }catch(NumberFormatException e){
                Toast.makeText(getActivity().getBaseContext(),"Incorrect format of width or height",Toast.LENGTH_LONG).show();
            }
        });

    }
}
