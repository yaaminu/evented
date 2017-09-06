package com.evented.events.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.evented.R;
import com.evented.ui.NearbyEventsFragment;
import com.evented.utils.GenericUtils;
import com.evented.utils.PLog;

import java.util.HashSet;
import java.util.Set;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by yaaminu on 9/6/17.
 */

public class NearbyEvents extends AppCompatActivity implements LocationListener, NearbyEventsFragment.LocationProvider {

    private static final String TAG = "NearbyEvents";

    private LocationManager locationManager;
    private Location location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_nearby_events);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        location = null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocaitonDisabledPrompt();
            return;
        }
        if (GenericUtils.hasPermission(this, true, 1002, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)) {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.getMainLooper());
        }
    }

    private void showLocaitonDisabledPrompt() {
        new AlertDialog.Builder(this)
                .setMessage("Location required")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }).create().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (GenericUtils.hasPermission(this, false,
                1002, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.location = location;
            PLog.d(TAG, location.toString());
            for (com.evented.ui.LocationListener listener : listeners) {
                listener.onLocation(location);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }


    @Override
    public void onProviderDisabled(String provider) {

    }

    Set<com.evented.ui.LocationListener> listeners = new HashSet<>(2);

    @SuppressLint("MissingPermission")
    @Override
    public void registerLocationListener(com.evented.ui.LocationListener locationListener) {
        listeners.add(locationListener);
        if (location != null) {
            locationListener.onLocation(location);
        }
    }

    @Override
    public void ungisterLocationListener(com.evented.ui.LocationListener locationListener) {
        listeners.remove(locationListener);
    }
}
