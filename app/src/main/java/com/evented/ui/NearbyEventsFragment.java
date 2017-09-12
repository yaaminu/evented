package com.evented.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.ui.BaseFragment;
import com.evented.utils.GenericUtils;
import com.evented.utils.ViewUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by yaaminu on 9/6/17.
 */

public class NearbyEventsFragment extends BaseFragment implements LocationListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final String TAG_YOU = "you";
    @Nullable
    GoogleMap googleMap;
    @Nullable
    Location currentLocation;
    private Realm realm;

    @BindView(R.id.loading_overlay)
    View loading_overlay;


    @Override
    protected int getLayout() {
        return R.layout.fragment_nearby;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GenericUtils.assertThat(context instanceof LocationProvider);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment supportMapFragment = new SupportMapFragment();
        supportMapFragment.getMapAsync(this);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.maps_fragment, supportMapFragment)
                .commit();
        ((LocationProvider) getActivity())
                .registerLocationListener(this);
    }

    @Override
    public void onDestroyView() {
        ((LocationProvider) getActivity()).unregisterLocationListener(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onLocation(@NonNull Location location) {
        this.currentLocation = location;
        if (this.googleMap != null) {
            ViewUtils.hideViews(loading_overlay);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            final MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(GenericUtils.vectorToDrawable(getContext(), R.drawable.ic_my_location_black_24dp)))
                    .title(getString(R.string.you));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(12.5f).tilt(20)
                    .bearing(0)
                    .build();
            Marker marker = this.googleMap.addMarker(markerOptions);
            marker.setTag(TAG_YOU);
            marker.showInfoWindow();
            this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            RealmResults<Event> events = realm.where(Event.class)
                    .findAll();
            for (Event event : events) {
                marker = this.googleMap
                        .addMarker(new MarkerOptions()
                                .title(event.getName())
                                .position(new LatLng(event.getVenue().getLatitude(),
                                        event.getVenue().getLongitude())));
                marker.showInfoWindow();
                marker.setTag(event.getEventId());
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!TAG_YOU.equals(marker.getTag())) {
            String eventId = ((String) marker.getTag());
            Intent intent = new Intent(getContext(), EventDetailsActivity.class);
            Event event = realm.where(Event.class)
                    .equalTo(Event.FIELD_EVENT_ID, eventId).findFirst();
            GenericUtils.assertThat(event != null);
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, eventId);
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_NAME, event.getName());
            startActivity(intent);
            return true;
        }
        return false;
    }

    public interface LocationProvider {
        void registerLocationListener(LocationListener locationListener);

        void unregisterLocationListener(LocationListener locationListener);
    }

}
