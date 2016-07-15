package com.example.hoang.datingproject.Activity;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.FontManager;

public class FeedbackActivity extends AppCompatActivity {

    TextView exit_button, right_arrow_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        exit_button = (TextView) findViewById(R.id.exit_button);
        right_arrow_button = (TextView) findViewById(R.id.right_arrow_button);
        Typeface font = FontManager.getTypeface(FeedbackActivity.this, FontManager.FONTAWESOME);
        exit_button.setTypeface(font);
        right_arrow_button.setTypeface(font);

        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
