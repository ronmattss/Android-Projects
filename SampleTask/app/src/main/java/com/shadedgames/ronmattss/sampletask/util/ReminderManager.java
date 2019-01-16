package com.shadedgames.ronmattss.sampletask.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.shadedgames.ronmattss.sampletask.provider.TaskProvider;
import com.shadedgames.ronmattss.sampletask.receiver.OnAlarmReceiver;

import java.util.Calendar;

public class ReminderManager {
    private ReminderManager(){}
    public static void setReminder(Context context, long taskId, String title, Calendar when)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, OnAlarmReceiver.class);

        i.putExtra(TaskProvider.COLUMN_TASKID,taskId);
        i.putExtra(TaskProvider.COLUMN_TITLE,title);

        PendingIntent pi = PendingIntent.getBroadcast(context,(int)taskId,i,PendingIntent.FLAG_ONE_SHOT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,when.getTimeInMillis(),pi);

    }
}
