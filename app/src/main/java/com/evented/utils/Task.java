package com.evented.utils;

import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Params;

import org.json.JSONObject;

/**
 * by Null-Pointer on 11/28/2015.
 */
@SuppressWarnings("WeakerAccess")
public abstract class Task extends com.birbit.android.jobqueue.Job {

    private static final String TAG = "Task";

    private final boolean isValid;

    protected Task(Params params) {
        super(params);
        isValid = true;
    }

    protected Task() { //required to deserialize task
        super(new Params(1));
        isValid = false;
    }

    protected final boolean isValid() {
        return isValid;
    }

    protected abstract JSONObject toJSON();

    protected abstract Task fromJSON(JSONObject jsonObject);

    @Override
    public void onAdded() {
        PLog.d(TAG, "added %s", this);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        PLog.d(TAG, " %s has been cancelled", this);
    }
}
