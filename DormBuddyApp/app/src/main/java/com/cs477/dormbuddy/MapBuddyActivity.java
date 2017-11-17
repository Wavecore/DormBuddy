package com.cs477.dormbuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MapBuddyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_buddy);
    }

    public void mapClicked(View view) {
       Toast.makeText(getApplicationContext(), "Map Clicked!", Toast.LENGTH_SHORT).show();
    }
}
