package com.shadedgames.ronmattss.sampletask.fragment;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.shadedgames.ronmattss.sampletask.R;
import com.shadedgames.ronmattss.sampletask.interfaces.OnEditFinished;
import com.shadedgames.ronmattss.sampletask.provider.DatabaseHelper;
import com.shadedgames.ronmattss.sampletask.provider.TaskProvider;
import com.shadedgames.ronmattss.sampletask.util.ReminderManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.shadedgames.ronmattss.sampletask.R.layout.fragment_task_edit;

@RequiresApi(api = Build.VERSION_CODES.M)
public class TaskEditFragment extends Fragment implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        LoaderManager.LoaderCallbacks<Cursor>,CustomDialogFragment.OnInputSelected,CustomEditDialogFragment.onEditTitle {

    public static final String DEFAULT_FRAGMENT_TAG = "taskEditFragment";
    private static final int MENU_SAVE = 1;
    static final String TASK_ID = "taskId";
    static final String TASK_DATE_AND_TIME = "taskDateAndTime";


  /*  TaskProvider dab = new TaskProvider();
    TaskProvider.DatabaseHelper dbo = new TaskProvider.DatabaseHelper(getContext());*/

    // Views
    View rootView;
    EditText titleText;
    EditText notesText;
   // ImageView imageView;
    TextView dateButton;
    TextView timeButton;
    Button addTaskButton;
    ListView listView;
    long taskId;
    Calendar taskDateAndTime;
    //DatabaseHelper
    DatabaseHelper mDatabaseHelper;

    // try list items
    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X","Wierd","Wierd","Wierd","Wierd","Wierd","Wierd","Wierd","Wierd",};
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(fragment_task_edit,container,false);

        ArrayAdapter adapter;
        rootView = v.getRootView();
        titleText = (EditText) v.findViewById(R.id.title);
        notesText = (EditText) v.findViewById(R.id.notes);
        //imageView = (ImageView) v.findViewById(R.id.image);
        dateButton = (TextView) v.findViewById(R.id.task_date);
        timeButton = (TextView) v.findViewById(R.id.task_time);
        addTaskButton = (Button) v.findViewById(R.id.add_task);
        listView = (ListView) v.findViewById(R.id.item_list);
        mDatabaseHelper = new DatabaseHelper(getContext());
        populateListView();




        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogFragment dialog = new CustomDialogFragment();
                dialog.setTargetFragment(TaskEditFragment.this, 1);
                dialog.show(getFragmentManager(),"TodoDialog");

            }
        });

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
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                populateListView();
                String name = adapterView.getItemAtPosition(position).toString();
                CustomEditDialogFragment dialog = new CustomEditDialogFragment();
                Bundle bundle = new Bundle();
                Cursor data = mDatabaseHelper.getItemID(name); //get the id associated with that name
                int itemID = -1;
                while (data.moveToNext())
                {
                    itemID = data.getInt(0);
                }
                if(itemID > -1)
                {
                    bundle.putInt("todoId",itemID);
                    bundle.putString("todoTitle",name);
                }


                dialog.setArguments(bundle);
                dialog.setTargetFragment(TaskEditFragment.this,1);
                dialog.show(getFragmentManager(),"TodoEditDialog");
                // Toast.makeText(getContext(), itemID +" " + name, Toast.LENGTH_SHORT).show();

                return true;
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

      /*  Picasso.get().load(TaskProvider.getImageUrlForTask(taskId)).into(imageView, new Callback() {
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
        });*/
        updateDateAndTimeButtons();

    }


  /*  @RequiresApi(api = Build.VERSION_CODES.M)
    public void AddData(String newEntry) {
        boolean insertData = mDatabaseHelper.addData(newEntry);
        mDatabaseHelper.addData(newEntry);

        if(insertData)
            Toast.makeText(getContext(), "Saved to database", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();


    }*/

   /* @RequiresApi(api = Build.VERSION_CODES.M)
    private void showList()
    {
        Cursor data = mDatabaseHelper.getData();
        ArrayList<String> listData  = new ArrayList<>();

    }*/

    @Override
    public void sendUpdateData(String newTodoTitle, int todoID, String oldTodoTitle) {
        updateData(newTodoTitle, todoID, oldTodoTitle);
        populateListView();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onAttach()
    {
    }

    @Override
    public void sendInput(String input) {
        String todoPlaceholder = input;
        addData(todoPlaceholder);
        populateListView();
        //Toast.makeText(getContext(), input, Toast.LENGTH_SHORT).show();
       // listView.setAdapter(new ArrayAdapter<String>(getContext(),R.layout.list_layout,mobileArray));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // nothing to reset
    }
    public void updateData (String _new, int id, String old)
    {
            mDatabaseHelper.updateName(_new,id,old);
            Toast.makeText(getContext(), "Todo Updated", Toast.LENGTH_SHORT).show();
    }
    public void addData (String newEntry)
    {
        boolean insertData = mDatabaseHelper.addData(newEntry);
        if(insertData)
        {
            Toast.makeText(getContext(), "Data Inserted", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
    }
    public void populateListView()
    {
        Cursor data = mDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(data.getString(1));
        }
        //Show todo
        listView.setAdapter(new ArrayAdapter<>(getContext(),R.layout.list_layout,listData));
    }





   /* @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void sendInput(String input) {
        //mInputDisplay.setText(input);
        // This is where we add to do in the list
        // id should also be same as the above taskid?
       // AddData(input);
        dbo.addDataToTodo(input);
        showList();




    }*/

    /*private void showList() {
        Cursor data = dbo.getData();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext())
        {
            listData.add(data.getString(1));
        }
        listView.setAdapter(new ArrayAdapter<>(getContext(),R.layout.list_layout,listData));
    }*/


}
