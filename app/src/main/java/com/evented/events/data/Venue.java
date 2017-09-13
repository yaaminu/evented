package com.evented.events.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;

/**
 * Created by yaaminu on 8/14/17.
 */

public class Venue extends RealmObject implements Parcelable {
    private String name;
    private String address;
    private double latitude;
    private double longitude;

    public Venue(String name, String address, double longitude, double latitude) {
        this.name = name;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Venue() {

    }

    protected Venue(Parcel in) {
        name = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<Venue> CREATOR = new Creator<Venue>() {
        @Override
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        @Override
        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    public String toJsonString() {
        try {
            return new JSONObject().put("name", getName())
                    .put("address", getAddress())
                    .put("latitude", getLatitude())
                    .put("longitude", getLongitude()).toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static Venue fromJson(String text) {
        try {
            JSONObject json = new JSONObject(text);
            return new Venue(json.getString("name"),
                    json.getString("address"),
                    json.getDouble("longitude"),
                    json.getDouble("latitude")
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
