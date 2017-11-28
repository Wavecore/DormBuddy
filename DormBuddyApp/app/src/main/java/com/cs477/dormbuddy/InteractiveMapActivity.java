package com.cs477.dormbuddy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class InteractiveMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean isGmu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_map);
        //get which map to retrieve
        isGmu = getIntent().getBooleanExtra(MapBuddyActivity.IS_GMU, true);
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
        /* debug for logcat to tell me lat longs
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                System.out.println(mMap.getCameraPosition());
            }
        });*/
    }
}
