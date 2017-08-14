package com.evented.events.data;

import android.os.Parcel;
import android.os.Parcelable;

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
}
