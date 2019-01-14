package com.shadedgames.ronmattss.sampletask.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.widget.Toolbar;

import com.shadedgames.ronmattss.sampletask.R;
import com.shadedgames.ronmattss.sampletask.fragment.TaskEditFragment;
import com.shadedgames.ronmattss.sampletask.interfaces.OnEditFinished;
import com.shadedgames.ronmattss.sampletask.provider.TaskProvider;

public class TaskEditActivity extends Activity implements OnEditFinished {

    public static final String EXTRA_TASKID = "taskId";

    @Override
    public void finishEditingTask() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        setActionBar((Toolbar)findViewById(R.id.toolbar));

        long id = getIntent().getLongExtra(TaskProvider.COLUMN_TASKID,0L);

        Fragment fragment = TaskEditFragment.newInstance(id);
        String fragmentTag = TaskEditFragment.DEFAULT_FRAGMENT_TAG;
        if(savedInstanceState == null)
        {
            getFragmentManager().beginTransaction()
                    .add(R.id.container,fragment,fragmentTag).commit();
        }
    }


}
