package com.cs477.dormbuddy;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import static com.cs477.dormbuddy.LocalUserHelper.USER_NAME;
import static com.cs477.dormbuddy.LocalUserHelper.USER_LOGGED_IN;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_USER;
import static com.cs477.dormbuddy.LocalUserHelper.USER_ID;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db = null;
    private LocalUserHelper dbHelper = null;
    private Cursor mCursor;
    final static String[] columns = { USER_ID, USER_NAME, USER_LOGGED_IN };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //check if user is logged in first, redirect them to log in if not
        dbHelper = new LocalUserHelper(this);
        db = dbHelper.getWritableDatabase();
        mCursor = db.query(TABLE_USER, columns, USER_LOGGED_IN+" = 1", new String[] {}, null, null,
                null);
        try { //if the user ever logged in, there will be a row in the database, but check that they didnt log out
            mCursor.moveToPosition(0);
            int isLoggedIn = mCursor.getInt(2);
            if (isLoggedIn == 1) {
                Toast.makeText(this, "Greetings, buddy " + mCursor.getString(1), Toast.LENGTH_SHORT).show(); //welcome message
                //user is logged in, therefore show activity_main
                return;
            } else {
                startActivity(new Intent(this, CredentialsActivity.class));
            }
        } catch (Exception e) { //otherwise direct to login
            startActivity(new Intent(this, CredentialsActivity.class));
        }
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
        startActivity(intent); //main activity never ends
    }
}
