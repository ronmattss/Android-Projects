package com.shadedgames.ronmattss.sampletask.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.shadedgames.ronmattss.sampletask.provider.TaskProvider;
import com.shadedgames.ronmattss.sampletask.util.ReminderManager;

import java.util.Calendar;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Cursor cursor = context.getContentResolver().query(TaskProvider.CONTENT_URI,null,null,null  ,null);
        // if db is empty
        if(cursor == null)
            return;
        try
        {
         cursor.moveToFirst();
         int taskIdColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TASKID);
         int dateTimeColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_DATE_TIME);
         int titleColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TITLE);
         while (!cursor.isAfterLast())
         {
             Log.d("On Boot Receiver", "hello: ");
             long taskId = cursor.getLong(taskIdColumnIndex);
             long dateTime = cursor.getLong(dateTimeColumnIndex);
             String title = cursor.getString(titleColumnIndex);

             Calendar cal = Calendar.getInstance();
             cal.setTime(new java.util.Date(dateTime));
             ReminderManager.setReminder(context, taskId,title,cal);

             cursor.moveToNext();
         }
        }
        finally {
            cursor.close();
        }
    }
}
