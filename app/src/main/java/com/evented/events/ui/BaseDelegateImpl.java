package com.evented.events.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.evented.ui.RecyclerViewBaseAdapter;

/**
 * Created by yaaminu on 8/9/17.
 */

public abstract class BaseDelegateImpl<T> implements RecyclerViewBaseAdapter.Delegate<T> {

    private final Context context;

    public BaseDelegateImpl(Context context) {
        this.context = context;
    }

    @Override
    public void onItemClick(RecyclerViewBaseAdapter<T, ?> adapter, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(RecyclerViewBaseAdapter<T, ?> adapter, View view, int position, long id) {
        return false;
    }


    @NonNull
    @Override
    public Context context() {
        return context;
    }
}
