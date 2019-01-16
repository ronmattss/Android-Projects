package com.shadedgames.ronmattss.sampletask.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import java.util.Calendar;

public class TimePickerDialogFragment extends DialogFragment {
    static final String HOUR = "hour";
    static final String MINS = "mins";

    public static TimePickerDialogFragment newInstance(Calendar time)
    {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(HOUR,time.get(Calendar.HOUR_OF_DAY));
        args.putInt(MINS,time.get(Calendar.MINUTE));
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        TimePickerDialog.OnTimeSetListener listener =(TimePickerDialog.OnTimeSetListener)
                getFragmentManager().findFragmentByTag(TaskEditFragment.DEFAULT_FRAGMENT_TAG);

        Bundle args = getArguments();
        return new TimePickerDialog(getActivity(), listener,args.getInt(HOUR),args.getInt(MINS),false);
    }
}
