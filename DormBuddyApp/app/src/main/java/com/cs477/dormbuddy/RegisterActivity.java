package com.cs477.dormbuddy;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_ID;
import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_NAME;
import static com.cs477.dormbuddy.LocalUserHelper.USER_NAME;
import static com.cs477.dormbuddy.LocalUserHelper.USER_ICON;
import static com.cs477.dormbuddy.LocalUserHelper.USER_LOGGED_IN;
import static com.cs477.dormbuddy.LocalUserHelper.ROOM_NUMBER;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_USER;
import static com.cs477.dormbuddy.LocalUserHelper.USER_ID;

public class RegisterActivity extends AppCompatActivity {
    EditText roomNumberView;
    Spinner buildingSpinnerView;
    Button registerOrUpdateButton;
    private SQLiteDatabase db = null;
    private LocalUserHelper dbHelper = null;
    private Cursor mCursor;
    ArrayAdapter<CharSequence> mAdapter;
    final static String[] columns = { USER_ID, USER_NAME, USER_LOGGED_IN, BUILDING_ID, BUILDING_NAME, ROOM_NUMBER, USER_ICON };
    String storedGNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //check if user is logged in first, redirect them to log in if not
        dbHelper = new LocalUserHelper(this);
        db = dbHelper.getWritableDatabase();
        roomNumberView = findViewById(R.id.roomNumber);
        buildingSpinnerView = findViewById(R.id.buildingSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        mAdapter = ArrayAdapter.createFromResource(this, R.array.GMU_houses, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        buildingSpinnerView.setAdapter(mAdapter);
        mCursor = db.query(TABLE_USER, columns, null, new String[] {}, null, null,
                null); //to check if user exists
        registerOrUpdateButton = findViewById(R.id.registerOrUpdate);
        //try to get the user
        try { //if try is successful, changes register button to update button
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
            } else { //user exists but is not logged in => user becomes logged in
                loginUser();
            }
        } catch (Exception e) { //user not in the system yet, so proceed without modifying buttons
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
        if (roomNumberView.getText().toString().equals("")) { //checks that room number is not empty
            Toast.makeText(this, "Enter a room number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //creates new suer
    public void onRegisterClicked() {
        ContentValues cv = new ContentValues(7);
        cv.put(USER_ID, 123456); //g number
        cv.put(USER_NAME, "John Doe");
        cv.put(USER_LOGGED_IN, 1);
        cv.put(BUILDING_ID, buildingSpinnerView.getSelectedItemPosition());
        cv.put(BUILDING_NAME, buildingSpinnerView.getSelectedItem().toString());
        cv.put(ROOM_NUMBER, roomNumberView.getText().toString());
        cv.put(USER_ICON, new byte[]{}); //icon is just an empty byte array to start
        db.insert(TABLE_USER, null, cv);
        db.close();
        mCursor.close();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    //modifies existing user data
    public void onUpdateClicked() {
        ContentValues cv = new ContentValues(3);
        cv.put(BUILDING_ID, buildingSpinnerView.getSelectedItemPosition());
        cv.put(BUILDING_NAME, buildingSpinnerView.getSelectedItem().toString());
        cv.put(ROOM_NUMBER, roomNumberView.getText().toString());
        //updates the 3 fields
        db.update(TABLE_USER, cv, USER_ID + "=" + storedGNumber, null);
        db.close();
        mCursor.close();
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
