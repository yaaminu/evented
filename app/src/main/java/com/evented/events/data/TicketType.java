package com.evented.events.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.evented.utils.GenericUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;

import io.realm.RealmObject;

/**
 * Created by yaaminu on 8/14/17.
 */

public class TicketType extends RealmObject implements Parcelable {
    private String name;
    private long cost;
    private int maxSeat;

    public static final int UNLIMITED = -1;

    public TicketType() {
    }

    public TicketType(String name, long cost, int maxSeat) {
        this.name = name;
        this.cost = cost;
        this.maxSeat = maxSeat;
    }

    protected TicketType(Parcel in) {
        name = in.readString();
        cost = in.readLong();
        maxSeat = in.readInt();
    }

    public static final Creator<TicketType> CREATOR = new Creator<TicketType>() {
        @Override
        public TicketType createFromParcel(Parcel in) {
            return new TicketType(in);
        }

        @Override
        public TicketType[] newArray(int size) {
            return new TicketType[size];
        }
    };

    public int getMaxSeat() {
        return maxSeat;
    }

    @Override
    public String toString() {
        return "TicketType{" +
                "name='" + name + '\'' +
                ", cost=" + cost +
                ", maxSeat=" + maxSeat +
                '}';
    }

    public String getName() {
        return name;
    }

    public long getCost() {
        return cost;
    }

    public String toReadableString() {
        //noinspection StringBufferReplaceableByString
        return new StringBuilder(name.length() + 35).append("<b>").append(name)
                .append(", seats - ").append(maxSeat).append(", cost - ")
                .append(getFormatedCost()).append("</b>").toString()
                ;
    }

    public String getFormatedCost() {
        return "GH₵" + GenericUtils.format(BigDecimal.valueOf(cost)
                .divide(BigDecimal.valueOf(100), MathContext.DECIMAL128).doubleValue());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeLong(cost);
        parcel.writeInt(maxSeat);
    }

    public String toJSONString() throws JSONException {
        return new JSONObject()
                .put("name", getName())
                .put("cost", getCost())
                .put("maxSeat", maxSeat).toString();
    }

    public static TicketType fromJson(String text) {
        try {
            JSONObject obj = new JSONObject(text);
            return new TicketType(obj.getString("name"), obj.getLong("cost")
                    , obj.getInt("maxSeat"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
