package com.example.signinscreen;

import android.os.Bundle;
import android.widget.TextView;

public class NextActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView nextActivityView;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        nextActivityView = findViewById(R.id.next_activity_text);
    }
}
