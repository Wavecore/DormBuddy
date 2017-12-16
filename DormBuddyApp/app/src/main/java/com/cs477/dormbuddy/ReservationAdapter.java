package com.cs477.dormbuddy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_ID;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_END_TIME;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_IS_EVENT;
import static com.cs477.dormbuddy.LocalUserHelper.RESERVATION_START_TIME;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_BUILDING;
import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_NAME;

/**
 * Created by white on 12/12/2017.
 */
public class ReservationAdapter extends ArrayAdapter<Reservation> {
    public Reservation[] reservations;
    private Context context;
    public boolean isEvent;
    public ReservationAdapter(Context context, int resourceID, Reservation[] r, boolean isEvent){
        super(context,resourceID);
        reservations = r;
        this.context = context;
        this.isEvent = isEvent;
        if(r != null){
            for(int x = 0; x < r.length;x++)
                add(r[x]);
        }
    }
    public void updateStudyAdapter(Context context){
        this.clear();
        Reservation[] r = Reservation.getStudy(context,Calendar.getInstance());
        if(r != null){
            for(int x = 0; x < r.length;x++)
                add(r[x]);
        }
        System.out.println("Updated study");
        this.notifyDataSetChanged();
    }
    public void updateEventAdapter(Context context){
        this.clear();
        Reservation[] r = Reservation.getUpcommingEvent(context,Calendar.getInstance());
        if(r != null){
            for(int x = 0; x < r.length;x++)
                add(r[x]);
        }
        System.out.println("Updated events");
        this.notifyDataSetChanged();
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(v == null){
            LayoutInflater vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.reservation_item,null);
        }
        Reservation r = getItem(position);
        if(r != null){
            TextView title = (TextView)v.findViewById(R.id.reservation_Title);
            TextView time = (TextView)v.findViewById(R.id.reservation_Time);
            TextView loca = (TextView)v.findViewById(R.id.reservation_Location);
            ImageView icon = (ImageView)v.findViewById(R.id.reservation_Icon);
            TextView currentEvent = v.findViewById(R.id.is_happening_now);

            title.setText("Title: "+r.title);

                loca.setText("Location: " + r.buildingID + " " + r.roomNum);

            time.setText("Time: "+Reservation.simpleResFormatter.format(r.startTime.getTime())+" to "+Reservation.simpleResFormatter.format(r.endTime.getTime()));
            //events that have started
            long now = new Date().getTime();
            if (r.startTime.getTimeInMillis() < now && r.endTime.getTimeInMillis() > now) {
                currentEvent.setVisibility(View.VISIBLE);
            }

            if(r.image != null) //attempts to set the image to the user uploaded image
                icon.setImageBitmap(r.image);
            else if (isEvent) //events get a different icon than study reservations
                icon.setImageResource(R.drawable.event);
        }
        return v;
    }
}
