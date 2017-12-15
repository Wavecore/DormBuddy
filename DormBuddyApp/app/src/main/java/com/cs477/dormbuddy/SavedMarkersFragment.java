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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static com.cs477.dormbuddy.InteractiveMapActivity.campusLocations;
import static com.cs477.dormbuddy.InteractiveMapActivity.cityLocations;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SavedMarkersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedMarkersFragment extends DialogFragment {
    public static final String IS_GMU = "isGmu";
    public ArrayAdapter<String> mAdapter;
    ListView locations;
    boolean isGmu = false;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_saved_markers, null);
        locations = (ListView) v.findViewById(R.id.userLocations);
        isGmu = getArguments().getBoolean(IS_GMU);
        if (isGmu) {
            mAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, InteractiveMapActivity.campusLocationNames);
        } else {
            mAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, InteractiveMapActivity.cityLocationNames);
        }
        locations.setAdapter(mAdapter);
        locations.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                SavedMarkersFragment.SavedMarkersFragmentDone activity = (SavedMarkersFragment.SavedMarkersFragmentDone) getActivity();
                activity.onClickName(position);
                dismiss();
            }
        });
        locations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View v, int position, long id){
                ArrayList<LatLng> listRef = (isGmu) ? campusLocations : cityLocations;
                SavedMarkersFragment.SavedMarkersFragmentDone activity = (SavedMarkersFragment.SavedMarkersFragmentDone) getActivity();
                activity.onRemoveMarker(position);
                dismiss();
                return true;
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setPositiveButton("Add New Marker", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SavedMarkersFragment.SavedMarkersFragmentDone activity = (SavedMarkersFragment.SavedMarkersFragmentDone) getActivity();
                activity.onAddFragmentRequested();
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

    static SavedMarkersFragment newInstance(boolean isGmu){
        SavedMarkersFragment savedLocations = new SavedMarkersFragment();
        Bundle args = new Bundle();
        // Drawable temp;
        args.putBoolean(IS_GMU,isGmu);
        savedLocations.setArguments(args);
        return savedLocations;
    }

    public interface SavedMarkersFragmentDone {
        void onAddFragmentRequested();
        void onClickName(int position);
        void onRemoveMarker(int position);
    }
}
