package com.cs477.dormbuddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SavedLocationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedLocationsFragment extends DialogFragment {
    public static final String IS_GMU = "isGmu";
    ArrayAdapter<String> mAdapter;
    ListView locations;
    boolean isGmu = false;
    int imageResource;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_saved_locations, null);
        locations = (ListView) v.findViewById(R.id.userLocations);
        isGmu = getArguments().getBoolean(IS_GMU);
        if (isGmu) {
            mAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, InteractiveMapActivity.campusLocationNames);
        } else {
            mAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, InteractiveMapActivity.cityLocationNames);
        }
        locations.setAdapter(mAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setPositiveButton("Add New Location", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                InteractiveMapActivity.displayAddMarker();
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        return builder.create();
    }

    static SavedLocationsFragment newInstance(boolean isGmu){
        SavedLocationsFragment savedLocations = new SavedLocationsFragment();
        Bundle args = new Bundle();
        // Drawable temp;
        args.putBoolean(IS_GMU,isGmu);
        savedLocations.setArguments(args);
        return savedLocations;
    }
}
