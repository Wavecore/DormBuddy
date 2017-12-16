package com.cs477.dormbuddy;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_ID;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_DESCRIPTION;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_END_TIME;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_IS_EVENT;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_START_TIME;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_TITLE;
import static com.cs477.dormbuddy.LocalUserHelper.ROOM_NUMBER;
import static com.cs477.dormbuddy.LocalUserHelper.SELECTED_DRYER_TEMPLATE;
import static com.cs477.dormbuddy.LocalUserHelper.SELECTED_WASHER_TEMPLATE;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_RESERVATION;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_USER;
import static com.cs477.dormbuddy.LocalUserHelper.USER_LOGGED_IN;
import static com.cs477.dormbuddy.LocalUserHelper.USER_NET_ID;

public class StudyBuddyActivity extends AppCompatActivity implements DisplayEventFragment.OnCompleteListener {
    private ReservationAdapter mAdapter;
    private SQLiteDatabase db;
    private ListView studyList;
    private final static String[] columnsUser = {BUILDING_ID, USER_NET_ID};
    //private Reservation[] reservations;
    private String userNetID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_buddy);
        LocalUserHelper dbHelper = new LocalUserHelper(this);
        db = dbHelper.getWritableDatabase();
        Cursor mCursorUser = db.query(TABLE_USER, columnsUser, USER_LOGGED_IN+ " = 1", new String[] {}, null, null,
                null);
        String buildingID = "";
        //String userNetID = "";
        if(mCursorUser.moveToFirst()) {
            buildingID = mCursorUser.getString(0);
            userNetID = mCursorUser.getString(1);
        }
        mCursorUser.close();

        studyList = (ListView)findViewById(R.id.reservationList);
        Reservation[] reservations = Reservation.getStudy(this, Calendar.getInstance());
        mAdapter = new ReservationAdapter(this, R.layout.reservation_item, reservations, false);
        studyList.setAdapter(mAdapter);
        studyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Reservation r = (Reservation)adapterView.getItemAtPosition(i);
                if(getSupportFragmentManager().findFragmentByTag(DisplayEventFragment.DISPLAY_EVENT_TAG) == null) {
                    DisplayEventFragment newFragment = DisplayEventFragment.newInstance(r, true);
                    newFragment.show(getSupportFragmentManager(), DisplayEventFragment.DISPLAY_EVENT_TAG);
                }
            }
        });

        LoadStudyReservationTask retrieveStudyReservationTask = new LoadStudyReservationTask(userNetID, this);
        retrieveStudyReservationTask.execute((Void) null);
    }

    public void createEventClicked(View view) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        intent.putExtra("isEvent",false);
        startActivity(intent);
    }
    public void onDisplayEventComplete(){
        LoadStudyReservationTask retrieveStudyReservationTask = new LoadStudyReservationTask(userNetID, this);
        retrieveStudyReservationTask.execute((Void) null);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        LoadStudyReservationTask retrieveStudyReservationTask = new LoadStudyReservationTask(userNetID, this);
        retrieveStudyReservationTask.execute((Void) null);
    }

    public class LoadStudyReservationTask extends AsyncTask<Void, Void, Boolean> {
        private final String requestURL;
        private Context context;

        LoadStudyReservationTask(String userNetID, Context context) {
            requestURL = String.format("https://hidden-caverns-60306.herokuapp.com/userReservations/%s", userNetID);
            System.out.println(requestURL);
            this.context = context;
        }

        //sends the request in background
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL requestMachinesURL = new URL(requestURL);
                //URL requestUsingLaundryMachinesURL = new URL(requestUsingLaundryURL);

                //GETS the machines
                HttpsURLConnection connection = (HttpsURLConnection) requestMachinesURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(false);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                //get the responses
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String getStudyContent = "",getStudyLine;
                while((getStudyLine = rd.readLine()) != null){
                    getStudyContent += getStudyLine +"\n";
                }

                System.out.println(getStudyContent);
                //Parse response in to JSON object
                JSONObject getStudyResponse = new JSONObject(getStudyContent);
                System.out.println(getStudyResponse.toString());
                Iterator<?> keys = getStudyResponse.keys();
                db.delete(TABLE_RESERVATION,"", new String[]{});
                while(keys.hasNext()){
                    String keyString = (String) keys.next();
                    System.out.println(keyString);
                    JSONObject resJSON = (JSONObject) getStudyResponse.get(keyString);
                    String buildingID = resJSON.getString("BuildingID");
                    String roomNum = resJSON.getString("RoomNum");
                    String userNetID = resJSON.getString("User");
                    String reservationTitle = "";
                    String reservationDescr = "";
                    if(!resJSON.isNull(RESERVATION_TITLE))
                        reservationTitle = resJSON.getString(RESERVATION_TITLE);
                    if(!resJSON.isNull(RESERVATION_DESCRIPTION))
                        reservationDescr = resJSON.getString(RESERVATION_DESCRIPTION);
                    boolean reservationIsEvent = resJSON.getBoolean("IsEvent");
                    long reservationStartTime = resJSON.getLong("TimeStart");
                    long reservationEndTime = resJSON.getLong("TimeEnd");
                    Cursor resCursor = db.query(TABLE_RESERVATION, new String[]{BUILDING_ID,ROOM_NUMBER,RESERVATION_START_TIME,RESERVATION_END_TIME},
                            BUILDING_ID+ " = '"+buildingID+"' AND "+ROOM_NUMBER+"='"+roomNum+"' AND "+RESERVATION_START_TIME+"="+reservationStartTime+" AND "+RESERVATION_END_TIME+"="+reservationEndTime,
                            new String[] {}, null, null,null);
                    if(resCursor.getCount() == 0){
                        ContentValues cv = new ContentValues(8);
                        cv.put(BUILDING_ID,buildingID);
                        cv.put(ROOM_NUMBER,roomNum);
                        cv.put(USER_NET_ID,userNetID);
                        cv.put(RESERVATION_TITLE,reservationTitle);
                        cv.put(RESERVATION_DESCRIPTION,reservationDescr);
                        cv.put(RESERVATION_IS_EVENT,reservationIsEvent);
                        cv.put(RESERVATION_START_TIME,reservationStartTime);
                        cv.put(RESERVATION_END_TIME,reservationEndTime);
                        db.insert(TABLE_RESERVATION,null,cv);
                        System.out.println("Added new reservation "+reservationTitle+buildingID+roomNum);
                    }
                    resCursor.close();
                    studyList.setBackground(null);
                }
                System.out.println("Finished loading study room");
                return true;
            } catch (Exception e) {
                System.out.println(e.toString());
                return false;
            }
        }



        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mAdapter.updateStudyAdapter(context);
            if(mAdapter.getCount() != 0)
                studyList.setBackground(null);
            else
                studyList.setBackground(ContextCompat.getDrawable(context, R.drawable.empty));
            super.onPostExecute(aBoolean);
        }
    }

}
