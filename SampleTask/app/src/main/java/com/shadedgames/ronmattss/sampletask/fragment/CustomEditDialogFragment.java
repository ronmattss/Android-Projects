package com.shadedgames.ronmattss.sampletask.fragment;

import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.shadedgames.ronmattss.sampletask.R;
import com.shadedgames.ronmattss.sampletask.provider.DatabaseHelper;

import static com.shadedgames.ronmattss.sampletask.R.layout.dialog_layout;

public class CustomEditDialogFragment extends DialogFragment {

    public static final String TAG = "TodoEditDialog";

    private EditText mInput;
    private TextView mActionOk, mActionCancel;
    DatabaseHelper mDatabaseHelper;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(dialog_layout, container, false);
        mActionOk = v.findViewById(R.id.SaveNow);
        mActionCancel = v.findViewById(R.id.cancel);
        mInput = v.findViewById(R.id.write);
        mDatabaseHelper = new DatabaseHelper(getContext());


        //get the intent with the extra from the ListViewIntent receivedIntent = new Intent();


        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input = mInput.getText().toString();
                if(!input.equals("")){
//
//                    //Easiest way: just set the value.
//                    MainFragment fragment = (MainFragment) getActivity().getFragmentManager().findFragmentByTag("MainFragment");
//                    fragment.mInputDisplay.setText(input);
                }


                getDialog().dismiss();
            }
        });
        return v;
    }


}
