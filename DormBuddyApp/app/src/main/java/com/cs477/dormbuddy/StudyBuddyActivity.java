package com.cs477.dormbuddy;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.Calendar;

public class StudyBuddyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_buddy);
        if(getSupportFragmentManager().findFragmentByTag(DisplayEventFragment.DISPLAY_EVENT_TAG) == null) {
            DisplayEventFragment newFragment = DisplayEventFragment.newInstance("Study", R.drawable.map, "next door", "yesterday", "procrastination", true);
            newFragment.show(getSupportFragmentManager(), DisplayEventFragment.DISPLAY_EVENT_TAG);
        }
        ListView studyList = (ListView)findViewById(R.id.reservationList);
        Reservation[] reservations = Reservation.getStudy(this, Calendar.getInstance());
        if(reservations.length != 0)
            studyList.setBackground(null);
        else
            studyList.setBackground(ContextCompat.getDrawable(this,R.drawable.empty));



    }
}
