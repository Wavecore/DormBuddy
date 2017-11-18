package com.cs477.dormbuddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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

public class DisplayImageFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Bitmap image = getArguments().getParcelable("image");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_display_image,null);
        if(image != null) {
            ImageView displayImage = (ImageView) v.findViewById(R.id.displayImage);
            displayImage.setImageBitmap(image);
        }
        builder.setView(v);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        return builder.create();
    }

    static DisplayImageFragment newInstance(Bitmap image){
        DisplayImageFragment display = new DisplayImageFragment();
        Bundle args = new Bundle();
       // Drawable temp;
        args.putParcelable("image",image);
        //args.putInt("imageID",imageID);
        display.setArguments(args);
        return display;
    }

}
