package com.cs477.dormbuddy;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Wave on 12/2/2017.
 */

public class Event {
    public static Calendar startTime;
    public static Calendar endTime;
    public static String title;
    public static String description;
    public static Bitmap image;
    private Event(Calendar start, Calendar end, String t, String d, Bitmap i){
        this.startTime = start;
        this.endTime = end;
        this.title = t;
        this.description = d;
        this.image = i;
    }
    public static Event[] createEventArray(){

    }
    public static Event createEvent(long start, long end, String title, String description, byte[] image){
        if(start == null)
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(start));
        c = Calendar.getInstance();
        c.setTime(new Date(end));

        return this;
    }
    public void displayImage(byte[] imageBytes) {
        if (imageBytes.length > 0) {
        Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }


}
