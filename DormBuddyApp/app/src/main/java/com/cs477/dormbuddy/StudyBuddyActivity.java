package com.cs477.dormbuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StudyBuddyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_buddy);
        if(getSupportFragmentManager().findFragmentByTag(DisplayEventFragment.DISPLAY_EVENT_TAG) == null) {
            DisplayEventFragment newFragment = DisplayEventFragment.newInstance("Study", R.drawable.map, "next door", "yesterday", "procrastination", true);
            newFragment.show(getSupportFragmentManager(), DisplayEventFragment.DISPLAY_EVENT_TAG);
        }
    }
}
