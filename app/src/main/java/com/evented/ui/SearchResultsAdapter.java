package com.evented.ui;

import android.view.ViewGroup;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;

/**
 * Created by yaaminu on 8/10/17.
 */

public class SearchResultsAdapter extends RecyclerViewBaseAdapter<Event, RecyclerViewBaseAdapter.Holder> {
    public SearchResultsAdapter(Delegate<Event> delegate) {
        super(delegate);
    }

    @Override
    protected void doBindHolder(Holder holder, int position) {
        ((TextView) holder.itemView).setText(getItem(position).getName());
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.event_search_results, parent, false));
    }
}
