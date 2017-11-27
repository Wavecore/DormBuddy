package com.cs477.dormbuddy;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_ID;
import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_NAME;
import static com.cs477.dormbuddy.LocalUserHelper.USER_NAME;
import static com.cs477.dormbuddy.LocalUserHelper.USER_ICON;
import static com.cs477.dormbuddy.LocalUserHelper.USER_LOGGED_IN;
import static com.cs477.dormbuddy.LocalUserHelper.ROOM_NUMBER;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_USER;
import static com.cs477.dormbuddy.LocalUserHelper.USER_ID;

public class ProfileBuddyActivity extends AppCompatActivity {
    TextView nameView, gNumberView, buildingNameView, roomNumberView;
    ImageView img; //user image
    private SQLiteDatabase db = null;
    private LocalUserHelper dbHelper = null;
    private Cursor mCursor;
    private String storedGNumber;
    final static String[] columns = { USER_ID, USER_NAME, USER_LOGGED_IN, BUILDING_ID, BUILDING_NAME, ROOM_NUMBER, USER_ICON };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_buddy);
        nameView = findViewById(R.id.nameText);
        gNumberView = findViewById(R.id.gNumberText);
        buildingNameView = findViewById(R.id.buildingText);
        roomNumberView = findViewById(R.id.roomText);
        dbHelper = new LocalUserHelper(this);
        db = dbHelper.getWritableDatabase();
        mCursor = db.query(TABLE_USER, columns, USER_LOGGED_IN+" = 1", new String[] {}, null, null,
                null);
        try { //since user is logged in, just gobble up his user data
            mCursor.moveToPosition(0);
            storedGNumber = "" + mCursor.getInt(0);
            String gNumber = "G" + storedGNumber;
            gNumberView.setText(gNumber);
            nameView.setText(mCursor.getString(1));
            buildingNameView.setText(mCursor.getString(4));
            roomNumberView.setText(mCursor.getString(5));
            byte[] userImage = mCursor.getBlob(6);
            if (userImage.length > 0) {
                displayImage(userImage); //image exists -- display it
            }
            mCursor.close();
        } catch (Exception e) {
            startActivity(new Intent(this, CredentialsActivity.class));
        }
    }

    public void changeHousingClicked(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    public void uploadImage(View view) {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 3);
    }

    //user intentionally logging out just updates the logged in column
    public void logoutClicked(View view) {
        ContentValues cv = new ContentValues(1);
        cv.put(USER_LOGGED_IN, 0); //gives illusion of being logged out but user info is still in table
        db.update(TABLE_USER, cv, USER_ID + "=" + storedGNumber, null);
        db.close();
        mCursor.close();
        startActivity(new Intent(this, CredentialsActivity.class));
        finish();
    }



    public void displayImage(byte[] imageBytes) {
        img = findViewById(R.id.userImage);
        Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        img.setImageBitmap(bmp);
    }

    //user uploaded image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                bmp = Bitmap.createScaledBitmap(bmp, 400,400,true); //makes bitmap tiny to not abuse memory
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 10, stream); //bmp gets compressed to the stream
                ContentValues cv = new ContentValues(1);
                byte[] byteArray = stream.toByteArray(); //stream becomes a byte array
                cv.put(USER_ICON, byteArray); //updates stored byte array for user local table
                db.update(TABLE_USER, cv, USER_ID + "=" + storedGNumber, null); //uploads image
                displayImage(byteArray); //displays on screen
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
