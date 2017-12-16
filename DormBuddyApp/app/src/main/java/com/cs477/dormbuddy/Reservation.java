package com.cs477.dormbuddy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_ID;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_DESCRIPTION;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_END_TIME;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_ICON;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_IS_EVENT;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_START_TIME;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_TITLE;
import static com.cs477.dormbuddy.LocalUserHelper.ROOM_NUMBER;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_RESERVATION;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_USER;
import static com.cs477.dormbuddy.LocalUserHelper.USER_ID;
import static com.cs477.dormbuddy.LocalUserHelper.USER_LOGGED_IN;
import static com.cs477.dormbuddy.LocalUserHelper.USER_NET_ID;

/**
 * Created by Wave on 12/2/2017.
 */

public class Reservation implements Comparable<Reservation>{
    public Calendar startTime;
    public Calendar endTime;
    public String title;
    public String description;
    public Bitmap image;
    public Boolean isEvent;
    public String buildingID;
    public String roomNum;
    public static String[] columns = {BUILDING_ID,ROOM_NUMBER,USER_NET_ID,RESERVATION_TITLE,RESERVATION_DESCRIPTION,
        RESERVATION_ICON,RESERVATION_IS_EVENT,RESERVATION_START_TIME,RESERVATION_END_TIME};
    public static final SimpleDateFormat resFormatter = new SimpleDateFormat("h:mm a, EEE d MMM yyyy");
    public static final SimpleDateFormat simpleResFormatter = new SimpleDateFormat("h:mm a, d MMM");

    private Reservation(boolean event,Calendar start, Calendar end, String t, String d, Bitmap i,String buildingID, String roomNum){
        this.isEvent = event;
        this.startTime = start;
        this.endTime = end;
        this.title = t;
        this.description = d;
        this.image = i;
        this.buildingID = buildingID;
        this.roomNum = roomNum;
    }
    @Nullable
    public static Reservation[] getStudy(Context c, Calendar time){
        long t = time.getTime().getTime();
        LocalUserHelper dbHelper = new LocalUserHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cCursor = db.query(TABLE_RESERVATION,columns,RESERVATION_IS_EVENT+"=0 AND "+RESERVATION_END_TIME+">"+t,
                new String[]{},null, null,null);                            //Query for all reservations that aren't events and haven't passed
        if(cCursor.moveToFirst()){
            Reservation[] actualOutput = new Reservation[cCursor.getCount()];
            ArrayList<Reservation> sortedOutput = new ArrayList<Reservation>();
            while ( !cCursor.isAfterLast() ) {
                String buildingID = cCursor.getString(0);                     //BuildingID
                String roomNum = cCursor.getString(1);                        //Room Number
                // Don't need to get userID because we already have it
                String reservationTitle = cCursor.getString(3);         //Reservation Title
                String reservationDescription = cCursor.getString(4);   //Reservation Description
                byte[] reservationIcon = cCursor.getBlob(5);
                boolean reservationIsEvent = 1==cCursor.getInt(6);
                long reservationStartTime = cCursor.getLong(7);
                long reservationEndTime = cCursor.getLong(8);
                System.out.println(reservationTitle);
                sortedOutput.add(createReservation(reservationIsEvent,reservationStartTime,reservationEndTime,
                        reservationTitle,reservationDescription, reservationIcon,buildingID,roomNum));
                cCursor.moveToNext();
            }
            Collections.sort(sortedOutput); //sorts all reservations
            //copies sorted output to actual output
            for (int count = 0; count < actualOutput.length; count++) {
                actualOutput[count] = sortedOutput.get(count);
            }
            cCursor.close();
            db.close();
            return actualOutput;

        }
        cCursor.close();
        db.close();
        return null;
    }
    @Nullable
    public static Reservation[] getUpcommingEvent(Context c, Calendar time){
        long t = time.getTime().getTime();
        LocalUserHelper dbHelper = new LocalUserHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cCursor = db.query(TABLE_RESERVATION,columns,RESERVATION_IS_EVENT+"=1 AND "+RESERVATION_END_TIME+">"+t,
                new String[]{},null, null,null);                            //Query for all reservations that are events and haven't passed
        if(cCursor.moveToFirst()){
            Reservation[] actualOutput = new Reservation[cCursor.getCount()];
            ArrayList<Reservation> sortedOutput = new ArrayList<Reservation>();
            while ( !cCursor.isAfterLast() ) {
                String buildingID = cCursor.getString(0);                     //BuildingID
                String roomNum = cCursor.getString(1);                        //Room Number
                // Don't need to get userID because we already have it
                String reservationTitle = cCursor.getString(3);         //Reservation Title
                String reservationDescription = cCursor.getString(4);   //Reservation Description
                byte[] reservationIcon = cCursor.getBlob(5);
                boolean reservationIsEvent = 1==cCursor.getInt(6);
                long reservationStartTime = cCursor.getLong(7);
                long reservationEndTime = cCursor.getLong(8);
                sortedOutput.add(createReservation(reservationIsEvent,reservationStartTime,reservationEndTime,
                        reservationTitle,reservationDescription, reservationIcon,buildingID,roomNum));
                cCursor.moveToNext();
            }
            Collections.sort(sortedOutput); //sorts all reservations
            //copies sorted output to actual output
            for (int count = 0; count < actualOutput.length; count++) {
                actualOutput[count] = sortedOutput.get(count);
            }
            cCursor.close();
            db.close();
            return actualOutput;
        }
        cCursor.close();
        db.close();
        return null;
    }

    public static Reservation createReservation(boolean isEvent,long start, long end, String title, String description, byte[] image,String bID,String rNum){
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(new Date(start));
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(new Date(end));
        Bitmap i = displayImage(image);
        Reservation e = new Reservation(isEvent,startTime,endTime, title,description, i,bID,rNum);
        return e;
    }
    @Nullable
    public static Bitmap displayImage(byte[] imageBytes) {
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            return bmp;
        }
        return null;
    }

    //earlier event start times show up first
    public int compareTo(Reservation other) {
        return (int)(startTime.getTimeInMillis()-other.startTime.getTimeInMillis());
    }
}
