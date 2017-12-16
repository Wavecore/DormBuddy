package com.cs477.dormbuddy;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_ID;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_DESCRIPTION;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_END_TIME;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_IS_EVENT;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_START_TIME;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_TITLE;
import static com.cs477.dormbuddy.LocalUserHelper.ROOM_NUMBER;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_RESERVATION;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_USER;
import static com.cs477.dormbuddy.LocalUserHelper.USER_IS_ADMIN;
import static com.cs477.dormbuddy.LocalUserHelper.USER_LOGGED_IN;
import static com.cs477.dormbuddy.LocalUserHelper.USER_NET_ID;

public class EventBuddyActivity extends AppCompatActivity {
    private ReservationAdapter mAdapter;
    private SQLiteDatabase db;
    final static String[] columnsUser = {BUILDING_ID, USER_NET_ID, USER_IS_ADMIN};
    private ListView eventList;
    private String buildingID = "";
    private Reservation[] reservations;
    private boolean isAdmin;
    private  Button createResButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_buddy);
        LocalUserHelper dbHelper = new LocalUserHelper(this);
        db = dbHelper.getWritableDatabase();
        Cursor mCursorUser = db.query(TABLE_USER, columnsUser, USER_LOGGED_IN+ " = 1", new String[] {}, null, null,
                null);
       // String buildingID = "";
        String userNetID = "";
        isAdmin=  false;
        if(mCursorUser.moveToFirst()) {
            buildingID = mCursorUser.getString(0);
            userNetID = mCursorUser.getString(1);
           // System.out.println(mCursorUser.getInt(2));
            isAdmin = mCursorUser.getInt(2)==1;
        }
        createResButton = (Button)findViewById(R.id.eventCreationButton);
        if(!isAdmin){
            createResButton.setFocusable(false);
            createResButton.setVisibility(View.GONE);
        }
        else{
            createResButton.setFocusable(true);
            createResButton.setVisibility(View.VISIBLE);
        }
       // System.out.println("IsAdmin="+isAdmin);
        mCursorUser.close();

        eventList = (ListView)findViewById(R.id.eventList);
        reservations = Reservation.getUpcommingEvent(this, Calendar.getInstance());
        mAdapter = new ReservationAdapter(this, R.layout.reservation_item, reservations);
        eventList.setAdapter(mAdapter);
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Reservation r = (Reservation)adapterView.getItemAtPosition(i);
                if(getSupportFragmentManager().findFragmentByTag(DisplayEventFragment.DISPLAY_EVENT_TAG) == null) {
                    DisplayEventFragment newFragment = DisplayEventFragment.newInstance(r, isAdmin);
                    newFragment.show(getSupportFragmentManager(), DisplayEventFragment.DISPLAY_EVENT_TAG);
                }
            }
        });
        /*
        LoadEventReservationTask loadEventReservationTask = new LoadEventReservationTask(buildingID, this);
        loadEventReservationTask.execute((Void) null);
        if(reservations !=null && reservations.length != 0)
            eventList.setBackground(null);
        else
            eventList.setBackground(ContextCompat.getDrawable(this, R.drawable.empty));
*/
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        LoadEventReservationTask loadEventReservationTask = new LoadEventReservationTask(buildingID, this);
        loadEventReservationTask.execute((Void) null);
        if(reservations !=null && reservations.length != 0)
            eventList.setBackground(null);
        else
            eventList.setBackground(ContextCompat.getDrawable(this, R.drawable.empty));
    }

    public void createEventClicked(View view) {
        if(createResButton.isFocusable()) {
            Intent intent = new Intent(this, CreateEventActivity.class);
            intent.putExtra("isEvent", true);
            startActivity(intent);
        }
    }
    public class LoadEventReservationTask extends AsyncTask<Void, Void, Boolean> {
        private final String requestURL;
        //private final String requestUsingLaundryURL;
        private Context context;

        LoadEventReservationTask(String buildingID, Context context) {
            requestURL = String.format("https://hidden-caverns-60306.herokuapp.com/buildingEvents/%s", buildingID);
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
                Iterator<?> keys = getStudyResponse.keys();
                while(keys.hasNext()){
                    String keyString = (String) keys.next();
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
                        System.out.println("Added new event "+buildingID+roomNum);
                    }
                    resCursor.close();
                    System.out.println("Finished loading study room");
                    eventList.setBackground(null);
                }
                return true;
            } catch (Exception e) {
                System.out.println(e.toString());
                return false;
            }
        }



        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mAdapter.updateEventAdapter(context);
            //washerAdapter.notifyDataSetChanged();
            //dryerAdapter.notifyDataSetChanged();
            super.onPostExecute(aBoolean);
        }
    }
}
