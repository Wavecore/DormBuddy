package com.cs477.dormbuddy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static com.cs477.dormbuddy.LocalUserHelper.MARKER_ID;
import static com.cs477.dormbuddy.LocalUserHelper.MARKER_IS_CAMPUS;
import static com.cs477.dormbuddy.LocalUserHelper.MARKER_IS_IMPORTANT;
import static com.cs477.dormbuddy.LocalUserHelper.MARKER_LATITUDE;
import static com.cs477.dormbuddy.LocalUserHelper.MARKER_LONGITUDE;
import static com.cs477.dormbuddy.LocalUserHelper.MARKER_NAME;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_MARKERS;
import static com.cs477.dormbuddy.LocalUserHelper.USER_LOGGED_IN;

public class InteractiveMapActivity extends FragmentActivity implements OnMapReadyCallback, SavedMarkersFragment.SavedMarkersFragmentDone {

    private GoogleMap mMap;
    private boolean isGmu;
    //stored user markers
    public static ArrayList<String> cityLocationNames, campusLocationNames;
    public static ArrayList<LatLng> cityLocations, campusLocations;
    public static ArrayList<Integer> cityIds, campusIds;
    LinearLayout addMarkerItems; //sorry for the memory leak, but this is a necessity for now
    Button viewSavedMarkersButton, cancelButton;
    EditText markerName;
    boolean isAddingMarker;
    final static String[] columns = { MARKER_ID, MARKER_LATITUDE, MARKER_LONGITUDE, MARKER_NAME, MARKER_IS_CAMPUS, MARKER_IS_IMPORTANT };
    LocalUserHelper dbHelper;
    SQLiteDatabase db;
    Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_map);
        //get which map to retrieve
        isGmu = getIntent().getBooleanExtra(MapBuddyActivity.IS_GMU, true);
        //initialize fragment layout elements
        addMarkerItems = findViewById(R.id.addMarkerItems);
        viewSavedMarkersButton = findViewById(R.id.viewSavedMarkersButton);
        markerName = findViewById(R.id.markerName);
        cancelButton = findViewById(R.id.cancelButton);
        //initialize lists
        campusLocationNames = new ArrayList<String>();
        cityLocationNames = new ArrayList<String>();
        campusLocations = new ArrayList<LatLng>();
        cityLocations = new ArrayList<LatLng>();
        cityIds = new ArrayList<Integer>();
        campusIds = new ArrayList<Integer>();
        //////////////////////////////////////////////////////
        hideAddMarker();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng location;
        if (isGmu) {
            location = new LatLng(38.8303461, -77.30693869292736);
            mMap.setMinZoomPreference(16.2f); //limit how much user can zoom out
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,17.6f));
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true); //if user allowed location, show it, otherwise it doesn't matter
            }
            //northwest bound (38.837525, -77.327185)
            //southeast bound (38.824336, -77.300813)
            //found this method randomly and it's awesome
            mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(new LatLng(38.824336, -77.327185), new LatLng(38.835725, -77.300813)));
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true); //if user allowed location, show it, otherwise it doesn't matter
            }
            location = new LatLng(38.846223, -77.306373);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,13.4f));
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                /*debug to find location of various buildings
                System.out.println("MAP CLICKED");
                System.out.println(latLng.latitude);
                System.out.println(latLng.longitude);*/
                if (! isAddingMarker) {
                    return; //map clicks do not do anything unless user is adding marker
                }
                addMarker(latLng);
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                displayAddMarker();
            }
        });
        loadMarkers(); //load markers as map is ready
        /* debug for logcat to tell me lat longs
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                System.out.println(mMap.getCameraPosition());
            }
        });*/
    }

    public void loadMarkers() {
        //retrieve markers from saved local database
        dbHelper = new LocalUserHelper(this);
        db = dbHelper.getWritableDatabase();
        mCursor = db.query(TABLE_MARKERS, columns, null, new String[] {}, null, null,
                null);
        mCursor.moveToFirst();
        while (!mCursor.isAfterLast()) {
            boolean isCampus = mCursor.getInt(4) == 1;
            if (isCampus) {
                campusIds.add(mCursor.getInt(0));
                campusLocations.add(new LatLng(mCursor.getFloat(1), mCursor.getFloat(2))); //no getReal??!
                campusLocationNames.add(mCursor.getString(3));
            } else {
                cityIds.add(mCursor.getInt(0));
                cityLocations.add(new LatLng(mCursor.getFloat(1), mCursor.getFloat(2))); //no getReal??!
                cityLocationNames.add(mCursor.getString(3));
            }
            mCursor.moveToNext();
        }
        displayMarkers();
    }

    public void addMarker(LatLng latLng) {
        if (markerName.getText().toString().isEmpty()) {
            Toast.makeText(InteractiveMapActivity.this, "Please name your marker", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isGmu) {
            cityLocationNames.add(markerName.getText().toString());
            cityLocations.add(latLng);
        } else {
            campusLocationNames.add(markerName.getText().toString());
            campusLocations.add(latLng);
        }
        Toast.makeText(InteractiveMapActivity.this, "Marker " + markerName.getText().toString() + " Added", Toast.LENGTH_SHORT).show();
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(markerName.getText().toString())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        int isCampus = isGmu ? 1 : 0;
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")VALUES( " + latLng.latitude + "," + latLng.longitude + ",'" +
                markerName.getText().toString() + "', " + isCampus + ", 0 );"); //wegmans should sponsor this app
        mCursor = db.query(TABLE_MARKERS, columns, null, new String[] {}, null, null,
                null);
        mCursor.moveToLast();
        if (isGmu) {
            campusLocationNames.add(markerName.getText().toString());
            campusLocations.add(latLng);
            campusIds.add(mCursor.getPosition());
        } else {
            cityLocationNames.add(markerName.getText().toString());
            cityLocations.add(latLng);
            cityIds.add(mCursor.getPosition());
        }
        hideAddMarker();
        markerName.setText("");
        displayMarkers();
        isAddingMarker = false;
    }

    public void displayMarkers() {
        ArrayList<String> namesToDisplay = (isGmu) ? campusLocationNames : cityLocationNames;
        ArrayList<LatLng> latLngsToDisplay = (isGmu) ? campusLocations : cityLocations;
        for (int i = 0; i < namesToDisplay.size(); i++) {
            if (isGmu) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLngsToDisplay.get(i))
                        .title(namesToDisplay.get(i))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            } else {
                mMap.addMarker(new MarkerOptions()
                        .position(latLngsToDisplay.get(i))
                        .title(namesToDisplay.get(i))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }
        }
    }

    public void displayAddMarker() {
        addMarkerItems.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        viewSavedMarkersButton.setVisibility(View.GONE);
        isAddingMarker = true;
    }

    public void hideAddMarker() {
        addMarkerItems.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        viewSavedMarkersButton.setVisibility(View.VISIBLE);
        isAddingMarker = false;
    }

    public void viewSavedMarkersClicked(View view) {
        SavedMarkersFragment savedLocationsFragment = SavedMarkersFragment.newInstance(isGmu);
        savedLocationsFragment.show(getSupportFragmentManager(),"savedLocations");
    }

    public void onAddFragmentRequested() {
            displayAddMarker();
    }

    public void onClickName(int position) {
        float zoomLevel = isGmu ? 17.6f : 15.0f;
        ArrayList<LatLng> listRef = isGmu ? campusLocations : cityLocations;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(listRef.get(position), zoomLevel));
    }

    public void onRemoveMarker(int position) {
        ArrayList<Integer> listRef = isGmu ? campusIds : cityIds;
        int id = listRef.get(position);
        db.delete(TABLE_MARKERS, MARKER_ID + "=" + id, null);
        if (isGmu) {
            campusIds.remove(position);
            campusLocations.remove(position);
            campusLocationNames.remove(position);
        } else {
            cityIds.remove(position);
            cityLocations.remove(position);
            cityLocationNames.remove(position);
        }
        Toast.makeText(this, "Successfully deleted ", Toast.LENGTH_SHORT).show();
    }

    public void cancelClicked(View view) {
        hideAddMarker();
    }
}
