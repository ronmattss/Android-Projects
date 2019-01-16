package com.shadedgames.ronmattss.sampletask.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.shadedgames.ronmattss.sampletask.R;
import com.shadedgames.ronmattss.sampletask.activity.TaskEditActivity;
import com.shadedgames.ronmattss.sampletask.provider.TaskProvider;

public class OnAlarmReceiver extends BroadcastReceiver {

    final public static String channelId = "CHANNEL_ID";
    @Override
    public void onReceive(Context context, Intent intent) {
            // for the data
        Intent taskEditIntent = new Intent(context, TaskEditActivity.class);
        long taskId = intent.getLongExtra(TaskProvider.COLUMN_TASKID, -1);
        String title = intent.getStringExtra(TaskProvider.COLUMN_TITLE);
        taskEditIntent.putExtra(TaskProvider.COLUMN_TASKID,taskId);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // FOR android 8.0 and up needed channel for notifs.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(channelId, title, importance);
            mgr.createNotificationChannel(channel);

        }

        PendingIntent pi = PendingIntent.getActivity(context, (int) taskId,taskEditIntent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder note = new NotificationCompat.Builder(context,channelId)
                .setContentTitle(context.getString(R.string.notify_new_task_title))
                .setContentText(title).setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //Send the Notification.
        mgr.notify((int)0,note.build());

        }


    }


/* else
            {
                PendingIntent pi = PendingIntent.getActivity(context,0,taskEditIntent,PendingIntent.FLAG_ONE_SHOT);
                //Build the notification object using a notif builder
                Notification note = new Notification.Builder(context).setContentTitle(context.getString(R.string.notify_new_task_title))
                        .setContentText(title).setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .build();
                //Send the Notification.
                // TODO: Clear notifs
                mgr.notify((int) taskId,note);
        }*/