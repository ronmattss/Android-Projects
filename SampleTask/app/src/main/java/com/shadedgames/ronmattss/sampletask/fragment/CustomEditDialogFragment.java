package com.shadedgames.ronmattss.sampletask.fragment;

import android.app.DialogFragment;
import android.content.Context;
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

    public interface onEditTitle
    {
        void sendUpdateData(String newTodoTitle,int todoID,String oldTodoTitle);
        void onAttach();
    }
    public static final String TAG = "TodoEditDialog";

    public onEditTitle mOnEditTitle;
    private EditText mInput;
    private TextView mActionOk, mActionCancel;
    private int editTodoId;
    private String editTodoTitle;
    DatabaseHelper mDatabaseHelper;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
        {
         editTodoId = getArguments().getInt("todoId", -1);
            editTodoTitle = getArguments().getString("todoTitle");
          //  Toast.makeText(getContext(), editTodoId + " " + editTodoTitle, Toast.LENGTH_SHORT).show();

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(dialog_layout, container, false);
        mActionOk = v.findViewById(R.id.SaveNow);
        mActionCancel = v.findViewById(R.id.cancel);
        mInput = v.findViewById(R.id.write);
        mDatabaseHelper = new DatabaseHelper(getContext());


        mInput.setText(editTodoTitle);



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
                   // mDatabaseHelper.updateName(input,editTodoId,editTodoTitle);
//                    //Easiest way: just set the value.
//                    MainFragment fragment = (MainFragment) getActivity().getFragmentManager().findFragmentByTag("MainFragment");
//                    fragment.mInputDisplay.setText(input);
                    mOnEditTitle.sendUpdateData(input,editTodoId,editTodoTitle);
                }
                getDialog().dismiss();

            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnEditTitle = (onEditTitle) getTargetFragment();
        }catch (ClassCastException e){
        }
    }

}
