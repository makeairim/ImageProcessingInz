package pl.edu.agh.imageprocessing.features.detail.android.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pl.edu.agh.imageprocessing.R;

/**
 * Created by bwolcerz on 05.08.2017.
 */

public class InformationCustomDialog extends DialogFragment {

    private TextView content;

    public InformationCustomDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static InformationCustomDialog newInstance(String title) {
        InformationCustomDialog frag = new InformationCustomDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_operation_info, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        getDialog().getWindow().setTitleColor(getResources().getColor(R.color.colorPrimary));
        content = view.findViewById(R.id.tv_desc);
        getDialog().setTitle(getArguments().getString("title", "Description"));
        // Fetch arguments from bundle and set title
        // Show soft keyboard automatically and request focus to field

    }
}
