package com.cs477.dormbuddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_ID;
import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_NAME;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_BUILDING;
import static com.cs477.dormbuddy.Reservation.image;

public class DisplayEventFragment extends DialogFragment {
    static final String DISPLAY_EVENT_TAG = "DisplayEventTag";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        // Get values from bundle
        String title = getArguments().getString("title");
        byte[] i = getArguments().getByteArray("image");
        String buildingID = getArguments().getString("buildingID");
        String room = getArguments().getString("roomNum");
        long start = getArguments().getLong("start");
        long end = getArguments().getLong("end");
        String description = getArguments().getString("description");
        boolean auth = getArguments().getBoolean("authorization");
        boolean isEvent = getArguments().getBoolean("isEvent");
        // For new values from these values
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(new Date(start));
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(new Date(end));
        Bitmap image = Reservation.displayImage(i);
        //

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_display_event,null);
        if(title != null && !title.isEmpty()) {
            TextView eventTitle = (TextView) v.findViewById(R.id.eventName);
            eventTitle.setText(title);
        }

        final ImageView eventImage = (ImageView) v.findViewById(R.id.eventImage);
        if(!isEvent){
            Drawable d = getResources().getDrawable(R.drawable.book);
            eventImage.setImageDrawable(d);
        }
        if(image != null)
            eventImage.setImageBitmap(image);
        eventImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(getActivity().getSupportFragmentManager().findFragmentByTag(DisplayImageFragment.DISPLAY_IMAGE_TAG) == null) {
                    BitmapDrawable drawable = (BitmapDrawable) eventImage.getDrawable();
                    DisplayImageFragment newFragment = DisplayImageFragment.newInstance(drawable.getBitmap());
                    newFragment.show(getActivity().getSupportFragmentManager(), DisplayImageFragment.DISPLAY_IMAGE_TAG);
                }
                return true;
            }
        });

        LocalUserHelper dbHelper = new LocalUserHelper(this.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cCursor = db.query(TABLE_BUILDING,new String[]{BUILDING_NAME},BUILDING_ID+"='"+buildingID+"'",
                new String[]{},null, null,null);                            //Get the name of the building from the
        // System.out.println(r.buildingID);
        if(cCursor.moveToFirst()) {
            TextView eventWhere = (TextView) v.findViewById(R.id.eventLocation);
            eventWhere.setText(cCursor.getString(0) + " " + room);
        }
            TextView eventWhen = (TextView) v.findViewById(R.id.eventTime);
            eventWhen.setText(Reservation.resFormatter.format(startTime.getTime())+" to "+Reservation.resFormatter.format(endTime.getTime()));
        if(description != null && !description.isEmpty()) {
            TextView eventDescription = (TextView) v.findViewById(R.id.eventDescription);
            eventDescription.setText(description);
        }

        builder.setView(v);
        if(auth)
             builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //TODO: Implement delete event functionality
                        }
                    });
         builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    static DisplayEventFragment newInstance(String title, byte[] image, String buildingID, String room, long start, long end, String description, boolean authorization){
        DisplayEventFragment display = new DisplayEventFragment();
        Bundle args = new Bundle();
        args.putString("title",title);
        args.putByteArray("image",image);
        args.putString("buildingID",buildingID);
        args.putString("roomNum",room);
        args.putLong("start",start);
        args.putLong("end",end);
        args.putString("description",description);
        args.putBoolean("authorization",authorization);
        display.setArguments(args);
        return display;
    }

    static DisplayEventFragment newInstance(Reservation r, boolean authorization){
        DisplayEventFragment display = new DisplayEventFragment();
        Bundle args = new Bundle();
        args.putString("title",r.title);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(r.image != null) {
            r.image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            args.putByteArray("image", byteArray);
        }
        else
            args.putByteArray("image",null);
        args.putString("buildingID",r.buildingID);
        args.putString("roomNum",r.roomNum);
        args.putLong("start",r.startTime.getTime().getTime());
        args.putLong("end",r.endTime.getTime().getTime());
        args.putString("description",r.description);
        args.putBoolean("authorization",authorization);
        args.putBoolean("isEvent",r.isEvent);
        display.setArguments(args);
        return display;
    }


}
