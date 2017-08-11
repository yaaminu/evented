package com.evented.ui;

import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by yaaminu on 8/11/17.
 */

public abstract class BaseListViewAdapter<T> extends BaseAdapter {

    private List<T> items;

    public BaseListViewAdapter(List<T> items) {
        this.items = items;
    }

    public void refill(List<T> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public T getItem(int i) {
        return items.get(i);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}

