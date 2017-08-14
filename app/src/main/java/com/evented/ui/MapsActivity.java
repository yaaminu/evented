package com.evented.ui;

import android.annotation.SuppressLint;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.evented.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_VENUE = "venue.direction";

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            this.googleMap = googleMap;
            Geocoder geocoder = new Geocoder(this);
            geocoder.getFromLocationName("name", 4);
            this.googleMap.setMyLocationEnabled(true);

            // Add a marker in Sydney and move the camera
            final LatLng loc = new LatLng(5.6218986, -0.17353469999999996);
            this.googleMap.addMarker(new MarkerOptions().position(loc).title("Silverbird Cinemas").visible(true));
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
