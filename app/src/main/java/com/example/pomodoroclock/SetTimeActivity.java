package com.example.pomodoroclock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SetTimeActivity extends AppCompatActivity {
    String key;
    EditText hours, minutes, seconds;
    Button btn;
    ImageView viewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.set_time);

        hours = findViewById(R.id.hoursBox);
        minutes = findViewById(R.id.minutesBox);
        seconds = findViewById(R.id.secondBox);
        btn = findViewById(R.id.setTime);
        viewer = findViewById(R.id.workingTimeView);

        if (Objects.requireNonNull(this.getIntent().getExtras()).getInt("requestCode") == 10)
            key = "startTimeSet";
        else {
            viewer.setImageResource(R.drawable.baseline_free_breakfast_24);
            key = "breakTimeSet";
        }


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewer.getLayoutParams().height = 500;
            viewer.getLayoutParams().width = 500;
        }
        else {
            viewer.getLayoutParams().height = 200;
            viewer.getLayoutParams().width = 200;
        }

        onStart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long x = 0, y = 0, z = 0;

                Editable hoursText = hours.getText();
                Editable minutesText = minutes.getText();
                Editable secondsText = seconds.getText();

                boolean any = false;

                String errorMessage = getString(R.string.error_edit_text);
                String allZeros = getString(R.string.all_zeros);

                if (TextUtils.isEmpty(hoursText) && TextUtils.isEmpty(minutesText) && TextUtils.isEmpty(secondsText)) {
                    hours.setError(errorMessage);
                    minutes.setError(errorMessage);
                    seconds.setError(errorMessage);

                    any = true;
                }

                if (hoursText.toString().equals("0") && minutesText.toString().equals("0") && secondsText.toString().equals("0")) {
                    hours.setError(allZeros);
                    minutes.setError(allZeros);
                    seconds.setError(allZeros);

                    any = true;
                }

                if (!any) {
                    if (!hoursText.toString().equals("")) x = TimeUnit.HOURS.toMillis(Long.parseLong(hoursText.toString()));
                    if (!minutesText.toString().equals("")) y = TimeUnit.MINUTES.toMillis(Long.parseLong(minutesText.toString()));
                    if (!secondsText.toString().equals("")) z = TimeUnit.SECONDS.toMillis(Long.parseLong(secondsText.toString()));

                    Intent result = new Intent();

                    if (key.equals("startTimeSet"))
                        result.putExtra("startTime", x + y + z);
                    else
                        result.putExtra("breakTime", x + y + z);

                    setResult(RESULT_OK, result);
                    finish();
                }
            }
        });
    }
}
