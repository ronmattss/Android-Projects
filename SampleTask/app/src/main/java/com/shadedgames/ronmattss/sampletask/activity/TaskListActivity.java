package com.shadedgames.ronmattss.sampletask.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toolbar;

import com.shadedgames.ronmattss.sampletask.R;
import com.shadedgames.ronmattss.sampletask.interfaces.OnEditTask;
import com.shadedgames.ronmattss.sampletask.provider.TaskProvider;

public class TaskListActivity extends Activity implements OnEditTask {

    @Override
    public void editTask(long id) {
        // When we are asked to edit or insert a task, start the
        // TaskEditActivity with the id of the task to edit.
        startActivity(new Intent(this, TaskEditActivity.class)
                .putExtra(TaskProvider.COLUMN_TASKID, id));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        setActionBar((Toolbar) findViewById(R.id.toolbar));


    }


}
