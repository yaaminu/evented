package com.evented.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;

import java.util.List;

/**
 * Created by yaaminu on 8/10/17.
 */

public class SuggestionsArrayAdapter extends BaseAdapter {
    List<Event> items;
    private int drawablePadding;

    public SuggestionsArrayAdapter(Context context, List<Event> items) {
        this.items = items;
        drawablePadding = context.getResources().getDimensionPixelSize(R.dimen.default_gap);
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
        ((TextView) convertView).setCompoundDrawablePadding(drawablePadding);
        ((TextView) convertView).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_suggestion_arrow_slanted_24dp, 0, 0, 0);
        return convertView;
    }
}
