package com.example.pomodoroclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    boolean isThereTimer = false;
    boolean isTimeRunning = false;
    long startTime = 60000;
    long breakTime = 25000;
    long millisToEndBreak = breakTime;
    long millisLeft = startTime;
    Button resumePauseButton, resetButton;
    CountDownTimer timer;
    CountDownTimer timerBreak;
    ProgressBar timerProgressBar;
    TextView timerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resumePauseButton = findViewById(R.id.resumePauseButton);
        resetButton = findViewById(R.id.resetButton);
        timerProgressBar = findViewById(R.id.progressBar);
        timerText = findViewById(R.id.textView);

        timerProgressBar.setProgress(100);

        resumePauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimeRunning)
                    pauseTimer();
                else
                    startTimer();
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

    private void startTimer() {
        timerProgressBar.setMax((int) TimeUnit.MILLISECONDS.toSeconds(startTime));
        timerProgressBar.setProgress(timerProgressBar.getMax());

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
                starBreak();
            }
        }.start();

        updateResumePauseButton();
    }

    private void starBreak(){
        timerProgressBar.setMax((int) TimeUnit.MILLISECONDS.toSeconds(breakTime));
        timerProgressBar.setProgress(timerProgressBar.getMax());

        isThereTimer = true;
        isTimeRunning = true;

        timerBreak = new CountDownTimer(millisToEndBreak, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisToEndBreak = millisUntilFinished;
                updateTimerProgressBreak();
            }

            @Override
            public void onFinish() {
                resetTimer();
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

        millisLeft = startTime;

        updateTimerProgress();
        resetResumePauseButton();
    }

    private void updateTimerProgressBreak(){
        String second = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(millisToEndBreak) % 60);
        String minute = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(millisToEndBreak) % 60);

        if (Integer.parseInt(second) < 10)
            second = "0" + second;

        timerText.setText(getString(R.string.time, minute, second));
        timerProgressBar.setProgress((int) TimeUnit.MILLISECONDS.toSeconds(millisToEndBreak));
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
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        isThereTimer = savedInstanceState.getBoolean("isThereTimer");
        isTimeRunning = savedInstanceState.getBoolean("isTimeRunning");
        millisLeft = savedInstanceState.getLong("millisLeft");

        updateTimerProgress();
        updateResumePauseButton();

        if (isTimeRunning)
            startTimer();
    }
}
