package com.example.pomodoroclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    boolean isThereTimer = false;
    boolean isTimeRunning = false;
    boolean isBreak = false;
    long startTime = 60000;
    long breakTime = 25000;
    long millisLeft = startTime;
    Button resumePauseButton, resetButton;
    CountDownTimer timer;
    ProgressBar timerProgressBar;
    TextView timerText;
    Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resumePauseButton = findViewById(R.id.resumePauseButton);
        resetButton = findViewById(R.id.resetButton);
        timerProgressBar = findViewById(R.id.progressBar);
        timerText = findViewById(R.id.textView);
        v = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if(!isBreak) defineProgress(startTime);
        else defineProgress(breakTime);

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

    public void defineProgress(long var){
        if(!isBreak){
            timerProgressBar.setMax((int) TimeUnit.MILLISECONDS.toSeconds(var));
            timerProgressBar.setProgress(timerProgressBar.getMax());
        }
        else{
            timerProgressBar.setMax((int) TimeUnit.MILLISECONDS.toSeconds(var));
            timerProgressBar.setProgress(timerProgressBar.getMax());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);

        return true;
    }

    private void startTimer() {
        if(!isBreak) defineProgress(startTime);
        else defineProgress(breakTime);

        isThereTimer = true;
        isTimeRunning = true;

        timer = new CountDownTimer(millisLeft, 10) {

            @Override
            public void onTick(long millisUntilFinished) {
                millisLeft = millisUntilFinished;
                updateTimerProgress();
            }

            @Override
            public void onFinish() {
                resetTimer();
                if(!isBreak) {
                    millisLeft = breakTime;
                    isBreak = true;
                }
                else {
                    millisLeft = startTime;
                    isBreak = false;
                }
                v.vibrate(1000);
                startTimer();
            }
        }.start();

        updateResumePauseButton();
    }

    private void destroyTimer() {
        timer.cancel();
        isThereTimer = false;
        isTimeRunning = false;
    }

    private void pauseTimer() {
        destroyTimer();
        updateResumePauseButton();
    }

    private void resetTimer() {
        if (isThereTimer)
            destroyTimer();

        if(!isBreak) millisLeft = startTime;
        else millisLeft = breakTime;

        updateTimerProgress();
        resetResumePauseButton();
    }

    private void updateTimerProgress() {
        String second = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(millisLeft) % 60);
        String minute = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(millisLeft) % 60);

        if (Integer.parseInt(second) < 10)
            second = "0" + second;

        timerText.setText(getString(R.string.time, minute, second));
        timerProgressBar.setProgress((int) TimeUnit.MILLISECONDS.toSeconds(millisLeft));
    }

    private void updateResumePauseButton() {
        resumePauseButton.setText(getString((isTimeRunning) ? R.string.button_pause : R.string.button_play));
    }

    private void resetResumePauseButton() {
        resumePauseButton.setText(getString(R.string.button_default));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("isThereTimer", isThereTimer);
        outState.putBoolean("isTimeRunning", isTimeRunning);
        outState.putLong("millisLeft", millisLeft);
        outState.putBoolean("isBreak", isBreak);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        isThereTimer = savedInstanceState.getBoolean("isThereTimer");
        isTimeRunning = savedInstanceState.getBoolean("isTimeRunning");
        millisLeft = savedInstanceState.getLong("millisLeft");
        isBreak = savedInstanceState.getBoolean("isBreak");

        updateTimerProgress();
        updateResumePauseButton();

        if (isTimeRunning)
            startTimer();
    }
}
