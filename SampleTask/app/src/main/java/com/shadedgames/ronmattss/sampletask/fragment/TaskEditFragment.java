package com.shadedgames.ronmattss.sampletask.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.shadedgames.ronmattss.sampletask.R;
import com.shadedgames.ronmattss.sampletask.interfaces.OnEditFinished;
import com.shadedgames.ronmattss.sampletask.provider.TaskProvider;
import com.shadedgames.ronmattss.sampletask.util.ReminderManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.support.v7.graphics.Palette.generate;

public class TaskEditFragment extends Fragment implements 
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DEFAULT_FRAGMENT_TAG = "taskEditFragment";
    private static final int MENU_SAVE = 1;
    static final String TASK_ID = "taskId";
    static final String TASK_DATE_AND_TIME = "taskDateAndTime";

    // Views
    View rootView;
    EditText titleText;
    EditText notesText;
    ImageView imageView;
    TextView dateButton;
    TextView timeButton;
    long taskId;
    Calendar taskDateAndTime;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(taskDateAndTime == null)
        {
            taskDateAndTime = Calendar.getInstance();
        }

        Bundle arguments = getArguments();
        if(arguments != null)
        {
            taskId = arguments.getLong(TaskProvider.COLUMN_TASKID, 0L);
        }
        // if we restore state from previous act, store date as well.

        if(savedInstanceState != null)
        {
            taskId = savedInstanceState.getLong(TASK_ID);
            taskDateAndTime = (Calendar) savedInstanceState.getSerializable(TASK_DATE_AND_TIME);
        }
        // If no prev. date tie, use "now"

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //This field may have changed while our activity was running,
        // so make sure we save it to our outState bundle so we can restore it after in on Create
        outState.putLong(TASK_ID, taskId);
        outState.putSerializable(TASK_DATE_AND_TIME, taskDateAndTime);
    }


    @Override
    public android.view.View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_task_edit,container,false);

        rootView = v.getRootView();
        titleText = (EditText) v.findViewById(R.id.title);
        notesText = (EditText) v.findViewById(R.id.notes);
        imageView = (ImageView) v.findViewById(R.id.image);
        dateButton = (TextView) v.findViewById(R.id.task_date);
        timeButton = (TextView) v.findViewById(R.id.task_time);
        

        //Tell the date and time buttons what to do when we click on them
        dateButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDatePicker();
            }
        });
        timeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
        if(taskId == 0)
        {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            String defaultTitleKey = getString(R.string
                    .pref_task_title_key);
            String defaultTimeKey = getString(R.string
                    .pref_default_time_from_now_key);

            String defaultTitle = prefs.getString(defaultTitleKey, null);
            String defaultTime = prefs.getString(defaultTimeKey, null);

            if (defaultTitle != null)
                titleText.setText(defaultTitle);

            if (defaultTime != null && defaultTime.length() > 0)
                taskDateAndTime.add(Calendar.MINUTE,
                        Integer.parseInt(defaultTime));

            updateDateAndTimeButtons();
        } else {
            // Load from background from database
            getLoaderManager().initLoader(0,null,this);
        }      
      
        return v;


    }
    /*
     * Helper method to show our Date Picker
     *
     * */
    private void showDatePicker()
    {
        // Create Fragment Transaction
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        DatePickerDialogFragment newFragment = DatePickerDialogFragment.newInstance(taskDateAndTime);
        newFragment.show(ft,"datePicker");
    }
    private void showTimePicker()
    {
        // Create Fragment Transaction
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        TimePickerDialogFragment fragment = TimePickerDialogFragment.newInstance(taskDateAndTime);
        fragment.show(ft,"timePicker");
    }


    private void updateDateAndTimeButtons() {
        // set the time button text

        DateFormat timeFormat =
                DateFormat.getTimeInstance(DateFormat.SHORT);
        String timeForButton = timeFormat.format(
                taskDateAndTime.getTime());
        timeButton.setText(timeForButton);
        // set the date button text
        DateFormat dateFormat = DateFormat.getDateInstance();
        String dateForButton = dateFormat.format(taskDateAndTime.getTime());
        dateButton.setText(dateForButton);
    }


    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu,inflater);

        menu.add(0,MENU_SAVE,0,R.string.confrirm)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
                // when the user press the Save
        {
            case MENU_SAVE:
                 save();
                ((OnEditFinished) getActivity()).finishEditingTask();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static TaskEditFragment newInstance(long id)
    {
        TaskEditFragment fragment = new TaskEditFragment();
        Bundle args = new Bundle();
        args.putLong(TaskProvider.COLUMN_TASKID,id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        taskDateAndTime.set(Calendar.YEAR, year);
        taskDateAndTime.set(Calendar.MONTH, monthOfYear);
        taskDateAndTime.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        updateDateAndTimeButtons();

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hours, int minute) {
        taskDateAndTime.set(Calendar.HOUR_OF_DAY,hours);
        taskDateAndTime.set(Calendar.MINUTE, minute);
        updateDateAndTimeButtons();
    }


    private void save()
    {
        //Put al the valueus the user entered into a Content Value object
        String title = titleText.getText().toString();
        ContentValues values = new ContentValues();
        values.put(TaskProvider.COLUMN_TITLE, title);
        values.put(TaskProvider.COLUMN_NOTES,notesText.getText().toString());
        values.put(TaskProvider.COLUMN_DATE_TIME,taskDateAndTime.getTimeInMillis());
        //taskid== 0 when we create a new task,
        // otherwise it's the id of the task being edited.
        if(taskId == 0 )
        {
            //Create the new task and set taskId to the id of the new task.
            Uri itemUri = getActivity().getContentResolver().insert(TaskProvider.CONTENT_URI,values);
            taskId = ContentUris.parseId(itemUri);
        }
        else {
            // update the existing task
            int count = getActivity().getContentResolver().update(
                    ContentUris.withAppendedId(TaskProvider.CONTENT_URI,
                            taskId),
                    values, null, null);
            // If somehow we didn't exactly on task, throw an error
            if(count != 1)
                throw new IllegalStateException("Unable to update" + taskId);
        }
        Toast.makeText(getActivity(),getString(R.string.task_saved_message),Toast.LENGTH_SHORT).show();
        ReminderManager.setReminder(getActivity(),taskId,title,taskDateAndTime);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri taskUri = ContentUris.withAppendedId(TaskProvider.CONTENT_URI,taskId);
        
        return new CursorLoader(getActivity(),taskUri,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor task) {
        if (task.getCount() == 0)
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((OnEditFinished) getActivity()).finishEditingTask();
                }
            });
            return;
        }
        titleText.setText(task.getString(task
                .getColumnIndexOrThrow(TaskProvider.COLUMN_TITLE)));
        notesText.setText(task.getString(task
                .getColumnIndexOrThrow(TaskProvider.COLUMN_NOTES)));
        Long dateInMillis = task.getLong(task
                .getColumnIndexOrThrow(TaskProvider.COLUMN_DATE_TIME));
        Date date = new Date(dateInMillis);
        taskDateAndTime.setTime(date);

        Picasso.get().load(TaskProvider.getImageUrlForTask(taskId)).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Activity activity = getActivity();
                if(activity == null)
                    return;
                // set the colors of the activity based on the color of the image
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                Palette palette;
                palette = generate(bitmap,32);

                int bgColor = palette.getLightMutedColor(0);
                if(bgColor != 0)
                {
                    rootView.setBackgroundColor(bgColor);
                }
            }

            @Override
            public void onError(Exception e) {
            // sup
            }
        });
        updateDateAndTimeButtons();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // nothing to reset
    }
}
