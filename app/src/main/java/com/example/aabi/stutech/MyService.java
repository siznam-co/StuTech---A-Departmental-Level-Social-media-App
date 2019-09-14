package com.example.aabi.stutech;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyService extends FirebaseMessagingService {

        private static final String TAG = "MyFirebaseMsgService";
        private static final int BROADCAST_NOTIFICATION_ID = 1;

        @Override
        public void onDeletedMessages() {
            super.onDeletedMessages();
        }

        /**
         * Called when message is received.
         *
         * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
         */
        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {

            String notificationBody = "";
            String notificationTitle = "";
            String notificationData = "";
            try{
                notificationData = remoteMessage.getData().toString();
                notificationTitle = remoteMessage.getNotification().getTitle();
                notificationBody = remoteMessage.getNotification().getBody();
            }catch (NullPointerException e){
                Log.e(TAG, "onMessageReceived: NullPointerException: " + e.getMessage() );
            }
            Log.d(TAG, "onMessageReceived: data: " + notificationData);
            Log.d(TAG, "onMessageReceived: notification body: " + notificationBody);
            Log.d(TAG, "onMessageReceived: notification title: " + notificationTitle);


            String dataType = remoteMessage.getData().get("data_type");


            Log.d(TAG, "onMessageReceived: new incoming message.");

            if(dataType.equals("post")){
                String postKey = remoteMessage.getData().get("postKey");
                String senderName = remoteMessage.getData().get("senderName");
                String subjectName = remoteMessage.getData().get("subjectName");
                String userPhoto = remoteMessage.getData().get("userPhoto");
                String Desc = "";
                if(subjectName.equals("announce"))
                    Desc = senderName + " made an Announcement";
                else
                    Desc = subjectName+" has new post from "+senderName;
                sendPostNotification(postKey, Desc, senderName);
            }else if(dataType.equals("like")){
                String postKey = remoteMessage.getData().get("postKey");
                String senderName = remoteMessage.getData().get("senderName");
                String subjectName = remoteMessage.getData().get("subjectName");
                String userPhoto = remoteMessage.getData().get("userPhoto");
                String Desc = senderName+" liked your post.";
                sendPostNotification(postKey, Desc, senderName);
            }else if(dataType.equals("message")){
                String  message = remoteMessage.getData().get("message");
                String  senderId = remoteMessage.getData().get("senderId");
                String  senderName = remoteMessage.getData().get("senderName");
                String  designation = remoteMessage.getData().get("designation");
                String  RecieverName = remoteMessage.getData().get("RecieverName");
                String  userPhoto = remoteMessage.getData().get("userPhoto");
                sendMessageNotification(message, senderId, senderName, designation, RecieverName, userPhoto);
            }else if(dataType.equals("attend")){
                String lectures = remoteMessage.getData().get("lectures");
                String section = remoteMessage.getData().get("section");
                String subjectName = remoteMessage.getData().get("subjectName");

                String Desc = "New Attendance today. Subject: "+subjectName+ " | Section: "+section;
                sendAttendNotify(lectures, Desc, subjectName);
            }else if(dataType.equals("marks")){
                String  marks = remoteMessage.getData().get("marks");
                String  userId = remoteMessage.getData().get("userId");
                String  subjectName = remoteMessage.getData().get("subjectName");
                String  marksType = remoteMessage.getData().get("marksType");

                String Desc = "Subject = "+subjectName+ " | Marks Type = " +marksType;
                String title = marksType+ " marks = "+marks;

                sendMarksNotification(userId, Desc, title, subjectName);
            }else {
                String postKey = remoteMessage.getData().get("postKey");
                String senderName = remoteMessage.getData().get("senderName");
                String subjectName = remoteMessage.getData().get("subjectName");
                String userPhoto = remoteMessage.getData().get("userPhoto");

                String Desc = senderName+" commented on your post.";
                sendPostNotification(postKey, Desc, senderName);
            }
        }

    private void sendMarksNotification(String userId, String desc, String title, String subjectName) {
        Log.d(TAG, "sendChatmessageNotification: building a chatmessage notification");

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel("MyNotifications","MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        //get the notification id
        int notificationId = buildNotificationId("6473geg34");

        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "MyNotifications");
        // Creates an Intent for the Activity
        Intent pendingIntent = new Intent(getApplicationContext(), AttendanceActivity.class);
        pendingIntent.putExtra("subjectName", subjectName);
        // Sets the Activity to start in a new, empty task
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        pendingIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //add properties to the builder
        builder.setSmallIcon(R.drawable.stutech_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.ic_notifications_black_24dp))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(desc)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setContentText(title);

        builder.setContentIntent(notifyPendingIntent);
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(getApplicationContext());

        mNotificationManager.notify(notificationId, builder.build());
    }

    private void sendAttendNotify(String lectures, String desc, String subjectName){


        Log.d(TAG, "sendChatmessageNotification: building a chatmessage notification");

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel("MyNotifications","MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        //get the notification id
        int notificationId = buildNotificationId("64573ydggeg34");

        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "MyNotifications");
        // Creates an Intent for the Activity
        Intent pendingIntent = new Intent(getApplicationContext(), AttendanceActivity.class);
        pendingIntent.putExtra("subjectName", subjectName);
        // Sets the Activity to start in a new, empty task
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        pendingIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //add properties to the builder
        builder.setSmallIcon(R.drawable.stutech_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.ic_notifications_black_24dp))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(desc)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setContentText("Lectures till today = "+lectures);

        builder.setContentIntent(notifyPendingIntent);
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(getApplicationContext());

        mNotificationManager.notify(notificationId, builder.build());

    }


    private void sendMessageNotification(String message, String senderId, String senderName, String designation, String recieverName, String userPhoto) {

        if(MessageActivity.active){
            Log.d(TAG, "self notification: no need of notification");
        }
        else{

            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
                NotificationChannel channel =
                        new NotificationChannel("MyNotifications","MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
            Log.d(TAG, "sendChatmessageNotification: building a chatmessage notification");

            //get the notification id
            int notificationId = buildNotificationId("3");

            // Instantiate a Builder object.
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "MyNotifications");
            // Creates an Intent for the Activity
            Intent pendingIntent = new Intent(getApplicationContext(), MessageActivity.class);
            pendingIntent.putExtra("designation", designation);
            pendingIntent.putExtra("userName", recieverName);
            pendingIntent.putExtra("userPhoto", userPhoto);
            pendingIntent.putExtra("userId", senderId);
            // Sets the Activity to start in a new, empty task
            pendingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Creates the PendingIntent
            PendingIntent notifyPendingIntent =
                    PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            pendingIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            //add properties to the builder
            builder.setSmallIcon(R.drawable.stutech_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.drawable.ic_notifications_black_24dp))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle("New Message from "+senderName)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setContentText(message);

            builder.setContentIntent(notifyPendingIntent);
            NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
            mNotificationManager.notify(notificationId, builder.build());
        }

    }

    private void sendPostNotification(String postKey, String Desc, String senderName){

            if(senderName.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString())){
                Log.d(TAG, "self notification: no need of notification");
            }else {

                if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
                    NotificationChannel channel =
                            new NotificationChannel("MyNotifications","MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }
                Log.d(TAG, "sendChatmessageNotification: building a chatmessage notification");

                //get the notification id
                int notificationId = buildNotificationId(postKey);

                // Instantiate a Builder object.
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "MyNotifications");
                // Creates an Intent for the Activity
                Intent pendingIntent = new Intent(getApplicationContext(), PostDetailActivity.class);
                if(postKey.equals("")){
                    pendingIntent.putExtra("subjectName", senderName);
                }else{
                    pendingIntent.putExtra("postKey", postKey);
                }
                // Sets the Activity to start in a new, empty task
                pendingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // Creates the PendingIntent
                PendingIntent notifyPendingIntent =
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                pendingIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                //add properties to the builder
                builder.setSmallIcon(R.drawable.stutech_logo)
                        .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.drawable.ic_notifications_black_24dp))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentTitle("StuTech!")
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setContentText(Desc);

                builder.setContentIntent(notifyPendingIntent);
                NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
                mNotificationManager.notify(notificationId, builder.build());
            }
        }


        private int buildNotificationId(String id){
            Log.d(TAG, "buildNotificationId: building a notification id.");

            int notificationId = 0;
            for(int i = 0; i < 9; i++){
                notificationId = notificationId + id.charAt(0);
            }
            Log.d(TAG, "buildNotificationId: id: " + id);
            Log.d(TAG, "buildNotificationId: notification id:" + notificationId);
            return notificationId;
        }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    /*@Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        *//*String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "initFCM: token: " + token);*//*
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {

        Log.d(TAG, "sendRegistrationToServer: sending token to server: " + token);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("FCMTokens")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("messaging_token")
                .setValue(token);
    }*/

}