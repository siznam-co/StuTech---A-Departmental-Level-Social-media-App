package com.example.aabi.stutech;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get id & message from intent.
        int notificationId = intent.getIntExtra("notificationId", 0);
        String message = intent.getStringExtra("todo");
        String postKey = intent.getStringExtra("postKey");
        String subjectName = intent.getStringExtra("subjectName");


        // Instantiate a Builder object.
        Notification.Builder builder = new Notification.Builder(context);
        // Creates an Intent for the Activity
        Intent pendingIntent = new Intent(context, PostDetailActivity.class);
        pendingIntent.putExtra("postKey", postKey);
        // Sets the Activity to start in a new, empty task
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        pendingIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //add properties to the builder
        builder.setSmallIcon(R.drawable.stutech_logo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getApplicationContext().getResources(),
                        R.drawable.ic_notifications_black_24dp))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                .setContentTitle(subjectName)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setContentText(message);

        builder.setContentIntent(notifyPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationId, builder.build());
    }
}
