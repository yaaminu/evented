package com.evented.ui;

import android.annotation.SuppressLint;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;

import java.util.Date;

/**
 * Created by yaaminu on 8/9/17.
 */

class EventsListAdapter extends RecyclerViewBaseAdapter<Event, EventListItemHolder> {

    private final String[] categories;

    public EventsListAdapter(Delegate<Event> delegate) {
        super(delegate);
        categories = delegate.context().getResources().getStringArray(R.array.event_cagories);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void doBindHolder(EventListItemHolder holder, int position) {
        final Event item = getItem(position);
        ((TextView) holder.itemView).setText(item.getName() + "\n" + categories[item.getCategory()] + "\n" +
                new Date(item.getStartDate()));
    }

    @Override
    public EventListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventListItemHolder(inflater.inflate(R.layout.list_item_event, parent, false));
    }
}
