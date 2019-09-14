package com.example.aabi.stutech;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity implements View.OnClickListener {

    private int notificationId = 1;
    TextView textView;
    String postKey, subjectName, todo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        textView = findViewById(R.id.editText);

        // Set Onclick Listener.
        findViewById(R.id.setBtn).setOnClickListener(this);
        findViewById(R.id.cancelBtn).setOnClickListener(this);

        postKey = getIntent().getExtras().getString("postKey");
        todo = getIntent().getExtras().getString("title");
        textView.setText(getIntent().getExtras().getString("subjectName"));
        subjectName = getIntent().getExtras().getString("subjectName");
        //textView.setText("sdkfjskdlf");
    }

    @Override
    public void onClick(View view) {
        TimePicker timePicker = findViewById(R.id.timePicker);

        // Set notificationId & text.
        Intent intent = new Intent(ReminderActivity.this, AlarmReceiver.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("todo", todo);
        intent.putExtra("postKey", postKey);
        intent.putExtra("subjectName", subjectName);

        // getBroadcast(context, requestCode, intent, flags)
        PendingIntent alarmIntent = PendingIntent.getBroadcast(ReminderActivity.this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        switch (view.getId()) {
            case R.id.setBtn:
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                // Create time.
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, hour);
                startTime.set(Calendar.MINUTE, minute);
                startTime.set(Calendar.SECOND, 0);
                long alarmStartTime = startTime.getTimeInMillis();

                // Set alarm.
                // set(type, milliseconds, intent)
                alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);

                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;

            case R.id.cancelBtn:
                alarm.cancel(alarmIntent);
                Toast.makeText(this, "Canceled.", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
