package com.evented.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evented.events.data.Event;

import java.util.List;

/**
 * Created by yaaminu on 8/10/17.
 */

public class SuggestionsArrayAdapter extends BaseAdapter {
    List<Event> items;

    public SuggestionsArrayAdapter(List<Event> items) {
        this.items = items;
    }

    public void refill(List<Event> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @NonNull
    @Override
    public Event getItem(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        ((TextView) convertView).setText(getItem(position).getName());
        return convertView;
    }
}
