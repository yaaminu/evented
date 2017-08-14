package com.evented.events.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.evented.R;
import com.evented.events.data.Venue;
import com.evented.utils.GenericUtils;
import com.evented.utils.PLog;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;

/**
 * Created by yaaminu on 8/14/17.
 */

public class PickPlaceActivity extends AppCompatActivity implements PlaceSelectionListener {
    private static final String TAG = "PickPlaceActivity";

    public static final String
            EXTRA_SEARCH = "search";
    static final String DATA_PLACE = "place";
    private String startWith;
    private SupportPlaceAutocompleteFragment placeAutocompleteFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_places);
        placeAutocompleteFragment = new SupportPlaceAutocompleteFragment();
        placeAutocompleteFragment.setFilter(new AutocompleteFilter.Builder().setTypeFilter(Place.TYPE_COUNTRY).setCountry("GH").build());
        placeAutocompleteFragment.setOnPlaceSelectedListener(this);
        startWith = getIntent().getStringExtra(EXTRA_SEARCH);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, placeAutocompleteFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!GenericUtils.isEmpty(startWith)) {
            placeAutocompleteFragment.setText(startWith);
            placeAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input).performClick();
            startWith = null;
        }
    }

    /**
     * Callback invoked when a place has been selected from the PlaceAutocompleteFragment.
     */
    @Override
    public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place Selected: " + place.getName());
        Log.e(TAG, TextUtils.join(",", new CharSequence[]{place.getName(), place.getId(), place.getAddress(), place.getPhoneNumber(),
                place.getWebsiteUri() + ""}));
        PLog.d(TAG, place.getLatLng().toString());


        Intent intent = new Intent();
        intent.putExtra(DATA_PLACE, new Venue(place.getName().toString(), place.getAddress().toString(),
                place.getLatLng().longitude, place.getLatLng().latitude));
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Callback invoked when PlaceAutocompleteFragment encounters an error.
     */
    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());

        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

}
