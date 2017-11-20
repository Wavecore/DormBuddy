package com.cs477.dormbuddy;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class EventBuddyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_buddy);

        DisplayEventFragment newFragment = DisplayEventFragment.newInstance("Test",R.drawable.laundry_machine,"here","now","Roses are read\nViolets are violet\n You had one job\n Don't fck it up\n",false);
        newFragment.show(getSupportFragmentManager(),"here");
    }

    public void createEventClicked(View view) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }
}
