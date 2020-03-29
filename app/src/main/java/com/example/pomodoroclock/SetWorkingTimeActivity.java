package com.example.pomodoroclock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SetWorkingTimeActivity extends AppCompatActivity {

    EditText hours, minutes, seconds;
    Button btn;
    ImageView workingViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.working_time);

        hours = findViewById(R.id.hoursBox);
        minutes = findViewById(R.id.minutesBox);
        seconds = findViewById(R.id.secondBox);
        btn = findViewById(R.id.setTime);
        workingViewer = findViewById(R.id.workingTimeView);
        workingViewer.setImageResource(R.drawable.working_time_icon);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            workingViewer.getLayoutParams().height = 400;
        else {
            workingViewer.getLayoutParams().height = 200;
            workingViewer.setX(20);
            workingViewer.setY(70);
        }

        long startTime = Objects.requireNonNull(getIntent().getExtras()).getLong("hours");

        setHints(startTime);
        onStart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void setHints(long value) {
        String second = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(value) % 60);
        String minute = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(value) % 60);
        String hour = String.valueOf(TimeUnit.MILLISECONDS.toHours(value) % 24);

        hours.setHint(hour);
        minutes.setHint(minute);
        seconds.setHint(second);
    }
}
