package com.cs477.dormbuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buddyClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.laundryBuddyImageButton:
                intent = new Intent(this, LaundryBuddyActivity.class);
                break;
            case R.id.mapBuddyImageButton:
                intent = new Intent(this, MapBuddyActivity.class);
                break;
            case R.id.studyBuddyImageButton:
                intent = new Intent(this, StudyBuddyActivity.class);
                break;
            case R.id.eventBuddyImageButton:
                intent = new Intent(this, EventBuddyActivity.class);
                break;
            case R.id.profileBuddyImageButton:
                intent = new Intent(this, ProfileBuddyActivity.class);
                break;
            default:
                Toast.makeText(this, "Error Retrieving Page", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
    }
}
