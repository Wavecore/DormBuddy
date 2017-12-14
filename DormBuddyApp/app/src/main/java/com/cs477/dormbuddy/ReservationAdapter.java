package com.cs477.dormbuddy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by white on 12/12/2017.
 */
public class ReservationAdapter extends ArrayAdapter<Reservation> {
    private Reservation[] reservations;
    public ReservationAdapter(Context context, int resourceID, Reservation[] r){
        super(context,resourceID);
        reservations = r;
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


        }
        return v;
    }
}
