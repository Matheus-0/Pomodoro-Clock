package com.example.pomodoroclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    boolean isTimeRunning = false, isBreak = false;
    long startTime = 20000, breakTime = 15000;
    long millisLeft = startTime;
    ImageButton resumePauseButton, resetButton;
    CountDownTimer timer;
    ProgressBar timerProgressBar;
    TextView timerText;
    Vibrator vibrator;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resumePauseButton = findViewById(R.id.resumePauseButton);
        resetButton = findViewById(R.id.resetButton);
        timerProgressBar = findViewById(R.id.progressBar);
        timerText = findViewById(R.id.textView);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(this, R.raw.consequence);

        defineProgress();

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

    public void defineProgress() {
        timerProgressBar.setMax((int) TimeUnit.MILLISECONDS.toSeconds((isBreak) ? breakTime : startTime));
        timerProgressBar.setProgress(timerProgressBar.getMax());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);

        return true;
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

    private void alertTimerFinish() {
        mediaPlayer.start();
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

        if (Integer.parseInt(second) < 10)
            second = "0" + second;

        timerText.setText(getString(R.string.time, minute, second));
        timerProgressBar.setProgress((int) TimeUnit.MILLISECONDS.toSeconds(millisLeft));
    }

    private void updateResumePauseButton() {
        resumePauseButton.setImageResource(isTimeRunning ? R.drawable.baseline_pause_24 : R.drawable.baseline_play_arrow_24);
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
}
