package com.cs477.dormbuddy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MapBuddyActivity extends AppCompatActivity {
    public static final String IS_GMU = "is_gmu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_buddy);
    }

    public void mapClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.interactiveCampusMap:
                intent = new Intent(this, InteractiveMapActivity.class);
                intent.putExtra(IS_GMU, true);
                break;
            case R.id.interactiveFairfaxMap:
                intent = new Intent(this, InteractiveMapActivity.class);
                intent.putExtra(IS_GMU, false);
            case R.id.campusMap:
                DisplayImageFragment displayImageFragment = DisplayImageFragment.newInstanceFromLocal(R.drawable.large_campus_map);
                displayImageFragment.show(getSupportFragmentManager(),"DisplayCampus");
                return;
            case R.id.dormMap:

                return;
            default:
                Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
    }
}
