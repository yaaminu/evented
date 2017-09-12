package com.evented.events.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;

/**
 * Created by yaaminu on 8/8/17.
 */

public class BillingAcount extends RealmObject implements Parcelable {

    private String accountName;
    private String accountNumber;
    private String accountType;

    public BillingAcount() {
    }

    public BillingAcount(String accountName, String accountNumber, String accountType) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
    }

    protected BillingAcount(Parcel in) {
        accountName = in.readString();
        accountNumber = in.readString();
        accountType = in.readString();
    }

    public static final Creator<BillingAcount> CREATOR = new Creator<BillingAcount>() {
        @Override
        public BillingAcount createFromParcel(Parcel in) {
            return new BillingAcount(in);
        }

        @Override
        public BillingAcount[] newArray(int size) {
            return new BillingAcount[size];
        }
    };

    @Override
    public String toString() {
        return "BillingAcount{" +
                "accountName='" + accountName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType='" + accountType + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(accountName);
        parcel.writeString(accountNumber);
        parcel.writeString(accountType);
    }

    public String toJsonString() {
        try {
            return new JSONObject()
                    .put("accountName", accountName)
                    .put("accountNumber", accountNumber)
                    .put("accountType", accountType).toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static BillingAcount fromJson(String billingAccount) {
        try {
            JSONObject jsonObject = new JSONObject(billingAccount);
            return new BillingAcount(jsonObject.getString("accountName"), jsonObject.getString("accountNumber"),
                    jsonObject.getString("accountType"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
