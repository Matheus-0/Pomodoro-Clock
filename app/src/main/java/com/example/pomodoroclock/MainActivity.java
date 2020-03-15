package com.example.pomodoroclock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    TextView timerText;

    public void startTimer(View view) {
        timerText = findViewById(R.id.textView);

        new CountDownTimer(1500000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                String second = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                String minute = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60);

                if (Integer.parseInt(second) < 10)
                    second = "0" + second;

                timerText.setText(getString(R.string.time, minute, second));
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);

        return true;
    }
}
