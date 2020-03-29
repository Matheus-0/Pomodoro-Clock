package com.example.pomodoroclock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class SetWorkingTimeActivity extends AppCompatActivity {

    EditText editText;
    Button btn;
    ImageView workingViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.working_time);
        editText = findViewById(R.id.time);
        btn = findViewById(R.id.setTime);
        workingViewer = findViewById(R.id.workingTimeView);
        workingViewer.setImageResource(R.drawable.working_time_icon);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) workingViewer.getLayoutParams().height = 400;
        else workingViewer.getLayoutParams().height = 200;
        workingViewer.requestLayout();
    }
}
