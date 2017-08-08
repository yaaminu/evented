package com.evented.events.data;

import io.realm.RealmObject;

/**
 * Created by yaaminu on 8/8/17.
 */

public class BillingAcount extends RealmObject {

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

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
