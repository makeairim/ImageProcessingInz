package pl.edu.agh.imageprocessing.features.detail.android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Locale;

import pl.edu.agh.imageprocessing.R;

/**
 * Created by bwolcerz on 05.08.2017.
 */

public class MatrixCustomDialog extends BottomSheetDialogFragment {


    static private final String WIDTH_KEY = "WIDTH_KEY";
    static private final String HEIGHT_KEY = "HEIGHT_KEY";
    private static final String TITLE_KEY = "titleKey";
    private DialogListener listener;
    private int[] mMatrix;
    private int mWidth;
    private int mHeight;

    public interface DialogListener {
        public void call(int width, int height, int[] matrix);
    }

    public MatrixCustomDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static MatrixCustomDialog newInstance(String title, int width, int height) {
        MatrixCustomDialog frag = new MatrixCustomDialog();
        Bundle args = new Bundle();
        args.putString(MatrixCustomDialog.TITLE_KEY, title);
        args.putInt(WIDTH_KEY, width);
        args.putInt(HEIGHT_KEY, height);
        frag.setArguments(args);
        return frag;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_matrix_values, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view

        // Fetch arguments from bundle and set TITLE_KEY
        String title = getArguments().getString(MatrixCustomDialog.TITLE_KEY, "Define mMatrix");
        getDialog().setTitle(title);
        this.mWidth = getArguments().getInt(this.WIDTH_KEY);
        this.mHeight = getArguments().getInt(HEIGHT_KEY);
        mMatrix = new int[mWidth * mHeight];
        // Show soft keyboard automatically and request focus to field
        init(view, mWidth, mHeight);
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getActivity().runOnUiThread(() -> {
                init(view, 5, 5);
            });
        }).start();
        view.findViewById(R.id.accept_params).setOnClickListener(view1 -> {
            if (mMatrix == null) {
                throw new AssertionError("Matrix can not be null");
            }
            listener.call(mWidth, mHeight, mMatrix);
            dismiss();
        });

    }

    public void init(View view, int width, int height) {
        TableLayout stk = view.findViewById(R.id.table_main);
        stk.removeAllViewsInLayout();
        TableRow tbrow0 = new TableRow(getActivity());
        for (int i = 0; i <= width; ++i) {
            TextView tv0 = new TextView(getActivity());
            tv0.setText(String.format(Locale.getDefault(), "%4d", i));
            tv0.setTextColor(Color.WHITE);
            if( i==0){
                tv0.setText(String.format(Locale.getDefault(), "%5d", i));
                tv0.setVisibility(View.INVISIBLE);
            }
            tbrow0.addView(tv0);
        }
        stk.addView(tbrow0);
//        <View
//        android:layout_width="match_parent"
//        android:layout_height="1dp"
//        android:background="@android:color/darker_gray"/>
        View separator = new View(getActivity());
        separator.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2));
        separator.setBackground(getActivity().getResources().getDrawable(android.R.color.background_light));
        stk.addView(separator);
        for (int i = 1; i <= height; i++) {
            TableRow tbrow = new TableRow(getActivity());
            for (int j = 0; j < width; ++j) {
                    if(j==0){
                        TextView t1v = new TextView(getActivity());
                        t1v.setText(String.format(Locale.getDefault(), "%3d", i));
                        t1v.setTextColor(Color.WHITE);
                        t1v.setGravity(Gravity.CENTER);
                        tbrow.addView(t1v);
                    }
                    EditText t1v = new EditText(getActivity());
                    t1v.setText(String.format(Locale.getDefault(), "%3d", 1));
                    t1v.setTextColor(Color.WHITE);
                    t1v.setGravity(Gravity.CENTER);
                    tbrow.addView(t1v);

            }
            stk.addView(tbrow);
        }

    }
}
