package com.cs477.dormbuddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
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

public class DisplayEventFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        String title = getArguments().getString("title");
        int imageID = getArguments().getInt("image");
        String where = getArguments().getString("where");
        String when = getArguments().getString("when");
        String description = getArguments().getString("description");
        boolean authorization = getArguments().getBoolean("authorization");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_display_event,null);
        if(title != null && !title.isEmpty()) {
            TextView eventTitle = (TextView) v.findViewById(R.id.eventName);
            eventTitle.setText(title);
        }
        if(imageID != 0) {
            final ImageView eventImage = (ImageView) v.findViewById(R.id.eventImage);
            Drawable image = ContextCompat.getDrawable(getContext(),imageID);
            eventImage.setImageDrawable(image);
            eventImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    BitmapDrawable drawable = (BitmapDrawable) eventImage.getDrawable();
                    DisplayImageFragment newFragment = DisplayImageFragment.newInstance(drawable.getBitmap());
                    newFragment.show(getActivity().getSupportFragmentManager(),"DisplayImage");
                    return true;
                }
            });
        }
        if(where != null && !where.isEmpty()) {
            TextView eventWhere = (TextView) v.findViewById(R.id.eventLocation);
            eventWhere.setText(where);
        }
        if(when != null && !when.isEmpty()) {
            TextView eventWhen = (TextView) v.findViewById(R.id.eventTime);
            eventWhen.setText(when);
        }
        if(description != null && !description.isEmpty()) {
            TextView eventDescription = (TextView) v.findViewById(R.id.eventDescription);
            eventDescription.setText(description);
        }

        builder.setView(v);
        if(authorization)
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

    static DisplayEventFragment newInstance(String title, int image, String where, String when, String description, boolean authorization){
        DisplayEventFragment display = new DisplayEventFragment();
        Bundle args = new Bundle();
        args.putString("title",title);
        args.putInt("image",image);
        args.putString("where",where);
        args.putString("when",when);
        args.putString("description",description);
        args.putBoolean("authorization",authorization);
        display.setArguments(args);
        return display;
    }

}
