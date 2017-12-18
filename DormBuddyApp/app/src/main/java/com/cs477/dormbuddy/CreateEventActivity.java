package com.cs477.dormbuddy;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_ID;
import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_NAME;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_DESCRIPTION;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_TITLE;
import static com.cs477.dormbuddy.LocalUserHelper.ROOM_NUMBER;
import static com.cs477.dormbuddy.LocalUserHelper.ROOM_TYPE;
import static com.cs477.dormbuddy.LocalUserHelper.ROOM_TYPE_DORM;
import static com.cs477.dormbuddy.LocalUserHelper.ROOM_TYPE_STUDY;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_BUILDING;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_ROOM;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_USER;
import static com.cs477.dormbuddy.LocalUserHelper.USER_ID;
import static com.cs477.dormbuddy.LocalUserHelper.USER_LOGGED_IN;
import static com.cs477.dormbuddy.LocalUserHelper.USER_NET_ID;

public class CreateEventActivity extends AppCompatActivity implements SelectTimeSlotFragment.OnCompleteListener {

    static final int PICK_IMAGE = 1;
    private SQLiteDatabase db = null;
    private LocalUserHelper dbHelper = null;
    private ImageButton imageButton;
    private Calendar startTime;
    private Calendar endTime;
    private EditText editText;
    private String user_id;
    private String building_id;
    private EditText titleText;
    private Spinner eventLocation;
    private EditText descpText;
    private boolean isEvent = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Intent intent = getIntent();
        isEvent = intent.getBooleanExtra("isEvent",false);
        FragmentManager fm = getFragmentManager();
        dbHelper = new LocalUserHelper(this);
        db = dbHelper.getWritableDatabase();
        titleText = (EditText)findViewById(R.id.eventNameEdit);
        descpText = (EditText)findViewById(R.id.eventDescription);
        editText = (EditText)findViewById(R.id.eventTime);
        editText.setFocusable(false);
        Cursor cCursor = db.query(TABLE_USER,new String[]{USER_NET_ID,BUILDING_ID},USER_LOGGED_IN+" = 1", new String[]{},null, null,null);
        if(cCursor.moveToFirst()){
            building_id = cCursor.getString(1);
            user_id = cCursor.getString(0);
        }
        cCursor.close();
        cCursor = db.query(TABLE_ROOM,new String[]{ROOM_NUMBER},BUILDING_ID+" = '"+building_id+"' AND "+ROOM_TYPE+" = '"+ROOM_TYPE_STUDY+"'", new String[]{},null, null,null);
        ArrayAdapter<CharSequence> mAdapter = new ArrayAdapter<CharSequence>(this,R.layout.spinner_item,R.id.spinnerItem);
        mAdapter.add("Select a Room");
        if (cCursor.moveToFirst()) {
            while ( !cCursor.isAfterLast() ) {
                mAdapter.add(cCursor.getString(0));
                cCursor.moveToNext();
            }
        }
        cCursor.close();
        eventLocation = (Spinner)findViewById(R.id.eventLocation);
        eventLocation.setAdapter(mAdapter);
        eventLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0)
                    editText.setFocusable(true);
                else
                    editText.setFocusable(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        imageButton =  (ImageButton)findViewById(R.id.createEventImageButton);
        imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
                DisplayImageFragment displayImageFragment = DisplayImageFragment.newInstance(drawable.getBitmap());
                displayImageFragment.show(getSupportFragmentManager(),"DisplayImage");
                return true;
            }
        });
        editText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(editText.isFocusable()) {
                    if (getSupportFragmentManager().findFragmentByTag(SelectTimeSlotFragment.SELECT_TIME_SLOT_TAG) == null) {
                        Calendar mcurrentTime = Calendar.getInstance();
                        SelectTimeSlotFragment selectTimeSlotFragment = SelectTimeSlotFragment.newInstance(startTime, endTime, building_id, (String) eventLocation.getSelectedItem());
                        selectTimeSlotFragment.show(getSupportFragmentManager(), SelectTimeSlotFragment.SELECT_TIME_SLOT_TAG);
                    }
                }
            }
        });
    }
    public void createReservation(View v){
        if(eventLocation.getSelectedItemPosition() == 0)
            Toast.makeText(this, "Select a location", Toast.LENGTH_SHORT).show();
        else if(startTime == null || endTime == null)
            Toast.makeText(this, "Select a time", Toast.LENGTH_SHORT).show();
        else{
            ReserveRoom reserveRoom = new ReserveRoom(this);
            reserveRoom.execute((Void) null);
        }
    }

    public void onComplete(Calendar start, Calendar end){
        startTime = start;
        endTime = end;
        if(startTime == null || endTime == null){
            editText.setText("");
        }
        else{
            SimpleDateFormat ft = new SimpleDateFormat("MMM d hh:mm aaa");
            editText.setText(ft.format(startTime.getTime())+" to "+ft.format(endTime.getTime()));
        }
    }
    public void selectImage(View v){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageButton.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(CreateEventActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class ReserveRoom extends AsyncTask<Void, Void, Integer> {
        private final String requestURL;
        private String title;
        private String roomNum;
        private long start;
        private long end;
        private String description;
        private Context context;

        ReserveRoom(Context context) {
            this.title = titleText.getText().toString();
            this.roomNum = eventLocation.getSelectedItem().toString();
            this.start = startTime.getTime().getTime();
            this.end = endTime.getTime().getTime();
            this.description = descpText.getText().toString();
            requestURL = String.format("https://hidden-caverns-60306.herokuapp.com/makeReservation/%s/%s", building_id, roomNum);
            System.out.println(requestURL);
            this.context = context;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                URL requestReserveMachineURL = new URL(requestURL);

                HttpsURLConnection connection = (HttpsURLConnection) requestReserveMachineURL.openConnection();
                connection.setRequestMethod("PUT");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                //prepares the request body, using reservation type to determine new status of machine
                String writeData = String.format("{\"BuildingID\":\"%s\",\"IsEvent\":%s, \"RoomNum\": \"%s\",\"TimeEnd\":%d,\"TimeStart\":%d,\"User\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\"}",
                        building_id, new Boolean(isEvent).toString(), roomNum,end,start,user_id,RESERVATION_TITLE,title,RESERVATION_DESCRIPTION,description);
                System.out.println(writeData);
                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                osw.write(writeData);
                osw.flush();
                osw.close();

                String responseMessage = connection.getResponseMessage();
                System.out.println(connection.getResponseCode()+" "+responseMessage);
                if (connection.getResponseCode() == 200)
                    return 1;
                else if(connection.getResponseCode() == 409)
                    return -1;
                else if(connection.getResponseCode() == 412)
                    return -2;
                else if(connection.getResponseCode() == 400)
                    return -3;
                else
                    return -4;
            } catch (Exception e) {
                System.out.println(e.toString());
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer type) {
            super.onPostExecute(type);
            if(type == -1)
                Toast.makeText(context, "ERROR: Reservation conflicts with another reservation", Toast.LENGTH_SHORT).show();
            else if(type == -2)
                Toast.makeText(context, "ERROR: Bad parameters", Toast.LENGTH_SHORT).show();
            else if(type == -3)
                Toast.makeText(context, "ERROR: Failed preconditions", Toast.LENGTH_SHORT).show();
            else if(type == -4)
                Toast.makeText(context, "ERROR: Something bad happened", Toast.LENGTH_SHORT).show();
            else if(type == 1)
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
