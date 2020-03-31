package com.example.pomodoroclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    boolean isTimeRunning = false, isBreak = false;
    final static long DEFAULT_WORKING_TIME = 3600000;
    final static long DEFAULT_BREAK_TIME = 900000;
    long startTime, breakTime;
    long millisLeft;
    ImageButton resumePauseButton, resetButton;
    CountDownTimer timer;
    ProgressBar timerProgressBar;
    SharedPreferences settings;
    TextView timerText;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resumePauseButton = findViewById(R.id.resumePauseButton);
        resetButton = findViewById(R.id.resetButton);
        timerProgressBar = findViewById(R.id.progressBar);
        timerText = findViewById(R.id.textView);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        settings = getApplicationContext().getSharedPreferences("times", MODE_PRIVATE);

        startTime = settings.getLong("startTimeSet", DEFAULT_WORKING_TIME);
        breakTime = settings.getLong("breakTimeSet", DEFAULT_BREAK_TIME);

        millisLeft = (isBreak) ? breakTime : startTime;

        defineProgress();
        updateTimerProgress();

        resumePauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimeRunning) pauseTimer();
                else startTimer();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.workingTimerOption:
                Intent myIntent = new Intent(MainActivity.this, SetTimeActivity.class);

                myIntent.putExtra("millis", startTime);
                myIntent.putExtra("requestCode", 10);
                startActivityForResult(myIntent, 10);

                return true;
            case R.id.breakTimerOption:
                Intent breakIntent = new Intent(MainActivity.this, SetTimeActivity.class);

                breakIntent.putExtra("millis", breakTime);
                breakIntent.putExtra("requestCode", 20);
                startActivityForResult(breakIntent, 20);


                return true;
            case R.id.setSound:
                Toast.makeText(this, "Okay", Toast.LENGTH_SHORT).show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("isTimeRunning", isTimeRunning);
        outState.putLong("millisLeft", millisLeft);
        outState.putBoolean("isBreak", isBreak);

        if (isTimeRunning)
            destroyTimer();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        isTimeRunning = savedInstanceState.getBoolean("isTimeRunning");
        millisLeft = savedInstanceState.getLong("millisLeft");
        isBreak = savedInstanceState.getBoolean("isBreak");

        defineProgress();
        updateTimerProgress();

        if (millisLeft != startTime)
            updateResumePauseButton();

        if (isTimeRunning)
            startTimer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 10) {
                startTime = settings.getLong("startTimeSet", DEFAULT_WORKING_TIME);
                resetTimer();
                defineProgress();
            }
            else if (requestCode == 20) {
                breakTime = settings.getLong("breakTimeSet", DEFAULT_BREAK_TIME);
                resetTimer();
                defineProgress();
            }
        }
    }

    private void startTimer() {
        isTimeRunning = true;

        timer = new CountDownTimer(millisLeft, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
                millisLeft = millisUntilFinished;
                updateTimerProgress();
            }

            @Override
            public void onFinish() {
                alertTimerFinish();
                changeTimerType();
                defineProgress();
                startTimer();
            }
        }.start();

        updateResumePauseButton();
    }

    public void defineProgress() {
        timerProgressBar.setMax((int) TimeUnit.MILLISECONDS.toSeconds((isBreak) ? breakTime : startTime));
        timerProgressBar.setProgress(timerProgressBar.getMax());
    }

    private void alertTimerFinish() {
        vibrator.vibrate(1000);
    }

    private void changeTimerType() {
        millisLeft = (!isBreak) ? breakTime : startTime;
        isBreak = !isBreak;
    }

    private void destroyTimer() {
        timer.cancel();
        isTimeRunning = false;
    }

    private void pauseTimer() {
        destroyTimer();
        updateResumePauseButton();
    }

    private void resetTimer() {
        if (isTimeRunning)
            destroyTimer();

        millisLeft = (!isBreak) ? startTime : breakTime;

        updateTimerProgress();
        updateResumePauseButton();
    }

    private void updateTimerProgress() {
        String second = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(millisLeft) % 60);
        String minute = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(millisLeft) % 60);
        String hour = String.valueOf(TimeUnit.MILLISECONDS.toHours(millisLeft));

        int hourInt = Integer.parseInt(hour);

        if (Integer.parseInt(minute) < 10 && hourInt > 0)
            minute = "0" + minute;
        if (Integer.parseInt(second) < 10)
            second = "0" + second;

        if (hourInt > 0)
            timerText.setText(getString(R.string.hour_time, hour, minute, second));
        else
            timerText.setText(getString(R.string.time, minute, second));

        timerProgressBar.setProgress((int) TimeUnit.MILLISECONDS.toSeconds(millisLeft));
    }

    private void updateResumePauseButton() {
        resumePauseButton.setImageResource(isTimeRunning ? R.drawable.baseline_pause_24 : R.drawable.baseline_play_arrow_24);
    }
}
