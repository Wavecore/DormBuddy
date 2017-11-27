package com.cs477.dormbuddy;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_ID;
import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_NAME;
import static com.cs477.dormbuddy.LocalUserHelper.ROOM_TYPE;
import static com.cs477.dormbuddy.LocalUserHelper.ROOM_TYPE_DORM;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_BUILDING;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_ROOM;
import static com.cs477.dormbuddy.LocalUserHelper.USER_NAME;
import static com.cs477.dormbuddy.LocalUserHelper.USER_ICON;
import static com.cs477.dormbuddy.LocalUserHelper.USER_LOGGED_IN;
import static com.cs477.dormbuddy.LocalUserHelper.ROOM_NUMBER;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_USER;
import static com.cs477.dormbuddy.LocalUserHelper.USER_ID;

public class RegisterActivity extends AppCompatActivity {
    Spinner buildingSpinnerView;
    Button registerOrUpdateButton;
    Spinner roomSpinnerView;
    private SQLiteDatabase db = null;
    private LocalUserHelper dbHelper = null;
    private Cursor mCursor;
    private ArrayAdapter<CharSequence> mAdapter;
    private ArrayAdapter<CharSequence> cAdapter;
    Cursor cCursor;
    final static String[] columns = { USER_ID, USER_NAME, USER_LOGGED_IN, BUILDING_ID, BUILDING_NAME, ROOM_NUMBER, USER_ICON };
    String storedGNumber;
    private int buildingID = 0;
    private String roomNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //check if user is logged in first, redirect them to log in if not
        dbHelper = new LocalUserHelper(this);
        db = dbHelper.getWritableDatabase();
        buildingSpinnerView = findViewById(R.id.buildingSpinner);
        roomSpinnerView = findViewById(R.id.registerRoomSpinner);
        roomSpinnerView.setFocusable(false);
        // Create an ArrayAdapter using the a Cursor contain buildings and a default spinner layout
        cCursor = db.query(TABLE_BUILDING,new String[]{BUILDING_ID,BUILDING_NAME},null, new String[]{},BUILDING_ID, BUILDING_ID+" > 0",BUILDING_ID);
        mAdapter = new ArrayAdapter<CharSequence>(this,R.layout.spinner_item,R.id.spinnerItem);
        mAdapter.add("Select a Building");
        if (cCursor.moveToFirst()) {
            while ( !cCursor.isAfterLast() ) {
                mAdapter.add(cCursor.getString(1));
                cCursor.moveToNext();
            }
        }
        cCursor.close();
        // Apply the adapter to the spinner
        buildingSpinnerView.setAdapter(mAdapter);
        buildingSpinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cAdapter = new ArrayAdapter<CharSequence>(view.getContext(),R.layout.spinner_item,R.id.spinnerItem);
                int roomPosition = 0;
                if(i != 0) {
                    cAdapter.add("Select a room");
                    Cursor c = db.query(TABLE_ROOM, new String[]{ROOM_NUMBER}, ROOM_TYPE + " = '" + ROOM_TYPE_DORM + "' AND "+BUILDING_ID+" = "+i, new String[]{}, null, null, null);
                    int p = 1;
                    if (c.moveToFirst()) {
                        while (!c.isAfterLast()) {
                            if(roomNum != null && c.getString(0).contentEquals(roomNum))
                                roomPosition = p;
                            cAdapter.add(c.getString(0));
                            c.moveToNext();
                            p++;
                        }
                    }
                    c.close();
                }
                else{
                    roomSpinnerView.setFocusable(false);
                }
                roomSpinnerView.setAdapter(cAdapter);
                if(roomPosition != 0) {
                    System.out.println(cAdapter.getPosition(roomNum));
                    roomSpinnerView.setSelection(roomPosition);
                    roomNum = null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Check to see if there is a user logged in
        mCursor = db.query(TABLE_USER, columns, USER_ID+" >= 0", new String[] {}, null, null,null); //to check if logged in user exists
        registerOrUpdateButton = findViewById(R.id.registerOrUpdate);
        //try to get the user
        if (mCursor.moveToFirst()) { //if try is successful, changes register button to update button
            mCursor.moveToPosition(0);
            storedGNumber = "" + mCursor.getInt(0); //gets the g number
            int isLoggedIn = mCursor.getInt(2);
            if (isLoggedIn == 1) { //user is not logged in but exists => he clicked change room
                registerOrUpdateButton.setText("Update");
                registerOrUpdateButton.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                registerOrUpdateButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (inputIsCorrect())
                            onUpdateClicked();
                    }
                });
                roomNum = mCursor.getString(5);
                buildingSpinnerView.setSelection( mCursor.getInt(3));
            } else { //user exists but is not logged in => user becomes logged in
                loginUser();
            }
        }
        else{ //user not in the system yet, so proceed without modifying buttons
            registerOrUpdateButton.setText("Register");
            registerOrUpdateButton.setBackgroundColor(getResources().getColor(R.color.colorAmber));
            registerOrUpdateButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (inputIsCorrect())
                        onRegisterClicked();
                }
            });
        }
    }

    private Boolean inputIsCorrect(){
        if(buildingSpinnerView.getSelectedItemPosition() == 0){
            Toast.makeText(this,"A building must be selected",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(roomSpinnerView.getSelectedItemPosition() == 0){
            Toast.makeText(this, "A room must be selected",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //creates new user
    public void onRegisterClicked() {

        ContentValues cv = new ContentValues(7);
        cv.put(USER_ID, 123456); //g number
        cv.put(USER_NAME, "John Doe");
        cv.put(USER_LOGGED_IN, 1);
        cv.put(BUILDING_ID, buildingSpinnerView.getSelectedItemPosition());
        cv.put(BUILDING_NAME, buildingSpinnerView.getSelectedItem().toString());
        cv.put(ROOM_NUMBER, roomSpinnerView.getSelectedItem().toString());
        cv.put(USER_ICON, new byte[]{}); //icon is just an empty byte array to start
        db.insert(TABLE_USER, null, cv);
        db.close();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    //modifies existing user data
    public void onUpdateClicked() {
        ContentValues cv = new ContentValues(3);
        cv.put(BUILDING_ID, buildingSpinnerView.getSelectedItemPosition());
        cv.put(BUILDING_NAME, buildingSpinnerView.getSelectedItem().toString());
        cv.put(ROOM_NUMBER, roomSpinnerView.getSelectedItem().toString());
        //updates the 3 fields
        db.update(TABLE_USER, cv, USER_ID + "=" + storedGNumber, null);
        db.close();
       // mCursor.close();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void loginUser() {
        ContentValues cv = new ContentValues(1);
        cv.put(USER_LOGGED_IN, 1);
        //marks user as logged in
        db.update(TABLE_USER, cv, USER_ID + "=" + storedGNumber, null);
        db.close();
        mCursor.close();
        finish();
    }
}
